package org.colorcoding.ibas.thirdpartyapp.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.bo.organization.User;
import org.colorcoding.ibas.initialfantasy.repository.BORepositoryInitialFantasy;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class WebApp extends ApplicationClient {

	protected static final String MSG_CONNECTING_URL = "Application Client: connection [%s] will open [%s].";
	protected static final String MSG_CONNECTED_URL = "Application Client: connection [%s] was connected.";

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

	protected JsonNode doGet(String url) throws IOException {
		Map<String, String> headers = new HashMap<String, String>(3);
		headers.put("Accept", "*/*");
		headers.put("Connection", "Keep-Alive");
		headers.put("Content-Type", "application/json; charset=UTF-8");
		return doGet(url, headers);
	}

	protected JsonNode doGet(String url, Map<String, String> headers) throws IOException {
		if (MyConfiguration.isDebugMode()) {
			// 显示请求
			StringBuilder builder = new StringBuilder();
			builder.append(String.format(MSG_CONNECTING_URL, url.hashCode(), url));
			for (Entry<String, String> item : headers.entrySet()) {
				if (builder.length() > 1) {
					builder.append(System.getProperty("NEW_LINE", "\n"));
				}
				builder.append("    ");
				builder.append(item.getKey());
				builder.append(": ");
				builder.append(item.getValue());
			}
			Logger.log(MessageLevel.INFO, builder.toString());
		} else {
			Logger.log(MessageLevel.INFO, MSG_CONNECTING_URL, url.hashCode(), url);
		}
		URL realUrl = new URL(url);
		// 打开和URL之间的连接
		URLConnection connection = realUrl.openConnection();
		if (headers != null && !headers.isEmpty()) {
			for (String key : headers.keySet()) {
				if (key == null || key.isEmpty()) {
					continue;
				}
				connection.setRequestProperty(key, headers.getOrDefault(key, ""));
			}
		}
		// 建立实际的连接
		connection.connect();
		InputStream inputStream = connection.getInputStream();
		if (MyConfiguration.isDebugMode()) {
			// 输出返回值
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			StringBuilder builder = new StringBuilder();
			builder.append(String.format(MSG_CONNECTED_URL, url.hashCode()));
			builder.append(System.getProperty("NEW_LINE", "\n"));
			builder.append(result.toString("UTF-8"));
			Logger.log(MessageLevel.INFO, builder.toString());
			// 重置数据
			inputStream = new ByteArrayInputStream(result.toByteArray());
		} else {
			Logger.log(MessageLevel.INFO, MSG_CONNECTED_URL, url.hashCode());
		}
		return new ObjectMapper().readTree(inputStream);
	}

	protected String paramValue(String name, JsonNode data) throws IndexOutOfBoundsException {
		JsonNode node = data.get(name);
		if (node == null) {
			throw new IndexOutOfBoundsException(I18N.prop("msg_tpa_no_return_value", name));
		}
		return node.textValue();
	}

	public final org.colorcoding.ibas.initialfantasy.bo.shell.User authenticate(Properties params)
			throws AuthenticationException {
		try {
			IUserMapping user = this.fetchUser(params);
			if (user == null) {
				throw new Exception(I18N.prop("msg_tpa_no_matching_user"));
			}
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(User.PROPERTY_CODE.getName());
			condition.setValue(user.getUser());
			condition = criteria.getConditions().create();
			condition.setAlias(User.PROPERTY_ACTIVATED.getName());
			condition.setValue(emYesNo.YES);
			BORepositoryInitialFantasy boRepository = new BORepositoryInitialFantasy();
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			IOperationResult<org.colorcoding.ibas.initialfantasy.bo.organization.IUser> opRsltUser = boRepository
					.fetchUser(criteria);
			org.colorcoding.ibas.initialfantasy.bo.organization.IUser boUser = opRsltUser.getResultObjects()
					.firstOrDefault();
			if (boUser == null) {
				return null;
			}
			org.colorcoding.ibas.initialfantasy.bo.shell.User orgUser = org.colorcoding.ibas.initialfantasy.bo.shell.User
					.create(boUser);
			OrganizationFactory.create().createManager().register(orgUser);
			return orgUser;
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}
	}

	protected abstract IUserMapping fetchUser(Properties params) throws Exception;
}
