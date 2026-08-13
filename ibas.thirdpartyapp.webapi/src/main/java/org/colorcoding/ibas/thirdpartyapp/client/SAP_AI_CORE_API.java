package org.colorcoding.ibas.thirdpartyapp.client;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import jakarta.json.JsonObject;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.task.Daemon;
import org.colorcoding.ibas.bobas.task.IDaemonTask;
import org.colorcoding.ibas.thirdpartyapp.client.openai.ChatCompletionRequest;
import org.colorcoding.ibas.thirdpartyapp.client.openai.ChatCompletionResponse;
import org.colorcoding.ibas.thirdpartyapp.client.openai.Serializer;

/**
 * SAP BTP AI Core 客户端。
 *
 * <p>继承 {@link OpenAI_API}，复用 OpenAI 客户端的 execute、消息处理、
 * 请求模型及响应模型；仅替换 OAuth2 认证和 AI Core deployment URL。</p>
 */
@ApplicationProvider("SAP_AI_CORE")
public class SAP_AI_CORE_API extends OpenAI_API {
	public static final String PARAM_NAME_API_URL = "api_url";
	public static final String PARAM_NAME_AUTH_URL = "auth_url";
	public static final String PARAM_NAME_CLIENT_ID = "client_id";
	public static final String PARAM_NAME_CLIENT_SECRET = "client_secret";
	public static final String PARAM_NAME_RESOURCE_GROUP = "resource_group";
	public static final String PARAM_NAME_DEPLOYMENT_ID = "deployment_id";
	public static final String PARAM_NAME_API_VERSION = "api_version";

	private static final long TOKEN_REFRESH_MARGIN_MILLIS = 60L * 1000L;
	private static final long TOKEN_CACHE_CLEAN_INTERVAL_SECONDS = 60L;

	/** 同一 JVM 内按 BTP OAuth 客户端复用 Token。 */
	private static final Map<String, TokenCache> TOKEN_CACHE = new java.util.concurrent.ConcurrentHashMap<>();
	/** 保护 Token 的检查、清理和刷新，避免同一时间重复请求 OAuth。 */
	private static final Object TOKEN_CACHE_LOCK = new Object();

	static {
		try {
			Daemon.register(new IDaemonTask() {
				@Override
				public void run() {
					clearExpiredTokens();
				}

				@Override
				public boolean isActivated() {
					return true;
				}

				@Override
				public String getName() {
					return "sap ai core token cache cleaner";
				}

				@Override
				public long getInterval() {
					return TOKEN_CACHE_CLEAN_INTERVAL_SECONDS;
				}
			});
		} catch (Exception e) {
			// daemon 初始化失败不应阻止客户端使用；请求前仍会执行过期检查。
		}
	}

	/**
	 * 使用 AI Core deployment 调用 Chat Completions。
	 * OpenAI_API 的 execute 和 completions(message/collection) 会自动复用此方法。
	 */
	@Override
	public ChatCompletionResponse completions(ChatCompletionRequest request) throws Exception {
		if (request == null || request.getMessages() == null || request.getMessages().isEmpty()) {
			throw new IllegalArgumentException("messages are required.");
		}
		if (request.getStream() != null && request.getStream()) {
			throw new IllegalArgumentException("streaming responses are not supported by this client.");
		}

		String apiUrl = required(PARAM_NAME_API_URL);
		String deploymentId = required(PARAM_NAME_DEPLOYMENT_ID);
		if (Strings.isNullOrEmpty(request.getModel())) {
			request.setModel(this.paramValue(PARAM_NAME_MODEL, Strings.VALUE_EMPTY));
		}
		if (request.getReasoningEffort() == null && this.getReasoningEffort() != null) {
			request.setReasoningEffort(this.getReasoningEffort());
		}
		if (request.getThinking() == null && this.getThinking() != null) {
			request.setThinking(this.getThinking());
		}

		String endpoint = this.normalizeUrl(apiUrl, "v2", "inference", "deployments", deploymentId,
				"chat", "completions");
		String apiVersion = this.paramValue(PARAM_NAME_API_VERSION, Strings.VALUE_EMPTY);
		if (!Strings.isNullOrEmpty(apiVersion)) {
			endpoint += "?api-version=" + URLEncoder.encode(apiVersion, "UTF-8");
		}

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + this.getAccessToken());
		headers.put("AI-Resource-Group", this.paramValue(PARAM_NAME_RESOURCE_GROUP, "default"));
		// 保持 OpenAI_API 的自定义请求头能力。
		if (this.getCustomHeaders() != null) {
			headers.putAll(this.getCustomHeaders());
		}

