package org.colorcoding.ibas.thirdpartyapp.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.json.JsonObject;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.bo.organization.IUser;
import org.colorcoding.ibas.initialfantasy.bo.organization.User;
import org.colorcoding.ibas.initialfantasy.repository.BORepositoryInitialFantasy;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.UserMapping;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

public class ALY_IDaaS extends OIDC {

	@Override
	protected IUserMapping fetchUser(Properties params) throws Exception {
		if (!params.containsKey(PARAM_NAME_CODE)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_CODE));
		}
		String endpoint = this.paramValue(PARAM_NAME_TOKEN_ENDPOINT, "");
		if (Strings.isNullOrEmpty(endpoint)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_TOKEN_ENDPOINT));
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(endpoint);
		stringBuilder.append("?");
		stringBuilder.append("grant_type");
		stringBuilder.append("=");
		stringBuilder.append("authorization_code");
		stringBuilder.append("&");
		stringBuilder.append(PARAM_NAME_CODE);
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue(PARAM_NAME_CODE, "", params));
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
		JsonObject result = this.doPost(stringBuilder.toString(), headers, null);
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		if (result.containsKey("error")) {
			throw new Exception(this.paramValue("error", result));
		}
		String accessToken = result.getString("access_token");
		if (accessToken == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		stringBuilder = new StringBuilder();
		endpoint = this.paramValue(PARAM_NAME_USERINFO_ENDPOINT, "");
		if (Strings.isNullOrEmpty(endpoint)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_USERINFO_ENDPOINT));
		}
		stringBuilder.append(endpoint);
		headers = new HashMap<>();
		headers.put("Authorization", String.format("Bearer %s", accessToken.toString()));
		result = this.doGet(stringBuilder.toString(), headers);
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		String userName = this.paramValue("preferred_username", result);
		if (Strings.isNullOrEmpty(userName)) {
			throw new Exception(I18N.prop("msg_tpa_faild_user_info_request"));
		}
		Criteria criteria = new Criteria();
		criteria.setResultCount(1);
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
		condition.setValue(this.getName());
		condition = criteria.getConditions().create();
		condition.setAlias(UserMapping.PROPERTY_ACCOUNT.getName());
		condition.setValue(userName);

		try (BORepositoryThirdPartyApp boRepository3RD = new BORepositoryThirdPartyApp()) {
			boRepository3RD.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			IOperationResult<IUserMapping> opRsltMap = boRepository3RD.fetchUserMapping(criteria);
			if (opRsltMap.getError() != null) {
				throw opRsltMap.getError();
			}
			// 没有应用用户映射，则按编码直查用户
			if (opRsltMap.getResultObjects().isEmpty()) {
				criteria = new Criteria();
				criteria.setResultCount(1);
				condition = criteria.getConditions().create();
				condition.setAlias(User.PROPERTY_CODE.getName());
				condition.setValue(userName);
				try (BORepositoryInitialFantasy boRepositoryIF = new BORepositoryInitialFantasy()) {
					boRepositoryIF.setTransaction(boRepository3RD.getTransaction());
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
			}
			return opRsltMap.getResultObjects().firstOrDefault();
		}
	}

}
