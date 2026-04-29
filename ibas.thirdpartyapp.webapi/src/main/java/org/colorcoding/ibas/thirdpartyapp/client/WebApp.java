package org.colorcoding.ibas.thirdpartyapp.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.ws.rs.WebApplicationException;

import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;

public abstract class WebApp extends ApplicationClient {

	static final String MSG_CONNECTING_URL = "[%s]: connecting [%s] [%s].";
	static final String MSG_CONNECTED_URL = "[%s]: connection status [%s].";

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

	protected JsonObject connection(String method, String url, Map<String, String> headers) throws IOException {
		return this.connection(method, url, headers, null);
	}

	protected JsonObject connection(String method, String url, Map<String, String> headers, byte[] body)
			throws IOException {
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
				builder.append(item.getValue());
			}
			if (body != null) {
				builder.append(System.getProperty("NEW_LINE", "\n"));
				builder.append("Body:");
				builder.append(System.getProperty("NEW_LINE", "\n"));
				if (body.length < 2048) {
					builder.append(new String(body, "utf-8"));
				} else {
					try (ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
						String line;
						while ((line = br.readLine()) != null) {
							builder.append(line);
							builder.append(System.getProperty("NEW_LINE", "\n"));
							if (Strings.startsWith(line, "Content-Type: application/octet-stream", true)) {
								builder.append("<....>");
								line = null;
								break;
							}
						}
					}
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
				if (body != null && body.length > 0) {
					connection.setDoOutput(true);
					try (OutputStream stream = connection.getOutputStream()) {
						stream.write(body);
						stream.flush();
					}
				}
			}
			// 建立实际的连接
			connection.connect();
			InputStream resultStream = null;
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// 正常返回值
				resultStream = connection.getInputStream();
			} else {
				// 错误返回值
				resultStream = connection.getErrorStream();
			}
			// 输出返回值
			if (MyConfiguration.isDebugMode() && !(resultStream == null || resultStream.available() == 0)) {
				try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
					Files.writeTo(resultStream, result);
					StringBuilder builder = new StringBuilder();
					builder.append(String.format(MSG_CONNECTED_URL, this.getName(), responseCode));
					builder.append(System.getProperty("NEW_LINE", "\n"));
					builder.append(result.toString("utf-8"));
					Logger.log(MessageLevel.INFO, builder.toString());
					// 重置数据
					resultStream = new ByteArrayInputStream(result.toByteArray());
				}
			} else {
				Logger.log(MessageLevel.INFO, MSG_CONNECTED_URL, this.getName(), responseCode);
			}
			if (resultStream != null && resultStream.available() != 0) {
				try (InputStream stream = resultStream) {
					if (responseCode == HttpURLConnection.HTTP_OK
							// 以下错误也可能返回可解析值
							|| responseCode == HttpURLConnection.HTTP_BAD_REQUEST
							|| responseCode == HttpURLConnection.HTTP_UNAUTHORIZED
							|| responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
						try {
							return Json.createReader(stream).readObject();
						} catch (Exception e) {
							throw new WebApplicationException(responseCode);
						}
					}
				}
			}
			throw new WebApplicationException(I18N.prop("msg_tpa_no_return_value", this.getName()));
		} finally {
			connection.disconnect();
		}
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
