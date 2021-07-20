package org.colorcoding.ibas.thirdpartyapp.client;

import java.util.Properties;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;

public class LightApp extends WebApp {

	@Override
	protected IUserMapping fetchUser(Properties params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <P> IOperationResult<P> execute(String instruct, Properties params) throws NotImplementedException {
		throw new NotImplementedException();
	}
}
