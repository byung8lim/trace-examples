package com.lbh.openapi.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.lbh.openapi.domain.TraceData;
import com.lbh.openapi.util.TraceUtil;

import ch.qos.logback.classic.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.LoggerFactory;

@Component
public class TraceLogInterceptor implements HandlerInterceptor {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(TraceLogInterceptor.class);

	@Value("${spring.application.name}")
	protected String applicationName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	long stmp = System.currentTimeMillis();
    	String values = request.getHeader(TraceUtil.TRACE_HEADER);
    	TraceData trace = null;
    	String prntSpanId = null;
    	String spanId = null;
    	String method = request.getMethod();
    	String reqUrl = request.getRequestURL().toString();
    	if (null == values || values == "") {
    		spanId = TraceUtil.getSpanId();
    		trace = TraceUtil.getTraceData("TRACE", spanId);
    		
    		response.addHeader(TraceUtil.TRACE_HEADER, trace.getTraceId()+","+spanId);
    	} else {
    		String[] tmp = values.split(",");
    		String traceId = tmp[0];
    		prntSpanId = tmp[1];
    		spanId = TraceUtil.getSpanId();
    		trace = TraceUtil.getTraceData("SPAN", traceId, spanId);
    		if (null == trace) {
    			
    			log.info("spanId:"+spanId+", not found");
    		} else {
	    		String newValues = TraceUtil.addSpanId(values, spanId);
	    		CustomResponseWrapper responseWrapper = new CustomResponseWrapper(response);
	    		responseWrapper.putHeader(TraceUtil.TRACE_HEADER, newValues);
    		}
    	}
		trace.setStartLog(stmp)
		.setMethod(method)
		.setParentSpanId(prntSpanId)
		.setServiceName(applicationName)
		.setUrl(reqUrl);
    	return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    	long stmp = System.currentTimeMillis();
//    	String values = request.getHeader(TraceUtil.TRACE_HEADER);
    	String values = response.getHeader(TraceUtil.TRACE_HEADER);
		String[] tmp = values!=null?values.split(","):null;
		String traceId = null;
		String spanId = null;
		String parentSpanId = null;
    	TraceData trace = null;
    	
    	if (null != values) {
    		traceId = tmp[0];
    		spanId = tmp.length>1?tmp[1]:null;
    		parentSpanId = tmp.length>2?tmp[2]:null;
    		trace = TraceUtil.remove(parentSpanId==null?"TRACE":"SPAN", spanId);
    		if (null == trace) {
    			log.info("spanId:"+spanId+", not found");
    		} else {
	    		trace.setStopLog(stmp)
	    			.setHttpCode(String.valueOf(response.getStatus()))
	    			.setLogMsg(null);
	    		response.addHeader(TraceUtil.TRACE_HEADER, traceId+","+spanId);
	    		log.trace(trace.toString());
    		}
    	}
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
   }

    private static class CustomResponseWrapper extends HttpServletResponseWrapper {

        public CustomResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setHeader(String name, String value) {
            super.setHeader(name, value);
        }

        public void putHeader(String name, String value) {
            super.setHeader(name, value);
        }
    }
}
