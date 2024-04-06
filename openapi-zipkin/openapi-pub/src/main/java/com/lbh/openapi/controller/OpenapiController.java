package com.lbh.openapi.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lbh.common.domain.Result;
import com.lbh.openapi.domain.ReqData;
import com.lbh.openapi.domain.ResultData;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import ch.qos.logback.classic.Logger;

@RestController
@RequestMapping(value = "/v1/openapi/pub")
@Configuration
public class OpenapiController {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(OpenapiController.class);
	
	@Value("${auth.appVersion}")
	private String appVersion;
	
	@Autowired
	Tracer tracer;
	
	protected String txId() {
		return UUID.randomUUID().toString();
	}

	String toJson(Object obj) {
		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyyMMddHHmmss").create();
		return gson.toJson(obj);
	}

	@RequestMapping(value = "/add/{a}/{b}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doAddition(
			HttpServletRequest request
			, @PathVariable("a") final int a
			, @PathVariable("b") final int b
			) {
		Result result = null;
		String txId = txId();
		ResultData ret = new ResultData();
		TraceContext parent = null;
		Span newSpan = null;
		Span newTrace = null;
		String traceId = request.getHeader("X-B3-TraceId");
		String spanId = request.getHeader("X-B3-SpanId");
		if (traceId == null || spanId == null) {
			newTrace = tracer.newTrace();
		    parent = newTrace.context();
		    newTrace.name("pub.add");
		    newTrace.start();
		    
		} else {
			parent = TraceContext.newBuilder()
	            .traceId(Long.parseUnsignedLong(traceId, 16))
	            .spanId(Long.parseUnsignedLong(spanId, 16))
	            .build();
		    newSpan = this.tracer.newChild(parent);
		    newSpan.name("pub.add");
		    newSpan.start();
		}
		newSpan.tag("Headers", getHeaders(request));
		try {
			int v = a+b;
			ret.setOperandA(String.valueOf(a));
			ret.setOperandB(String.valueOf(b));
			ret.setValue(String.valueOf(v));
			ret.setExtention(null);
			result = new Result(txId, Result.OK).putValue("add", ret).putValue("appVersion", appVersion);
			newSpan.tag("response", result.toJson());
		} finally {
			if (newSpan != null) {
				newSpan.finish();
			}
			if (newTrace != null) {
				newTrace.finish();
			}
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/sub/{a}/{b}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doSubtraction(
			HttpServletRequest request
			, @PathVariable("a") final int a
			, @PathVariable("b") final int b
			) {
		String txId = txId();
		Result result = null;
		ResultData ret = new ResultData();
		TraceContext parent = null;
		Span newSpan = null;
		Span newTrace = null;
		String traceId = request.getHeader("X-B3-TraceId");
		String spanId = request.getHeader("X-B3-SpanId");
		if (traceId == null || spanId == null) {
			newTrace = tracer.newTrace();
		    parent = newTrace.context();
		    newTrace.name("pub.sub");
		    newTrace.start();
		    
		} else {
			parent = TraceContext.newBuilder()
	            .traceId(Long.parseUnsignedLong(traceId, 16))
	            .spanId(Long.parseUnsignedLong(spanId, 16))
	            .build();
		    newSpan = this.tracer.newChild(parent);
		    newSpan.name("pub.sub");
		    newSpan.start();
		}
		newSpan.tag("Headers", getHeaders(request));
		try {
			int v = a-b;
			ret.setOperandA(String.valueOf(a));
			ret.setOperandB(String.valueOf(b));
			ret.setValue(String.valueOf(v));
			ret.setExtention(null);
			result = new Result(txId, Result.OK).putValue("sub", ret).putValue("appVersion", appVersion);
			newSpan.tag("response", result.toJson());
		} finally {
			if (newSpan != null) {
				newSpan.finish();
			}
			if (newTrace != null) {
				newTrace.finish();
			}
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/div", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doDivision(
			HttpServletRequest request
			, @RequestBody final ReqData reqData) {
		Result result = null;
		String txId = txId();

		ResultData ret = new ResultData();
		TraceContext parent = null;
		Span newSpan = null;
		Span newTrace = null;
		String traceId = request.getHeader("X-B3-TraceId");
		String spanId = request.getHeader("X-B3-SpanId");
		if (traceId == null || spanId == null) {
			newTrace = tracer.newTrace();
		    parent = newTrace.context();
		    newTrace.name("pub.div");
		    newTrace.start();
		    
		} else {
			parent = TraceContext.newBuilder()
	            .traceId(Long.parseUnsignedLong(traceId, 16))
	            .spanId(Long.parseUnsignedLong(spanId, 16))
	            .build();
		    newSpan = this.tracer.newChild(parent);
		    newSpan.name("pub.div");
		    newSpan.start();
		}
		newSpan.tag("Headers", getHeaders(request));
		try {
			float v = Float.parseFloat(reqData.getDataParam().getA()) / Float.parseFloat(reqData.getDataParam().getB());
			ret.setOperandA(reqData.getDataParam().getA());
			ret.setOperandB(reqData.getDataParam().getB());
			ret.setValue(String.valueOf(v));
			ret.setExtention(null);
			result = new Result(txId, Result.OK).putValue("div", ret).putValue("appVersion", appVersion);
			newSpan.tag("response", result.toJson());
		} finally {
			if (newSpan != null) {
				newSpan.finish();
			}
			if (newTrace != null) {
				newTrace.finish();
			}
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/multi", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doMultiply(
			HttpServletRequest request
			, @RequestBody final ReqData reqData) {
		Result result = null;
		String txId = txId();
		TraceContext parent = null;
		Span newSpan = null;
		Span newTrace = null;
		String traceId = request.getHeader("X-B3-TraceId");
		String spanId = request.getHeader("X-B3-SpanId");
		if (traceId == null || spanId == null) {
			newTrace = tracer.newTrace();
		    parent = newTrace.context();
		    newTrace.name("pub.multi");
		    newTrace.start();
		    
		} else {
			parent = TraceContext.newBuilder()
	            .traceId(Long.parseUnsignedLong(traceId, 16))
	            .spanId(Long.parseUnsignedLong(spanId, 16))
	            .build();
		    newSpan = this.tracer.newChild(parent);
		    newSpan.name("pub.multi");
		    newSpan.start();
		}
		newSpan.tag("Headers", getHeaders(request));
		log.info(txId+",Begin to doDivision,param={"+reqData+"}");

		ResultData ret = new ResultData();
		try {
			long v = Long.parseLong(reqData.getDataParam().getA()) * Long.parseLong(reqData.getDataParam().getB());
			ret.setOperandA(reqData.getDataParam().getA());
			ret.setOperandB(reqData.getDataParam().getB());
			ret.setValue(String.valueOf(v));
			ret.setExtention(null);
			result = new Result(txId, Result.OK).putValue("multiply", ret);
			newSpan.tag("response", result.toJson());
		} finally {
			if (newSpan != null) {
				newSpan.finish();
			}
			if (newTrace != null) {
				newTrace.finish();
			}
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/pow", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doPower(
			HttpServletRequest request
			, @RequestBody final ReqData reqData) {
		Result result = null;
		String txId = txId();
		TraceContext parent = null;
		Span newSpan = null;
		Span newTrace = null;
		String traceId = request.getHeader("X-B3-TraceId");
		String spanId = request.getHeader("X-B3-SpanId");
		if (traceId == null || spanId == null) {
			newTrace = tracer.newTrace();
		    parent = newTrace.context();
		    newTrace.name("pub.pow");
		    newTrace.start();
		    
		} else {
			parent = TraceContext.newBuilder()
	            .traceId(Long.parseUnsignedLong(traceId, 16))
	            .spanId(Long.parseUnsignedLong(spanId, 16))
	            .build();
		    newSpan = this.tracer.newChild(parent);
		    newSpan.name("pub.pow");
		    newSpan.start();
		}
		newSpan.tag("Headers", getHeaders(request));
		log.info(txId+",Begin to doPower,param={"+reqData+"}");

		ResultData ret = new ResultData();
		try {
			long v = 1;
			for (long i=0; i<Long.parseLong(reqData.getDataParam().getB()); i++) {
				v *= Long.parseLong(reqData.getDataParam().getA());
			}
			ret.setOperandA(reqData.getDataParam().getA());
			ret.setOperandB(reqData.getDataParam().getB());
			ret.setValue(String.valueOf(v));
			ret.setExtention(null);
			result = new Result(txId, Result.OK).putValue("power", ret).putValue("appVersion", appVersion);
			newSpan.tag("response", result.toJson());
		} finally {
			if (newSpan != null) {
				newSpan.finish();
			}
			if (newTrace != null) {
				newTrace.finish();
			}
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}
	
	
	private String getHeaders(HttpServletRequest request) {
		Map<String,String> map = new HashMap<String,String>();
		Enumeration keys = request.getHeaderNames();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			map.put(key, request.getHeader(key));
		}
		return toJson(map);
	}
}
