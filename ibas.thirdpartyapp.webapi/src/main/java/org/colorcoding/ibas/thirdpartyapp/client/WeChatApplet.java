package org.colorcoding.ibas.thirdpartyapp.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.json.JsonObject;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.UserMapping;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

public class WeChatApplet extends WeChat {

	@Override
	public IUserMapping fetchUser(Properties params) throws Exception {
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
		stringBuilder.append("js_code");
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue(PARAM_NAME_CODE, "", params));
		stringBuilder.append("&");
		stringBuilder.append("appid");
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue(PARAM_NAME_CLIENT_ID, ""));
		stringBuilder.append("&");
		stringBuilder.append("secret");
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue(PARAM_NAME_CLIENT_SECRET, ""));

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		JsonObject result = this.doGet(stringBuilder.toString(), headers);
		if (result == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_oauth_request"));
		}
		// 是否有错误
		if (result.containsKey("errmsg")) {
			throw new Exception(this.paramValue("errmsg", result));
		}
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
		condition.setValue(this.getName());
		int count = criteria.getConditions().size();
		// 尝试使用统一用户编码
		if (result.containsKey("unionid")) {
			params.put("unionid", this.paramValue("unionid", result));
			condition = criteria.getConditions().create();
			condition.setAlias(UserMapping.PROPERTY_ACCOUNT.getName());
			condition.setOperation(ConditionOperation.CONTAIN);
			condition.setValue(Strings.format("UnionId: %s;", params.get("unionid")));
		}
		// 尝试使用应用用户编码
		if (result.containsKey("openid")) {
			params.put("openid", this.paramValue("openid", result));
			condition = criteria.getConditions().create();
			condition.setAlias(UserMapping.PROPERTY_ACCOUNT.getName());
			condition.setOperation(ConditionOperation.CONTAIN);
			condition.setValue(Strings.format("OpenId: %s;", params.get("openid")));
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
		try (BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			IOperationResult<IUserMapping> operationResult = boRepository.fetchUserMapping(criteria);
			if (operationResult.getError() != null) {
				throw operationResult.getError();
			}
			IUserMapping user = operationResult.getResultObjects().firstOrDefault();
			if (user == null) {
				user = this.createUser(params, result);
			}
			return user;
		}
	}

	@Override
	public <P> IOperationResult<P> execute(String instruct, Properties params) throws ApplicationException {
		throw new ApplicationException("not implemented.");
	}
}
