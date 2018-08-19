package org.colorcoding.ibas.thirdpartyapp.repository;

import java.lang.reflect.Method;

import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOSeriesKey;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.repository.BORepositoryServiceApplication;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.application.IApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.user.IUser;
import org.colorcoding.ibas.thirdpartyapp.bo.user.User;
import org.colorcoding.ibas.thirdpartyapp.data.UserApplication;

/**
 * ThirdPartyApp仓库
 */
public class BORepositoryThirdPartyApp extends BORepositoryServiceApplication
		implements IBORepositoryThirdPartyAppSvc, IBORepositoryThirdPartyAppApp {

	// --------------------------------------------------------------------------------------------//

	public IOperationResult<UserApplication> fetchUserApplications(String user) {
		return this.fetchUserApplications(user, this.getUserToken());
	}

	public OperationResult<UserApplication> fetchUserApplications(String user, String token) {
		OperationResult<UserApplication> opRslt = new OperationResult<UserApplication>();
		try {
			this.setUserToken(token);
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(User.PROPERTY_USER.getName());
			condition.setValue(user);
			condition = criteria.getConditions().create();
			condition.setAlias(User.PROPERTY_ACTIVATED.getName());
			condition.setValue(emYesNo.YES);
			IOperationResult<IUser> opRsltUser = this.fetchUser(criteria);
			if (opRsltUser.getError() != null) {
				throw opRsltUser.getError();
			}
			for (IUser appUser : opRsltUser.getResultObjects()) {
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
					return opRslt;
				}
				if (application.getAppUrl() == null || application.getAppUrl().isEmpty()) {
					return opRslt;
				}
				UserApplication userApplication = new UserApplication();
				userApplication.setCode(application.getCode());
				userApplication.setName(application.getName());
				userApplication.setUrl(application.getAppUrl());
				opRslt.addResultObjects(userApplication);
			}
		} catch (Exception e) {
			opRslt.setError(e);
		}
		return opRslt;
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-应用配置
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	public IOperationResult<KeyValue> fetchApplicationConfig(ICriteria criteria) {
		return this.fetchApplicationConfig(criteria, this.getUserToken());
	}

	/**
	 * 查询-应用配置
	 * 
	 * @param criteria
	 *            查询
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<KeyValue> fetchApplicationConfig(ICriteria criteria, String token) {
		OperationResult<KeyValue> opRslt = new OperationResult<KeyValue>();
		try {
			this.setUserToken(token);
			if (criteria == null) {
				criteria = new Criteria();
			}
			criteria.setResultCount(1);
			IOperationResult<IApplication> opRsltApp = this.fetchApplication(criteria);
			if (opRsltApp.getError() != null) {
				throw opRsltApp.getError();
			}
			Application application = (Application) opRsltApp.getResultObjects().firstOrDefault();
			if (application == null) {
				return opRslt;
			}
			ArrayList<String> skipFields = new ArrayList<>();
			skipFields.add(IBOMasterData.SERIAL_NUMBER_KEY_NAME);
			for (Method item : IBOStorageTag.class.getDeclaredMethods()) {
				if (!item.getName().startsWith("get") && item.getParameterCount() == 0) {
					continue;
				}
				skipFields.add(item.getName().substring(3));
			}
			for (Method item : IBOSeriesKey.class.getDeclaredMethods()) {
				if (!item.getName().startsWith("get") && item.getParameterCount() == 0) {
					continue;
				}
				skipFields.add(item.getName().substring(3));
			}
			for (IFieldData field : application.getFields()) {
				if (skipFields.contains(field.getName())) {
					continue;
				}
				String name = field.getName();
				if (field.getName().equals(Application.PROPERTY_CODE.getName())
						|| field.getName().equals(Application.PROPERTY_NAME.getName())) {
					name = String.format("App%s", field.getName());
				}
				Object value = field.getValue();
				if (value == null) {
					continue;
				}
				if (value instanceof String) {
					// 文件则扩展到完整路径
					if (value.toString().startsWith("file://")) {
						value = value.toString().replace("file://", MyConfiguration.getFileFolder());
					}
				}
				opRslt.getResultObjects().add(new KeyValue(name, value));
			}
		} catch (Exception e) {
			opRslt.setError(e);
		}
		return opRslt;
	}

	/**
	 * 查询-应用
	 * 
	 * @param criteria
	 *            查询
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<Application> fetchApplication(ICriteria criteria, String token) {
		return super.fetch(criteria, token, Application.class);
	}

	/**
	 * 查询-应用（提前设置用户口令）
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	public IOperationResult<IApplication> fetchApplication(ICriteria criteria) {
		return new OperationResult<IApplication>(this.fetchApplication(criteria, this.getUserToken()));
	}

	/**
	 * 保存-应用
	 * 
	 * @param bo
	 *            对象实例
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<Application> saveApplication(Application bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-应用（提前设置用户口令）
	 * 
	 * @param bo
	 *            对象实例
	 * @return 操作结果
	 */
	public IOperationResult<IApplication> saveApplication(IApplication bo) {
		return new OperationResult<IApplication>(this.saveApplication((Application) bo, this.getUserToken()));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户
	 * 
	 * @param criteria
	 *            查询
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<User> fetchUser(ICriteria criteria, String token) {
		return super.fetch(criteria, token, User.class);
	}

	/**
	 * 查询-用户（提前设置用户口令）
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	public IOperationResult<IUser> fetchUser(ICriteria criteria) {
		return new OperationResult<IUser>(this.fetchUser(criteria, this.getUserToken()));
	}

	/**
	 * 保存-用户
	 * 
	 * @param bo
	 *            对象实例
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<User> saveUser(User bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-用户（提前设置用户口令）
	 * 
	 * @param bo
	 *            对象实例
	 * @return 操作结果
	 */
	public IOperationResult<IUser> saveUser(IUser bo) {
		return new OperationResult<IUser>(this.saveUser((User) bo, this.getUserToken()));
	}

	// --------------------------------------------------------------------------------------------//

}
