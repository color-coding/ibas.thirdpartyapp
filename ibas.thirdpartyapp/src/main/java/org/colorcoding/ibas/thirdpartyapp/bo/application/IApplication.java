package org.colorcoding.ibas.thirdpartyapp.bo.application;

import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 应用 接口
 * 
 */
public interface IApplication extends IBOMasterData {

	/**
	 * 获取-编码
	 * 
	 * @return 值
	 */
	String getCode();

	/**
	 * 设置-编码
	 * 
	 * @param value
	 *            值
	 */
	void setCode(String value);

	/**
	 * 获取-名称
	 * 
	 * @return 值
	 */
	String getName();

	/**
	 * 设置-名称
	 * 
	 * @param value
	 *            值
	 */
	void setName(String value);

	/**
	 * 获取-激活
	 * 
	 * @return 值
	 */
	emYesNo getActivated();

	/**
	 * 设置-激活
	 * 
	 * @param value
	 *            值
	 */
	void setActivated(emYesNo value);

	/**
	 * 获取-应用标记
	 * 
	 * @return 值
	 */
	String getAppId();

	/**
	 * 设置-应用标记
	 * 
	 * @param value
	 *            值
	 */
	void setAppId(String value);

	/**
	 * 获取-应用公钥
	 * 
	 * @return 值
	 */
	String getAppKey();

	/**
	 * 设置-应用公钥
	 * 
	 * @param value
	 *            值
	 */
	void setAppKey(String value);

	/**
	 * 获取-应用私钥
	 * 
	 * @return 值
	 */
	String getAppSecret();

	/**
	 * 设置-应用私钥
	 * 
	 * @param value
	 *            值
	 */
	void setAppSecret(String value);

	/**
	 * 获取-应用地址
	 * 
	 * @return 值
	 */
	String getAppUrl();

	/**
	 * 设置-应用地址
	 * 
	 * @param value
	 *            值
	 */
	void setAppUrl(String value);

	/**
	 * 获取-应用接口地址
	 * 
	 * @return 值
	 */
	String getApiUrl();

	/**
	 * 设置-应用接口地址
	 * 
	 * @param value
	 *            值
	 */
	void setApiUrl(String value);

	/**
	 * 获取-证书地址
	 * 
	 * @return 值
	 */
	String getCertificate();

	/**
	 * 设置-证书地址
	 * 
	 * @param value
	 *            值
	 */
	void setCertificate(String value);

	/**
	 * 获取-账号
	 * 
	 * @return 值
	 */
	String getAccount();

	/**
	 * 设置-账号
	 * 
	 * @param value
	 *            值
	 */
	void setAccount(String value);

	/**
	 * 获取-接收地址
	 * 
	 * @return 值
	 */
	String getReceivingUrl();

	/**
	 * 设置-接收地址
	 * 
	 * @param value
	 *            值
	 */
	void setReceivingUrl(String value);

	/**
	 * 获取-对象编号
	 * 
	 * @return 值
	 */
	Integer getDocEntry();

	/**
	 * 设置-对象编号
	 * 
	 * @param value
	 *            值
	 */
	void setDocEntry(Integer value);

	/**
	 * 获取-对象类型
	 * 
	 * @return 值
	 */
	String getObjectCode();

	/**
	 * 设置-对象类型
	 * 
	 * @param value
	 *            值
	 */
	void setObjectCode(String value);

	/**
	 * 获取-创建日期
	 * 
	 * @return 值
	 */
	DateTime getCreateDate();

	/**
	 * 设置-创建日期
	 * 
	 * @param value
	 *            值
	 */
	void setCreateDate(DateTime value);

	/**
	 * 获取-创建时间
	 * 
	 * @return 值
	 */
	Short getCreateTime();

	/**
	 * 设置-创建时间
	 * 
	 * @param value
	 *            值
	 */
	void setCreateTime(Short value);

	/**
	 * 获取-修改日期
	 * 
	 * @return 值
	 */
	DateTime getUpdateDate();

	/**
	 * 设置-修改日期
	 * 
	 * @param value
	 *            值
	 */
	void setUpdateDate(DateTime value);

	/**
	 * 获取-修改时间
	 * 
	 * @return 值
	 */
	Short getUpdateTime();

	/**
	 * 设置-修改时间
	 * 
	 * @param value
	 *            值
	 */
	void setUpdateTime(Short value);

	/**
	 * 获取-数据源
	 * 
	 * @return 值
	 */
	String getDataSource();

	/**
	 * 设置-数据源
	 * 
	 * @param value
	 *            值
	 */
	void setDataSource(String value);

	/**
	 * 获取-实例号（版本）
	 * 
	 * @return 值
	 */
	Integer getLogInst();

	/**
	 * 设置-实例号（版本）
	 * 
	 * @param value
	 *            值
	 */
	void setLogInst(Integer value);

	/**
	 * 获取-服务系列
	 * 
	 * @return 值
	 */
	Integer getSeries();

	/**
	 * 设置-服务系列
	 * 
	 * @param value
	 *            值
	 */
	void setSeries(Integer value);

	/**
	 * 获取-创建用户
	 * 
	 * @return 值
	 */
	Integer getCreateUserSign();

	/**
	 * 设置-创建用户
	 * 
	 * @param value
	 *            值
	 */
	void setCreateUserSign(Integer value);

	/**
	 * 获取-修改用户
	 * 
	 * @return 值
	 */
	Integer getUpdateUserSign();

	/**
	 * 设置-修改用户
	 * 
	 * @param value
	 *            值
	 */
	void setUpdateUserSign(Integer value);

	/**
	 * 获取-创建动作标识
	 * 
	 * @return 值
	 */
	String getCreateActionId();

	/**
	 * 设置-创建动作标识
	 * 
	 * @param value
	 *            值
	 */
	void setCreateActionId(String value);

	/**
	 * 获取-更新动作标识
	 * 
	 * @return 值
	 */
	String getUpdateActionId();

	/**
	 * 设置-更新动作标识
	 * 
	 * @param value
	 *            值
	 */
	void setUpdateActionId(String value);

}
