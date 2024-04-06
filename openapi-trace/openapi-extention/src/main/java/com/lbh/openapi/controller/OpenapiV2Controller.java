package com.lbh.openapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lbh.common.domain.Result;
import com.lbh.common.rest.MyResttemplate;
import com.lbh.openapi.domain.AuthRequest;
import com.lbh.openapi.domain.ReqData;
import com.lbh.openapi.domain.ResultData;
import com.lbh.openapi.domain.ResultExtra;
import com.lbh.openapi.exception.OpenapiError;
import com.lbh.openapi.exception.OpenapiException;
import com.lbh.openapi.exception.StatusCode;
import com.lbh.openapi.util.AuthUtil;
import com.lbh.openapi.util.TraceUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.classic.Logger;
//import io.opentelemetry.api.GlobalOpenTelemetry;
//import io.opentelemetry.api.trace.Span;
//import io.opentelemetry.api.trace.Tracer;

@RestController
@RequestMapping(value = "/v2/openapi/extention")
public class OpenapiV2Controller {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(OpenapiV2Controller.class);
	@Value("${auth.authServer}")
	protected String authServer;
	
	@Value("${auth.appVersion}")
	protected String appVersion;
	
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
		try {
			validateRequest(txId, request);
			ResultData ret = new ResultData();
			int v = a+b;
			ret.setOperandA(String.valueOf(a));
			ret.setOperandB(String.valueOf(b));
			ret.setValue(String.valueOf(v));
			List<ResultExtra> extentions = new ArrayList<ResultExtra>();
			extentions.add(new ResultExtra().setName("type").setResult("EXTENTION"));
			extentions.add(new ResultExtra().setName("formular").setResult(a+"+"+b+"="+ret.getValue()));
			ret.setExtention(extentions);
			result = new Result(txId, Result.OK).putValue("add", ret).putValue("appVersion", appVersion);
		} catch (OpenapiException e) {
			OpenapiError error = new OpenapiError().setCode(e.getCode()).setMessage(e.getMessage());
			result = new Result(txId, Result.ERROR).putValue("error", error);
		}
//		if (log.isInfoEnabled()) {
//			Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-extention");
//			Span span = tracer.spanBuilder("openapi-extention").startSpan();
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
		try {
			validateRequest(txId, request);

			ResultData ret = new ResultData();
			int v = a-b;
			ret.setOperandA(String.valueOf(a));
			ret.setOperandB(String.valueOf(b));
			ret.setValue(String.valueOf(v));
			List<ResultExtra> extentions = new ArrayList<ResultExtra>();
			extentions.add(new ResultExtra().setName("type").setResult("EXTENTION"));
			extentions.add(new ResultExtra().setName("formular").setResult(a+"-"+b+"="+ret.getValue()));
			ret.setExtention(extentions);
			result = new Result(txId, Result.OK).putValue("sub", ret).putValue("appVersion", appVersion);
		} catch (OpenapiException e) {
			OpenapiError error = new OpenapiError().setCode(e.getCode()).setMessage(e.getMessage());
			result = new Result(txId, Result.ERROR).putValue("error", error);
		}
//		if (log.isInfoEnabled()) {
//			Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-extention");
//			Span span = tracer.spanBuilder("openapi-extention").startSpan();
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

