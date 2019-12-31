package org.colorcoding.ibas.thirdpartyapp.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.repository.IBORepositorySmartService;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.applicationconfig.ApplicationConfig;
import org.colorcoding.ibas.thirdpartyapp.bo.other.UserApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.user.User;

/**
 * ThirdPartyApp仓库服务
 */
public interface IBORepositoryThirdPartyAppSvc extends IBORepositorySmartService {

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户应用
	 * 
	 * @param user  用户
	 * @param token 口令
	 * @return 操作结果
	 */
	OperationResult<UserApplication> fetchUserApplications(String user, String token);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-应用
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	OperationResult<Application> fetchApplication(ICriteria criteria, String token);

	/**
	 * 保存-应用
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	OperationResult<Application> saveApplication(Application bo, String token);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	OperationResult<User> fetchUser(ICriteria criteria, String token);

	/**
	 * 保存-用户
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	OperationResult<User> saveUser(User bo, String token);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-应用配置
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	OperationResult<ApplicationConfig> fetchApplicationConfig(ICriteria criteria, String token);

	/**
	 * 保存-应用配置
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	OperationResult<ApplicationConfig> saveApplicationConfig(ApplicationConfig bo, String token);
	// --------------------------------------------------------------------------------------------//

}
