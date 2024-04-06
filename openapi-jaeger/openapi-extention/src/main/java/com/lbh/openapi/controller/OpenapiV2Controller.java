package com.lbh.openapi.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lbh.openapi.domain.AuthRequest;
import com.lbh.openapi.domain.ReqData;
import com.lbh.openapi.domain.ResultData;
import com.lbh.openapi.domain.ResultExtra;
import com.lbh.openapi.exception.OpenapiError;
import com.lbh.openapi.exception.OpenapiException;
import com.lbh.openapi.exception.StatusCode;
import com.lbh.openapi.util.AuthUtil;

import ch.qos.logback.classic.Logger;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.tag.Tags;

@RestController
@RequestMapping(value = "/v2/openapi/extention")
public class OpenapiV2Controller {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(OpenapiV2Controller.class);

	@Value("${auth.authServer}")
	protected String authServer;
	
	@Value("${auth.appVersion}")
	protected String appVersion;

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
		//Setting reqData for Jaeger
		Span span =  this.startServerSpan("extention.add", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			Map<String,String> fields = new LinkedHashMap<>();
			fields.put("a", String.valueOf(a));
			fields.put("b", String.valueOf(b));
			span.log(fields);
			try {
				validateRequest(txId, request, span);
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
		} finally {
			//Setting response for Jaeger
			span.setTag("response", result.toJson());
			span.finish();
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
		//Setting reqData for Jaeger
		Span span =  this.startServerSpan("extention.sub", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			Map<String,String> fields = new LinkedHashMap<>();
			fields.put("a", String.valueOf(a));
			fields.put("b", String.valueOf(b));
			span.log(fields);
			try {
				validateRequest(txId, request, span);
	
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
		} finally {
			//Setting response for Jaeger
			span.setTag("response", result.toJson());
			span.finish();
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/div", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doDivision(
			HttpServletRequest request
			, @RequestBody final ReqData reqData) {
		Result result = null;
		String txId = txId();
		//Setting reqData for Jaeger
		Span span =  this.startServerSpan("extention.div", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			Map<String,String> fields = new LinkedHashMap<>();
			fields.put("clientId", reqData.getDataBody().getClientId());
			fields.put("a", reqData.getDataParam().getA());
			fields.put("b", reqData.getDataParam().getB());
			span.log(fields);
	
			try {
				validateRequest(txId, request, span);
	
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
				span.setTag("response", result.toJson());
	
			} catch (OpenapiException e) {
				OpenapiError error = new OpenapiError().setCode(e.getCode()).setMessage(e.getMessage());
				result = new Result(txId, Result.ERROR).putValue("error", error);
			}
		} finally {
			//Setting response for Jaeger
			span.setTag("response", result.toJson());
			span.finish();			
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/multi", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doMultiply(
			HttpServletRequest request
			, @RequestBody final ReqData reqData) {
		Result result = null;
		String txId = txId();
		//Setting reqData for Jaeger
		Span span =  this.startServerSpan("extention.multi", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			Map<String,String> fields = new LinkedHashMap<>();
			fields.put("clientId", reqData.getDataBody().getClientId());
			fields.put("a", reqData.getDataParam().getA());
			fields.put("b", reqData.getDataParam().getB());
			span.log(fields);
			try {
				validateRequest(txId, request, span);
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
		} finally {
			//Setting response for Jaeger
			span.setTag("response", result.toJson());
			span.finish();
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/pow", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doPower(
			HttpServletRequest request
			, @RequestBody final ReqData reqData) {
		Result result = null;
		String txId = txId();
		//Setting reqData for Jaeger
		Span span =  this.startServerSpan("extention.pow", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			Map<String,String> fields = new LinkedHashMap<>();
			fields.put("clientId", reqData.getDataBody().getClientId());
			fields.put("a", reqData.getDataParam().getA());
			fields.put("b", reqData.getDataParam().getB());
			span.log(fields);
			try {
				validateRequest(txId, request, span);
	
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
		} finally {
			//Setting response for Jaeger
			span.setTag("response", result.toJson());
			span.finish();
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	
	private void validateRequest(String txid, HttpServletRequest request, Span parent) throws OpenapiException {
		HttpEntity<AuthRequest> entity = null;
		MyResttemplate rest = new MyResttemplate();
		HttpHeaders headers = new HttpHeaders();
		Span span = tracer.buildSpan("extention.validate").asChildOf(parent).start();
		try {
			Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
			Tags.HTTP_METHOD.set(span, "POST");
			Tags.HTTP_URL.set(span, request.getContextPath());
			headers.add("content-type", "application/json");
			String tokenString = AuthUtil.getToken(request);
			AuthRequest authRequest = new AuthRequest();
			
			//Setting for Jaeger fields
			Map<String, String> fields = new LinkedHashMap<>();
			fields.put("token", tokenString);
			span.log(fields);
			
			authRequest.setReqTime(System.currentTimeMillis());
			authRequest.setToken(tokenString);
			HttpHeaderInjectAdapter carrier = new HttpHeaderInjectAdapter(headers);
            tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, carrier);				
			entity = new HttpEntity<AuthRequest>(authRequest, headers);
			ResponseEntity<String> httpResponse = rest.exchange(authServer, HttpMethod.POST, entity, String.class);

			if(httpResponse.getStatusCode().equals(HttpStatus.OK)==false) {
				if (log.isInfoEnabled()) {
					log.info(this.getClass().getSimpleName()+" got Response OK {"+httpResponse.getBody()+"}");
				}
				throw new OpenapiException("INVALID_AUTHORIZATION").setCode(httpResponse.getStatusCode().value());
			}
			//Setting for Jaeger result
			span.setTag("result", true);
		} catch(Exception e) {
			//Setting for Jaeger result
			span.setTag("result", false);
			if (e instanceof OpenapiException) {
				throw e;
			} else {
				log.error("FAILED to postAgentResult {"+e.getClass().getSimpleName()+", message:"+e.getMessage(), e);
				throw new OpenapiException(e.getClass().getSimpleName()+","+e.getMessage()).setCode(StatusCode.UNAUTHORIZED.code);
			}
		} finally {
			span.finish();
		}

	}
	
    protected Span startServerSpan(String operationName, HttpServletRequest request) {
        HttpServletRequestExtractAdapter carrier = new HttpServletRequestExtractAdapter(request);
        SpanContext parent = tracer.extract(Format.Builtin.HTTP_HEADERS, carrier);
        Span span = tracer.buildSpan(operationName).asChildOf(parent).start();
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_SERVER);
        return span;
    }


    private static class HttpHeaderInjectAdapter implements TextMap {
        private final HttpHeaders headers;

        HttpHeaderInjectAdapter(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override
        public Iterator<Entry<String, String>> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void put(String key, String value) {
            headers.set(key, value);
        }
    }
    
    private static class HttpServletRequestExtractAdapter implements TextMap {
        private final Map<String, String> headers;

        HttpServletRequestExtractAdapter(HttpServletRequest request) {
            this.headers = new LinkedHashMap<>();
            Enumeration<String> keys = request.getHeaderNames();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = request.getHeader(key);
                headers.put(key, value);
            }
        }

        @Override
        public Iterator<Entry<String, String>> iterator() {
            return headers.entrySet().iterator();
        }

        @Override
        public void put(String key, String value) {
            throw new UnsupportedOperationException();
        }
    }
}
