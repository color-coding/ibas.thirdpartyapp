package org.colorcoding.ibas.thirdpartyapp.test.bo;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;
import org.colorcoding.ibas.thirdpartyapp.repository.IBORepositoryThirdPartyAppApp;

import junit.framework.TestCase;

/**
 * 应用 测试
 * 
 */
public class testApplication extends TestCase {
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
		Application application = new Application();
		System.out.println(String.format("new bo: %s", application.toString()));
		// 测试属性赋值

		// 测试对象的保存和查询
		IOperationResult<?> operationResult = null;
		ICriteria criteria = null;
		IBORepositoryThirdPartyAppApp boRepository = new BORepositoryThirdPartyApp();
		// 设置用户口令
		boRepository.setUserToken(this.getToken());

		// 测试保存
		operationResult = boRepository.saveApplication(application);
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
		Application boSaved = (Application) operationResult.getResultObjects().firstOrDefault();

		// 测试查询
		criteria = boSaved.getCriteria();
		criteria.setResultCount(10);
		operationResult = boRepository.fetchApplication(criteria);
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

	}

}
