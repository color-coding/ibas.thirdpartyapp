package org.colorcoding.ibas.thirdpartyapp.bo.user;

import org.colorcoding.ibas.bobas.bo.*;
import org.colorcoding.ibas.bobas.data.*;

/**
* 用户 接口
* 
*/
public interface IUser extends IBOSimple {

    /**
    * 获取-用户
    * 
    * @return 值
    */
    String getUser();

    /**
    * 设置-用户
    * 
    * @param value 值
    */
    void setUser(String value);


    /**
    * 获取-应用
    * 
    * @return 值
    */
    String getApplication();

    /**
    * 设置-应用
    * 
    * @param value 值
    */
    void setApplication(String value);


    /**
    * 获取-激活
    * 
    * @return 值
    */
    emYesNo getActivated();

    /**
    * 设置-激活
    * 
    * @param value 值
    */
    void setActivated(emYesNo value);


    /**
    * 获取-映射标记
    * 
    * @return 值
    */
    String getMappedId();

    /**
    * 设置-映射标记
    * 
    * @param value 值
    */
    void setMappedId(String value);


    /**
    * 获取-映射用户
    * 
    * @return 值
    */
    String getMappedUser();

    /**
    * 设置-映射用户
    * 
    * @param value 值
    */
    void setMappedUser(String value);


    /**
    * 获取-映射密码
    * 
    * @return 值
    */
    String getMappedPassword();

    /**
    * 设置-映射密码
    * 
    * @param value 值
    */
    void setMappedPassword(String value);


    /**
    * 获取-对象编号
    * 
    * @return 值
    */
    Integer getObjectKey();

    /**
    * 设置-对象编号
    * 
    * @param value 值
    */
    void setObjectKey(Integer value);


    /**
    * 获取-对象类型
    * 
    * @return 值
    */
    String getObjectCode();

    /**
    * 设置-对象类型
    * 
    * @param value 值
    */
    void setObjectCode(String value);


    /**
    * 获取-实例号
    * 
    * @return 值
    */
    Integer getLogInst();

    /**
    * 设置-实例号
    * 
    * @param value 值
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
    * @param value 值
    */
    void setSeries(Integer value);


    /**
    * 获取-数据源
    * 
    * @return 值
    */
    String getDataSource();

    /**
    * 设置-数据源
    * 
    * @param value 值
    */
    void setDataSource(String value);


    /**
    * 获取-创建日期
    * 
    * @return 值
    */
    DateTime getCreateDate();

    /**
    * 设置-创建日期
    * 
    * @param value 值
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
    * @param value 值
    */
    void setCreateTime(Short value);


    /**
    * 获取-更新日期
    * 
    * @return 值
    */
    DateTime getUpdateDate();

    /**
    * 设置-更新日期
    * 
    * @param value 值
    */
    void setUpdateDate(DateTime value);


    /**
    * 获取-更新时间
    * 
    * @return 值
    */
    Short getUpdateTime();

    /**
    * 设置-更新时间
    * 
    * @param value 值
    */
    void setUpdateTime(Short value);


    /**
    * 获取-创建用户
    * 
    * @return 值
    */
    Integer getCreateUserSign();

    /**
    * 设置-创建用户
    * 
    * @param value 值
    */
    void setCreateUserSign(Integer value);


    /**
    * 获取-更新用户
    * 
    * @return 值
    */
    Integer getUpdateUserSign();

    /**
    * 设置-更新用户
    * 
    * @param value 值
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
    * @param value 值
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
    * @param value 值
    */
    void setUpdateActionId(String value);



}
