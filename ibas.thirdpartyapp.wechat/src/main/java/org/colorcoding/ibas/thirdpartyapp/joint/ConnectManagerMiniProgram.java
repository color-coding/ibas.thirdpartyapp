package org.colorcoding.ibas.thirdpartyapp.joint;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import org.colorcoding.ibas.bobas.common.*;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.initialfantasy.repository.BORepositoryInitialFantasy;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.user.IUser;
import org.colorcoding.ibas.thirdpartyapp.bo.user.User;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConnectManagerMiniProgram extends ConnectManager {
    protected static final String MSG_CONNECTING_URL = "connectManager: open url [%s].";
    /**
     * 配置项目-用户编号系列
     */
    public final static String CONFIG_ITEM_USER_SERIES = "UserSeries";
    /**
     * 参数名称-授权码
     */
    public static final String PARAM_NAME_CODE = "code";
    /**
     * 参数名称-用户昵称
     */
    public static final String PARAM_NICKNAME = "nickname";
    /**
     * 地址模板-用户认证
     */
    public static final String URL_TEMPLATE_OAUTH = "https://api.weixin.qq.com/sns/jscode2session?appid=${AppId}&secret=${AppSecret}&js_code=${Code}&grant_type=authorization_code";

    @Override
    protected IUser getUser(Map<String, Object> params) throws Exception {
        if (params.get(PARAM_NAME_CODE) == null) {
            throw new Exception(I18N.prop("msg_tpa_no_param", PARAM_NAME_CODE));
        }
        String app = String.valueOf(params.get(PARAM_NAME_APP_CODE));
        String url = this.applyVariables(URL_TEMPLATE_OAUTH, params);
        JsonNode data = this.doGet(url);
        if (data == null) {
            throw new BadRequestException(I18N.prop("msg_tpa_faild_oauth_request"));
        }
        JsonNode errNode = data.get("errmsg");
        if (errNode != null) {
            throw new BadRequestException(errNode.textValue());
        }
        IUser user = null;
        ICriteria criteria = new Criteria();
        ICondition condition = criteria.getConditions().create();
        condition.setAlias(User.PROPERTY_APPLICATION.getName());
        condition.setValue(app);
        condition = criteria.getConditions().create();
        condition.setAlias(User.PROPERTY_ACTIVATED.getName());
        condition.setValue(emYesNo.YES);
        condition = criteria.getConditions().create();
        try {
            // 使用unionid查询用户
            condition.setAlias(User.PROPERTY_MAPPEDID.getName());
            condition.setValue(this.nodeValue(data, "unionid"));
            params.put("UnionId", condition.getValue());
            params.put("OpenId", this.nodeValue(data, "openid"));
            user = this.getUser(criteria);
        } catch (Exception e) {
            // 使用openid查询用户
            condition.setAlias(User.PROPERTY_MAPPEDUSER.getName());
            condition.setValue(this.nodeValue(data, "openid"));
            params.put("OpenId", condition.getValue());
            user = this.getUser(criteria);
        }
        if (user == null) {
            user = this.createUser(params);
        }
        return user;
    }

    protected String nodeValue(JsonNode data, String name) throws BadRequestException {
        JsonNode node = data.get(name);
        if (node == null) {
            throw new BadRequestException(I18N.prop("msg_tpa_no_return_value", name));
        }
        return node.textValue();
    }

    protected IUser getUser(ICriteria criteria) throws Exception {
        BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
        boRepository.setRepository(this.getRepository());
        boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
        IOperationResult<IUser> operationResult = boRepository.fetchUser(criteria);
        if (operationResult.getError() != null) {
            throw operationResult.getError();
        }
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

    protected IUser createUser(Map<String, Object> params) throws Exception {

        // 查询别的应用是否已映射用户
        IUser existUser = null;
        try {
            ICriteria criteria = new Criteria();
            ICondition condition = criteria.getConditions().create();
            condition.setAlias(User.PROPERTY_APPLICATION.getName());
            condition.setOperation(ConditionOperation.NOT_EQUAL);
            condition.setValue(String.valueOf(params.get(PARAM_NAME_APP_CODE)));
            condition = criteria.getConditions().create();
            condition.setAlias(User.PROPERTY_ACTIVATED.getName());
            condition.setValue(emYesNo.YES);
            condition = criteria.getConditions().create();
            condition.setAlias(User.PROPERTY_MAPPEDID.getName());
            condition.setValue(String.valueOf(params.get("UnionId")));
            existUser = this.getUser(criteria);
        } catch (Exception e) {
        }
        String code;
        if (existUser == null) {
            // 创建系统用户
            org.colorcoding.ibas.initialfantasy.bo.organization.User userIF = new org.colorcoding.ibas.initialfantasy.bo.organization.User();
            userIF.setSeries(MyConfiguration.getConfigValue(CONFIG_ITEM_USER_SERIES, 1));// 编号系列
            userIF.setName((String.valueOf(params.get(PARAM_NICKNAME))));
            userIF.setActivated(emYesNo.YES);
            BORepositoryInitialFantasy boRepositoryIF = new BORepositoryInitialFantasy();
            boRepositoryIF.setRepository(this.getRepository());
            boRepositoryIF.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
            IOperationResult<org.colorcoding.ibas.initialfantasy.bo.organization.IUser> opRsltIF = boRepositoryIF
                    .saveUser(userIF);
            if (opRsltIF.getError() != null) {
                throw opRsltIF.getError();
            }
            code = userIF.getCode();
        } else {
            code = existUser.getUser();
        }
        // 创建应用用户
        IUser userTA = new User();
        userTA.setUser(code);
        userTA.setApplication(String.valueOf(params.get(PARAM_NAME_APP_CODE)));
        userTA.setActivated(emYesNo.YES);
        userTA.setMappedUser(String.valueOf(params.get("OpenId")));
        try {
            userTA.setMappedId(String.valueOf(params.get("UnionId")));
        } catch (Exception e) {
        }
        BORepositoryThirdPartyApp boRepositoryTA = new BORepositoryThirdPartyApp();
        boRepositoryTA.setRepository(this.getRepository());
        boRepositoryTA.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
        IOperationResult<IUser> opRsltTA = boRepositoryTA.saveUser(userTA);
        if (opRsltTA.getError() != null) {
            throw opRsltTA.getError();
        }
        return opRsltTA.getResultObjects().firstOrDefault();
    }
}
