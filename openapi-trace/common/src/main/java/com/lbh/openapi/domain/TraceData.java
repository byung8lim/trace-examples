package com.lbh.openapi.domain;

import com.lbh.openapi.util.TraceUtil;

public class TraceData {
	public final static String TRACE_SEPERATOR="\u250f\u2510";
	private String traceType;
	private String traceId;
	private String spanId;
	private String parentSpanId;
	private long startLog;
	private long stopLog;
	private String method;
	private String url;
	private String httpCode;
	private String serviceName;
	private String logMsg;
	
	public String getTraceType() {
		return traceType;
	}
	
	public TraceData setTraceType(String traceType) {
		this.traceType = traceType;
		return this;
	}
	
	public String getTraceId() {
		return traceId;
	}
	public TraceData setTraceId(String traceId) {
		this.traceId = traceId;
		return this;
	}
	public String getSpanId() {
		return spanId;
	}
	public TraceData setSpanId(String spanId) {
		this.spanId = spanId;
		return this;
	}
	public String getParentSpanId() {
		return parentSpanId;
	}
	public TraceData setParentSpanId(String parentSpanId) {
		this.parentSpanId = parentSpanId;
		return this;
	}
	public long getStartLog() {
		return startLog;
	}
	public TraceData setStartLog(long startLog) {
		this.startLog = startLog;
		return this;
	}
	public long getStopLog() {
		return stopLog;
	}
	public TraceData setStopLog(long stopLog) {
		this.stopLog = stopLog;
		return this;
	}
	public String getMethod() {
		return method;
	}
	public TraceData setMethod(String method) {
		this.method = method;
		return this;
	}
	public String getUrl() {
		return url;
	}
	public TraceData setUrl(String url) {
		this.url = url;
		return this;
	}
	public String getHttpCode() {
		return httpCode;
	}
	public TraceData setHttpCode(String httpCode) {
		this.httpCode = httpCode;
		return this;
	}
	public String getServiceName() {
		return serviceName;
	}
	public TraceData setServiceName(String serviceName) {
		this.serviceName = serviceName;
		return this;
	}
	public String getLogMsg() {
		return logMsg;
	}
	public TraceData setLogMsg(String logMsg) {
		this.logMsg = logMsg;
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(TraceUtil.getTimestamp(this.stopLog)).append(TRACE_SEPERATOR)
		.append(this.traceId!=null?this.traceId:"").append(TRACE_SEPERATOR)
		.append(this.spanId!=null?this.spanId:"").append(TRACE_SEPERATOR)
		.append(this.parentSpanId!=null?this.parentSpanId:"0").append(TRACE_SEPERATOR)
		.append(this.traceType.equals("TRACE")?"TRACE_END":"SPAN_END").append(TRACE_SEPERATOR)
		.append(TraceUtil.getTimestamp(this.startLog)).append(TRACE_SEPERATOR)
		.append(TraceUtil.getTimestamp(this.stopLog)).append(TRACE_SEPERATOR)
		.append(this.stopLog-this.startLog).append(TRACE_SEPERATOR)
		.append(this.method).append(TRACE_SEPERATOR)
		.append(this.url).append(TRACE_SEPERATOR)
		.append(this.httpCode).append(TRACE_SEPERATOR)
		.append(this.serviceName).append(TRACE_SEPERATOR)
		.append(logMsg!=null?logMsg:"NONE");
		return sb.toString();
	}
}
