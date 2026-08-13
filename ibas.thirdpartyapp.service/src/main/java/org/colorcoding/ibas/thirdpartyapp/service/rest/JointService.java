package org.colorcoding.ibas.thirdpartyapp.service.rest;

import java.net.URLDecoder;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.EncryptMD5;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.bo.shell.User;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationClient;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationClientManager;
import org.colorcoding.ibas.thirdpartyapp.client.SSO;

/**
 * 联合应用
 */
@Path("joint")
public class JointService {

	/**
	 * 配置项目-登录地址
	 */
	public final static String CONFIG_ITEM_LOGIN_URL = "LoginUrl";
	/**
	 * 配置项目-启用登录口令
	 */
	public final static String CONFIG_ITEM_ENABLE_LOGIN_TOKEN = "EnableLoginToken";
	/**
	 * 配置项目-登录地址
	 */
	public final static String PARAMETER_REDIRECT_URI = "redirect";
	/**
	 * 配置项目-应用
	 */
	public final static String PARAMETER_APP = "app";

	/**
	 * 连接
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("connect")
	public OperationResult<User> connect(@Context HttpServletRequest request) {
		try {
			String key = null;
			String app = null;
			StringBuilder stringBuilder = null;
			Properties params = new Properties();
			for (Entry<String, String[]> item : request.getParameterMap().entrySet()) {
				key = item.getKey();
				stringBuilder = new StringBuilder();
				for (String value : item.getValue()) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(",");
					}
					stringBuilder.append(value);
				}
				if (app == null) {
					if (PARAMETER_APP.equalsIgnoreCase(key)) {
						app = stringBuilder.toString();
						continue;
					}
				}
				params.put(key, stringBuilder.toString());
			}
			if (Strings.isNullOrEmpty(app)) {
				throw new Exception(I18N.prop("msg_tpa_no_param", PARAMETER_APP));
			}
			ApplicationClient appClient = ApplicationClientManager.newInstance().create(app);
			if (!(appClient instanceof SSO)) {
				throw new Exception(I18N.prop("msg_tpa_invalid_application", PARAMETER_APP));
			}
			SSO ssoClient = (SSO) appClient;
			User user = ssoClient.authenticate(params);
			if (user == null) {
				throw new Exception(I18N.prop("msg_tpa_no_matching_user"));
			}
			return new OperationResult<User>().addResultObjects(user);
		} catch (Exception e) {
			Logger.log(e);
			return new OperationResult<User>(e);
		}
	}

	/**
	 * 登录
	 * 
	 * @param request  请求
	 * @param response 响应
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_HTML)
	@Path("login")
	public void login(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		OperationResult<User> operationResult = this.connect(request);
		if (operationResult.getError() != null) {
			throw new WebApplicationException(operationResult.getError(), 500);
		}
		try {
			User user = operationResult.getResultObjects().firstOrDefault();
			if (user == null || !(user.getId() > 0)) {
				throw new Exception(I18N.prop("msg_tpa_no_matching_user"));
			}
			String url = request.getParameter(PARAMETER_REDIRECT_URI);
			if (url != null && !url.isEmpty()) {
				url = URLDecoder.decode(url, "utf-8");
			}
			url = MyConfiguration.getConfigValue(CONFIG_ITEM_LOGIN_URL, url);
			if (url == null || url.isEmpty()) {
				throw new Exception(I18N.prop("msg_tpa_no_param", CONFIG_ITEM_LOGIN_URL));
			}
			if (MyConfiguration.getConfigValue(CONFIG_ITEM_ENABLE_LOGIN_TOKEN, true)) {
				// 启用登录token（临时用户，用完即清）
				User tmpUser = new User();
				tmpUser.setId(User.TEMPORARY_USER_ID_FEATURE_VALUE - user.getId());
				tmpUser.setCode(user.getCode());
				tmpUser.setName(user.getName());
				tmpUser.setSuper(false);
				tmpUser.setTokenTimeStamp();
				tmpUser.setTokenCreateTime(tmpUser.getTokenTimeStamp());
				tmpUser.setIdentities(Strings.VALUE_EMPTY);
				tmpUser.setToken(EncryptMD5.md5(UUID.randomUUID().toString()));
				OrganizationFactory.createManager().register(tmpUser);
				user = tmpUser;
			}
			response.setHeader("authorization",
					String.format("%s %s", MyConfiguration.AUTHENTICATION_SCHEMES_BEARER, user.getToken()));
			StringBuilder stringBuilder = new StringBuilder(url);
			stringBuilder.append(url.indexOf("?") > 0 ? "&" : "?");
			stringBuilder.append("userToken=");
			stringBuilder.append(user.getToken());
			response.sendRedirect(stringBuilder.toString());
		} catch (Exception e) {
			Logger.log(e);
			throw new WebApplicationException(e, 500);
		}
	}

	/**
	 * 授权
	 * 
	 * @param request
	 * @param response
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_HTML)
	@Path("authorize")
	public void authorize(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		try {
			String key = null;
			String app = null;
			StringBuilder stringBuilder = null;
			Properties params = new Properties();
			for (Entry<String, String[]> item : request.getParameterMap().entrySet()) {
				key = item.getKey();
				stringBuilder = new StringBuilder();
				for (String value : item.getValue()) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(",");
					}
					stringBuilder.append(value);
				}
				if (app == null) {
					if (PARAMETER_APP.equalsIgnoreCase(key)) {
						app = stringBuilder.toString();
						continue;
					}
				}
				params.put(key, stringBuilder.toString());
			}
			if (Strings.isNullOrEmpty(app)) {
				throw new Exception(I18N.prop("msg_tpa_no_param", PARAMETER_APP));
			}
			params.put("request", request.getRequestURL().toString());
			ApplicationClient appClient = ApplicationClientManager.newInstance().create(app);
			IOperationResult<String> operationResult = appClient.execute("authorize", params);
			if (operationResult.getError() != null) {
				throw operationResult.getError();
			}
			String url = operationResult.getResultObjects().firstOrDefault();
			if (Strings.isNullOrEmpty(url)) {
				throw new Exception(I18N.prop("msg_tpa_no_return_value", appClient.getName()));
			}
			Logger.log("authorize: %s", url);
			response.sendRedirect(url);
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			Logger.log(e);
			throw new WebApplicationException(e, 500);
		}
	}
}
