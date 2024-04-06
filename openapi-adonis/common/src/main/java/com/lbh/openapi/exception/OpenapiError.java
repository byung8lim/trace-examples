package com.lbh.openapi.exception;

public class OpenapiError {
	private int code;
	private String message;
	
	public int getCode() {
		return this.code;
	}
	
	public OpenapiError setCode(int code) {
		this.code = code;
		return this;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public OpenapiError setMessage(String message) {
		this.message = message;
		return this;
	}
}
