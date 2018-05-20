package org.colorcoding.ibas.thirdpartyapp.joint;

import java.util.Map;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.thirdpartyapp.bo.user.IUser;
import org.colorcoding.ibas.thirdpartyapp.bo.user.User;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

public class ConnectManagerDefault extends ConnectManager {

	/**
	 * 参数名称-用户
	 */
	public static final String PARAM_NAME_USER = "user";

	@Override
	protected IUser getUser(Map<String, Object> params) throws Exception {
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_MAPPEDUSER.getName());
		condition.setValue(String.valueOf(params.get(PARAM_NAME_USER)));
		condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_ACTIVATED.getName());
		condition.setValue(emYesNo.YES);
		condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_APPLICATION.getName());
		condition.setValue(String.valueOf(params.get(PARAM_NAME_APP_CODE)));
		BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
		boRepository.setRepository(this.getRepository());
		boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
		IOperationResult<IUser> operationResult = boRepository.fetchUser(criteria);
		return operationResult.getResultObjects().firstOrDefault();
	}

}
