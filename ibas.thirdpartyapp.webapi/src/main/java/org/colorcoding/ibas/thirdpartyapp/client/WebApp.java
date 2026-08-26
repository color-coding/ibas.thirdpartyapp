package org.colorcoding.ibas.thirdpartyapp.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.ws.rs.WebApplicationException;

import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;

public abstract class WebApp extends ApplicationClient {

	static final String MSG_CONNECTING_URL = "[%s]: connecting [%s] [%s].";
	static final String MSG_CONNECTED_URL = "[%s]: connection status [%s].";
	private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
	private static final int DEFAULT_READ_TIMEOUT = 300000;
	private static final int DEBUG_BODY_PREVIEW_LENGTH = 2000;

	@FunctionalInterface
	protected interface RequestBodyWriter {
		void write(OutputStream output) throws IOException;
	}

	protected int getConnectTimeout() {
		return DEFAULT_CONNECT_TIMEOUT;
	}

	protected int getReadTimeout() {
		return DEFAULT_READ_TIMEOUT;
	}

	protected String normalizeUrl(String... values) {
		StringBuilder builder = new StringBuilder();
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				builder.append(values[i]);
				if (i + 1 < values.length) {
					if (!values[i].endsWith("/") && !values[i + 1].startsWith("/")) {
						builder.append("/");
					}
				}
			}
		}
		return builder.toString();
	}

	protected Map<String, String> paramUrl(String url) {
		Map<String, String> params = new HashMap<String, String>();
		int index = url == null ? -1 : url.indexOf("?");
		if (index > 0 && index < url.length()) {
			String values = url.substring(index + 1);
			for (String item : values.split("&")) {
				index = item.indexOf("=");
				if (index > 0 && index < item.length()) {
					params.put(item.substring(0, index), item.substring(index + 1));
				}
			}
		}
		return params;
	}

	protected JsonObject doGet(String url) throws IOException {
		return this.doGet(url, new HashMap<String, String>());
	}

	protected JsonObject doGet(String url, Map<String, String> headers) throws IOException {
		if (!headers.containsKey("Accept")) {
			headers.put("Accept", "*/*");
		}
		if (!headers.containsKey("Connection")) {
			headers.put("Connection", "keep-alive");
		}
		return this.connection("GET", url, headers);
	}

	protected JsonObject doPost(String url) throws IOException {
		return this.doPost(url, new HashMap<String, String>(), null);
	}

	protected JsonObject doPost(String url, byte[] body) throws IOException {
		return this.doPost(url, new HashMap<String, String>(), body);
	}

	protected JsonObject doPost(String url, Map<String, String> headers) throws IOException {
		return this.doPost(url, headers, null);
	}

	protected JsonObject doPost(String url, Map<String, String> headers, byte[] body) throws IOException {
		if (!headers.containsKey("Accept")) {
			headers.put("Accept", "*/*");
		}
		if (!headers.containsKey("Connection")) {
			headers.put("Connection", "keep-alive");
		}
		if (!headers.containsKey("Content-Type")) {
			headers.put("Content-Type", "application/json; charset=utf-8");
		}
		return this.connection("POST", url, headers, body);
	}

	protected JsonObject doPost(String url, Map<String, String> headers, long contentLength,
			RequestBodyWriter bodyWriter) throws IOException {
		Map<String, String> requestHeaders = new HashMap<String, String>(headers);
		if (!requestHeaders.containsKey("Accept")) {
			requestHeaders.put("Accept", "*/*");
		}
		if (!requestHeaders.containsKey("Connection")) {
			requestHeaders.put("Connection", "keep-alive");
		}
		return this.connection("POST", url, requestHeaders, contentLength, bodyWriter, null);
	}

	/**
	 * 发送 SSE 请求。该方法独立于现有的完整响应 connection 方法，避免改变旧客户端行为。
	 */
	protected void streamPost(String url, Map<String, String> headers, byte[] body,
			OpenAIStreamListener listener) throws IOException {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		if (headers != null) {
			requestHeaders.putAll(headers);
		}
		if (!requestHeaders.containsKey("Accept")) {
			requestHeaders.put("Accept", "text/event-stream");
		}
		if (!requestHeaders.containsKey("Cache-Control")) {
			requestHeaders.put("Cache-Control", "no-cache");
		}
		if (!requestHeaders.containsKey("Connection")) {
			requestHeaders.put("Connection", "keep-alive");
		}
		if (!requestHeaders.containsKey("Content-Type")) {
			requestHeaders.put("Content-Type", "application/json; charset=utf-8");
		}
		Logger.log(MessageLevel.INFO, MSG_CONNECTING_URL, this.getName(), "POST", url);
		HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
		try {
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(this.getConnectTimeout());
			connection.setReadTimeout(this.getReadTimeout());
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			for (Entry<String, String> item : requestHeaders.entrySet()) {
				connection.setRequestProperty(item.getKey(), item.getValue());
			}
			if (body != null) {
				connection.setFixedLengthStreamingMode(body.length);
				try (OutputStream output = connection.getOutputStream()) {
					output.write(body);
					output.flush();
				}
			}
			int responseCode = connection.getResponseCode();
			Logger.log(MessageLevel.INFO, MSG_CONNECTED_URL, this.getName(), responseCode);
			InputStream input = responseCode >= 200 && responseCode < 300
					? connection.getInputStream() : connection.getErrorStream();
			if (input == null) {
				throw new IOException(String.format("[%s]: connection status [%s], response body: <empty>.",
						this.getName(), responseCode));
			}
			if (responseCode < 200 || responseCode >= 300) {
				try (InputStream stream = input;
						BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
					StringBuilder error = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						error.append(line).append('\n');
					}
					String detail = error.toString().trim();
					Logger.log(MessageLevel.WARN, "[%s]: connection status [%s], response body: %s.", this.getName(),
							responseCode, detail);
					throw new IOException(String.format("[%s]: connection status [%s], response body: %s.",
							this.getName(), responseCode, detail));
				}
			}
			String contentType = connection.getContentType();
			if (contentType == null || contentType.toLowerCase().indexOf("text/event-stream") < 0) {
				try (InputStream stream = input; ByteArrayOutputStream result = new ByteArrayOutputStream()) {
					Files.writeTo(stream, result);
					listener.onEvent(new OpenAIStreamEvent("message", result.toString(StandardCharsets.UTF_8.name())));
				}
				listener.onCompleted();
				return;
			}
			try (InputStream stream = input;
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
				String eventName = "message";
				StringBuilder data = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.length() == 0) {
						if (data.length() > 0) {
							listener.onEvent(new OpenAIStreamEvent(eventName, data.toString()));
						}
						eventName = "message";
						data.setLength(0);
						continue;
					}
					if (line.startsWith(":")) {
						continue;
					}
					int separator = line.indexOf(':');
					String field = separator >= 0 ? line.substring(0, separator) : line;
					String value = separator >= 0 ? line.substring(separator + 1) : "";
					if (value.startsWith(" ")) {
						value = value.substring(1);
					}
					if ("event".equals(field)) {
						eventName = value;
					} else if ("data".equals(field)) {
						if (data.length() > 0) {
							data.append('\n');
						}
						data.append(value);
					}
				}
				if (data.length() > 0) {
					listener.onEvent(new OpenAIStreamEvent(eventName, data.toString()));
				}
			}
			listener.onCompleted();
		} catch (Exception e) {
			listener.onError(e instanceof Exception ? (Exception) e : new IOException(e));
			if (e instanceof IOException) {
				throw (IOException) e;
			}
			throw new IOException(e);
		} finally {
			connection.disconnect();
		}
	}

	protected JsonObject connection(String method, String url, Map<String, String> headers) throws IOException {
		return this.connection(method, url, headers, null);
	}

	protected JsonObject connection(String method, String url, Map<String, String> headers, byte[] body)
			throws IOException {
		return this.connection(method, url, headers, body == null ? 0L : body.length,
				body == null ? null : output -> output.write(body), this.bodyPreview(body, headers));
	}

	private JsonObject connection(String method, String url, Map<String, String> headers, long contentLength,
			RequestBodyWriter bodyWriter, String bodyPreview) throws IOException {
		if (MyConfiguration.isDebugMode()) {
			// 显示请求
			StringBuilder builder = new StringBuilder();
			builder.append(String.format(MSG_CONNECTING_URL, this.getName(), method, url));
			builder.append(System.getProperty("NEW_LINE", "\n"));
			builder.append("Header:");
			for (Entry<String, String> item : headers.entrySet()) {
				builder.append(System.getProperty("NEW_LINE", "\n"));
				builder.append("    ");
				builder.append(item.getKey());
				builder.append(": ");
				builder.append(this.isSensitiveHeader(item.getKey()) ? "<redacted>" : item.getValue());
			}
			if (bodyWriter != null) {
				builder.append(System.getProperty("NEW_LINE", "\n"));
				builder.append(String.format("Body: <%s bytes", contentLength));
				if (bodyPreview == null) {
					builder.append("; content omitted>");
				} else {
					builder.append("> ");
					builder.append(System.getProperty("NEW_LINE", "\n"));
					builder.append(bodyPreview);
				}
			}
			Logger.log(MessageLevel.INFO, builder.toString());
		} else {
			Logger.log(MessageLevel.INFO, MSG_CONNECTING_URL, this.getName(), method, url);
		}
		URL realUrl = URI.create(url).toURL();
		// 打开和URL之间的连接
		HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
		try {
			connection.setRequestMethod(method);
			connection.setConnectTimeout(this.getConnectTimeout());
			connection.setReadTimeout(this.getReadTimeout());
			if (headers != null && !headers.isEmpty()) {
				for (String key : headers.keySet()) {
					if (Strings.isNullOrEmpty(key)) {
						continue;
					}
					connection.setRequestProperty(key, headers.getOrDefault(key, ""));
				}
			}
			// POST请求
			if (Strings.equalsIgnoreCase("POST", method)) {
				connection.setUseCaches(false);
				if (bodyWriter != null) {
					connection.setDoOutput(true);
					if (contentLength >= 0) {
						connection.setFixedLengthStreamingMode(contentLength);
					}
					try (OutputStream stream = connection.getOutputStream()) {
						bodyWriter.write(stream);
						stream.flush();
					}
				}
			}
			// 建立实际的连接
			connection.connect();
			int responseCode = connection.getResponseCode();
			InputStream resultStream;
			if (responseCode >= 200 && responseCode < 300) {
				// 正常返回值
				resultStream = connection.getInputStream();
			} else {
				// 错误返回值
				resultStream = connection.getErrorStream();
			}
			if (resultStream == null) {
				throw new WebApplicationException(responseCode);
			}
			byte[] responseBody;
			try (InputStream stream = resultStream; ByteArrayOutputStream result = new ByteArrayOutputStream()) {
				Files.writeTo(stream, result);
				responseBody = result.toByteArray();
			}
			Logger.log(MessageLevel.INFO, MSG_CONNECTED_URL, this.getName(), responseCode);
			if (responseBody.length == 0) {
				throw new WebApplicationException(I18N.prop("msg_tpa_no_return_value", this.getName()), responseCode);
			}
			if (responseCode < 200 || responseCode >= 300) {
				// 保留第三方服务返回的校验详情（例如 model/messages 无效），否则调用方只能看到裸状态码。
				String detail = new String(responseBody, StandardCharsets.UTF_8);
				Logger.log(MessageLevel.WARN, "[%s]: connection status [%s], response body: %s.", this.getName(),
						responseCode, detail);
				throw new WebApplicationException(detail, responseCode);
			}
			try (InputStream stream = new java.io.ByteArrayInputStream(responseBody)) {
				return Json.createReader(stream).readObject();
			} catch (Exception e) {
				throw new WebApplicationException("Invalid JSON response from " + this.getName(), responseCode);
			}
		} finally {
			connection.disconnect();
		}
	}

	private String bodyPreview(byte[] body, Map<String, String> headers) {
		if (!MyConfiguration.isDebugMode() || body == null || body.length == 0) {
			return null;
		}
		String contentType = headers == null ? null : headers.get("Content-Type");
		if (contentType == null) {
			return null;
		}
		String mediaType = contentType.toLowerCase(java.util.Locale.ROOT);
		if (!mediaType.startsWith("application/json") && !mediaType.startsWith("text/")) {
			return null;
		}
		int length = Math.min(body.length, DEBUG_BODY_PREVIEW_LENGTH);
		String text = new String(body, 0, length, StandardCharsets.UTF_8);
		if (body.length > length) {
			text += "...";
		}
		return text.replace("\n", "\n    ");
	}

	private boolean isSensitiveHeader(String name) {
		return Strings.equalsIgnoreCase(name, "Authorization") || Strings.equalsIgnoreCase(name, "Proxy-Authorization")
				|| Strings.equalsIgnoreCase(name, "Cookie") || Strings.equalsIgnoreCase(name, "Set-Cookie")
				|| Strings.equalsIgnoreCase(name, "X-API-Key") || Strings.equalsIgnoreCase(name, "Api-Key");
	}

	protected String paramValue(String name, JsonObject data) throws IndexOutOfBoundsException {
		JsonValue node = data.get(name);
		if (node == null) {
			throw new IndexOutOfBoundsException(I18N.prop("msg_tpa_no_return_value", name));
		}
		if (node.getValueType() == ValueType.STRING) {
			return ((JsonString) node).getString();
		} else if (node.getValueType() == ValueType.NUMBER) {
			return ((JsonNumber) node).numberValue().toString();
		} else if (node.getValueType() == ValueType.TRUE) {
			return Boolean.TRUE.toString();
		} else if (node.getValueType() == ValueType.FALSE) {
			return Boolean.FALSE.toString();
		}
		return node.toString();
	}
}
