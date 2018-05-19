package org.colorcoding.ibas.thirdpartyapp.joint;

import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.bo.shell.User;
import org.colorcoding.ibas.initialfantasy.repository.BORepositoryInitialFantasy;
import org.colorcoding.ibas.initialfantasy.repository.IBORepositoryInitialFantasyApp;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.application.IApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.user.IUser;

/**
 * 连接管理员
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class ConnectManager {

	protected static final String MSG_CONNECTMANAGER_USING = "connectManager: application [%s] use [%s].";

	/**
	 * 配置项目模板-连接管理员
	 */
	public static final String CONFIG_ITEM_TEMPLATE_CONNECT_MANAGER = "ConnectManager|%s";

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

	private static Map<String, ConnectManager> managers = new HashMap<>();

	/**
	 * 创建管理员
	 * 
	 * @param application
	 *            应用名称
	 * @return
	 */
	public synchronized static ConnectManager create(IApplication application) {
		try {
			ConnectManager manager = managers.get(application.getCode());
			if (manager == null) {
				String managerName = MyConfiguration.getConfigValue(
						String.format(ConnectManager.CONFIG_ITEM_TEMPLATE_CONNECT_MANAGER, application.getCode()));
				if (managerName == null || managerName.isEmpty()) {
					throw new Exception(
							I18N.prop("msg_tpa_not_found_application_connect_manager", application.getCode()));
				}
				if (managerName.indexOf(".") < 0) {
					// 补充命名空间
					managerName = String.format("%s.%s", ConnectManager.class.getName().substring(0,
							ConnectManager.class.getName().lastIndexOf(".")), managerName);
				}
				manager = (ConnectManager) Class.forName(managerName).newInstance();
				managers.put(application.getCode(), manager);
			}
			Logger.log(MessageLevel.DEBUG, MSG_CONNECTMANAGER_USING, application.getCode(),
					manager.getClass().getName());
			return manager;
		} catch (Exception e) {
			Logger.log(e);
			return null;
		}
	}

	private volatile IBORepositoryInitialFantasyApp boRepository;

	public final User connect(Map<String, Object> params) throws Exception {
		IUser user = this.getUser(params);
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_CODE.getName());
		condition.setValue(user.getUser());
		condition = criteria.getConditions().create();
		condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_ACTIVATED.getName());
		condition.setValue(emYesNo.YES);
		if (boRepository == null) {
			synchronized (this) {
				if (boRepository == null) {
					boRepository = new BORepositoryInitialFantasy();
					boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
				}
			}
		}
		IOperationResult<org.colorcoding.ibas.initialfantasy.bo.organization.IUser> opRsltUser = boRepository
				.fetchUser(criteria);
		org.colorcoding.ibas.initialfantasy.bo.organization.IUser boUser = opRsltUser.getResultObjects()
				.firstOrDefault();
		if (boUser == null) {
			return null;
		}
		User orgUser = User.create(boUser);
		OrganizationFactory.create().createManager().register(orgUser);
		return orgUser;
	}

	protected abstract IUser getUser(Map<String, Object> params) throws Exception;
}
