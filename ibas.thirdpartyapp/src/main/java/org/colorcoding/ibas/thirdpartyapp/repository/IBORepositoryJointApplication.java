package org.colorcoding.ibas.thirdpartyapp.repository;

import java.util.Map;

import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.initialfantasy.bo.shell.User;

/**
 * 联合应用
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBORepositoryJointApplication {
	/**
	 * 联合登录
	 * 
	 * @param app
	 *            应用
	 * @param params
	 *            参数
	 * @return 操作结果
	 */
	OperationResult<User> jointConnect(String app, Map<String, Object> params);

}
