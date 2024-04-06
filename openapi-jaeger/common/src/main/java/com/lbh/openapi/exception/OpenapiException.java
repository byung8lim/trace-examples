package com.lbh.openapi.exception;

/**
 * 
 *
 */
public class OpenapiException extends Exception {

	private static final long serialVersionUID = 2800990333710490224L;
	private int code;
	
	/**
	 */
	public OpenapiException() {
		super();
		code = StatusCode.OK.code;
	}

	/**
	 * @param message
	 */
	public OpenapiException(String message) {
		super(message);
		code = StatusCode.INTERNAL_SERVER_ERROR.code;
	}

	/**
	 * @param e
	 */
	public OpenapiException(Exception e) {
		super(e);
		code = StatusCode.INTERNAL_SERVER_ERROR.code;
	}
	
	/**
	 * @param message
	 * @param statusCode
	 */
	public OpenapiException(String message, int value) {
		super(message);
		setCode(value);
	}

	public OpenapiException(int code) {
		super(StatusCode.parseMessage(code));
		this.code = code;
	}

	/**
	 * @param statusCode
	 */
	public OpenapiException setCode(int code) {
		this.code = code;
		return this;
	}
	
	/**
	 * @return int
	 */
	public int getCode() {
		return this.code;
	}
	
	/**
	 * @return String
	 */
	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
