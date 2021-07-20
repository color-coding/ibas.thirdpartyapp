package org.colorcoding.ibas.thirdpartyapp.test;

import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.thirdpartyapp.client.ApplicationClient;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationClientManager;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationException;
import org.colorcoding.ibas.thirdpartyapp.client.E_Mail;

import junit.framework.TestCase;

public class TestE_Mail extends TestCase {

	public void testE_Mail() throws ApplicationException {
		ApplicationClient client = ApplicationClientManager.newInstance().create("E_MAIL-TEST", "admin");
		Map<String, Object> mapMail = new HashMap<String, Object>();
		mapMail.put("Recipient", "neil.zhou@avatech.com.cn;niuren.zhu@qq.com");
		mapMail.put("Subject", "我是一个测试邮件");
		mapMail.put("Content", "哈哈，测试邮件哟！！！");
		// client.execute(E_Mail.EXECUTE_COMMAND_SEND, mapMail);
		client.execute(E_Mail.EXECUTE_COMMAND_RECEIVE, null);
	}
}
