package org.colorcoding.ibas.thirdpartyapp.repository;

import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.bo.shell.User;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.application.IApplication;
import org.colorcoding.ibas.thirdpartyapp.joint.ConnectManager;

/**
 * 联合应用
 * 
 * @author Niuren.Zhu
 *
 */
public class BORepositoryJointApplication implements IBORepositoryJointApplication {

	private volatile IBORepositoryThirdPartyAppApp boRepository;

	@Override
	public OperationResult<User> jointConnect(String app, Map<String, Object> params) {
		try {
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(Application.PROPERTY_CODE.getName());
			condition.setValue(app);
			condition = criteria.getConditions().create();
			condition.setAlias(Application.PROPERTY_ACTIVATED.getName());
			condition.setValue(emYesNo.YES);

			if (boRepository == null) {
				synchronized (this) {
					if (boRepository == null) {
						boRepository = new BORepositoryThirdPartyApp();
						boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
					}
				}
			}
			IOperationResult<IApplication> opRsltApp = boRepository.fetchApplication(criteria);
			IApplication application = opRsltApp.getResultObjects().firstOrDefault();
			if (application == null) {
				throw new Exception(I18N.prop("msg_tpa_invaild_application", app));
			}
			ConnectManager manager = ConnectManager.create(application);
			if (manager == null) {
				throw new ClassNotFoundException(I18N.prop("msg_tpa_not_found_application_connect_manager",
						application.getName() != null ? application.getName() : application.getCode()));
			}
			if (params == null) {
				params = new HashMap<>();
			}
			// 补充运行参数
			for (IFieldData field : ((IManagedFields) application).getFields()) {
				if (!field.isSavable()) {
					continue;
				}
				if (field.getName().equals(Application.PROPERTY_CODE.getName())
						|| field.getName().equals(Application.PROPERTY_NAME.getName())) {
					params.put(String.format("App%s", field.getName()), field.getValue());
				} else {
					params.put(field.getName(), field.getValue());
				}
			}
			User user = manager.connect(params);
			if (user == null) {
				throw new Exception(I18N.prop("msg_tpa_no_matching_user"));
			}
			OperationResult<User> operationResult = new OperationResult<>();
			operationResult.setUserSign(user.getToken());
			operationResult.addResultObjects(user);
			return operationResult;
		} catch (Exception e) {
			Logger.log(e);
			return new OperationResult<>(e);
		}
	}

}
