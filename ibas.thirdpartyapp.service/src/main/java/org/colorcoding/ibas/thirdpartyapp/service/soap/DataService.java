package org.colorcoding.ibas.thirdpartyapp.service.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.cxf.WebServicePath;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.other.UserApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.user.User;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

/**
 * ThirdPartyApp 数据服务JSON
 */
@WebService
@WebServicePath("data")
public class DataService extends BORepositoryThirdPartyApp {

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户应用
	 * 
	 * @param user
	 *            用户
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	@WebMethod
	public OperationResult<UserApplication> fetchUserApplications(@WebParam(name = "user") String user,
			@WebParam(name = "token") String token) {
		return super.fetchUserApplications(user, token);
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
	@WebMethod
	public OperationResult<Application> fetchApplication(@WebParam(name = "criteria") Criteria criteria,
			@WebParam(name = "token") String token) {
		return super.fetchApplication(criteria, token);
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
	@WebMethod
	public OperationResult<Application> saveApplication(@WebParam(name = "bo") Application bo,
			@WebParam(name = "token") String token) {
		return super.saveApplication(bo, token);
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
	@WebMethod
	public OperationResult<User> fetchUser(@WebParam(name = "criteria") Criteria criteria,
			@WebParam(name = "token") String token) {
		return super.fetchUser(criteria, token);
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
	@WebMethod
	public OperationResult<User> saveUser(@WebParam(name = "bo") User bo, @WebParam(name = "token") String token) {
		return super.saveUser(bo, token);
	}

	// --------------------------------------------------------------------------------------------//

}
