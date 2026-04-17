package org.colorcoding.ibas.thirdpartyapp.client;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.json.JsonObject;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.thirdpartyapp.client.openai.ChatCompletionRequest;
import org.colorcoding.ibas.thirdpartyapp.client.openai.ChatCompletionResponse;
import org.colorcoding.ibas.thirdpartyapp.client.openai.ChatMessage;
import org.colorcoding.ibas.thirdpartyapp.client.openai.Serializer;

public class OpenAI_API extends WebApp {
	/**
	 * 参数名称-应用地址
	 */
	public static final String PARAM_NAME_BASE_URL = "base_url";
	/**
	 * 参数名称-应用密钥
	 */
	public static final String PARAM_NAME_API_KEY = "api_key";
	/**
	 * 参数名称-模型
	 */
	public static final String PARAM_NAME_MODEL = "model";
	/**
	 * 参数名称-应用密钥
	 */
	public static final String PARAM_NAME_MESSAGE = "message";
	/**
	 * 运行命令-聊天生成
	 */
	public static final String EXECUT_NAME_CHAT_COMPLETIONS = "chat/completions";

	@Override
	public <P> IOperationResult<P> execute(String instruct, Properties params) throws ApplicationException {
		try {
			if (EXECUT_NAME_CHAT_COMPLETIONS.equalsIgnoreCase(instruct)) {
				// 按名称排序
				List<Object> keys = ArrayList.create(params.keys())
						.where(c -> Strings.startsWith(Strings.valueOf(c), PARAM_NAME_MESSAGE));
				keys.sort((o1, o2) -> Strings.valueOf(o1).compareTo(Strings.valueOf(o2)));

				Object value;
				ArrayList<ChatMessage> messages = new ArrayList<>();
				for (Object key : keys) {
					value = params.get(key);
					if (value instanceof ChatMessage) {
						messages.add((ChatMessage) value);
					}
				}
				if (!messages.isEmpty()) {
					return new OperationResult<P>().addResultObjects(this.completions(messages));
				} else {
					throw new ApplicationException("not found message.");
				}
			}
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		throw new ApplicationException("not implemented.");
	}

	public ChatCompletionResponse completions(ChatMessage message) throws Exception {
		return this.completions(ArrayList.create(message));
	}

	public ChatCompletionResponse completions(Collection<ChatMessage> messages) throws Exception {
		String baseUrl = this.paramValue(PARAM_NAME_BASE_URL, Strings.VALUE_EMPTY);
		if (Strings.isNullOrEmpty(baseUrl)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_BASE_URL));
		}
		String apiKey = this.paramValue(PARAM_NAME_API_KEY, Strings.VALUE_EMPTY);
		if (Strings.isNullOrEmpty(apiKey)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_API_KEY));
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(baseUrl);
		stringBuilder.append(Strings.VALUE_SLASH);
		stringBuilder.append("chat");
		stringBuilder.append(Strings.VALUE_SLASH);
		stringBuilder.append("completions");

		ChatCompletionRequest request = new ChatCompletionRequest();
		request.setModel(this.paramValue(PARAM_NAME_MODEL, Strings.VALUE_EMPTY));
		request.setMessages(ArrayList.create(messages));

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", String.format("Bearer %s", apiKey));
		JsonObject result = null;
		Serializer serializer = new Serializer();
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			serializer.serialize(request, outputStream);
			result = this.doPost(new URI(stringBuilder.toString()).normalize().toString(), headers,
					outputStream.toByteArray());
		}
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		ChatCompletionResponse response = serializer.deserialize(result, new ChatCompletionResponse());
		if (response == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		return response;
	}

}
