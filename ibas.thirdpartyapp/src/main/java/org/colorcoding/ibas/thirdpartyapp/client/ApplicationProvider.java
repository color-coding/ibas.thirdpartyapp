package org.colorcoding.ibas.thirdpartyapp.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应用客户端提供者标识。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationProvider {

	/**
	 * 应用编码。
	 *
	 * @return 应用编码
	 */
	String value();
}
