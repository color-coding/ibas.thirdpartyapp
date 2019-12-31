package org.colorcoding.ibas.thirdpartyapp.bo.applicationconfig;

import java.beans.PropertyChangeEvent;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.bo.BusinessObjects;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;

/**
 * 应用配置-项目 集合
 */
@XmlType(name = ApplicationConfigItems.BUSINESS_OBJECT_NAME, namespace = MyConfiguration.NAMESPACE_BO)
@XmlSeeAlso({ ApplicationConfigItem.class })
public class ApplicationConfigItems extends BusinessObjects<IApplicationConfigItem, IApplicationConfig>
		implements IApplicationConfigItems {

	/**
	 * 业务对象名称
	 */
	public static final String BUSINESS_OBJECT_NAME = "ApplicationConfigItems";

	/**
	 * 序列化版本标记
	 */
	private static final long serialVersionUID = 267271537706179239L;

	/**
	 * 构造方法
	 */
	public ApplicationConfigItems() {
		super();
	}

	/**
	 * 构造方法
	 * 
	 * @param parent 父项对象
	 */
	public ApplicationConfigItems(IApplicationConfig parent) {
		super(parent);
	}

	/**
	 * 元素类型
	 */
	public Class<?> getElementType() {
		return ApplicationConfigItem.class;
	}

	/**
	 * 创建应用配置-项目
	 * 
	 * @return 应用配置-项目
	 */
	public IApplicationConfigItem create() {
		IApplicationConfigItem item = new ApplicationConfigItem();
		if (this.add(item)) {
			return item;
		}
		return null;
	}

	@Override
	protected void afterAddItem(IApplicationConfigItem item) {
		super.afterAddItem(item);
	}

	@Override
	public ICriteria getElementCriteria() {
		ICriteria criteria = super.getElementCriteria();
		return criteria;
	}

	@Override
	protected void onParentPropertyChanged(PropertyChangeEvent evt) {
		super.onParentPropertyChanged(evt);
	}
}
