package org.colorcoding.ibas.thirdpartyapp.joint;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.BadRequestException;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.IKeyText;
import org.colorcoding.ibas.bobas.data.KeyText;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.user.IUser;
import org.colorcoding.ibas.thirdpartyapp.bo.user.User;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConnectManagerWechat extends ConnectManager {

	protected static final String MSG_CONNECTING_URL = "connectManager: open url [%s].";
	/**
	 * 参数名称-授权码
	 */
	public static final String PARAM_NAME_CODE = "code";
	/**
	 * 地址模板-用户认证
	 */
	public static final String URL_TEMPLATE_OAUTH = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=${AppId}&secret=${AppSecret}&code=${Code}&grant_type=authorization_code";
	/**
	 * 地址模板-用户信息
	 */
	public static final String URL_TEMPLATE_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token=${AccessToken}&openid=${OpenId}&lang=zh_CN";

	@Override
	protected IUser getUser(Map<String, Object> params) throws Exception {
		if (params.get(PARAM_NAME_CODE) == null) {
			throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_CODE));
		}
		String app = String.valueOf(params.get(PARAM_NAME_APP_CODE));
		String url = MyConfiguration.applyVariables(URL_TEMPLATE_OAUTH, new Iterator<IKeyText>() {

			Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();

			@Override
			public IKeyText next() {
				Entry<String, Object> item = iterator.next();
				return new KeyText(item.getKey(), String.valueOf(item.getValue()));
			}

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
		});
		JsonNode data = this.doGet(url);
		if (data == null) {
			throw new BadRequestException(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		// 获取返回值
		JsonNode node = data.get("openid");
		if (node == null) {
			throw new BadRequestException(I18N.prop("msg_tpa_faild_oauth_request"));
		}
		return this.getUser(app, node.textValue());
	}

	protected IUser getUser(String app, String userId) throws Exception {
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_MAPPEDUSER.getName());
		condition.setValue(userId);
		condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_ACTIVATED.getName());
		condition.setValue(emYesNo.YES);
		condition = criteria.getConditions().create();
		condition.setAlias(User.PROPERTY_APPLICATION.getName());
		condition.setValue(app);
		BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
		boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
		IOperationResult<IUser> operationResult = boRepository.fetchUser(criteria);
		return operationResult.getResultObjects().firstOrDefault();
	}

	protected JsonNode doGet(String url) throws IOException {
		Logger.log(MessageLevel.DEBUG, MSG_CONNECTING_URL, url);
		URL realUrl = new URL(url);
		// 打开和URL之间的连接
		URLConnection connection = realUrl.openConnection();
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		// 建立实际的连接
		connection.connect();
		return new ObjectMapper().readTree(connection.getInputStream());
	}
}
