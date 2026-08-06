package org.colorcoding.ibas.thirdpartyapp.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.file.FileItem;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.bobas.repository.FileRepository;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.application.IApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSetting;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSettingItem;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.UserMapping;
import org.colorcoding.ibas.thirdpartyapp.data.emConfigItemCategory;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

public class ApplicationClientManager {

	/**
	 * 配置项目模板-应用客户端
	 */
	public static final String CONFIG_ITEM_TEMPLATE_APPLICATION_CLINET = "ApplicationClient|%s";

	private static ApplicationClientManager instance;

	public final static ApplicationClientManager newInstance() {
		if (instance == null) {
			synchronized (ApplicationClientManager.class) {
				if (instance == null) {
					instance = new ApplicationClientManager();
				}
			}
		}
		return instance;
	}

	private ApplicationClientManager() {
	}

	protected ApplicationClient create(ApplicationSetting appSetting) throws Exception {
		if (appSetting == null) {
			throw new IllegalArgumentException(I18N.prop("msg_tpa_no_param", "appSetting"));
		}
		if (Strings.isNullOrEmpty(appSetting.getName())) {
			throw new IllegalArgumentException(I18N.prop("msg_tpa_no_param", "name"));
		}
		if (Strings.isNullOrEmpty(appSetting.getGroup())) {
			throw new IllegalArgumentException(I18N.prop("msg_tpa_no_param", "group"));
		}
		String clientPackage = ApplicationClientManager.class.getPackage().getName();
		String managerName = MyConfiguration
				.getConfigValue(String.format(CONFIG_ITEM_TEMPLATE_APPLICATION_CLINET, appSetting.getGroup()));
		if (Strings.isNullOrEmpty(managerName)) {
			throw new Exception(I18N.prop("msg_tpa_not_found_application_client", appSetting.getDescription()));
		}
		if (managerName.indexOf(".") < 0) {
			// 补充命名空间
			managerName = String.format("%s.%s", clientPackage, managerName);
		}
		Class<?> managerClass = Class.forName(managerName, false, ApplicationClientManager.class.getClassLoader());
		Package managerPackage = managerClass.getPackage();
		if (managerPackage == null || !clientPackage.equals(managerPackage.getName())
				|| !ApplicationClient.class.isAssignableFrom(managerClass)
				|| !Modifier.isPublic(managerClass.getModifiers()) || Modifier.isAbstract(managerClass.getModifiers())) {
			throw new SecurityException(String.format("invalid application client class [%s].", managerName));
		}
		ApplicationClient client = managerClass.asSubclass(ApplicationClient.class).getDeclaredConstructor().newInstance();
		client.setSetting(appSetting);
		for (ApplicationSettingItem item : appSetting.getSettingItems()) {
			if (item.getCategory() == emConfigItemCategory.FILE) {
				String fileName = item.getValue();
				if (Strings.isNullOrEmpty(fileName)) {
					continue;
				}
				if (Strings.startsWith(fileName, ApplicationSettingItem.URL_HEAD_FILE)) {
					fileName = fileName.substring(ApplicationSettingItem.URL_HEAD_FILE.length());
				}
				// 目标文件必须位于本地模块文档目录内
				File documentsFolder = Files
						.valueOf(MyConfiguration.getDataFolder(), MyConfiguration.getDocumetsFolder()).getCanonicalFile();
				File file = new File(documentsFolder, fileName).getCanonicalFile();
				if (file.equals(documentsFolder) || !file.toPath().startsWith(documentsFolder.toPath())) {
					throw new IOException(String.format("invalid file path [%s].", item.getValue()));
				}
				// 检查本地模块目录是否存在
				if (file.exists() && file.isFile()) {
					item.setValue(file.getPath());
					continue;
				}
				if (file.exists()) {
					throw new IOException(String.format("invalid file [%s].", file.getPath()));
				}
				// 复制到本地模块目录
				try (FileRepository fileRepository = new FileRepository()) {
					fileRepository.setRepositoryFolder(MyConfiguration.getDocumetsFolder());
					fileRepository.setGroupingFiles(false);
					Criteria criteria = new Criteria();
					ICondition condition = criteria.getConditions().create();
					condition.setAlias(FileRepository.CONDITION_ALIAS_FILE_NAME);
					condition.setValue(fileName);
					IOperationResult<FileItem> opRsltFile = fileRepository.fetch(criteria);
					if (opRsltFile.getError() != null) {
						throw opRsltFile.getError();
					}
					if (opRsltFile.getResultObjects().isEmpty()) {
						throw new FileNotFoundException(item.getValue());
					}
					FileItem fileItem = opRsltFile.getResultObjects().firstOrDefault();
					File parent = file.getParentFile();
					if ((!parent.exists() && !parent.mkdirs()) || !parent.isDirectory()) {
						throw new IOException(String.format("failed to create directory [%s].", parent.getPath()));
					}
					try (FileOutputStream outputStream = new FileOutputStream(file)) {
						fileItem.writeTo(outputStream);
						outputStream.flush();
					}
					item.setValue(file.getPath());
					Logger.log(MessageLevel.DEBUG, "%s: write file [%s] to [%s].", client.getName(),
							fileItem.getName(), file.getPath());
				}
			}
		}
		return client;
	}

	public ApplicationClient create(String appCode) {
		try {
			if (Strings.isNullOrEmpty(appCode)) {
				throw new Exception(I18N.prop("msg_tpa_no_param", "appCode"));
			}
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(Application.PROPERTY_CODE.getName());
			condition.setValue(appCode);
			condition = criteria.getConditions().create();
			condition.setAlias(Application.PROPERTY_ACTIVATED.getName());
			condition.setValue(emYesNo.YES);
			try (BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp()) {
				boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
				IOperationResult<IApplication> opRslt = boRepository.fetchApplication(criteria);
				if (opRslt.getError() != null) {
					throw opRslt.getError();
				}
				if (opRslt.getResultObjects().isEmpty()) {
					throw new Exception(I18N.prop("msg_tpa_invalid_application", appCode));
				}
				return this.create(opRslt.getResultObjects().firstOrDefault());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ApplicationClient create(IApplication application) {
		if (application == null || application.getActivated() != emYesNo.YES) {
			throw new IllegalArgumentException(I18N.prop("msg_tpa_invalid_application",
					application == null ? Strings.VALUE_EMPTY : application.getCode()));
		}
		try (BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			return this.create(boRepository.createApplicationSetting(application));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ApplicationClient create(String appCode, String user) {
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
		condition.setValue(appCode);
		condition = criteria.getConditions().create();
		condition.setAlias(UserMapping.PROPERTY_USER.getName());
		condition.setValue(user);
		try (BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			IOperationResult<IUserMapping> opRslt = boRepository.fetchUserMapping(criteria);
			if (opRslt.getError() != null) {
				throw opRslt.getError();
			}
			if (opRslt.getResultObjects().isEmpty()) {
				throw new Exception(I18N.prop("msg_tpa_invalid_application_user_mapping", appCode, user));
			}
			return this.create(opRslt.getResultObjects().firstOrDefault());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ApplicationClient create(IUserMapping userMapping) {
		if (userMapping == null) {
			throw new IllegalArgumentException(I18N.prop("msg_tpa_invalid_application_user_mapping",
					Strings.VALUE_EMPTY, Strings.VALUE_EMPTY));
		}
		try (BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			return this.create(boRepository.createApplicationSetting(userMapping));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
