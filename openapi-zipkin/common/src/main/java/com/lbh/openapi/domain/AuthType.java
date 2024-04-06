package com.lbh.openapi.domain;

public enum AuthType {
	BASIC(1, "BASIC"),
	SAML(2, "SAML"),
	NONE(10, "NONE");
	
	public int type;
	public String name;
	
	AuthType(int type, String name) {
		this.type = type;
		this.name = name;
	}

	public boolean equals(int type) {
		if (this.type == type) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean equals(String name) {
		if (name.equals(this.name)) {
			return true;
		} else {
			return false;
		}
	}
}
