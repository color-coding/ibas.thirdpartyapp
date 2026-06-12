package org.colorcoding.ibas.thirdpartyapp.client;

import java.net.URLEncoder;
import java.util.Properties;
import java.util.UUID;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;

public abstract class OIDC extends SSO {
	/**
	 * 参数名称-应用标记
	 */
	public static final String PARAM_NAME_CLIENT_ID = "client_id";
	/**
	 * 参数名称-应用密钥
	 */
	public static final String PARAM_NAME_CLIENT_SECRET = "client_secret";
	/**
	 * 参数名称-授权终端地址
	 */
	public static final String PARAM_NAME_AUTHORIZE_ENDPOINT = "authorize_endpoint";
	/**
	 * 参数名称-令牌端点地址
	 */
	public static final String PARAM_NAME_TOKEN_ENDPOINT = "token_endpoint";
	/**
	 * 参数名称-用户信息端点地址
	 */
	public static final String PARAM_NAME_USERINFO_ENDPOINT = "userinfo_endpoint";
	/**
	 * 参数名称-用户信息内容
	 */
	public static final String PARAM_NAME_SCOPE = "scope";
	/**
	 * 参数名称-授权码
	 */
	public static final String PARAM_NAME_CODE = "code";
	/**
	 * 参数名称-授权模式
	 */
	public static final String PARAM_NAME_RESPONSE_TYPE = "response_type";
	/**
	 * 参数名称-回调地址
	 */
	public static final String PARAM_NAME_REDIRECT_URI = "redirect_uri";
	/**
	 * 参数名称-请求地址
	 */
	public static final String PARAM_NAME_REQUEST = "request";

	@Override
	public <P> IOperationResult<P> execute(String instruct, Properties params) throws ApplicationException {
		try {
			if ("authorize".equalsIgnoreCase(instruct)) {
				String endpoint = this.paramValue(PARAM_NAME_AUTHORIZE_ENDPOINT, "");
				if (Strings.isNullOrEmpty(endpoint)) {
					throw new Exception(
							I18N.prop("msg_tpa_invalid_application_setting_item", PARAM_NAME_AUTHORIZE_ENDPOINT));
				}
				String request = this.paramValue(PARAM_NAME_REQUEST, "", params);

				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(endpoint);
				stringBuilder.append("?");
				stringBuilder.append("client_id");
				stringBuilder.append("=");
				stringBuilder.append(this.paramValue(PARAM_NAME_CLIENT_ID, ""));
				stringBuilder.append("&");
				stringBuilder.append("response_type");
				stringBuilder.append("=");
				stringBuilder.append(this.paramValue(PARAM_NAME_RESPONSE_TYPE, "code"));
				stringBuilder.append("&");
				stringBuilder.append("scope");
				stringBuilder.append("=");
				stringBuilder.append(URLEncoder.encode(this.paramValue(PARAM_NAME_SCOPE, ""), "utf-8"));
				stringBuilder.append("&");
				stringBuilder.append("state");
				stringBuilder.append("=");
				stringBuilder.append(this.getName());
				stringBuilder.append("-");
				stringBuilder.append(UUID.randomUUID().toString());
				stringBuilder.append("&");
				stringBuilder.append("redirect_uri");
				stringBuilder.append("=");
				stringBuilder.append(URLEncoder.encode(this.paramValue(PARAM_NAME_REDIRECT_URI,
						request.replace("/authorize", "/login?app=" + this.getName())), "utf-8"));
				return new OperationResult<P>().addResultObjects(stringBuilder.toString());
			}
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
		throw new ApplicationException("not implemented.");
	}

	@Override
	protected abstract IUserMapping fetchUser(Properties params) throws Exception;
}
