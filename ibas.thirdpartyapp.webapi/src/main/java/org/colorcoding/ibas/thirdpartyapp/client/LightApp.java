package org.colorcoding.ibas.thirdpartyapp.client;

import java.util.Properties;

import org.colorcoding.ibas.bobas.common.IOperationResult;

public class LightApp extends WebApp {

	@Override
	public <P> IOperationResult<P> execute(String instruct, Properties params) throws ApplicationException {
		throw new ApplicationException("not implemented.");
	}
}
