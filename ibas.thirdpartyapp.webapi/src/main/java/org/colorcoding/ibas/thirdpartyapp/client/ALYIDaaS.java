package org.colorcoding.ibas.thirdpartyapp.client;

import javax.json.JsonObject;
import javax.json.JsonValue;

public class ALYIDaaS extends OIDC {

	@Override
	protected String userNameOf(JsonObject result) {
		JsonValue jsonValue = result.getJsonObject("preferred_username");
		if (jsonValue != null) {
			return jsonValue.toString();
		}
		return null;
	}

}
