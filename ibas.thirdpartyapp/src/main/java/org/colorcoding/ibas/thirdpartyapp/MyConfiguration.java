package org.colorcoding.ibas.thirdpartyapp;

import java.io.File;

import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.configuration.ConfigurationFactory;
import org.colorcoding.ibas.bobas.configuration.ConfigurationManager;

/**
 * 我的配置项
 */
public class MyConfiguration extends org.colorcoding.ibas.initialfantasy.MyConfiguration {

	private volatile static ConfigurationManager instance;

	public static ConfigurationManager create() {
		if (instance == null) {
			synchronized (MyConfiguration.class) {
				if (instance == null) {
					instance = ConfigurationFactory.createManager();
					instance.setConfigSign(MODULE_ID);
					instance.update();
				}
			}
		}
		return instance;
	}

	public static <P> P getConfigValue(String key, P defaultValue) {
		return create().getConfigValue(key, defaultValue);
	}

	public static String getConfigValue(String key) {
		return create().getConfigValue(key);
	}

	/**
	 * 模块标识
	 */
	public static final String MODULE_ID = "e25dde1f-2d1f-48de-a519-a0f9e88090ae";

	/**
	 * 命名空间
	 */
	public static final String NAMESPACE_ROOT = "https://colorcoding.org/ibas/thirdpartyapp/";

	/**
	 * 数据命名空间
	 */
	public static final String NAMESPACE_DATA = NAMESPACE_ROOT + "data";

	/**
	 * 业务对象命名空间
	 */
	public static final String NAMESPACE_BO = NAMESPACE_ROOT + "bo";

	/**
	 * 服务命名空间
	 */
	public static final String NAMESPACE_SERVICE = NAMESPACE_ROOT + "service";

	/**
	 * 获取数据文件目录
	 * 
	 * @return
	 */
	public static String getFileFolder() {
		String workFolder = Files.valueOf(MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DOCUMENT_FOLDER,
				MyConfiguration.getDataFolder()), "thirdpartyapp_files").getPath();
		if (!workFolder.endsWith(File.separator)) {
			workFolder += File.separator;
		}
		return workFolder;
	}
}
