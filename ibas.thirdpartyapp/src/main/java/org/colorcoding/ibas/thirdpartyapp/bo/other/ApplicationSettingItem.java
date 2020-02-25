package org.colorcoding.ibas.thirdpartyapp.bo.other;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.colorcoding.ibas.bobas.serialization.Serializable;
import org.colorcoding.ibas.bobas.util.Encrypt;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
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
			return this.decryptValue(this.value);
		} else if (this.getCategory() == emConfigItemCategory.FILE) {
			if (this.value != null && this.value.indexOf(File.separator) < 0) {
				File file = new File(MyConfiguration.getFileFolder(), this.value);
				return file.getPath();
			}
		}
		return this.value;
	}

	public final void setValue(String value) {
		if (this.getCategory() == emConfigItemCategory.PASSWORD) {
			if (this.decryptValue(value) == null) {
				// 返回空，则表示未加密
				value = this.encryptValue(value);
			}
		}
		this.value = value;
	}

	protected String encryptValue(String value) {
		try {
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
			byte[] valueMdBytes, valueBytes, secretBytes, tmpValues;
			MessageDigest digest = MessageDigest.getInstance("MD5");
			valueBytes = this.toBytes(value, -1);
			digest.update(valueBytes);
			valueMdBytes = digest.digest();
			secretBytes = this.toBytes(secretKey, valueBytes.length);
			for (int i = 0; i < valueBytes.length; i++) {
				valueBytes[i] = (byte) (valueBytes[i] ^ secretBytes[i]);
			}
			// 合并md5数组
			tmpValues = new byte[valueBytes.length + valueMdBytes.length];
			System.arraycopy(valueMdBytes, 0, tmpValues, 0, valueMdBytes.length);
			System.arraycopy(valueBytes, 0, tmpValues, valueMdBytes.length, valueBytes.length);
			return Encrypt.toHexString(tmpValues);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String decryptValue(String value) {
		try {
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
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] valueMdBytes, valueBytes, secretBytes, tmpValues;
			valueBytes = Encrypt.toBytes(value);
			if (valueBytes != null && valueBytes.length > 16) {
				// 判断是否已加密，前16字节为值的md信息
				valueMdBytes = new byte[16];
				System.arraycopy(valueBytes, 0, valueMdBytes, 0, valueMdBytes.length);
				tmpValues = new byte[valueBytes.length - valueMdBytes.length];
				System.arraycopy(valueBytes, valueMdBytes.length, tmpValues, 0, tmpValues.length);
				secretBytes = this.toBytes(secretKey, tmpValues.length);
				for (int i = 0; i < tmpValues.length; i++) {
					tmpValues[i] = (byte) (tmpValues[i] ^ secretBytes[i]);
				}
				digest.update(tmpValues);
				if (this.equals(valueMdBytes, digest.digest())) {
					// 已加密
					return new String(tmpValues, "utf-8");
				}
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean equals(byte[] a, byte[] b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.length != b.length) {
			return false;
		}
		boolean equals = true;
		for (int i = 0; i < a.length; i++) {
			if (Byte.compare(a[i], b[i]) != 0) {
				equals = false;
				break;
			}
		}
		return equals;
	}

	private byte[] toBytes(String value, int size) throws UnsupportedEncodingException {
		byte[] byteValues = value == null ? new byte[] {} : value.getBytes("utf-8");
		if (size > 0) {
			byte[] bytes = new byte[size];
			int j = -1;
			for (int i = 0; i < size; i++) {
				j++;
				if (j > byteValues.length - 1) {
					j = 0;
				}
				bytes[i] = byteValues[j];
			}
			byteValues = bytes;
		}
		return byteValues;
	}

	@Override
	public String toString() {
		return String.format("{applicationSettingItem: %s}", this.name);
	}

}
