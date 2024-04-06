package com.lbh.openapi.domain;

public class AuthToken {
	private String type;
	private String appKey;
	private String dateStr;
	
	public static AuthToken fromArray(String...args) {
		return new AuthToken()
				.setType(args[0])
				.setAppKey(args[1])
				.setDateStr(args[2]);
	}
	
	public String getType() {
		return this.type;
	}
	
	public AuthToken setType(String type) {
		this.type = type;
		return this;
	}
	
	public String getAppKey() {
		return this.appKey;
	}
	
	public AuthToken setAppKey(String appKey) {
		this.appKey = appKey;
		return this;
	}
	
	public String getDateStr() {
		return this.dateStr;
	}
	
	public AuthToken setDateStr(String dateStr) {
		this.dateStr = dateStr;
		return this;
	}
}
