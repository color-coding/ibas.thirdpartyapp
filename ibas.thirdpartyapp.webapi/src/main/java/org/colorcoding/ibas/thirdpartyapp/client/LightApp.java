package org.colorcoding.ibas.thirdpartyapp.client;

import java.util.Map;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.thirdpartyapp.bo.user.IUser;

public class LightApp extends WebApp {

	@Override
	protected IUser fetchUser(Map<String, Object> params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <P> IOperationResult<P> execute(String instruct, Map<String, Object> params) throws NotImplementedException {
		throw new NotImplementedException();
	}
}
