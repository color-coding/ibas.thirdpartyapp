package org.colorcoding.ibas.thirdpartyapp.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.common.Value;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;

@XmlType(namespace = MyConfiguration.NAMESPACE_BO)
public enum emConfigItemCategory {
	/**
	 * 文本
	 */
	@Value("T")
	TEXT,
	/**
	 * 密码
	 */
	@Value("P")
	PASSWORD,
	/**
	 * 文件
	 */
	@Value("F")
	FILE,
}
