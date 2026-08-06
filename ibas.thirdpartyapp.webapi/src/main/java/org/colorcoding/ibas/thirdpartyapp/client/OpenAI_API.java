package org.colorcoding.ibas.thirdpartyapp.client;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

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
import org.colorcoding.ibas.thirdpartyapp.client.openai.ContentPart;
import org.colorcoding.ibas.thirdpartyapp.client.openai.FileReference;
import org.colorcoding.ibas.thirdpartyapp.client.openai.FileUploadRequest;
import org.colorcoding.ibas.thirdpartyapp.client.openai.FileUploadResponse;
import org.colorcoding.ibas.thirdpartyapp.client.openai.Serializer;

public class OpenAI_API extends WebApp {
	private static final long MAX_INLINE_FILE_BYTES = 50L * 1024L * 1024L;
	private static final long MAX_UPLOAD_FILE_BYTES = 512L * 1024L * 1024L;
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
	 * 参数名称-文件模式(base64;upload)
	 */
	public static final String PARAM_NAME_FILE_MODE = "file_mode";
	/**
	 * 参数名称-消息
	 */
	public static final String PARAM_NAME_MESSAGE = "message";
	/**
	 * 参数名称-文件
	 */
	public static final String PARAM_NAME_FILE = "file";
	/**
	 * 运行命令-聊天生成
	 */
	public static final String EXECUT_NAME_CHAT_COMPLETIONS = "chat/completions";
	/**
	 * 运行命令-文件上传
	 */
	public static final String EXECUT_NAME_FILES_UPLOAD = "files/upload";

	/**
	 * 自定义HTTP请求头
	 */
	private Map<String, String> customHeaders;

	/**
	 * 第三方 OpenAI 兼容服务的 thinking 扩展参数
	 */
	private Object thinking;
	/**
	 * OpenAI 标准推理强度（none/minimal/low/medium/high/xhigh/max）
	 */
	private String reasoningEffort;

	/**
	 * 获取自定义HTTP请求头
	 *
	 * @return 请求头集合
	 */
	public Map<String, String> getCustomHeaders() {
		if (this.customHeaders == null) {
			this.customHeaders = new HashMap<String, String>();
		}
		return this.customHeaders;
	}

	/**
	 * 设置自定义HTTP请求头
	 *
	 * @param customHeaders 请求头集合
	 */
	public void setCustomHeaders(Map<String, String> customHeaders) {
		this.customHeaders = null;
		if (customHeaders != null) {
			for (Map.Entry<String, String> item : customHeaders.entrySet()) {
				this.addCustomHeader(item.getKey(), item.getValue());
			}
		}
	}

	/**
	 * 添加自定义HTTP请求头
	 *
	 * @param name  请求头名称
	 * @param value 请求头值
	 */
	public void addCustomHeader(String name, String value) {
		this.validateCustomHeader(name);
		this.getCustomHeaders().put(name, value);
	}

	private void validateCustomHeader(String name) {
		if (Strings.equalsIgnoreCase(name, "Authorization") || Strings.equalsIgnoreCase(name, "Content-Type")) {
			throw new IllegalArgumentException(String.format("header [%s] is reserved.", name));
		}
	}

	/**
	 * 获取推理控制参数
	 *
	 * @return 推理控制参数
	 */
	public Object getThinking() {
		return thinking;
	}

	/**
	 * 设置第三方 OpenAI 兼容服务的 thinking 扩展参数。官方 OpenAI 请使用
	 * {@link #setReasoningEffort(String)}。
	 *
	 * @param thinking 推理控制参数
	 */
	public void setThinking(Object thinking) {
		this.thinking = thinking;
	}

	public String getReasoningEffort() {
		return reasoningEffort;
	}

	public void setReasoningEffort(String reasoningEffort) {
		this.reasoningEffort = reasoningEffort;
	}

