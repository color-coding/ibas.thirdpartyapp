package org.colorcoding.ibas.thirdpartyapp.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.json.JsonObject;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.emYesNo;
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

public class WeChat extends OIDC {
	/**
	 * 用户模板-编码
	 */
	public final static String USER_TEMPLATE_CODE = "#_3RD_WC";

	@Override
	protected String getAuthorizeClientIdParameterName() {
		return "appid";
	}

	@Override
	protected String getAuthorizeUrlSuffix() {
		return "#wechat_redirect";
	}

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
		stringBuilder.append(PARAM_NAME_CODE);
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue(PARAM_NAME_CODE, "", params));
		stringBuilder.append("&");
		stringBuilder.append("appid");
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue(PARAM_NAME_CLIENT_ID, ""));
		stringBuilder.append("&");
		stringBuilder.append(PARAM_NAME_CLIENT_SECRET);
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
				params.put("access_token", this.paramValue("access_token", result));
				user = this.createUser(params);
			}
			return user;
		}
	}

	protected IUserMapping createUser(Properties params) throws Exception {
		String endpoint = this.paramValue(PARAM_NAME_USERINFO_ENDPOINT, "");
		if (Strings.isNullOrEmpty(endpoint)) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_USERINFO_ENDPOINT));
		}
		if (!params.containsKey("openid")) {
			throw new Exception(I18N.prop("msg_tpa_no_param", "openid"));
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(endpoint);
		stringBuilder.append("?");
		stringBuilder.append("access_token");
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue("access_token", "", params));
		stringBuilder.append("&");
		stringBuilder.append("openid");
		stringBuilder.append("=");
		stringBuilder.append(this.paramValue("openid", "", params));
		stringBuilder.append("&");
		stringBuilder.append("lang");
		stringBuilder.append("=");
		stringBuilder.append("zh_CN");
		JsonObject data = this.doGet(stringBuilder.toString());
		if (data == null) {
			throw new Exception(I18N.prop("msg_tpa_failed_user_info_request"));
		}
		// 是否有错误
		if (data.containsKey("errmsg")) {
			throw new Exception(this.paramValue("errmsg", data));
		}
		return this.createUser(params, data);
	}

	protected IUserMapping createUser(Properties params, JsonObject data) throws Exception {
		// 创建系统用户
		try (BORepositoryInitialFantasy boRepositoryIF = new BORepositoryInitialFantasy()) {
			boRepositoryIF.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());

			Criteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_CODE);
			condition.setValue(USER_TEMPLATE_CODE);
			IOperationResult<IUser> opRsltIF = boRepositoryIF.fetchUser(criteria);
			if (opRsltIF.getError() != null) {
				throw opRsltIF.getError();
			}
			IUser user = opRsltIF.getResultObjects().firstOrDefault();
			if (user != null) {
				user = BOUtilities.clone(user);
			} else {
				user = new User();
			}
			user.setActivated(emYesNo.YES);
			if (data.containsKey("nickname")) {
				user.setName(this.paramValue("nickname", data));
			} else {
				user.setName(UUID.randomUUID().toString());
			}
			user.setPassword(UUID.randomUUID().toString());
			if (!(user.getSeries() > 0)) {
				// 无自动编码，则根据最大编号生成
				criteria = new Criteria();
				criteria.setResultCount(1);
				condition = criteria.getConditions().create();
				condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_DOCENTRY);
				condition.setOperation(ConditionOperation.GREATER_THAN);
				condition.setValue("0");
				ISort sort = criteria.getSorts().create();
				sort.setAlias(condition.getAlias());
				sort.setSortType(SortType.DESCENDING);
				int index = Long.signum((DateTimes.now().getTime() - DateTimes.VALUE_MIN.getTime()) / 1000);
				for (IUser item : boRepositoryIF.fetchUser(criteria).getResultObjects()) {
					index = item.getDocEntry() + 1;
				}
				user.setCode(Integer.toHexString(index));
				if (user.getCode().length() > 7) {
					user.setCode(user.getCode().substring(user.getCode().length() - 7));
				}
			}

			boRepositoryIF.beginTransaction();
			opRsltIF = boRepositoryIF.saveUser(user);
			if (opRsltIF.getError() != null) {
				throw opRsltIF.getError();
			}
			if (opRsltIF.getResultObjects().isEmpty()) {
				throw new Exception(I18N.prop("msg_tpa_no_matching_user"));
			}
			user = opRsltIF.getResultObjects().firstOrDefault();
			Logger.log(MessageLevel.WARN, "%s: create user [%s], password [%s].", this.getName(), user.getCode(),
					this.paramValue("code", "", params));
			// 组织注册用户，使之生效
			OrganizationFactory.createManager()
					.register(org.colorcoding.ibas.initialfantasy.bo.shell.User.create(user));
			// 维护应用用户映射
			try (BORepositoryThirdPartyApp boRepository3rd = new BORepositoryThirdPartyApp()) {
				boRepository3rd.setTransaction(boRepositoryIF.getTransaction());
				criteria = new Criteria();
				condition = criteria.getConditions().create();
				condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
				condition.setValue(this.getName());
				int count = criteria.getConditions().size();
				if (params.containsKey("unionid")) {
					condition = criteria.getConditions().create();
					condition.setAlias(UserMapping.PROPERTY_ACCOUNT.getName());
					condition.setOperation(ConditionOperation.CONTAIN);
					condition.setValue(Strings.format("UnionId: %s;", params.get("unionid")));
				}
				if (params.containsKey("openid")) {
					condition = criteria.getConditions().create();
					condition.setAlias(UserMapping.PROPERTY_ACCOUNT.getName());
					condition.setOperation(ConditionOperation.CONTAIN);
					condition.setValue(Strings.format("OpenId: %s;", params.get("openid")));
				}
				if (criteria.getConditions().size() > count + 1) {
					condition = criteria.getConditions().get(count);
					condition.setBracketOpen(1);
					condition = criteria.getConditions().get(criteria.getConditions().size() - 1);
					condition.setRelationship(ConditionRelationship.OR);
					condition.setBracketClose(1);
				}
				// 删除原有用户映射
				IOperationResult<IUserMapping> opRslt3rd;
				for (IUserMapping item : boRepository3rd.fetchUserMapping(criteria).getResultObjects()) {
					item.delete();
					opRslt3rd = boRepository3rd.saveUserMapping(item);
					if (opRslt3rd.getError() != null) {
						throw opRslt3rd.getError();
					}
				}
				// 创建用户映射
				IUserMapping userMapping = new UserMapping();
				userMapping.setApplication(this.getName());
				userMapping.setUser(user.getCode());
				// 尝试使用统一用户编码
				if (params.containsKey("unionid")) {
					userMapping.setAccount(Strings.concat(userMapping.getAccount(),
							Strings.format("UnionId: %s;", params.get("unionid"))));
				}
				// 尝试使用应用用户编码
				if (params.containsKey("openid")) {
					userMapping.setAccount(Strings.concat(userMapping.getAccount(),
							Strings.format("OpenId: %s;", params.get("openid"))));
				}
				opRslt3rd = boRepository3rd.saveUserMapping(userMapping);
				if (opRslt3rd.getError() != null) {
					throw opRslt3rd.getError();
				}
				boRepositoryIF.commitTransaction();
				return opRslt3rd.getResultObjects().firstOrDefault();
			} catch (Exception e) {
				boRepositoryIF.rollbackTransaction();
				throw e;
			}
		}
	}
}
