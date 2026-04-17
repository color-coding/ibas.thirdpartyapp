package org.colorcoding.ibas.thirdpartyapp.client;

import java.util.Properties;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.bo.organization.IUser;
import org.colorcoding.ibas.initialfantasy.bo.shell.User;
import org.colorcoding.ibas.initialfantasy.repository.BORepositoryInitialFantasy;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;

public abstract class SSO extends WebApp {

	/**
	 * 授权用户
	 * 
	 * @param params 参数
	 * @return
	 * @throws ApplicationException
	 */
	public final User authenticate(Properties params) throws ApplicationException {
		try {
			IUserMapping user = this.fetchUser(params);
			if (user == null) {
				throw new Exception(I18N.prop("msg_tpa_no_matching_user"));
			}
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_CODE.getName());
			condition.setValue(user.getUser());
			condition = criteria.getConditions().create();
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_ACTIVATED.getName());
			condition.setValue(emYesNo.YES);
			// 批准的用户
			condition = criteria.getConditions().create();
			condition.setBracketOpen(1);
			condition.setAlias(
					org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_APPROVALSTATUS.getName());
			condition.setValue(emApprovalStatus.UNAFFECTED);
			condition = criteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setRelationship(ConditionRelationship.OR);
			condition.setAlias(
					org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_APPROVALSTATUS.getName());
			condition.setValue(emApprovalStatus.APPROVED);
			// 当前日期
			String date = DateTimes.today().toString();
			// 有效日期
			condition = criteria.getConditions().create();
			condition.setBracketOpen(1);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_VALIDDATE.getName());
			condition.setOperation(ConditionOperation.IS_NULL);
			condition = criteria.getConditions().create();
			condition.setRelationship(ConditionRelationship.OR);
			condition.setBracketOpen(1);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_VALIDDATE.getName());
			condition.setOperation(ConditionOperation.NOT_NULL);
			condition = criteria.getConditions().create();
			condition.setBracketClose(2);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_VALIDDATE.getName());
			condition.setOperation(ConditionOperation.LESS_EQUAL);
			condition.setValue(date);
			// 失效日期
			condition = criteria.getConditions().create();
			condition.setBracketOpen(1);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_INVALIDDATE.getName());
			condition.setOperation(ConditionOperation.IS_NULL);
			condition = criteria.getConditions().create();
			condition.setRelationship(ConditionRelationship.OR);
			condition.setBracketOpen(1);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_INVALIDDATE.getName());
			condition.setOperation(ConditionOperation.NOT_NULL);
			condition = criteria.getConditions().create();
			condition.setBracketClose(2);
			condition.setAlias(org.colorcoding.ibas.initialfantasy.bo.organization.User.PROPERTY_INVALIDDATE.getName());
			condition.setOperation(ConditionOperation.GRATER_EQUAL);
			condition.setValue(date);

			try (BORepositoryInitialFantasy boRepository = new BORepositoryInitialFantasy()) {
				boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
				IOperationResult<IUser> opRsltUser = boRepository.fetchUser(criteria);
				IUser boUser = opRsltUser.getResultObjects().firstOrDefault();
				if (boUser == null) {
					throw new Exception(I18N.prop("msg_tpa_no_matching_user"));
				}
				User orgUser = User.create(boUser);
				OrganizationFactory.createManager().register(orgUser);
				return orgUser;
			}
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	protected abstract IUserMapping fetchUser(Properties params) throws Exception;
}
