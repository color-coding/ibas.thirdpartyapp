package org.colorcoding.ibas.thirdpartyapp.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
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
		return this.connection("POST", url, requestHeaders, contentLength, bodyWriter);
	}

	protected JsonObject connection(String method, String url, Map<String, String> headers) throws IOException {
		return this.connection(method, url, headers, null);
	}

	protected JsonObject connection(String method, String url, Map<String, String> headers, byte[] body)
			throws IOException {
		return this.connection(method, url, headers, body == null ? 0L : body.length,
				body == null ? null : output -> output.write(body));
	}

	private JsonObject connection(String method, String url, Map<String, String> headers, long contentLength,
			RequestBodyWriter bodyWriter) throws IOException {
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
				builder.append(String.format("Body: <%s bytes; content omitted>", contentLength));
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
			try (InputStream stream = new java.io.ByteArrayInputStream(responseBody)) {
				return Json.createReader(stream).readObject();
			} catch (Exception e) {
				throw new WebApplicationException(responseCode);
			}
		} finally {
			connection.disconnect();
		}
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
