package org.colorcoding.ibas.thirdpartyapp.service.rest;

import java.net.URLDecoder;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.initialfantasy.bo.shell.User;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationClient;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationClientManager;

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
			OperationResult<User> operationResult = new OperationResult<User>();
			ApplicationClient appClient = ApplicationClientManager.newInstance().create(app);
			operationResult.addResultObjects(appClient.authenticate(params));
			return operationResult;
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
		try {
			OperationResult<User> operationResult = this.connect(request);
			if (operationResult.getError() != null) {
				throw new WebApplicationException(400);
			}
			User user = operationResult.getResultObjects().firstOrDefault();
			if (user == null) {
				throw new WebApplicationException(404);
			}
			String url = request.getParameter(PARAMETER_REDIRECT_URI);
			if (url != null && !url.isEmpty()) {
				url = URLDecoder.decode(url, "utf-8");
			}
			url = MyConfiguration.getConfigValue(CONFIG_ITEM_LOGIN_URL, url);
			if (url == null || url.isEmpty()) {
				throw new WebApplicationException(500);
			}
			response.setHeader("authorization",
					String.format("%s %s", MyConfiguration.AUTHENTICATION_SCHEMES_BEARER, user.getToken()));
			if (MyConfiguration.isDisabledUrlToken()) {
				response.sendRedirect(url);
			} else {
				url += url.indexOf("?") > 0 ? "&" : "?";
				response.sendRedirect(url + String.format("userToken=%s", user.getToken()));
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new WebApplicationException(e);
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
			if (operationResult.getResultObjects().isEmpty()) {
				throw new WebApplicationException(400);
			}
			Logger.log("authorize: %s", operationResult.getResultObjects().firstOrDefault());
			response.sendRedirect(operationResult.getResultObjects().firstOrDefault());
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			Logger.log(e);
			throw new WebApplicationException(e);
		}
	}
}
