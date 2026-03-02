package org.colorcoding.ibas.thirdpartyapp.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.ws.rs.WebApplicationException;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.bo.organization.IUser;
import org.colorcoding.ibas.initialfantasy.bo.shell.User;
import org.colorcoding.ibas.initialfantasy.repository.BORepositoryInitialFantasy;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;

public abstract class WebApp extends ApplicationClient {

	static final String MSG_CONNECTING_URL = "[%s]: connecting [%s] [%s]";
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
				builder.append("    ");
				builder.append(new String(body, "utf-8"));
			}
			Logger.log(MessageLevel.INFO, builder.toString());
		} else {
			Logger.log(MessageLevel.INFO, MSG_CONNECTING_URL, this.getName(), method, url);
		}
		URL realUrl = new URL(url);
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
			InputStream inputStream = null;
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// 正常返回值
				inputStream = connection.getInputStream();
			} else {
				// 错误返回值
				inputStream = connection.getErrorStream();
			}
			// 输出返回值
			if (MyConfiguration.isDebugMode() && !(inputStream == null || inputStream.available() == 0)) {
				try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
					Files.writeTo(inputStream, result);
					StringBuilder builder = new StringBuilder();
					builder.append(String.format(MSG_CONNECTED_URL, this.getName(), responseCode));
					builder.append(System.getProperty("NEW_LINE", "\n"));
					builder.append(result.toString("utf-8"));
					Logger.log(MessageLevel.INFO, builder.toString());
					// 重置数据
					inputStream = new ByteArrayInputStream(result.toByteArray());
				}
			} else {
				Logger.log(MessageLevel.INFO, MSG_CONNECTED_URL, this.getName(), responseCode);
			}
			if (inputStream != null && inputStream.available() != 0) {
				try (InputStream stream = inputStream) {
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

	public final User authenticate(Properties params) throws AuthenticationException {
		try {
			IUserMapping user = this.fetchUser(params);
			if (user == null) {
				throw new Exception(I18N.prop("msg_tpa_no_matching_user"));
			}
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_CODE.getName());
			condition.setValue(user.getUser());
			condition = criteria.getConditions().create();
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_ACTIVATED.getName());
			condition.setValue(emYesNo.YES);
			// 批准的用户
			condition = criteria.getConditions().create();
			condition.setBracketOpen(1);
			condition.setAlias(
					org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_APPROVALSTATUS.getName());
			condition.setValue(emApprovalStatus.UNAFFECTED);
			condition = criteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setRelationship(ConditionRelationship.OR);
			condition.setAlias(
					org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_APPROVALSTATUS.getName());
			condition.setValue(emApprovalStatus.APPROVED);
			// 当前日期
			String date = DateTimes.today().toString();
			// 有效日期
			condition = criteria.getConditions().create();
			condition.setBracketOpen(1);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_VALIDDATE.getName());
			condition.setOperation(ConditionOperation.IS_NULL);
			condition = criteria.getConditions().create();
			condition.setRelationship(ConditionRelationship.OR);
			condition.setBracketOpen(1);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_VALIDDATE.getName());
			condition.setOperation(ConditionOperation.NOT_NULL);
			condition = criteria.getConditions().create();
			condition.setBracketClose(2);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_VALIDDATE.getName());
			condition.setOperation(ConditionOperation.LESS_EQUAL);
			condition.setValue(date);
			// 失效日期
			condition = criteria.getConditions().create();
			condition.setBracketOpen(1);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_INVALIDDATE.getName());
			condition.setOperation(ConditionOperation.IS_NULL);
			condition = criteria.getConditions().create();
			condition.setRelationship(ConditionRelationship.OR);
			condition.setBracketOpen(1);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_INVALIDDATE.getName());
			condition.setOperation(ConditionOperation.NOT_NULL);
			condition = criteria.getConditions().create();
			condition.setBracketClose(2);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_INVALIDDATE.getName());
			condition.setOperation(ConditionOperation.GRATER_EQUAL);
			condition.setValue(date);

			try (BORepositoryInitialFantasy boRepository = new BORepositoryInitialFantasy()) {
				boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
				IOperationResult<IUser> opRsltUser = boRepository.fetchUser(criteria);
				IUser boUser = opRsltUser.getResultObjects().firstOrDefault();
				if (boUser == null) {
					throw new Exception(I18N.prop("msg_tpa_no_matching_user"));
				}
				User orgUser = User.create(boUser);
				OrganizationFactory.createManager().register(orgUser);
				return orgUser;
			}
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}
	}

	protected abstract IUserMapping fetchUser(Properties params) throws Exception;
}
