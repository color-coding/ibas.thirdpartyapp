package org.colorcoding.ibas.thirdpartyapp.bo.other;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.data.ArrayList;
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
}
