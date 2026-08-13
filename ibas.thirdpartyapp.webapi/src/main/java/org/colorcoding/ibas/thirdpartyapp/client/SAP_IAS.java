package org.colorcoding.ibas.thirdpartyapp.client;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.json.JsonObject;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.bo.organization.IUser;
import org.colorcoding.ibas.initialfantasy.bo.organization.User;
import org.colorcoding.ibas.initialfantasy.repository.BORepositoryInitialFantasy;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.UserMapping;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

@ApplicationProvider("SAP_IAS")
public class SAP_IAS extends OIDC {
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
		stringBuilder.append(URLEncoder.encode(this.paramValue(PARAM_NAME_CLIENT_SECRET, ""), "utf-8"));
		stringBuilder.append("&");
		stringBuilder.append(PARAM_NAME_SCOPE);
		stringBuilder.append("=");
		stringBuilder.append(URLEncoder.encode(this.paramValue(PARAM_NAME_SCOPE, ""), "utf-8"));
		stringBuilder.append("&");
		stringBuilder.append(PARAM_NAME_REDIRECT_URI);
		stringBuilder.append("=");
		stringBuilder.append(URLEncoder.encode(this.paramValue(PARAM_NAME_REDIRECT_URI, ""), "utf-8"));

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		JsonObject result = this.doPost(endpoint, headers, stringBuilder.toString().getBytes("utf-8"));
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
		}
		if (result.containsKey("error_description")) {
			throw new Exception(this.paramValue("error_description", result));
		}
		String accessToken = result.getString("access_token");
		if (accessToken == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
		}
		stringBuilder = new StringBuilder();
		endpoint = this.paramValue(PARAM_NAME_USERINFO_ENDPOINT, "");
		if (Strings.isNullOrEmpty(endpoint)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_USERINFO_ENDPOINT));
		}
		stringBuilder.append(endpoint);
		headers = new HashMap<>();
		headers.put("Authorization", String.format("Bearer %s", accessToken));
		result = this.doGet(stringBuilder.toString(), headers);
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
		}
		ICriteria criteria = new Criteria();
		criteria.setResultCount(1);
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
		condition.setValue(this.getName());
		int count = criteria.getConditions().size();
		// 尝试使用统一用户编码
		if (result.containsKey("user_uuid")) {
			condition = criteria.getConditions().create();
			condition.setAlias(UserMapping.PROPERTY_ACCOUNT.getName());
			condition.setOperation(ConditionOperation.CONTAIN);
			condition.setValue(Strings.format("UnionId: %s;", this.paramValue("user_uuid", result)));
		}
		// 尝试使用应用用户编码
		if (result.containsKey("sub")) {
			condition = criteria.getConditions().create();
			condition.setAlias(UserMapping.PROPERTY_ACCOUNT.getName());
			condition.setOperation(ConditionOperation.CONTAIN);
			condition.setValue(Strings.format("Sub: %s;", this.paramValue("sub", result)));
		}
		if (criteria.getConditions().size() == count) {
			// 未能获取有效用户信息
			throw new Exception(I18N.prop("msg_tpa_failed_user_info_request"));
		}
		if (criteria.getConditions().size() > count + 1) {
			condition = criteria.getConditions().get(count);
			condition.setBracketOpen(1);
			condition = criteria.getConditions().get(criteria.getConditions().size() - 1);
			condition.setRelationship(ConditionRelationship.OR);
			condition.setBracketClose(1);
		}
		try (BORepositoryThirdPartyApp boRepository3RD = new BORepositoryThirdPartyApp()) {
			boRepository3RD.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			IOperationResult<IUserMapping> opRsltMap = boRepository3RD.fetchUserMapping(criteria);
			if (opRsltMap.getError() != null) {
				throw opRsltMap.getError();
			}
			// 没有应用用户映射，则按编码直查用户
			if (opRsltMap.getResultObjects().isEmpty()) {
				String unionId = result.containsKey("user_uuid") ? this.paramValue("user_uuid", result) : "";
				String subject = result.containsKey("sub") ? this.paramValue("sub", result) : "";
				String userCode = Strings.isNullOrEmpty(subject) ? unionId : subject;
				criteria = new Criteria();
				criteria.setResultCount(1);
				condition = criteria.getConditions().create();
				condition.setAlias(User.PROPERTY_CODE.getName());
				condition.setValue(userCode);
				try (BORepositoryInitialFantasy boRepositoryIF = new BORepositoryInitialFantasy()) {
					boRepositoryIF.setTransaction(boRepository3RD.getTransaction());
					IOperationResult<IUser> opRsltUsr = boRepositoryIF.fetchUser(criteria);
					if (opRsltUsr.getError() != null) {
						throw opRsltUsr.getError();
					}
					IUser user = opRsltUsr.getResultObjects().firstOrDefault();
					if (user == null) {
						// 自动创建用户
						user = new User();
						user.setDataSource("SAP_ISS");
						// 统一编码
						if (!Strings.isNullOrEmpty(unionId)) {
							user.setCreateActionId(unionId);
						}
						// 系统编码
						user.setCode(userCode);
						user.setName(Strings.concat(this.paramValue("last_name", result),
								this.paramValue("first_name", result)));
						user.setMail(this.paramValue("mail", result));
						// 访问码做为初始密码
						user.setPassword(this.paramValue("code", "", params));
						opRsltUsr = boRepositoryIF.saveUser(user);
						if (opRsltUsr.getError() != null) {
							throw opRsltUsr.getError();
						}
						user = opRsltUsr.getResultObjects().firstOrDefault();
						Logger.log(MessageLevel.WARN, "%s: create user [%s], password [%s].", this.getName(),
								user.getCode(), this.paramValue("code", "", params));
						// 组织注册用户，使之生效
						OrganizationFactory.createManager()
								.register(org.colorcoding.ibas.initialfantasy.bo.shell.User.create(user));
					}
					IUserMapping userMapping = new UserMapping();
					userMapping.setApplication(this.getName());
					userMapping.setUser(user.getCode());
					if (!Strings.isNullOrEmpty(unionId)) {
						userMapping.setAccount(Strings.concat(userMapping.getAccount(),
								Strings.format("UnionId: %s;", unionId)));
					}
					if (!Strings.isNullOrEmpty(subject)) {
						userMapping.setAccount(Strings.concat(userMapping.getAccount(),
								Strings.format("Sub: %s;", subject)));
					}
					opRsltMap.getResultObjects().add(userMapping);
				}
			}
			return opRsltMap.getResultObjects().firstOrDefault();
		}
	}
}
