package com.lbh.openapi.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lbh.common.domain.Result;
import com.lbh.openapi.domain.ReqData;
import com.lbh.openapi.domain.ResultData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.classic.Logger;
//import io.opentelemetry.api.GlobalOpenTelemetry;
//import io.opentelemetry.api.trace.Span;
//import io.opentelemetry.api.trace.Tracer;

@RestController
@RequestMapping(value = "/v1/openapi/pub")
@Configuration
public class OpenapiController {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(OpenapiController.class);
	
	@Value("${auth.appVersion}")
	private String appVersion;
	
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
		int v = a+b;
		ret.setOperandA(String.valueOf(a));
		ret.setOperandB(String.valueOf(b));
		ret.setValue(String.valueOf(v));
		ret.setExtention(null);
		result = new Result(txId, Result.OK).putValue("add", ret).putValue("appVersion", appVersion);
//		if (log.isInfoEnabled()) {
//			Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-pub");
//			Span span = tracer.spanBuilder("openapi-pub").startSpan();
//			span.addEvent("doAddition "+result.toJson());
//			span.end();
//		}
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
		int v = a-b;
		ret.setOperandA(String.valueOf(a));
		ret.setOperandB(String.valueOf(b));
		ret.setValue(String.valueOf(v));
		ret.setExtention(null);
		result = new Result(txId, Result.OK).putValue("sub", ret).putValue("appVersion", appVersion);
//		if (log.isInfoEnabled()) {
//			Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-pub");
//			Span span = tracer.spanBuilder("openapi-pub").startSpan();
//			span.addEvent("doSubtraction "+result.toJson());
//			span.end();
//		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/div", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doDivision(
			HttpServletRequest request
			, @RequestBody final ReqData reqData) {
		Result result = null;
		String txId = txId();

		ResultData ret = new ResultData();
		float v = Float.parseFloat(reqData.getDataParam().getA()) / Float.parseFloat(reqData.getDataParam().getB());
		ret.setOperandA(reqData.getDataParam().getA());
		ret.setOperandB(reqData.getDataParam().getB());
		ret.setValue(String.valueOf(v));
		ret.setExtention(null);
		result = new Result(txId, Result.OK).putValue("div", ret).putValue("appVersion", appVersion);

//		if (log.isInfoEnabled()) {
//			Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-pub");
//			Span span = tracer.spanBuilder("openapi-pub").startSpan();
//			span.addEvent("doDivision "+result.toJson());
//			span.end();
//		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/multi", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doMultiply(
			HttpServletRequest request
			, @RequestBody final ReqData reqData) {
		Result result = null;
		String txId = txId();
		log.info(txId+",Begin to doDivision,param={"+reqData+"}");

		ResultData ret = new ResultData();
		long v = Long.parseLong(reqData.getDataParam().getA()) * Long.parseLong(reqData.getDataParam().getB());
		ret.setOperandA(reqData.getDataParam().getA());
		ret.setOperandB(reqData.getDataParam().getB());
		ret.setValue(String.valueOf(v));
		ret.setExtention(null);
		result = new Result(txId, Result.OK).putValue("multiply", ret);
//		if (log.isInfoEnabled()) {
//			Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-pub");
//			Span span = tracer.spanBuilder("openapi-pub").startSpan();
//			span.addEvent("doMultiply "+result.toJson());
//			span.end();
//		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/pow", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doPower(
			HttpServletRequest request
			, @RequestBody final ReqData reqData) {
		Result result = null;
		String txId = txId();
		log.info(txId+",Begin to doPower,param={"+reqData+"}");

		ResultData ret = new ResultData();
		long v = 1;
		for (long i=0; i<Long.parseLong(reqData.getDataParam().getB()); i++) {
			v *= Long.parseLong(reqData.getDataParam().getA());
		}
		ret.setOperandA(reqData.getDataParam().getA());
		ret.setOperandB(reqData.getDataParam().getB());
		ret.setValue(String.valueOf(v));
		ret.setExtention(null);
		result = new Result(txId, Result.OK).putValue("power", ret).putValue("appVersion", appVersion);
//		if (log.isInfoEnabled()) {
//			Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-pub");
//			Span span = tracer.spanBuilder("openapi-pub").startSpan();
//			span.addEvent("doPower "+result.toJson());
//			span.end();
//		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}
	
}
