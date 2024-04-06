package com.lbh.openapi.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.lbh.openapi.auth.domain.AppKey;

@Entity
@Table(name = "APPKEY")
public class AppKeyEntity {
	@Id
	private String appKey;
	
	@Column(nullable = false)
	private String type;
	
    @Column(nullable = false)
	private String appName;
    
    @Column(nullable = false)
	private String orgNo;

    public AppKeyEntity() {
    	
    }
    
	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	
	public AppKeyEntity(AppKey appKey) {
		this.appKey = appKey.getAppKey();
		this.appName = appKey.getAppName();
		this.orgNo = appKey.getOrgNo();
		this.type = appKey.getType();
	}
	
	public AppKey toAppKey( ) {
		AppKey ret = new AppKey();
		ret.setAppKey(this.appKey);
		ret.setAppName(this.appName);
		ret.setOrgNo(this.orgNo);
		ret.setType(this.type);
		return ret;
	}	
}
