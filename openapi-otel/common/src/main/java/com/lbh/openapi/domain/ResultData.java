package com.lbh.openapi.domain;

import java.util.List;

public class ResultData {
	private String operandA;
	private String operandB;
	private String value;
	private List<ResultExtra> extention;
	
	public String getOperandA() {
		return operandA;
	}
	public void setOperandA(String operandA) {
		this.operandA = operandA;
	}
	public String getOperandB() {
		return operandB;
	}
	public void setOperandB(String operandB) {
		this.operandB = operandB;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<ResultExtra> getExtention() {
		return extention;
	}
	public void setExtention(List<ResultExtra> extention) {
		this.extention = extention;
	}
}
