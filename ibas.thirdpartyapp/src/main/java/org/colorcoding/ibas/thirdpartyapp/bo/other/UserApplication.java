package org.colorcoding.ibas.thirdpartyapp.bo.other;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.core.Serializable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "UserApplication")
@XmlRootElement(name = "UserApplication")
public class UserApplication extends Serializable {

	private static final long serialVersionUID = -3351635492148194096L;

	/**
	 * 参数名称-应用地址
	 */
	public static final String PARAM_NAME_APP_URL = "AppUrl";

	private String code;

	@XmlElement(name = "Code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	private String name;

	@XmlElement(name = "Name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String url;

	@XmlElement(name = "Url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String user;

	@XmlElement(name = "User")
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return String.format("{app: %s %s}", this.getName(), this.getUser());
	}

}
