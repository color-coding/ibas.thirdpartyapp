package org.colorcoding.ibas.thirdpartyapp.repository;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.repository.BORepositoryServiceApplication;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.application.IApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.applicationconfig.ApplicationConfig;
import org.colorcoding.ibas.thirdpartyapp.bo.applicationconfig.IApplicationConfig;
import org.colorcoding.ibas.thirdpartyapp.bo.applicationconfig.IApplicationConfigItem;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSetting;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSettingItem;
import org.colorcoding.ibas.thirdpartyapp.bo.other.UserApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.UserMapping;

/**
 * ThirdPartyApp仓库
 */
public class BORepositoryThirdPartyApp extends BORepositoryServiceApplication
		implements IBORepositoryThirdPartyAppSvc, IBORepositoryThirdPartyAppApp {

	// --------------------------------------------------------------------------------------------
	/**
	 * 创建应用配置
	 * 
	 * @param application 应用
	 * @param noUsers     不包含用户配置
	 * @return
	 * @throws Exception
	 */
	public ApplicationSetting createApplicationSetting(IApplication application, boolean noUsers) throws Exception {
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(ApplicationConfig.PROPERTY_CODE.getName());
		condition.setValue(application.getConfig());
		IOperationResult<IApplicationConfig> opRsltConfig = this.fetchApplicationConfig(criteria);
		IApplicationConfig applicationConfig = opRsltConfig.getResultObjects().firstOrDefault();
		if (applicationConfig == null) {
			throw new Exception(I18N.prop("msg_tpa_invaild_application_config", application.getConfig()));
		}
		ApplicationSetting appSetting = new ApplicationSetting();
		appSetting.setName(application.getCode());
		appSetting.setGroup(application.getConfig());
		appSetting.setDescription(application.getName());
		appSetting.setSecretKey(MyConfiguration.getConfigValue(
				org.colorcoding.ibas.initialfantasy.MyConfiguration.CONFIG_ITEM_USER_TOKEN_KEY,
				applicationConfig.getCreateActionId()));
		for (IApplicationConfigItem configItem : applicationConfig.getApplicationConfigItems()) {
			if (noUsers && configItem.getForUser() == emYesNo.YES) {
				continue;
			}
			ApplicationSettingItem appSettingItem = appSetting.getSettingItems().create();
			appSettingItem.setName(configItem.getName());
			appSettingItem.setDescription(configItem.getDescription());
			appSettingItem.setCategory(configItem.getCategory());
		}
		if (!Strings.isNullOrEmpty(application.getSettings())) {
			appSetting.getSettingItems().decode(application.getSettings());
		}
		return appSetting;
	}

	/**
	 * 创建应用配置（不含用户的）
	 * 
	 * @param application 应用
	 * @return
	 * @throws Exception
	 */
	public ApplicationSetting createApplicationSetting(IApplication application) throws Exception {
		return this.createApplicationSetting(application, true);
	}

	/**
	 * 创建用户应用配置（全部）
	 * 
	 * @param userMapping
	 * @return
	 * @throws Exception
	 */
	public ApplicationSetting createApplicationSetting(IUserMapping userMapping) throws Exception {
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(Application.PROPERTY_CODE.getName());
		condition.setValue(userMapping.getApplication());
		IOperationResult<IApplication> opRsltApp = this.fetchApplication(criteria);
		IApplication application = opRsltApp.getResultObjects().firstOrDefault();
		if (application == null) {
			throw new Exception(I18N.prop("msg_tpa_invaild_application", userMapping.getApplication()));
		}
		ApplicationSetting appSetting = this.createApplicationSetting(application, false);
		if (!Strings.isNullOrEmpty(userMapping.getSettings())) {
			appSetting.getSettingItems().decode(userMapping.getSettings());
		}
		return appSetting;
	}

	public IOperationResult<UserApplication> fetchUserApplications(String user) {
		return this.fetchUserApplications(user, this.getUserToken());
	}

	public OperationResult<UserApplication> fetchUserApplications(String user, String token) {
		OperationResult<UserApplication> opRslt = new OperationResult<UserApplication>();
		try {
			this.setUserToken(token);
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(UserMapping.PROPERTY_USER.getName());
			condition.setValue(user);
			IOperationResult<IUserMapping> opRsltUser = this.fetchUserMapping(criteria);
			if (opRsltUser.getError() != null) {
				throw opRsltUser.getError();
			}
			for (IUserMapping appUser : opRsltUser.getResultObjects()) {
				if (opRslt.getResultObjects()
						.firstOrDefault(c -> c.getCode().equals(appUser.getApplication())) != null) {
					continue;
				}
				criteria = new Criteria();
				condition = criteria.getConditions().create();
				condition.setAlias(Application.PROPERTY_CODE.getName());
				condition.setValue(appUser.getApplication());
				condition = criteria.getConditions().create();
				condition.setAlias(Application.PROPERTY_ACTIVATED.getName());
				condition.setValue(emYesNo.YES);
				IOperationResult<IApplication> opRsltApp = this.fetchApplication(criteria);
				IApplication application = opRsltApp.getResultObjects().firstOrDefault();
				if (application == null) {
					continue;
				}
				ApplicationSetting appSetting = this.createApplicationSetting(appUser);
				if (appSetting == null) {
					continue;
				}
				String appUrl = appSetting.paramValue(UserApplication.PARAM_NAME_APP_URL);
				if (appUrl == null || appUrl.isEmpty()) {
					continue;
				}
				UserApplication userApplication = new UserApplication();
				userApplication.setCode(application.getCode());
				userApplication.setName(application.getName());
				userApplication.setUrl(appUrl);
				opRslt.addResultObjects(userApplication);
			}
		} catch (Exception e) {
			opRslt.setError(e);
		}
		return opRslt;
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-应用
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	public OperationResult<Application> fetchApplication(ICriteria criteria, String token) {
		return super.fetch(Application.class, criteria, token);
	}

	/**
	 * 查询-应用（提前设置用户口令）
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	public IOperationResult<IApplication> fetchApplication(ICriteria criteria) {
		return new OperationResult<IApplication>(this.fetchApplication(criteria, this.getUserToken()));
	}

	/**
	 * 保存-应用
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	public OperationResult<Application> saveApplication(Application bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-应用（提前设置用户口令）
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	public IOperationResult<IApplication> saveApplication(IApplication bo) {
		return new OperationResult<IApplication>(this.saveApplication((Application) bo, this.getUserToken()));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-应用配置
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	public OperationResult<ApplicationConfig> fetchApplicationConfig(ICriteria criteria, String token) {
		return super.fetch(ApplicationConfig.class, criteria, token);
	}

	/**
	 * 查询-应用配置（提前设置用户口令）
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	public IOperationResult<IApplicationConfig> fetchApplicationConfig(ICriteria criteria) {
		return new OperationResult<IApplicationConfig>(this.fetchApplicationConfig(criteria, this.getUserToken()));
	}

	/**
	 * 保存-应用配置
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	public OperationResult<ApplicationConfig> saveApplicationConfig(ApplicationConfig bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-应用配置（提前设置用户口令）
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	public IOperationResult<IApplicationConfig> saveApplicationConfig(IApplicationConfig bo) {
		return new OperationResult<IApplicationConfig>(
				this.saveApplicationConfig((ApplicationConfig) bo, this.getUserToken()));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户映射
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	public OperationResult<UserMapping> fetchUserMapping(ICriteria criteria, String token) {
		return super.fetch(UserMapping.class, criteria, token);
	}

	/**
	 * 查询-用户映射（提前设置用户口令）
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	public IOperationResult<IUserMapping> fetchUserMapping(ICriteria criteria) {
		return new OperationResult<IUserMapping>(this.fetchUserMapping(criteria, this.getUserToken()));
	}

	/**
	 * 保存-用户映射
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	public OperationResult<UserMapping> saveUserMapping(UserMapping bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-用户映射（提前设置用户口令）
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	public IOperationResult<IUserMapping> saveUserMapping(IUserMapping bo) {
		return new OperationResult<IUserMapping>(this.saveUserMapping((UserMapping) bo, this.getUserToken()));
	}

	// --------------------------------------------------------------------------------------------//

}
