package org.colorcoding.ibas.thirdpartyapp.joint;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.IKeyText;
import org.colorcoding.ibas.bobas.data.KeyText;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.user.IUser;
import org.colorcoding.ibas.thirdpartyapp.bo.user.User;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

public class ConnectManagerOAuthUrl extends ConnectManager {

	/**
	 * 参数名称-授权码
	 */
	public static final String PARAM_NAME_CODE = "code";
	/**
	 * 参数名称-认证地址
	 */
	public static final String PARAM_NAME_OAUTHURL = Application.PROPERTY_OAUTHURL.getName();

	@Override
	protected IUser getUser(Map<String, Object> params) throws Exception {
		String app = String.valueOf(params.get(PARAM_NAME_APP_CODE));
		// 应用变量
		String url = MyConfiguration.applyVariables(String.valueOf(params.get(PARAM_NAME_OAUTHURL)),
				new Iterator<IKeyText>() {

					Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();

					@Override
					public IKeyText next() {
						Entry<String, Object> item = iterator.next();
						return new KeyText(item.getKey(), String.valueOf(item.getValue()));
					}

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}
				});
		return this.getUser(app, url);
	}

	protected IUser getUser(String app, String userId) throws Exception {
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_MAPPEDUSER.getName());
		condition.setValue(userId);
		condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_ACTIVATED.getName());
		condition.setValue(emYesNo.YES);
		condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_APPLICATION.getName());
		condition.setValue(app);
		BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
		boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
		IOperationResult<IUser> operationResult = boRepository.fetchUser(criteria);
		return operationResult.getResultObjects().firstOrDefault();
	}
}
