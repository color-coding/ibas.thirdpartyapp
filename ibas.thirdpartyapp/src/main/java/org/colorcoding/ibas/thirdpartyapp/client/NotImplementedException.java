package org.colorcoding.ibas.thirdpartyapp.client;

public class NotImplementedException extends Exception {

	private static final long serialVersionUID = 7519825555035916452L;

	public NotImplementedException() {
		super();
	}

	public NotImplementedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotImplementedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotImplementedException(String message) {
		super(message);
	}

	public NotImplementedException(Throwable cause) {
		super(cause);
	}

}
