package org.colorcoding.ibas.thirdpartyapp.client;

public class AuthenticationException extends Exception {

	private static final long serialVersionUID = -8630668493398522261L;

	public AuthenticationException() {
		super();
	}

	public AuthenticationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}

}