	@Override
	public <P> IOperationResult<P> execute(String instruct, Properties params) throws ApplicationException {
		try {
			if (params == null) {
				throw new ApplicationException("params are required.");
			}
			if (EXECUT_NAME_CHAT_COMPLETIONS.equalsIgnoreCase(instruct)) {
				// 按名称排序
				List<Object> keys = ArrayList.create(params.keys())
						.where(c -> this.parseIndex(PARAM_NAME_MESSAGE, Strings.valueOf(c)) != null);
				keys.sort((o1, o2) -> this.compareIndexedKeys(PARAM_NAME_MESSAGE, o1, o2));

				Object value;
				ArrayList<ChatMessage> messages = new ArrayList<>();
				for (Object key : keys) {
					value = params.get(key);
					if (value instanceof ChatMessage) {
						messages.add((ChatMessage) value);
					} else if (value instanceof Collection) {
						for (Object item : (Collection<?>) value) {
							if (!(item instanceof ChatMessage)) {
								throw new ApplicationException("invalid message collection item.");
							}
							messages.add((ChatMessage) item);
						}
					} else {
						throw new ApplicationException(String.format("invalid message [%s].", key));
					}
				}
				if (!messages.isEmpty()) {
					return new OperationResult<P>().addResultObjects(this.completions(messages));
				} else {
					throw new ApplicationException("not found message.");
				}
			} else if (EXECUT_NAME_FILES_UPLOAD.equalsIgnoreCase(instruct)) {
				// 按名称排序
				List<Object> keys = ArrayList.create(params.keys())
						.where(c -> this.parseIndex(PARAM_NAME_FILE, Strings.valueOf(c)) != null);
				keys.sort((o1, o2) -> this.compareIndexedKeys(PARAM_NAME_FILE, o1, o2));

				Object value;
				OperationResult<P> operationResult = new OperationResult<>();
				for (Object key : keys) {
					value = params.get(key);
					if (value instanceof FileReference) {
						operationResult.addResultObjects(this.fileUpload((FileReference) value));
					} else if (value instanceof FileUploadRequest) {
						operationResult.addResultObjects(this.fileUpload((FileUploadRequest) value));
					} else {
						throw new ApplicationException(String.format("invalid file [%s].", key));
					}
				}
				if (operationResult.getResultObjects().isEmpty()) {
					throw new ApplicationException("not found file.");
				}
				return operationResult;
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
		if (messages == null || messages.isEmpty()) {
			throw new IllegalArgumentException("messages are required.");
		}
		ChatCompletionRequest request = new ChatCompletionRequest();
		request.setMessages(ArrayList.create(messages));
		return this.completions(request);
	}

	/**
	 * 使用完整 Chat Completions 请求。流式响应需要专用 SSE 客户端，本方法仅支持 {@code stream=false}。
	 */
	public ChatCompletionResponse completions(ChatCompletionRequest request) throws Exception {
		if (request == null || request.getMessages() == null || request.getMessages().isEmpty()) {
			throw new IllegalArgumentException("messages are required.");
		}
		String baseUrl = this.paramValue(PARAM_NAME_BASE_URL, Strings.VALUE_EMPTY);
		if (Strings.isNullOrEmpty(baseUrl)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_BASE_URL));
		}
		String apiKey = this.paramValue(PARAM_NAME_API_KEY, Strings.VALUE_EMPTY);
		if (Strings.isNullOrEmpty(apiKey)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_API_KEY));
		}
		// 提前上传的文件
		for (ChatMessage message : request.getMessages()) {
			java.util.List<ContentPart> contentParts = message.getContentAsParts();
			if (contentParts == null) {
				continue;
			}
			for (ContentPart contentPart : contentParts) {
				if (!contentPart.isFile() || contentPart.getFile() == null) {
					continue;
				}
				if (!Strings.isNullOrEmpty(contentPart.getFile().getFileId())) {
					continue;
				}
				if (contentPart.getFile().getData() == null) {
					continue;
				}
				if (!Strings.equalsIgnoreCase(this.paramValue(PARAM_NAME_FILE_MODE, "base64"), "base64")
						|| contentPart.getFile().getData().length > MAX_INLINE_FILE_BYTES) {
					// upload模式: 更新文件ID号，大于50m强制使用
					contentPart.getFile().setFileId(this.fileUpload(contentPart.getFile()).getId());
				}
			}
		}
		// 发送正式消息
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(baseUrl);
		stringBuilder.append(Strings.VALUE_SLASH);
		stringBuilder.append("chat");
		stringBuilder.append(Strings.VALUE_SLASH);
		stringBuilder.append("completions");

		if (Strings.isNullOrEmpty(request.getModel())) {
			request.setModel(this.paramValue(PARAM_NAME_MODEL, Strings.VALUE_EMPTY));
		}
		if (Strings.isNullOrEmpty(request.getReasoningEffort()) && !Strings.isNullOrEmpty(this.reasoningEffort)) {
			request.setReasoningEffort(this.reasoningEffort);
		}
		if (request.getThinking() == null && this.thinking != null) {
			request.setThinking(this.thinking);
		}

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", String.format("Bearer %s", apiKey));
		// 附加自定义请求头
		if (this.customHeaders != null && !this.customHeaders.isEmpty()) {
			for (Map.Entry<String, String> item : this.customHeaders.entrySet()) {
				this.validateCustomHeader(item.getKey());
				headers.put(item.getKey(), item.getValue());
			}
		}
		JsonObject result = null;
		Serializer serializer = new Serializer();
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			serializer.serialize(request, outputStream);
			result = this.doPost(new URI(stringBuilder.toString()).normalize().toString(), headers,
					outputStream.toByteArray());
		}
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
		}
		ChatCompletionResponse response = serializer.deserialize(result, new ChatCompletionResponse());
		if (response == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
		}
		// 处理非标准错误响应（如 FastAPI 的 {"detail":"Not Found"}）
		if (response.getError() == null && response.getChoices().isEmpty()) {
			ChatCompletionResponse.Error error = new ChatCompletionResponse.Error();
			if (result.containsKey("detail")) {
				javax.json.JsonValue detailNode = result.get("detail");
				if (detailNode instanceof javax.json.JsonString) {
					error.setMessage(((javax.json.JsonString) detailNode).getString());
				} else {
					error.setMessage(detailNode.toString());
				}
			} else if (result.containsKey("message")) {
				javax.json.JsonValue msgNode = result.get("message");
				if (msgNode instanceof javax.json.JsonString) {
					error.setMessage(((javax.json.JsonString) msgNode).getString());
				} else {
					error.setMessage(msgNode.toString());
				}
			} else {
				error.setMessage(result.toString());
			}
			error.setType("api_error");
			response.setError(error);
		}
		return response;
	}

	public FileUploadResponse fileUpload(FileReference file) throws Exception {
		if (file == null) {
			throw new IllegalArgumentException("file is required.");
		}
		FileUploadRequest request = new FileUploadRequest();
		request.setPurpose(FileUploadRequest.PURPOSE_VALUE_USER_DATA);
		request.setFileName(file.getFileName());
		request.setData(file.getData());
		return this.fileUpload(request);
	}

	private static final String NEW_LINE = "\r\n";

	public FileUploadResponse fileUpload(FileUploadRequest request) throws Exception {
		this.validateFileUploadRequest(request);
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
		stringBuilder.append("files");

		String boundary = "----ibas-" + UUID.randomUUID().toString();

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", String.format("Bearer %s", apiKey));
		headers.put("Content-Type", String.format("multipart/form-data; boundary=%s", boundary));

		StringBuilder prefixBuilder = new StringBuilder();
		prefixBuilder.append("--").append(boundary).append(NEW_LINE);
		prefixBuilder.append("Content-Disposition: form-data; name=\"purpose\"").append(NEW_LINE).append(NEW_LINE)
				.append(request.getPurpose()).append(NEW_LINE);
		prefixBuilder.append("--").append(boundary).append(NEW_LINE);
		prefixBuilder.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(request.getFileName())
				.append("\"").append(NEW_LINE);
		prefixBuilder.append("Content-Type: application/octet-stream").append(NEW_LINE).append(NEW_LINE);
		byte[] prefix = prefixBuilder.toString().getBytes(StandardCharsets.UTF_8);
		byte[] suffix = (NEW_LINE + "--" + boundary + "--" + NEW_LINE).getBytes(StandardCharsets.UTF_8);
		long contentLength = (long) prefix.length + request.getData().length + suffix.length;
		JsonObject result = this.doPost(new URI(stringBuilder.toString()).normalize().toString(), headers,
				contentLength, output -> {
					output.write(prefix);
					output.write(request.getData());
					output.write(suffix);
				});
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
		}
		Serializer serializer = new Serializer();
		FileUploadResponse response = serializer.deserialize(result, new FileUploadResponse());
		if (response == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
		}
		if (Strings.isNullOrEmpty(response.getId())) {
			throw new Exception(this.getApiErrorMessage(result));
		}
		return response;
	}

	private void validateFileUploadRequest(FileUploadRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("file upload request is required.");
		}
		if (Strings.isNullOrEmpty(request.getPurpose())) {
			throw new IllegalArgumentException("purpose is required.");
		}
		if (Strings.isNullOrEmpty(request.getFileName())) {
			throw new IllegalArgumentException("file name is required.");
		}
		if (request.getData() == null) {
			throw new IllegalArgumentException("file data is required.");
		}
		if (request.getData().length > MAX_UPLOAD_FILE_BYTES) {
			throw new IllegalArgumentException("file data exceeds the 512 MB Files API limit.");
		}
		if (this.containsMultipartControl(request.getPurpose())
				|| this.containsMultipartControl(request.getFileName())) {
			throw new IllegalArgumentException("purpose and file name must not contain quotes or line breaks.");
		}
	}

	private boolean containsMultipartControl(String value) {
		return value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('"') >= 0;
	}

	private String getApiErrorMessage(JsonObject result) {
		if (result != null && result.get("error") instanceof JsonObject) {
			JsonObject error = (JsonObject) result.get("error");
			if (error.get("message") instanceof javax.json.JsonString) {
				return error.getString("message");
			}
		}
		if (result != null && result.get("detail") instanceof javax.json.JsonString) {
			return result.getString("detail");
		}
		if (result != null && result.get("message") instanceof javax.json.JsonString) {
			return result.getString("message");
		}
		return I18N.prop("msg_tpa_failed_oauth_request");
	}

	private int compareIndexedKeys(String prefix, Object left, Object right) {
		String leftValue = Strings.valueOf(left);
		String rightValue = Strings.valueOf(right);
		Long leftIndex = this.parseIndex(prefix, leftValue);
		Long rightIndex = this.parseIndex(prefix, rightValue);
		if (leftIndex != null && rightIndex != null) {
			int value = leftIndex.compareTo(rightIndex);
			return value == 0 ? leftValue.compareTo(rightValue) : value;
		}
		if (leftIndex != null) {
			return -1;
		}
		if (rightIndex != null) {
			return 1;
		}
		return leftValue.compareTo(rightValue);
	}

	private Long parseIndex(String prefix, String value) {
		if (!value.startsWith(prefix)) {
			return null;
		}
		String suffix = value.substring(prefix.length());
		if (suffix.startsWith(".") || suffix.startsWith("_") || suffix.startsWith("-")) {
			suffix = suffix.substring(1);
		}
		if (Strings.isNullOrEmpty(suffix)) {
			return Long.valueOf(0L);
		}
		try {
			return Long.valueOf(suffix);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
