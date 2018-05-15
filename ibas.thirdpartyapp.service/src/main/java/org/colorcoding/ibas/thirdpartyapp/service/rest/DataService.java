package org.colorcoding.ibas.thirdpartyapp.service.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.user.User;
import org.colorcoding.ibas.thirdpartyapp.data.UserApplication;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

/**
 * ThirdPartyApp 数据服务JSON
 */
@Path("data")
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetchUserApplications")
	public OperationResult<UserApplication> fetchUserApplications(@QueryParam("user") String user,
			@QueryParam("token") String token) {
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetchApplication")
	public OperationResult<Application> fetchApplication(Criteria criteria, @QueryParam("token") String token) {
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("saveApplication")
	public OperationResult<Application> saveApplication(Application bo, @QueryParam("token") String token) {
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetchUser")
	public OperationResult<User> fetchUser(Criteria criteria, @QueryParam("token") String token) {
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("saveUser")
	public OperationResult<User> saveUser(User bo, @QueryParam("token") String token) {
		return super.saveUser(bo, token);
	}

	// --------------------------------------------------------------------------------------------//

}
