package com.lbh.openapi.exception;

import org.springframework.http.HttpStatus;

public enum StatusCode {
	OK(HttpStatus.OK.value(), "OK"),
	BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST"),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "UNAUTHORIZED"),
	FORBIDDEN(HttpStatus.NOT_FOUND.value(), "FORBIDDEN"),
	NOT_FOUND(HttpStatus.NOT_FOUND.value(), "NOT_FOUND"),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "METHOD_NOT_ALLOWED"),
	NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE.value(), "NOT_ACCEPTABLE"),
	PROXY_AUTHENTICATION_REQUIRED(HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value(), "PROXY_AUTHENTICATION_REQUIRED"),
	REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT.value(), "REQUEST_TIMEOUT"),
	CONFLICT (HttpStatus.CONFLICT.value(), "CONFLICT"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR"),
	NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED.value(), "NOT_IMPLEMENTED"),
	BAD_GATEWAY(HttpStatus.BAD_GATEWAY.value(), "BAD_GATEWAY");
	
	public int code;
	public String message;

	StatusCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public static StatusCode parseCode(int value) {
		if (StatusCode.OK.equals(value)) {
			return OK;
		} else if (StatusCode.BAD_REQUEST.equals(value)) {
			return BAD_REQUEST;
		} else if (StatusCode.UNAUTHORIZED.equals(value)) {
			return UNAUTHORIZED;
		} else if (StatusCode.FORBIDDEN.equals(value)) {
			return FORBIDDEN;
		} else if (StatusCode.NOT_FOUND.equals(value)) {
			return NOT_FOUND;
		} else if (StatusCode.METHOD_NOT_ALLOWED.equals(value)) {
			return METHOD_NOT_ALLOWED;
		} else if (StatusCode.PROXY_AUTHENTICATION_REQUIRED.equals(value)) {
			return PROXY_AUTHENTICATION_REQUIRED;
		} else if (StatusCode.REQUEST_TIMEOUT.equals(value)) {
			return REQUEST_TIMEOUT;
		} else if (StatusCode.CONFLICT.equals(value)) {
			return CONFLICT;
		} else if (StatusCode.INTERNAL_SERVER_ERROR.equals(value)) {
			return INTERNAL_SERVER_ERROR;
		} else if (StatusCode.NOT_IMPLEMENTED.equals(value)) {
			return NOT_IMPLEMENTED;
		} else if (StatusCode.BAD_GATEWAY.equals(value)) {
			return BAD_GATEWAY;
		} else {
			return BAD_REQUEST;
		}
	}
	
	
	public static String parseMessage(int code) {
		if (StatusCode.OK.equals(code)) {
			return OK.message;
		} else if (StatusCode.BAD_REQUEST.equals(code)) {
			return BAD_REQUEST.message;
		} else if (StatusCode.UNAUTHORIZED.equals(code)) {
			return UNAUTHORIZED.message;
		} else if (StatusCode.FORBIDDEN.equals(code)) {
			return FORBIDDEN.message;
		} else if (StatusCode.NOT_FOUND.equals(code)) {
			return NOT_FOUND.message;
		} else if (StatusCode.METHOD_NOT_ALLOWED.equals(code)) {
			return METHOD_NOT_ALLOWED.message;
		} else if (StatusCode.PROXY_AUTHENTICATION_REQUIRED.equals(code)) {
			return PROXY_AUTHENTICATION_REQUIRED.message;
		} else if (StatusCode.REQUEST_TIMEOUT.equals(code)) {
			return REQUEST_TIMEOUT.message;
		} else if (StatusCode.CONFLICT.equals(code)) {
			return CONFLICT.message;
		} else if (StatusCode.INTERNAL_SERVER_ERROR.equals(code)) {
			return INTERNAL_SERVER_ERROR.message;
		} else if (StatusCode.NOT_IMPLEMENTED.equals(code)) {
			return NOT_IMPLEMENTED.message;
		} else if (StatusCode.BAD_GATEWAY.equals(code)) {
			return BAD_GATEWAY.message;
		} else {
			return BAD_REQUEST.message;
		}
	}

	public boolean equals(int code) {
		if (this.code == code) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals(String message) {
		if (this.message.equals(message)) {
			return true;
		} else {
			return false;
		}
	}
}
