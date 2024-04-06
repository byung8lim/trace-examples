package com.lbh.openapi.domain;

public class ResultExtra {
	private String name;
	private String result;
	
	public String getName() {return this.name;}
	public ResultExtra setName(String name) {
		this.name = name;
		return this;
	}
	public String getResult() {return this.result;}
	public ResultExtra setResult(String result) {
		this.result = result;
		return this;
	}
}
