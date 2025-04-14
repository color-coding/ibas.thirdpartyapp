package org.colorcoding.ibas.thirdpartyapp.client;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.bo.organization.IUser;
import org.colorcoding.ibas.initialfantasy.bo.organization.User;
import org.colorcoding.ibas.initialfantasy.repository.BORepositoryInitialFantasy;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.UserMapping;
import org.colorcoding.ibas.thirdpartyapp.data.DataConvert;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

import com.fasterxml.jackson.databind.JsonNode;

public class IDaaS extends WebApp {
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
	public <P> IOperationResult<P> execute(String instruct, Properties params) throws NotImplementedException {
		try {
			if ("authorize".equalsIgnoreCase(instruct)) {
				String endpoint = this.paramValue(PARAM_NAME_AUTHORIZE_ENDPOINT, "");
				if (DataConvert.isNullOrEmpty(endpoint)) {
					throw new Exception(
							I18N.prop("msg_tpa_invaild_application_setting_item", PARAM_NAME_AUTHORIZE_ENDPOINT));
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
				stringBuilder.append(this.paramValue(PARAM_NAME_RESPONSE_TYPE, ""));
				stringBuilder.append("&");
				stringBuilder.append("scope");
				stringBuilder.append("=");
				stringBuilder.append(URLEncoder.encode(this.paramValue(PARAM_NAME_SCOPE, ""), "utf8"));
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
						request.replace("/authorize", "/login?app=" + this.getName())), "utf8"));
				return new OperationResult<P>().addResultObjects(stringBuilder.toString());
			}
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
		throw new NotImplementedException();
	}

	@Override
	protected IUserMapping fetchUser(Properties params) throws Exception {
		String endpoint = this.paramValue(PARAM_NAME_TOKEN_ENDPOINT, "");
		if (DataConvert.isNullOrEmpty(endpoint)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_TOKEN_ENDPOINT));
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(endpoint);
		stringBuilder.append("?");
		stringBuilder.append("grant_type");
		stringBuilder.append("=");
		stringBuilder.append("authorization_code");
		stringBuilder.append("&");
		stringBuilder.append("code");
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue("code", "", params));
		stringBuilder.append("&");
		stringBuilder.append(PARAM_NAME_CLIENT_ID);
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue(PARAM_NAME_CLIENT_ID, ""));
		stringBuilder.append("&");
		stringBuilder.append(PARAM_NAME_CLIENT_SECRET);
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue(PARAM_NAME_CLIENT_SECRET, ""));
		stringBuilder.append("&");
		stringBuilder.append(PARAM_NAME_REDIRECT_URI);
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue(PARAM_NAME_REDIRECT_URI, ""));

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		JsonNode result = this.doPost(stringBuilder.toString(), headers);
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		JsonNode idToken = result.get("id_token");
		if (idToken == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		JsonNode accessToken = result.get("access_token");
		if (accessToken == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		stringBuilder = new StringBuilder();
		endpoint = this.paramValue(PARAM_NAME_USERINFO_ENDPOINT, "");
		if (DataConvert.isNullOrEmpty(endpoint)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_USERINFO_ENDPOINT));
		}
		stringBuilder.append(endpoint);
		headers = new HashMap<>();
		headers.put("Authorization", String.format("Bearer %s", accessToken.asText()));
		result = this.doGet(stringBuilder.toString(), headers);
		JsonNode userName = result.get("preferred_username");
		if (userName == null || DataConvert.isNullOrEmpty(userName.asText())) {
			throw new Exception(I18N.prop("msg_tpa_faild_user_info_request"));
		}
		Criteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
		condition.setValue(this.getName());
		condition = criteria.getConditions().create();
		condition.setAlias(UserMapping.PROPERTY_ACCOUNT.getName());
		condition.setValue(userName.asText());

		BORepositoryThirdPartyApp boRepository3RD = new BORepositoryThirdPartyApp();
		boRepository3RD.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
		IOperationResult<IUserMapping> opRsltMap = boRepository3RD.fetchUserMapping(criteria);
		if (opRsltMap.getError() != null) {
			throw opRsltMap.getError();
		}
		// 没有应用用户映射，则按编码直查用户
		if (opRsltMap.getResultObjects().isEmpty()) {
			criteria = new Criteria();
			condition = criteria.getConditions().create();
			condition.setAlias(User.PROPERTY_CODE.getName());
			condition.setValue(userName.asText());
			BORepositoryInitialFantasy boRepositoryIF = new BORepositoryInitialFantasy();
			boRepositoryIF.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			IOperationResult<IUser> opRsltUsr = boRepositoryIF.fetchUser(criteria);
			if (opRsltUsr.getError() != null) {
				throw opRsltUsr.getError();
			}
			IUser user = opRsltUsr.getResultObjects().firstOrDefault();
			if (user == null) {
				throw new Exception(I18N.prop("msg_tpa_no_matching_user"));
			}
			IUserMapping userMapping = new UserMapping();
			userMapping.setApplication(this.getName());
			userMapping.setUser(user.getCode());
			userMapping.setAccount(user.getCode());
			opRsltMap.getResultObjects().add(userMapping);
		}
		return opRsltMap.getResultObjects().firstOrDefault();
	}
}
