package org.colorcoding.ibas.thirdpartyapp.bo.other;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collection;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;

@XmlType(name = "ApplicationSettingItems", namespace = MyConfiguration.NAMESPACE_BO)
@XmlSeeAlso({ ApplicationSettingItem.class })
public class ApplicationSettingItems extends ArrayList<ApplicationSettingItem> {

	private static final long serialVersionUID = 6716302995249784804L;

	public ApplicationSettingItems() {
	}

	public ApplicationSettingItems(ApplicationSetting parent) {
		this();
		this.setParent(parent);
	}

	private ApplicationSetting parent;

	protected final ApplicationSetting getParent() {
		return parent;
	}

	private final void setParent(ApplicationSetting parent) {
		this.parent = parent;
	}

	@Override
	public boolean add(ApplicationSettingItem e) {
		if (super.add(e)) {
			e.setParent(this.getParent());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void add(int index, ApplicationSettingItem element) {
		super.add(index, element);
		element.setParent(this.getParent());
	}

	public ApplicationSettingItem create() {
		ApplicationSettingItem item = new ApplicationSettingItem();
		this.add(item);
		return item;
	}

	public String encode() throws Exception {
		ISerializer<?> serializer = SerializerFactory.create().createManager().create("json");
		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			serializer.serialize(this, writer, ApplicationSettingItem.class);
			return Base64.getEncoder().encodeToString(writer.toByteArray());
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public void decode(String value) throws Exception {
		ISerializer<?> serializer = SerializerFactory.create().createManager().create("json");
		try (InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(value))) {
			Object settings = serializer.deserialize(inputStream, ApplicationSettingItem.class);
			if (settings instanceof Collection) {
				for (ApplicationSettingItem item : (Collection<ApplicationSettingItem>) settings) {
					ApplicationSettingItem cItem = this
							.firstOrDefault(c -> c.getName() != null && c.getName().equals(item.getName()));
					if (cItem != null) {
						cItem.setValue(item.getValue());
					} else {
						this.add(item);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
}