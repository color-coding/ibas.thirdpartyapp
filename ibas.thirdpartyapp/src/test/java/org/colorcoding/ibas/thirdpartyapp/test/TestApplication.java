package org.colorcoding.ibas.thirdpartyapp.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.bobas.repository.InvalidTokenException;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

import junit.framework.TestCase;

public class TestApplication extends TestCase {

	public void testApplication() throws InvalidTokenException {
		BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
		boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(Application.PROPERTY_CODE.getName());
		condition.setValue("AL-PAY01");
		for (KeyValue item : boRepository.fetchApplicationConfig(criteria).getResultObjects()) {
			System.out.println(item.toString());
			if (item.getKey() == Application.PROPERTY_APPKEY.getName()
					|| item.getKey() == Application.PROPERTY_APPSECRET.getName()
					|| item.getKey() == Application.PROPERTY_CERTIFICATE.getName()) {
				File file = new File(item.getValue().toString());
				if (file.exists()) {
					System.out.println(this.txt2String(file));
				}
			}
		}
	}

	public String txt2String(File file) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(System.lineSeparator() + s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
}
