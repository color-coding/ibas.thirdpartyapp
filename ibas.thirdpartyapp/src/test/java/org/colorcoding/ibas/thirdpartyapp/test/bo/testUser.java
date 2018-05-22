package org.colorcoding.ibas.thirdpartyapp.test.bo;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.thirdpartyapp.bo.user.User;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;
import org.colorcoding.ibas.thirdpartyapp.repository.IBORepositoryThirdPartyAppApp;

import junit.framework.TestCase;

/**
 * 用户 测试
 * 
 */
public class testUser extends TestCase {
	/**
	 * 获取连接口令
	 */
	String getToken() {
		return OrganizationFactory.SYSTEM_USER.getToken();
	}

	/**
	 * 基本项目测试
	 * 
	 * @throws Exception
	 */
	public void testBasicItems() throws Exception {
		User user = new User();
		System.out.println(String.format("new bo: %s", user.toString()));
		// 测试属性赋值

		// 测试对象的保存和查询
		IOperationResult<?> operationResult = null;
		ICriteria criteria = null;
		IBORepositoryThirdPartyAppApp boRepository = new BORepositoryThirdPartyApp();
		// 设置用户口令
		boRepository.setUserToken(this.getToken());

		// 测试保存
		operationResult = boRepository.saveUser(user);
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
		User boSaved = (User) operationResult.getResultObjects().firstOrDefault();

		// 测试查询
		criteria = boSaved.getCriteria();
		criteria.setResultCount(10);
		operationResult = boRepository.fetchUser(criteria);
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

	}

}
