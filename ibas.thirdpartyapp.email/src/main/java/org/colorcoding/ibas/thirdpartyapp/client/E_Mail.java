package org.colorcoding.ibas.thirdpartyapp.client;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.initialfantasy.bo.shell.User;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;

public class E_Mail extends ApplicationClient {
	/**
	 * 运行命令 - 收邮件
	 */
	public final static String EXECUTE_COMMAND_RECEIVE = "receive";
	/**
	 * 运行命令 - 发邮件
	 */
	public final static String EXECUTE_COMMAND_SEND = "send";

	@Override
	public User authenticate(Properties params) throws AuthenticationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <P> IOperationResult<P> execute(String instruct, Properties params) throws ApplicationException {
		try {
			if (EXECUTE_COMMAND_RECEIVE.equalsIgnoreCase(instruct)) {
				this.receive();
				return new OperationResult<P>();
			} else if (EXECUTE_COMMAND_SEND.equalsIgnoreCase(instruct)) {
				this.send(params);
				return new OperationResult<P>();
			} else {
				throw new NotImplementedException();
			}
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	protected Properties receiveProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.debug", this.paramValue("mail.debug", "false"));
		if (MyConfiguration.isDebugMode()) {
			properties.setProperty("mail.debug.auth", this.paramValue("mail.debug.auth", "false"));
		}
		if ("imap".equalsIgnoreCase(this.paramValue("mail.store.protocol", "pop3"))) {
			properties.setProperty("mail.store.protocol", "imap");
			properties.setProperty("mail.imap.host", this.paramValue("mail.imap.host", ""));
			properties.setProperty("mail.imap.port", this.paramValue("mail.imap.port", "995"));
			properties.setProperty("mail.imap.auth", this.paramValue("mail.imap.auth", "true"));
			// properties.setProperty("mail.user", this.paramValue("mail.user", ""));
			// properties.setProperty("mail.pass", this.paramValue("mail.pass", ""));
			// 使用ssl
			if (this.paramValue("mail.imap.ssl", true) == true) {
				properties.setProperty("mail.imap.starttls.enable", "true");
				properties.setProperty("mail.imap.socketFactory.fallback", "false");
				properties.setProperty("mail.imap.socketFactory.class", "com.sun.mail.util.MailSSLSocketFactory");
				properties.setProperty("mail.imap.socketFactory.port", this.paramValue("mail.imap.port", "995"));
			}
			/*
			 * properties.setProperty("mail.imap.auth.plain.disable", "false");
			 * properties.setProperty("mail.imap.auth.login.disable", "false");
			 * properties.setProperty("mail.imap.auth.ntlm.disable", "false");
			 * properties.setProperty("mail.imap.auth.xoauth2.disable", "false");
			 */
		} else {
			properties.setProperty("mail.store.protocol", "pop3");
			properties.setProperty("mail.pop3.host", this.paramValue("mail.pop3.host", ""));
			properties.setProperty("mail.pop3.port", this.paramValue("mail.pop3.port", "993"));
			properties.setProperty("mail.pop3.auth", this.paramValue("mail.pop3.auth", "true"));
			// properties.setProperty("mail.user", this.paramValue("mail.user", ""));
			// properties.setProperty("mail.pass", this.paramValue("mail.pass", ""));
			// 使用ssl
			if (this.paramValue("mail.pop3.ssl", true) == true) {
				properties.setProperty("mail.pop3.starttls.enable", "true");
				// properties.setProperty("mail.pop3.socketFactory.class",
				// "javax.net.ssl.SSLSocketFactory");
				properties.setProperty("mail.pop3.socketFactory.fallback", "false");
				properties.setProperty("mail.pop3.socketFactory.class", "com.sun.mail.util.MailSSLSocketFactory");
				properties.setProperty("mail.pop3.socketFactory.port", this.paramValue("mail.pop3.port", "993"));
			}
			/*
			 * properties.setProperty("mail.pop3.auth.plain.disable", "false");
			 * properties.setProperty("mail.pop3.auth.login.disable", "true");
			 * properties.setProperty("mail.pop3.auth.ntlm.disable", "false");
			 * properties.setProperty("mail.pop3.auth.xoauth2.disable", "false");
			 */
		}
		return properties;
	}

	public void receive() throws NumberFormatException, MessagingException {
		Properties properties = this.receiveProperties();
		String protocol = properties.getProperty("mail.store.protocol");
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				if (Boolean.valueOf(properties.getProperty(String.format("mail.%s.auth", protocol)))) {
					// 需要认证
					return new PasswordAuthentication(E_Mail.this.paramValue("mail.user", ""),
							E_Mail.this.paramValue("mail.pass", ""));
				}
				return super.getPasswordAuthentication();
			}
		});
		session.setDebug(this.paramValue("mail.debug", false));
		Store store = session.getStore();
		if (Boolean.valueOf(properties.getProperty(String.format("mail.%s.auth", protocol)))) {
			// 需要认证
			store.connect(properties.getProperty(String.format("mail.%s.host", protocol)),
					Integer.valueOf(properties.getProperty(String.format("mail.%s.port", protocol))),
					this.paramValue("mail.user", ""), this.paramValue("mail.pass", ""));
		} else {
			store.connect();
		}
		Folder mbox = store.getFolder("INBOX");
		// INBOX
		mbox.open(Folder.READ_ONLY);
		System.out.println(mbox.getName());
		int msgCount = mbox.getMessageCount();
		System.out.println(msgCount);
	}

	protected Properties sendProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.debug", this.paramValue("mail.debug", "false"));
		properties.setProperty("mail.debug.auth", "true");
		if (MyConfiguration.isDebugMode()) {
			properties.setProperty("mail.debug.auth", this.paramValue("mail.debug.auth", "false"));
		}
		properties.setProperty("mail.smtp.host", this.paramValue("mail.smtp.host", ""));
		properties.setProperty("mail.smtp.port", this.paramValue("mail.smtp.port", "465"));
		properties.setProperty("mail.smtp.auth", this.paramValue("mail.smtp.auth", "true"));
		if (this.paramValue("mail.smtp.ssl", true) == true) {
			properties.setProperty("mail.smtp.starttls.enable", "true");
			properties.setProperty("mail.smtp.socketFactory.fallback", "false");
			properties.setProperty("mail.smtp.socketFactory.class", "com.sun.mail.util.MailSSLSocketFactory");
			properties.setProperty("mail.smtp.socketFactory.port", this.paramValue("mail.smtp.port", "465"));
		}
		return properties;
	}

	public void send(Properties params) throws MessagingException {
		Properties properties = this.sendProperties();
		String protocol = "smtp";
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				if (Boolean.valueOf(properties.getProperty(String.format("mail.%s.auth", protocol)))) {
					// 需要认证
					return new PasswordAuthentication(E_Mail.this.paramValue("mail.user", ""),
							E_Mail.this.paramValue("mail.pass", ""));
				}
				return super.getPasswordAuthentication();
			}
		});
		session.setDebug(this.paramValue("mail.debug", false));
		// 由邮件会话新建一个消息对象
		MimeMessage message = new MimeMessage(session);
		// 设置邮件内容
		// 设置发件人的地址
		message.setFrom(new InternetAddress(this.paramValue("mail.user", "anonymous")));
		// 设置收件人
		for (String item : this.paramValue("Recipient", "", params).split(";")) {
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(item));// 设置收件人,并设置其接收类型为TO
		}
		message.setSubject(this.paramValue("Subject", "", params));// 设置标题
		// 设置信件内容
		message.setText(this.paramValue("Content", "", params)); // 发送 纯文本
		// message.setContent(mailContent, "text/html;charset=utf-8"); //
		// 发送HTML邮件，内容样式比较丰富
		message.setSentDate(DateTime.getNow());// 设置发信时间
		message.saveChanges();// 存储邮件信息
		// 发送邮件
		Transport transport = session.getTransport("smtp");
		transport.connect(this.paramValue("mail.user", ""), this.paramValue("mail.pass", ""));
		transport.sendMessage(message, message.getAllRecipients());// 发送邮件,其中第二个参数是所有已设好的收件人地址
		transport.close();
	}
}
