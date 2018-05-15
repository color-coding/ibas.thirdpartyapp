package org.colorcoding.ibas.thirdpartyapp;

/**
 * 我的配置项
 */
public class MyConfiguration extends org.colorcoding.ibas.bobas.MyConfiguration {

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
     * 配置项目-文件文件夹
     */
    public final static String CONFIG_ITEM_THIRDPARTYAPP_FILE_FOLDER = "TPAFileFolder";

}
