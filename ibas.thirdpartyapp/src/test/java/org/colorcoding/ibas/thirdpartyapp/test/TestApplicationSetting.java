package org.colorcoding.ibas.thirdpartyapp.test;

import java.lang.reflect.Field;
import java.util.UUID;

import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializationFactory;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSetting;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSettingItem;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSettingItems;
import org.colorcoding.ibas.thirdpartyapp.data.emConfigItemCategory;

import junit.framework.TestCase;

public class TestApplicationSetting extends TestCase {

	public void testEncrypt()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field valueField = ApplicationSettingItem.class.getDeclaredField("value");
		valueField.setAccessible(true);
		String value = "niuren.zhu";
		ApplicationSetting setting = new ApplicationSetting();
		setting.setSecretKey(UUID.randomUUID().toString());
		ApplicationSettingItem settingItem = new ApplicationSettingItem();
		setting.getSettingItems().add(settingItem);
		settingItem.setName("test");
		settingItem.setCategory(emConfigItemCategory.PASSWORD);
		settingItem.setValue(value);

		System.out.println("*********************************************************");
		System.out.println(String.format("Orgion: %s", value));
		System.out.println(String.format("Encrypted: %s", valueField.get(settingItem)));
		System.out.println(String.format("Using: %s", settingItem.getValue()));
		System.out.println("*********************************************************");

		ISerializer serializer = SerializationFactory.createManager().create("json");
		ApplicationSetting nSetting = (ApplicationSetting) serializer.clone(setting, ApplicationSetting.class,
				ApplicationSettingItem.class, ApplicationSettingItems.class);
		nSetting.setSecretKey(setting.getSecretKey());
		System.out.println("*********************************************************");
		settingItem = nSetting.getSettingItems().get(0);
		System.out.println(String.format("Orgion: %s", value));
		System.out.println(String.format("Encrypted: %s", valueField.get(settingItem)));
		System.out.println(String.format("Using: %s", settingItem.getValue()));
		System.out.println("*********************************************************");
	}
}
