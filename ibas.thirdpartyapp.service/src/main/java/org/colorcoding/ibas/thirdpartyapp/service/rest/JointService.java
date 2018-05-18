package org.colorcoding.ibas.thirdpartyapp.service.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.initialfantasy.bo.shell.User;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryJointApplication;

/**
 * 联合应用
 */
@Path("joint")
public class JointService extends BORepositoryJointApplication {

	/**
	 * 配置项目-登录地址
	 */
	public final static String CONFIG_ITEM_LOGIN_URL = "LoginUrl";

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
		String app = "";
		Map<String, Object> params = new HashMap<>();
		for (Entry<String, String[]> item : request.getParameterMap().entrySet()) {
			String key = item.getKey();
			StringBuilder stringBuilder = new StringBuilder();
			for (String value : item.getValue()) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(",");
				}
				stringBuilder.append(value);
			}
			if (key.equalsIgnoreCase("app")) {
				app = stringBuilder.toString();
				continue;
			}
			params.put(key, stringBuilder.toString());
		}
		return super.jointConnect(app, params);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_HTML)
	@Path("login")
	public void login(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		try {
			OperationResult<User> operationResult = this.connect(request);
			if (operationResult.getError() != null) {
				throw operationResult.getError();
			}
			User user = operationResult.getResultObjects().firstOrDefault();
			if (user == null) {
				throw new WebApplicationException(404);
			}
			String url = MyConfiguration.getConfigValue(CONFIG_ITEM_LOGIN_URL, "");
			if (!url.endsWith("/index.html")) {
				if (!url.endsWith("/")) {
					url += "/";
				}
				url += "index.html";
			}
			response.sendRedirect(url + String.format("?userToken=%s", user.getToken()));
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
}