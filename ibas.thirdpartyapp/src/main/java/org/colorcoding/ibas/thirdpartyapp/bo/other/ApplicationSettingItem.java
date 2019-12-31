package org.colorcoding.ibas.thirdpartyapp.bo.other;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.colorcoding.ibas.bobas.serialization.Serializable;
import org.colorcoding.ibas.thirdpartyapp.data.emConfigItemCategory;

@XmlAccessorType(XmlAccessType.NONE)
public class ApplicationSettingItem extends Serializable {

	private static final long serialVersionUID = 1059078765093164250L;

	private ApplicationSetting parent;

	protected final ApplicationSetting getParent() {
		return parent;
	}

	final void setParent(ApplicationSetting parent) {
		this.parent = parent;
	}

	@XmlElement(name = "Name")
	private String name;

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "Description")
	private String description;

	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name = "Category")
	private emConfigItemCategory category;

	public final emConfigItemCategory getCategory() {
		return category;
	}

	public final void setCategory(emConfigItemCategory category) {
		this.category = category;
	}

	@XmlElement(name = "Value")
	private String value;

	public final String getValue() {
		if (this.getCategory() == emConfigItemCategory.PASSWORD) {
			return this.encryptValue(this.value);
		} else {
			return this.value;
		}
	}

	public final void setValue(String value) {
		if (this.getCategory() == emConfigItemCategory.PASSWORD) {
			this.value = this.encryptValue(value);
		} else {
			this.value = value;
		}
	}

	protected String encryptValue(String value) {
		if (value == null || value.isEmpty()) {
			return value;
		}
		if (this.getParent() == null) {
			return value;
		}
		String secretKey = this.getParent().getSecretKey();
		if (secretKey == null || secretKey.isEmpty()) {
			return value;
		}
		char[] charValues = this.toChars(value, -1);
		char[] charSecret = this.toChars(secretKey, charValues.length);
		for (int i = 0; i < charValues.length; i++) {
			charValues[i] = (char) (charValues[i] ^ charSecret[i]);
		}
		return new String(charValues);
	}

	private char[] toChars(String value, int size) {
		char[] charValues = value.toCharArray();
		if (size > 0) {
			char[] chars = new char[size];
			int j = -1;
			for (int i = 0; i < size; i++) {
				j++;
				if (j > charValues.length) {
					j = 0;
				}
				chars[i] = charValues[j];
			}
			charValues = chars;
		}
		return charValues;
	}

	@Override
	public String toString() {
		return String.format("{applicationSettingItem: %s}", this.name);
	}

}
