package org.colorcoding.ibas.thirdpartyapp.client;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.IKeyText;
import org.colorcoding.ibas.bobas.data.KeyText;
import org.colorcoding.ibas.initialfantasy.bo.shell.User;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSetting;
import org.colorcoding.ibas.thirdpartyapp.data.DataConvert;

public abstract class ApplicationClient {

	private ApplicationSetting setting;

	private final ApplicationSetting getSetting() {
		if (this.setting == null) {
			throw new ParameterException("Not found Setting.");
		}
		return this.setting;
	}

	final void setSetting(ApplicationSetting setting) {
		this.setting = setting;
	}

	public final String getName() {
		return this.getSetting().getName();
	}

	public final String getDescription() {
		return this.getSetting().getDescription();
	}

	/**
	 * 获取设置的参数值
	 * 
	 * @param <T>          返回类型
	 * @param name         值名称
	 * @param defaultValue 默认值
	 * @return
	 */
	protected final <T> T paramValue(String name, T defaultValue) {
		return this.getSetting().paramValue(name, defaultValue);
	}

	/**
	 * 获集合中的参数值
	 * 
	 * @param <T>          返回类型
	 * @param name         值名称
	 * @param defaultValue 默认值
	 * @param params       取值集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected final <T> T paramValue(String name, T defaultValue, Map<String, Object> params) {
		if (params == null || params.isEmpty()) {
			return defaultValue;
		}
		if (params.containsKey(name)) {
			Object value = params.get(name);
			if (value == null) {
				return defaultValue;
			}
			if (defaultValue == null) {
				return (T) value;
			} else {
				return (T) DataConvert.convert(defaultValue.getClass(), value);
			}
		}
		return defaultValue;
	}

	/**
	 * 应用变量
	 * 
	 * @param template 模板，变量${XXXX}
	 * @param params   变量，name-value
	 * @return
	 */
	protected final String applyVariables(String template, Map<String, Object> params) {
		return MyConfiguration.applyVariables(template, new Iterator<IKeyText>() {

			Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();

			@Override
			public IKeyText next() {
				Entry<String, Object> item = iterator.next();
				return new KeyText(item.getKey(), item.getValue() == null ? "" : String.valueOf(item.getValue()));
			}

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
		});
	}

	@Override
	public String toString() {
		return String.format("{%s %s}", "AppClient", this.getName());
	}

	/**
	 * 认证用户
	 * 
	 * @param params 参数
	 * @return 识别的用户
	 * @throws Exception
	 */
	public abstract User authenticate(Map<String, Object> params) throws AuthenticationException;

	/**
	 * 执行指令
	 * 
	 * @param <P>      返回值类型
	 * @param instruct 指令名称
	 * @param params   参数
	 * @throws Exception 未实现
	 * @return 操作结果
	 */
	public abstract <P> IOperationResult<P> execute(String instruct, Map<String, Object> params)
			throws NotImplementedException;
}
