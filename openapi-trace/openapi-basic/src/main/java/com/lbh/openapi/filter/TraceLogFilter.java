package com.lbh.openapi.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.lbh.openapi.domain.TraceData;
import com.lbh.openapi.util.TraceUtil;

import ch.qos.logback.classic.Logger;

public class TraceLogFilter implements Filter {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(TraceLogFilter.class);

	@Value("${spring.application.name}")
	protected String applicationName;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
    	long startStmp = System.currentTimeMillis();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
    	String values = request.getHeader(TraceUtil.TRACE_HEADER);
    	String traceId = null;
    	String prntSpanId = null;
    	String spanId = null;
    	TraceData trace = null;
    	String method = request.getMethod();
    	String reqUrl = request.getRequestURL().toString();
    	if (null == values) {
    		spanId = TraceUtil.getSpanId();
    		trace = TraceUtil.getTraceData("TRACE", spanId);
    		traceId = trace.getTraceId();
    		response.addHeader(TraceUtil.TRACE_HEADER, trace.getTraceId()+","+spanId);
    	} else {
    		String[] tmp = values.split(",");
    		traceId = tmp[0];
    		prntSpanId = tmp.length>1?tmp[1]:null;
    		spanId = TraceUtil.getSpanId();
    		trace = TraceUtil.getTraceData("SPAN", traceId, spanId);
    		
    		String newValues = TraceUtil.addSpanId(values, spanId);
    		response.addHeader(TraceUtil.TRACE_HEADER, newValues);
    	}
    	trace.setMethod(method)
    		.setUrl(reqUrl)
    		.setStartLog(startStmp)
    		.setServiceName(applicationName);
    	chain.doFilter(servletRequest, servletResponse);
    	long endStmp = System.currentTimeMillis();
    	trace.setStopLog(endStmp)
    		.setHttpCode(String.valueOf(response.getStatus()))
    		.setLogMsg(null);
    	TraceUtil.remove(trace.getTraceType(), trace.getSpanId());
    	log.trace(trace.toString());
	}

}
