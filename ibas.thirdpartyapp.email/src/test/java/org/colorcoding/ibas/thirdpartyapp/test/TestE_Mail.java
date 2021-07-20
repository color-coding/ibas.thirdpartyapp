package org.colorcoding.ibas.thirdpartyapp.test;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationClient;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationClientManager;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationException;
import org.colorcoding.ibas.thirdpartyapp.client.E_Mail;

import junit.framework.TestCase;

public class TestE_Mail extends TestCase {

	public void testSenderMail() throws ApplicationException {
		ApplicationClient client = ApplicationClientManager.newInstance().create("E_MAIL-TEST", "admin");
		Properties properties = new Properties();
		properties.put(E_Mail.PROPERTIES_SEND_RECIPIENT, "neil.zhou@avatech.com.cn;niuren.zhu@qq.com");
		properties.put(E_Mail.PROPERTIES_SEND_SUBJECT, "我是一个测试邮件");
		properties.put(E_Mail.PROPERTIES_SEND_CONTENT, "哈哈，测试邮件哟！！！");
		client.execute(E_Mail.EXECUTE_COMMAND_SEND, properties);
	}

	public void testReciverMail() throws ApplicationException, MessagingException, IOException {
		ApplicationClient client = ApplicationClientManager.newInstance().create("E_MAIL-TEST", "admin");
		Properties properties = new Properties();
		// properties.put(E_Mail.PROPERTIES_SEARCH_DATE_FROM,
		// DateTime.valueOf("2021-07-01"));
		// properties.put(E_Mail.PROPERTIES_SEARCH_DATE_TO,
		// DateTime.valueOf("2021-07-31"));
		// properties.put(E_Mail.PROPERTIES_SEARCH_SUBJECT, "电子发票");
		properties.put(E_Mail.PROPERTIES_SEARCH_TOP, 10);
		IOperationResult<Message> opRslt = client.execute(E_Mail.EXECUTE_COMMAND_RECEIVE, properties);
		System.out.println("eMail:");
		Collections.reverse(opRslt.getResultObjects());
		for (Message message : opRslt.getResultObjects()) {
			System.out.println(String.format("    %s", message.getSubject()));
			Object content = message.getContent();
			if (content instanceof MimeMultipart) {
				this.printAttachment((MimeMultipart) content);
			}
		}
	}

	private void printAttachment(MimeMultipart multipart) throws MessagingException, IOException {
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (bodyPart.isMimeType("application/octet-stream")) {
				String disposition = bodyPart.getDisposition();
				if (disposition != null && disposition.equalsIgnoreCase(BodyPart.ATTACHMENT)) {
					System.out.println(String.format("      %s", MimeUtility.decodeText(bodyPart.getFileName())));
				}
			} else if (bodyPart.isMimeType("multipart/*")) {
				Object content = bodyPart.getContent();
				if (content instanceof MimeMultipart) {
					this.printAttachment((MimeMultipart) content);
				}
			}
		}
	}
}
