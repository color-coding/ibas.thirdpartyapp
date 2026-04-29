package org.colorcoding.ibas.thirdpartyapp.client;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.json.JsonObject;

import org.colorcoding.ibas.bobas.common.Bytes;
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
			} else if (EXECUT_NAME_FILES_UPLOAD.equalsIgnoreCase(instruct)) {
				// 按名称排序
				List<Object> keys = ArrayList.create(params.keys())
						.where(c -> Strings.startsWith(Strings.valueOf(c), PARAM_NAME_FILE));
				keys.sort((o1, o2) -> Strings.valueOf(o1).compareTo(Strings.valueOf(o2)));

				Object value;
				OperationResult<P> operationResult = new OperationResult<>();
				for (Object key : keys) {
					value = params.get(key);
					if (value instanceof FileReference) {
						operationResult.addResultObjects(this.fileUpload((FileReference) value));
					} else if (value instanceof FileUploadRequest) {
						operationResult.addResultObjects(this.fileUpload((FileUploadRequest) value));
					}
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
		String baseUrl = this.paramValue(PARAM_NAME_BASE_URL, Strings.VALUE_EMPTY);
		if (Strings.isNullOrEmpty(baseUrl)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_BASE_URL));
		}
		String apiKey = this.paramValue(PARAM_NAME_API_KEY, Strings.VALUE_EMPTY);
		if (Strings.isNullOrEmpty(apiKey)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_API_KEY));
		}
		// 提前上传的文件
		for (ChatMessage message : messages) {
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
						|| contentPart.getFile().getData().length > 50485760) {
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

	public FileUploadResponse fileUpload(FileReference file) throws Exception {
		FileUploadRequest request = new FileUploadRequest();
		request.setPurpose(FileUploadRequest.PURPOSE_VALUE_ASSISTANTS);
		request.setFileName(file.getFileName());
		request.setData(file.getData());
		return this.fileUpload(request);
	}

	private static final String HTTP_HEADER_BOUNDARY = "----WebKitFormBoundaryZX%sX";
	private static final String NEW_LINE = "\r\n";

	public FileUploadResponse fileUpload(FileUploadRequest request) throws Exception {
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

		String boundary = String.format(HTTP_HEADER_BOUNDARY, this.hashCode());

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", String.format("Bearer %s", apiKey));
		headers.put("Content-Type", String.format("multipart/form-data; boundary=%s", boundary));

		JsonObject result = null;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
			/*
			// @formatter:off
			// model 参数
			if (!Strings.isNullOrEmpty(this.paramValue(PARAM_NAME_MODEL, Strings.VALUE_EMPTY))) {
				outputStream.write(Bytes.valueOf("--"));
				outputStream.write(Bytes.valueOf(boundary));
				outputStream.write(Bytes.valueOf(NEW_LINE));
				outputStream.write(Bytes.valueOf("Content-Disposition: form-data; name=\""));
				outputStream.write(Bytes.valueOf("model"));
				outputStream.write(Bytes.valueOf("\""));
				outputStream.write(Bytes.valueOf(NEW_LINE));
				outputStream.write(Bytes.valueOf(NEW_LINE));
				outputStream.write(Bytes.valueOf(this.paramValue(PARAM_NAME_MODEL, Strings.VALUE_EMPTY)));
				outputStream.write(Bytes.valueOf(NEW_LINE));
				outputStream.flush();
			}
			*/
			// purpose 参数
			outputStream.write(Bytes.valueOf("--"));
			outputStream.write(Bytes.valueOf(boundary));
			outputStream.write(Bytes.valueOf(NEW_LINE));
			outputStream.write(Bytes.valueOf("Content-Disposition: form-data; name=\""));
			outputStream.write(Bytes.valueOf("purpose"));
			outputStream.write(Bytes.valueOf("\""));
			outputStream.write(Bytes.valueOf(NEW_LINE));
			outputStream.write(Bytes.valueOf(NEW_LINE));
			outputStream.write(Bytes.valueOf(request.getPurpose()));
			outputStream.write(Bytes.valueOf(NEW_LINE));
			outputStream.flush();
			// file 文件
			outputStream.write(Bytes.valueOf("--"));
			outputStream.write(Bytes.valueOf(boundary));
			outputStream.write(Bytes.valueOf(NEW_LINE));
			outputStream.write(Bytes.valueOf("Content-Disposition: form-data; name=\""));
			outputStream.write(Bytes.valueOf("file"));
			outputStream.write(Bytes.valueOf("\""));
			outputStream.write(Bytes.valueOf("; "));
			outputStream.write(Bytes.valueOf("filename=\""));
			outputStream.write(Bytes.valueOf(request.getFileName()));
			outputStream.write(Bytes.valueOf("\""));
			outputStream.write(Bytes.valueOf(NEW_LINE));
			outputStream.write(Bytes.valueOf("Content-Type: application/octet-stream"));
			outputStream.write(Bytes.valueOf(NEW_LINE));
			// 写入文件内容
			outputStream.write(Bytes.valueOf(NEW_LINE));
			outputStream.write(request.getData());
			outputStream.write(Bytes.valueOf(NEW_LINE));
			outputStream.flush();
			// 结束边界
			outputStream.write(Bytes.valueOf("--"));
			outputStream.write(Bytes.valueOf(boundary));
			outputStream.write(Bytes.valueOf("--"));
			outputStream.write(Bytes.valueOf(NEW_LINE));
			outputStream.flush();

			// 调用请求
			result = this.doPost(new URI(stringBuilder.toString()).normalize().toString(), headers,
					outputStream.toByteArray());
		}
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		Serializer serializer = new Serializer();
		FileUploadResponse response = serializer.deserialize(result, new FileUploadResponse());
		if (response == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		return response;
	}

}
