package org.colorcoding.ibas.thirdpartyapp.client;

import javax.json.JsonObject;
import javax.json.JsonValue;

public class SAPIAS extends OIDC {

	@Override
	protected String userNameOf(JsonObject result) {
		// 其他可用的字段有
		// user_uuid:用户主键(UUID) sub:用户编码(p000038)
		JsonValue jsonValue = result.getJsonObject("sub");
		if (jsonValue != null) {
			return jsonValue.toString();
		}
		return null;
	}

}
