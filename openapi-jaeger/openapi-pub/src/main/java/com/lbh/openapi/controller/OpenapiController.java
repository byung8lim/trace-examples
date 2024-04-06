package com.lbh.openapi.controller;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lbh.common.domain.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lbh.openapi.domain.ReqData;
import com.lbh.openapi.domain.ResultData;

import ch.qos.logback.classic.Logger;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.tag.Tags;

@RestController
@RequestMapping(value = "/v1/openapi/pub")
@Configuration
public class OpenapiController {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(OpenapiController.class);
	
	@Autowired
	Tracer tracer;
	
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
		Span span =  this.startServerSpan("pub.add", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			Map<String,String> fields = new LinkedHashMap<>();
			fields.put("a", String.valueOf(a));
			fields.put("b", String.valueOf(b));
			span.log(fields);
			ResultData ret = new ResultData();
			int v = a+b;
			ret.setOperandA(String.valueOf(a));
			ret.setOperandB(String.valueOf(b));
			ret.setValue(String.valueOf(v));
			ret.setExtention(null);
			result = new Result(txId, Result.OK).putValue("add", ret).putValue("appVersion", appVersion);
			return new ResponseEntity<String>(result.toJson(), result.status());
		} finally {
			span.setTag("response", result.toJson());
			span.finish();
		}
	}

	@RequestMapping(value = "/sub/{a}/{b}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doSubtraction(
			HttpServletRequest request
			, @PathVariable("a") final int a
			, @PathVariable("b") final int b
			) {
		String txId = txId();
		Result result = null;
		Span span =  this.startServerSpan("pub.sub", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			Map<String,String> fields = new LinkedHashMap<>();
			fields.put("a", String.valueOf(a));
			fields.put("b", String.valueOf(b));
			span.log(fields);
			ResultData ret = new ResultData();
			int v = a-b;
			ret.setOperandA(String.valueOf(a));
			ret.setOperandB(String.valueOf(b));
			ret.setValue(String.valueOf(v));
			ret.setExtention(null);
			result = new Result(txId, Result.OK).putValue("sub", ret).putValue("appVersion", appVersion);
			span.setTag("response", result.toJson());
		} finally {
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
		Span span =  this.startServerSpan("pub.div", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			Map<String,String> fields = new LinkedHashMap<>();
			fields.put("reqData", toJson(reqData));
			span.log(fields);
			ResultData ret = new ResultData();
			float v = Float.parseFloat(reqData.getDataParam().getA()) / Float.parseFloat(reqData.getDataParam().getB());
			ret.setOperandA(reqData.getDataParam().getA());
			ret.setOperandB(reqData.getDataParam().getB());
			ret.setValue(String.valueOf(v));
			ret.setExtention(null);
			result = new Result(txId, Result.OK).putValue("div", ret).putValue("appVersion", appVersion);
			span.setTag("response", result.toJson());
		} finally {
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
		Span span =  this.startServerSpan("pub.multi", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			Map<String,String> fields = new LinkedHashMap<>();
			fields.put("reqData", toJson(reqData));
			span.log(fields);
			log.info(txId+",Begin to doDivision,param={"+reqData+"}");
			ResultData ret = new ResultData();
			long v = Long.parseLong(reqData.getDataParam().getA()) * Long.parseLong(reqData.getDataParam().getB());
			ret.setOperandA(reqData.getDataParam().getA());
			ret.setOperandB(reqData.getDataParam().getB());
			ret.setValue(String.valueOf(v));
			ret.setExtention(null);
			result = new Result(txId, Result.OK).putValue("multiply", ret);
		} finally {
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
		Span span =  this.startServerSpan("pub.pow", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			Map<String,String> fields = new LinkedHashMap<>();
			fields.put("clientId", reqData.getDataBody().getClientId());
			fields.put("a", reqData.getDataParam().getA());
			fields.put("b", reqData.getDataParam().getB());
			span.log(fields);
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
		} finally {
			span.setTag("response", result.toJson());
			span.finish();
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
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
