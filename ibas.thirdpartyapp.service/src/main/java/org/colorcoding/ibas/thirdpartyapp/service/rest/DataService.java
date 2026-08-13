package org.colorcoding.ibas.thirdpartyapp.service.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.applicationconfig.ApplicationConfig;
import org.colorcoding.ibas.thirdpartyapp.bo.other.UserApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.UserMapping;
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
	 * @param user  用户
	 * @param token 口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetchUserApplications")
	public OperationResult<UserApplication> fetchUserApplications(@QueryParam("user") String user,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.fetchUserApplications(user, MyConfiguration.optToken(authorization, token));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-应用
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetchApplication")
	public OperationResult<Application> fetchApplication(Criteria criteria,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.fetchApplication(criteria, MyConfiguration.optToken(authorization, token));
	}

	/**
	 * 保存-应用
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("saveApplication")
	public OperationResult<Application> saveApplication(Application bo,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.saveApplication(bo, MyConfiguration.optToken(authorization, token));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-应用配置
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetchApplicationConfig")
	public OperationResult<ApplicationConfig> fetchApplicationConfig(Criteria criteria,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.fetchApplicationConfig(criteria, MyConfiguration.optToken(authorization, token));
	}

	/**
	 * 保存-应用配置
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("saveApplicationConfig")
	public OperationResult<ApplicationConfig> saveApplicationConfig(ApplicationConfig bo,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.saveApplicationConfig(bo, MyConfiguration.optToken(authorization, token));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户映射
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetchUserMapping")
	public OperationResult<UserMapping> fetchUserMapping(Criteria criteria,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.fetchUserMapping(criteria, MyConfiguration.optToken(authorization, token));
	}

	// --------------------------------------------------------------------------------------------//

}
