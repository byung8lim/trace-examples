package com.lbh.openapi.domain;

public class ReqData {
	public DataHeader getDataHeader() {
		return dataHeader;
	}
	public void setDataHeader(DataHeader dataHeader) {
		this.dataHeader = dataHeader;
	}
	public DataBody getDataBody() {
		return dataBody;
	}
	public void setDataBody(DataBody dataBody) {
		this.dataBody = dataBody;
	}
	public InputParam getDataParam() {
		return dataParam;
	}
	public void setDataParam(InputParam dataParam) {
		this.dataParam = dataParam;
	}
	private DataHeader dataHeader;
	private DataBody dataBody;
	private InputParam dataParam;
}