		try {
			validateRequest(txId, request);

			ResultData ret = new ResultData();
			float v = Float.parseFloat(reqData.getDataParam().getA()) / Float.parseFloat(reqData.getDataParam().getB());
			ret.setOperandA(reqData.getDataParam().getA());
			ret.setOperandB(reqData.getDataParam().getB());
			ret.setValue(String.valueOf(v));
			List<ResultExtra> extentions = new ArrayList<ResultExtra>();
			extentions.add(new ResultExtra().setName("type").setResult("EXTENTION"));
			extentions.add(new ResultExtra().setName("formular").setResult(reqData.getDataParam().getA()+"/"+reqData.getDataParam().getB()+"="+ret.getValue()));
			ret.setExtention(extentions);
			result = new Result(txId, Result.OK).putValue("div", ret).putValue("appVersion", appVersion);

		} catch (OpenapiException e) {
			OpenapiError error = new OpenapiError().setCode(e.getCode()).setMessage(e.getMessage());
			result = new Result(txId, Result.ERROR).putValue("error", error);
		}
//		if (log.isInfoEnabled()) {
//			Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-extention");
//			Span span = tracer.spanBuilder("openapi-extention").startSpan();
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
		try {
			validateRequest(txId, request);
			ResultData ret = new ResultData();
			long v = Long.parseLong(reqData.getDataParam().getA()) * Long.parseLong(reqData.getDataParam().getB());
			ret.setOperandA(reqData.getDataParam().getA());
			ret.setOperandB(reqData.getDataParam().getB());
			ret.setValue(String.valueOf(v));
			List<ResultExtra> extentions = new ArrayList<ResultExtra>();
			extentions.add(new ResultExtra().setName("type").setResult("EXTENTION"));
			extentions.add(new ResultExtra().setName("formular").setResult(reqData.getDataParam().getA()+"X"+reqData.getDataParam().getB()+"="+ret.getValue()));
			ret.setExtention(extentions);
			result = new Result(txId, Result.OK).putValue("multiply", ret).putValue("appVersion", appVersion);
		} catch (OpenapiException e) {
			OpenapiError error = new OpenapiError().setCode(e.getCode()).setMessage(e.getMessage());
			result = new Result(txId, Result.ERROR).putValue("error", error);
		}
//		if (log.isInfoEnabled()) {
//			Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-extention");
//			Span span = tracer.spanBuilder("openapi-extention").startSpan();
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
		try {
			validateRequest(txId, request);

			ResultData ret = new ResultData();
			long v = Long.parseLong(reqData.getDataParam().getA());
			for (long i=0; i<Long.parseLong(reqData.getDataParam().getB()); i++) {
				v *= Long.parseLong(reqData.getDataParam().getA());
			}
			ret.setOperandA(reqData.getDataParam().getA());
			ret.setOperandB(reqData.getDataParam().getB());
			ret.setValue(String.valueOf(v));
			List<ResultExtra> extentions = new ArrayList<ResultExtra>();
			extentions.add(new ResultExtra().setName("type").setResult("EXTENTION"));
			extentions.add(new ResultExtra().setName("formular").setResult(reqData.getDataParam().getA()+"^"+reqData.getDataParam().getB()+"="+ret.getValue()));
			ret.setExtention(extentions);
			result = new Result(txId, Result.OK).putValue("power", ret).putValue("appVersion", appVersion);
		} catch (OpenapiException e) {
			OpenapiError error = new OpenapiError().setCode(e.getCode()).setMessage(e.getMessage());
			result = new Result(txId, Result.ERROR).putValue("error", error);
		}
//		if (log.isInfoEnabled()) {
//			Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-extention");
//			Span span = tracer.spanBuilder("openapi-extention").startSpan();
//			span.addEvent("doPower "+result.toJson());
//			span.end();
//		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	
	private void validateRequest(String txid, HttpServletRequest request) throws OpenapiException {
		HttpEntity<AuthRequest> entity = null;
		MyResttemplate rest = new MyResttemplate();
		HttpHeaders headers = new HttpHeaders();
		StringBuilder sb = new StringBuilder();
		try {
			String values = request.getHeader(TraceUtil.TRACE_HEADER);
			headers.add(TraceUtil.TRACE_HEADER, values);
			headers.add("content-type", "application/json");
			String tokenString = AuthUtil.getToken(request);
			AuthRequest authRequest = new AuthRequest();
			authRequest.setReqTime(System.currentTimeMillis());
			authRequest.setToken(tokenString);
			entity = new HttpEntity<AuthRequest>(authRequest, headers);
			ResponseEntity<String> httpResponse = rest.exchange(authServer, HttpMethod.POST, entity, String.class);

			if(httpResponse.getStatusCode().equals(HttpStatus.OK)==false) {
				if (log.isInfoEnabled()) {
					log.info(this.getClass().getSimpleName()+" got Response OK {"+httpResponse.getBody()+"}");
				}
				throw new OpenapiException("INVALID_AUTHORIZATION").setCode(httpResponse.getStatusCode().value());
			}
			sb.append("SUCCEED in validateRequest "+authRequest.toString());
		} catch(Exception e) {
			sb.append("FAILED in validateRequest Exception:"+e.getClass().getSimpleName()+",msg:"+e.getMessage());
			if (e instanceof OpenapiException) {
				throw e;
			} else {
				log.error("FAILED to postAgentResult {"+e.getClass().getSimpleName()+", message:"+e.getMessage(), e);
				throw new OpenapiException(e.getClass().getSimpleName()+","+e.getMessage()).setCode(StatusCode.UNAUTHORIZED.code);
			}
//       } finally {
//			if (log.isInfoEnabled()) {
//				Span span = Span.current();
//				Tracer tracer = GlobalOpenTelemetry.getTracer("openapi-basic");
//				span.addEvent(sb.toString());
//				span.end();
//			}
		}
	}
}
