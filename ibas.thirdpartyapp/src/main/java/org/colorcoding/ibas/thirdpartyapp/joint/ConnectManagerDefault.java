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

	public static final String PARAM_USER = "user";

	@Override
	protected IUser getUser(Map<String, Object> params) throws Exception {
		String user = String.valueOf(params.get(PARAM_USER));
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_MAPPEDUSER.getName());
		condition.setValue(user);
		condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_ACTIVATED.getName());
		condition.setValue(emYesNo.YES);
		condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_APPLICATION.getName());
		condition.setValue(this.getApplication().getCode());
		BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
		boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
		IOperationResult<IUser> operationResult = boRepository.fetchUser(criteria);
		return operationResult.getResultObjects().firstOrDefault();
	}

}
