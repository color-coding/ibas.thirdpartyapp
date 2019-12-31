package org.colorcoding.ibas.thirdpartyapp.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.repository.IBORepositoryApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.application.IApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.applicationconfig.IApplicationConfig;
import org.colorcoding.ibas.thirdpartyapp.bo.other.UserApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.user.IUser;

/**
 * ThirdPartyApp仓库应用
 */
public interface IBORepositoryThirdPartyAppApp extends IBORepositoryApplication {

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户应用
	 * 
	 * @param user  用户
	 * @param token 口令
	 * @return 操作结果
	 */
	IOperationResult<UserApplication> fetchUserApplications(String user);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-应用
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	IOperationResult<IApplication> fetchApplication(ICriteria criteria);

	/**
	 * 保存-应用
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	IOperationResult<IApplication> saveApplication(IApplication bo);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	IOperationResult<IUser> fetchUser(ICriteria criteria);

	/**
	 * 保存-用户
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	IOperationResult<IUser> saveUser(IUser bo);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-应用配置
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	IOperationResult<IApplicationConfig> fetchApplicationConfig(ICriteria criteria);

	/**
	 * 保存-应用配置
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	IOperationResult<IApplicationConfig> saveApplicationConfig(IApplicationConfig bo);
	// --------------------------------------------------------------------------------------------//

}