		JsonObject result;
		Serializer serializer = new Serializer();
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			serializer.serialize(request, output);
			result = this.doPost(new URI(endpoint).normalize().toString(), headers, output.toByteArray());
		}
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
		}
		ChatCompletionResponse response = serializer.deserialize(result, new ChatCompletionResponse());
		if (response == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
		}
		if (response.getError() == null && response.getChoices().isEmpty()) {
			ChatCompletionResponse.Error error = new ChatCompletionResponse.Error();
			error.setType("api_error");
			error.setMessage(result.containsKey("detail") ? result.get("detail").toString() : result.toString());
			response.setError(error);
		}
		return response;
	}

	private String getAccessToken() throws Exception {
		String authUrl = required(PARAM_NAME_AUTH_URL);
		String clientId = required(PARAM_NAME_CLIENT_ID);
		String clientSecret = required(PARAM_NAME_CLIENT_SECRET);
		String cacheKey = authUrl + "|" + clientId;
		clearExpiredTokens();
		TokenCache cached = TOKEN_CACHE.get(cacheKey);
		if (cached != null && System.currentTimeMillis() < cached.expiresAt - TOKEN_REFRESH_MARGIN_MILLIS) {
			return cached.accessToken;
		}
		synchronized (TOKEN_CACHE_LOCK) {
			// 进入锁后再次检查，避免并发请求重复获取 Token。
			clearExpiredTokens();
			cached = TOKEN_CACHE.get(cacheKey);
			if (cached != null && System.currentTimeMillis() < cached.expiresAt - TOKEN_REFRESH_MARGIN_MILLIS) {
				return cached.accessToken;
			}
			String body = "grant_type=client_credentials&client_id=" + URLEncoder.encode(clientId, "UTF-8")
					+ "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8");
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/x-www-form-urlencoded");
			JsonObject result = this.doPost(authUrl, headers, body.getBytes(StandardCharsets.UTF_8));
			if (result == null || !result.containsKey("access_token")) {
				throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
			}
			String accessToken = result.getString("access_token");
			long expiresIn = result.containsKey("expires_in") ? result.getJsonNumber("expires_in").longValue() : 3600L;
			TokenCache token = new TokenCache(accessToken, System.currentTimeMillis() + expiresIn * 1000L);
			TOKEN_CACHE.put(cacheKey, token);
			return accessToken;
		}
	}

	/** 清理已过期 Token；请求前和 daemon 任务都会调用。 */
	private static void clearExpiredTokens() {
		for (Map.Entry<String, TokenCache> item : TOKEN_CACHE.entrySet()) {
			TokenCache token = item.getValue();
			if (token == null || System.currentTimeMillis() >= token.expiresAt - TOKEN_REFRESH_MARGIN_MILLIS) {
				TOKEN_CACHE.remove(item.getKey(), token);
			}
		}
	}

	private static class TokenCache {
		private final String accessToken;
		private final long expiresAt;

		private TokenCache(String accessToken, long expiresAt) {
			this.accessToken = accessToken;
			this.expiresAt = expiresAt;
		}

	}

	private String required(String name) throws Exception {
		String value = this.paramValue(name, Strings.VALUE_EMPTY);
		if (Strings.isNullOrEmpty(value)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", name));
		}
		return value;
	}
}
