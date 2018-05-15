package org.colorcoding.ibas.thirdpartyapp.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.repository.BORepositoryServiceApplication;
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

		} catch (Exception e) {
			opRslt.setError(e);
		}
		return opRslt;
	}

	// --------------------------------------------------------------------------------------------//
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
