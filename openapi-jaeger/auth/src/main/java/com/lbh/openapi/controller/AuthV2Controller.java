package com.lbh.openapi.controller;

import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Logger;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.tag.Tags;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lbh.common.domain.Result;
import com.lbh.openapi.auth.domain.AppKey;
import com.lbh.openapi.auth.entity.AppKeyEntity;
import com.lbh.openapi.auth.repository.AppKeyRepository;
import com.lbh.openapi.domain.AuthRequest;
import com.lbh.openapi.domain.AuthToken;
import com.lbh.openapi.exception.OpenapiException;
import com.lbh.openapi.exception.StatusCode;
import com.lbh.openapi.util.AuthUtil;
import com.lbh.openapi.util.CryptoUtil;

@RestController
@RequestMapping(value = "/v2/auth/extention")
@Configuration
public class AuthV2Controller {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(AuthV2Controller.class);
	
	@Value("${auth.appVersion}")
	protected String appVersion;
	
	@Value("${auth.seed}")
	protected String seed;
	
	@Autowired
	AppKeyRepository appKeyRepository;

	@Autowired
	Tracer tracer;
	
	protected String txId() {
		return UUID.randomUUID().toString();
	}

	String toJson(Object obj) {
		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyyMMddHHmmss").create();
		return gson.toJson(obj);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<String> getAllList(
			HttpServletRequest request) {
		String txId = txId();
		log.info(txId+",Begin to getAllList");
		Result result = null;
		Span span = this.startServerSpan("auth.extention.list", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			try {
				List<AppKeyEntity> list = appKeyRepository.findAll();
				result = new Result(txId, Result.OK).putValue("appKeys", list);
			} catch(Exception e) {
				StringBuilder sb = new StringBuilder();
				sb.append(e.getClass().getSimpleName());
				if(e instanceof OpenapiException) {
					sb.append(",code:")
						.append(((OpenapiException) e).getCode())
						.append(",message:").append(e.getMessage());
					log.error(sb.toString(),e);
				} else {
					sb.append(",code:")
					.append(StatusCode.UNAUTHORIZED)
					.append(",message:").append(e.getMessage());
				}
				log.error(sb.toString(),e);
				result = new Result(txId, Result.ERROR).putValue("message", sb.toString()).putValue("appVersion", appVersion);
			}
		} finally {
			span.setTag("response", result.toJson());
			span.finish();
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}

	@RequestMapping(value = "/issue", method = RequestMethod.POST)
	public ResponseEntity<String> createAppKey(
			HttpServletRequest request
			, @RequestBody final AppKey reqData) {
		String txId = txId();
		log.info(txId+",Begin to createAppKey,param={"+reqData+"}");
		Result result = null;
		Span span = this.startServerSpan("auth.extention.issue", request);

		try (Scope scope = tracer.scopeManager().activate(span)) {
			try {
				Map<String,String> fields = new LinkedHashMap<>();
				fields.put("reqData", toJson(reqData));
				span.log(fields);
				
				String encAppKey = CryptoUtil.encrypt(reqData.getType()+":"+reqData.getOrgNo(), seed);
				
				AppKeyEntity entity = new AppKeyEntity(reqData);
				entity.setAppKey(encAppKey);
				appKeyRepository.save(entity);
				result = new Result(txId, Result.OK).putValue("appKey", entity);
			} catch(Exception e) {
				StringBuilder sb = new StringBuilder();
				sb.append(e.getClass().getSimpleName());
				if(e instanceof OpenapiException) {
					sb.append(",code:")
						.append(((OpenapiException) e).getCode())
						.append(",message:").append(e.getMessage());
					log.error(sb.toString(),e);
				} else {
					sb.append(",code:")
					.append(StatusCode.UNAUTHORIZED)
					.append(",message:").append(e.getMessage());
				}
				log.error(sb.toString(),e);
				result = new Result(txId, Result.ERROR).putValue("message", sb.toString()).putValue("appVersion", appVersion);
			}
		} finally {
			span.setTag("response", result.toJson());
			span.finish();
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}
	
	@RequestMapping(value = "/validate", method = RequestMethod.POST)
	public ResponseEntity<String> vaildateAppKey(
			HttpServletRequest request
			, @RequestBody final AuthRequest authRequest) {
		String txId = txId();
		Result result = null;
		Span span = this.startServerSpan("auth.extention.validate", request);
		try (Scope scope = tracer.scopeManager().activate(span)) {
			try {
				AuthToken token = AuthUtil.checkToken(authRequest.getToken());
				if ("BASIC".equals(token.getType()) == false) {
					throw new OpenapiException();
				}
				String appKeyString = token.getAppKey();
				log.info("appKeyString==========>"+appKeyString);
				String decAppKeyString = CryptoUtil.decrypt(appKeyString, seed);
				log.info("decAppKeyString==========>"+decAppKeyString);
				Map<String,String> fields = new LinkedHashMap<>();
				fields.put("appKeyString", appKeyString);
				fields.put("decAppKeyString", decAppKeyString);
				span.log(fields);
				if (hasKey(decAppKeyString, span)) {
					result = new Result(txId, Result.OK).putValue("isOK", false);
				} else {
					result = new Result(txId, Result.OK).putValue("isOK", true);
				}
			} catch(Exception e) {
				StringBuilder sb = new StringBuilder();
				sb.append(e.getClass().getSimpleName());
				if(e instanceof OpenapiException) {
					sb.append(",code:")
						.append(((OpenapiException) e).getCode())
						.append(",message:").append(e.getMessage());
					log.error(sb.toString(),e);
				} else {
					sb.append(",code:")
					.append(StatusCode.UNAUTHORIZED)
					.append(",message:").append(e.getMessage());
				}
				log.error(sb.toString(),e);
				result = new Result(txId, Result.ERROR).putValue("message", sb.toString()).putValue("appVersion", appVersion);
			}
		} finally {
			span.setTag("response", result.toJson());
			span.finish();
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}
	
	synchronized private boolean hasKey(String key, Span parent) {
		boolean ret = false;
		String msg = null;
		Span span = tracer.buildSpan("auth.extention.hasKey").asChildOf(parent).start();
		try {
			Map<String, String> fields = new LinkedHashMap<>();
			fields.put("key", key);
			if (null != appKeyRepository.findByAppKey(key)) {
				msg = "Succeeded in finding key";
				ret = true;
			} else {
				msg = "Fained to find key";
			}
			span.log(fields);
		} catch(Exception e) {
			msg = e.getClass().getSimpleName() + "{Failed to check key}";
			log.error(msg, e);
		} finally {
			span.setTag("result", msg);
			span.finish();
		}
		return ret;
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
