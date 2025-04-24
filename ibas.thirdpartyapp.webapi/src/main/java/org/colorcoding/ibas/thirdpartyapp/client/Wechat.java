package org.colorcoding.ibas.thirdpartyapp.client;

import java.util.Properties;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.repository.BORepositoryInitialFantasy;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.UserMapping;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

public class Wechat extends WebApp {

	/**
	 * 参数名称-应用编码
	 */
	public static final String PARAM_NAME_APP_CODE = "AppCode";
	/**
	 * 参数名称-应用名称
	 */
	public static final String PARAM_NAME_APP_NAME = "AppName";
	/**
	 * 参数名称-应用接口地址
	 */
	public static final String PARAM_NAME_API_URL = "ApiUrl";
	/**
	 * 配置项目-用户编号系列
	 */
	public final static String CONFIG_ITEM_USER_SERIES = "UserSeries";
	/**
	 * 参数名称-授权码
	 */
	public static final String PARAM_NAME_CODE = "code";
	/**
	 * 地址模板-用户认证
	 */
	public static final String URL_TEMPLATE_OAUTH = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=${AppId}&secret=${AppSecret}&code=${Code}&grant_type=authorization_code";
	/**
	 * 地址模板-用户信息
	 */
	public static final String URL_TEMPLATE_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token=${AccessToken}&openid=${OpenId}&lang=zh_CN";

	@Override
	public IUserMapping fetchUser(Properties params) throws Exception {
		if (params.get(PARAM_NAME_CODE) == null) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_CODE));
		}
		String url = this.applyVariables(URL_TEMPLATE_OAUTH, params);
		JsonObject data = this.doGet(url);
		// 1. 检查空响应
		if (data == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		// 2. 检查错误消息字段
		if (data.containsKey("errmsg")) { // 直接判断字段是否存在
			JsonValue errValue = data.get("errmsg");
			if (errValue.getValueType() == JsonValue.ValueType.STRING) { // 确保类型是字符串
				throw new Exception(((JsonString) errValue).getString());
			} else {
				throw new Exception(I18N.prop("msg_bobas_data_type_not_support", errValue.getValueType()));
			}
		}
		IUserMapping user = null;
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
		condition.setValue(this.getName());
		condition = criteria.getConditions().create();
		try {
			// 使用unionid查询用户
			condition.setAlias(UserMapping.PROPERTY_ACCOUNT.getName());
			condition.setValue(this.paramValue("unionid", data));
			params.put("UnionId", condition.getValue());
			user = this.fetchUser(criteria);
		} catch (Exception e) {
			// 使用openid查询用户
		}
		if (user == null) {
			params.put("AccessToken", this.paramValue("access_token", data));
			user = this.createUser(params);
		}
		return user;
	}

	protected IUserMapping fetchUser(ICriteria criteria) throws Exception {
		try (BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			IOperationResult<IUserMapping> operationResult = boRepository.fetchUserMapping(criteria);
			if (operationResult.getError() != null) {
				throw operationResult.getError();
			}
			return operationResult.getResultObjects().firstOrDefault();
		}
	}

	protected IUserMapping createUser(Properties params) throws Exception {
		String url = this.applyVariables(URL_TEMPLATE_USER_INFO, params);
		JsonObject data = this.doGet(url);
		// 1. 检查空响应
		if (data == null) {
			throw new Exception(I18N.prop("msg_tpa_faild_user_info_request"));
		}
		// 2. 检查错误消息字段
		if (data.containsKey("errmsg")) { // 直接判断字段是否存在
			JsonValue errValue = data.get("errmsg");
			if (errValue.getValueType() == JsonValue.ValueType.STRING) { // 确保类型是字符串
				throw new Exception(((JsonString) errValue).getString());
			} else {
				throw new Exception(I18N.prop("msg_bobas_data_type_not_support", errValue.getValueType()));
			}
		}
		// 查询别的应用是否已映射用户
		IUserMapping existUser = null;
		try {
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
			condition.setOperation(ConditionOperation.NOT_EQUAL);
			condition.setValue(String.valueOf(params.get(PARAM_NAME_APP_CODE)));
			condition = criteria.getConditions().create();
			condition.setAlias(UserMapping.PROPERTY_ACCOUNT.getName());
			condition.setValue(this.paramValue("unionid", data));
			existUser = this.fetchUser(criteria);
		} catch (Exception e) {
		}
		String code;
		if (existUser == null) {
			// 创建系统用户
			org.colorcoding.ibas.initialfantasy.bo.organization.User userIF = new org.colorcoding.ibas.initialfantasy.bo.organization.User();
			userIF.setSeries(MyConfiguration.getConfigValue(CONFIG_ITEM_USER_SERIES, 1));// 编号系列
			userIF.setName(this.paramValue("nickname", data));
			userIF.setActivated(emYesNo.YES);
			try (BORepositoryInitialFantasy boRepositoryIF = new BORepositoryInitialFantasy()) {
				boRepositoryIF.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
				IOperationResult<org.colorcoding.ibas.initialfantasy.bo.organization.IUser> opRsltIF = boRepositoryIF
						.saveUser(userIF);
				if (opRsltIF.getError() != null) {
					throw opRsltIF.getError();
				}
				code = userIF.getCode();
			}
		} else {
			code = existUser.getUser();
		}
		// 创建应用用户
		IUserMapping userTA = new UserMapping();
		userTA.setUser(code);
		userTA.setApplication(String.valueOf(params.get(PARAM_NAME_APP_CODE)));
		try {
			userTA.setAccount(this.paramValue("openid", data));
		} catch (Exception e) {
			userTA.setAccount(this.paramValue("unionid", data));
		}
		try (BORepositoryThirdPartyApp boRepositoryTA = new BORepositoryThirdPartyApp()) {
			boRepositoryTA.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			IOperationResult<IUserMapping> opRsltTA = boRepositoryTA.saveUserMapping(userTA);
			if (opRsltTA.getError() != null) {
				throw opRsltTA.getError();
			}
			return opRsltTA.getResultObjects().firstOrDefault();
		}
	}

	@Override
	public <P> IOperationResult<P> execute(String instruct, Properties params) throws NotImplementedException {
		throw new NotImplementedException();
	}
}
