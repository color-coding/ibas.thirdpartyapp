package org.colorcoding.ibas.thirdpartyapp.client;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.application.IApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSetting;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

public class ApplicationClientManager {

	/**
	 * 配置项目模板-应用客户端
	 */
	public static final String CONFIG_ITEM_TEMPLATE_APPLICATION_CLINET = "ApplicationClient|%s";

	private static ApplicationClientManager instance;

	public final static ApplicationClientManager newInstance() {
		if (instance == null) {
			synchronized (ApplicationClientManager.class) {
				if (instance == null) {
					instance = new ApplicationClientManager();
				}
			}
		}
		return instance;
	}

	private ApplicationClientManager() {
	}

	public ApplicationClient create(String appCode) throws RuntimeException {
		try {
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(Application.PROPERTY_CODE.getName());
			condition.setValue(appCode);
			condition = criteria.getConditions().create();
			condition.setAlias(Application.PROPERTY_ACTIVATED.getName());
			condition.setValue(emYesNo.YES);
			BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			IOperationResult<IApplication> opRslt = boRepository.fetchApplication(criteria);
			if (opRslt.getError() != null) {
				throw opRslt.getError();
			}
			return this.create(opRslt.getResultObjects().firstOrDefault());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ApplicationClient create(IApplication application) throws RuntimeException {
		try {
			BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			ApplicationSetting appSetting = boRepository.createApplicationSetting(application);
			String managerName = MyConfiguration
					.getConfigValue(String.format(CONFIG_ITEM_TEMPLATE_APPLICATION_CLINET, appSetting.getGroup()));
			if (managerName == null || managerName.isEmpty()) {
				throw new Exception(I18N.prop("msg_tpa_not_found_application_client", application.getName()));
			}
			if (managerName.indexOf(".") < 0) {
				// 补充命名空间
				managerName = String.format("%s.%s", ApplicationClientManager.class.getName().substring(0,
						ApplicationClientManager.class.getName().lastIndexOf(".")), managerName);
			}
			ApplicationClient client = (ApplicationClient) Class.forName(managerName).newInstance();
			client.setSetting(appSetting);
			return client;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
