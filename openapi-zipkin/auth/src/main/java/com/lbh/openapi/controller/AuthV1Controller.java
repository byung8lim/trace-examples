package com.lbh.openapi.controller;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Logger;

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

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;

@RestController
@RequestMapping(value = "/v1/auth/basic")
@Configuration
public class AuthV1Controller {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(AuthV1Controller.class);
	
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
			HttpServletRequest request
		) {
		String txId = txId();
		Result result = null;
		TraceContext parent = null;
		Span newSpan = null;
		Span newTrace = null;
		String traceId = request.getHeader("X-B3-TraceId");
		String spanId = request.getHeader("X-B3-SpanId");
		if (traceId == null || spanId == null) {
			newTrace = tracer.newTrace();
		    parent = newTrace.context();
		    newTrace.name("auth.basic.list");
		    newTrace.start();
		    
		} else {
			parent = TraceContext.newBuilder()
	            .traceId(Long.parseUnsignedLong(traceId, 16))
	            .spanId(Long.parseUnsignedLong(spanId, 16))
	            .build();
		}
	    newSpan = this.tracer.newChild(parent);
	    newSpan.name("auth.basic.list");
	    newSpan.start();
		newSpan.tag("Headers", getHeaders(request));
		try {
			List<AppKeyEntity> list = appKeyRepository.findAll();
			result = new Result(txId, Result.OK).putValue("appKeys", list);
			newSpan.tag("response", result.toJson());
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

	@RequestMapping(value = "/issue", method = RequestMethod.POST)
	public ResponseEntity<String> createAppKey(
			HttpServletRequest request
			, @RequestBody final AppKey reqData) {
		String txId = txId();
		log.info(txId+",Begin to createAppKey,param={"+reqData+"}");
		Result result = null;
		TraceContext parent = null;
		Span newSpan = null;
		Span newTrace = null;
		String traceId = request.getHeader("X-B3-TraceId");
		String spanId = request.getHeader("X-B3-SpanId");
		if (traceId == null || spanId == null) {
			newTrace = tracer.newTrace();
		    parent = newTrace.context();
		    newTrace.name("auth.basic.issue");
		    newTrace.start();
		    
		} else {
			parent = TraceContext.newBuilder()
	            .traceId(Long.parseUnsignedLong(traceId, 16))
	            .spanId(Long.parseUnsignedLong(spanId, 16))
	            .build();
		}
	    newSpan = this.tracer.newChild(parent);
	    newSpan.name("auth.basic.issue");
	    newSpan.start();
		newSpan.tag("Headers", getHeaders(request));
		try {
			String encAppKey = CryptoUtil.encrypt(reqData.getType()+":"+reqData.getOrgNo(), seed);
			
			AppKeyEntity entity = new AppKeyEntity(reqData);
			entity.setAppKey(encAppKey);
			appKeyRepository.save(entity);
			result = new Result(txId, Result.OK).putValue("appKey", entity);
			newSpan.tag("response", result.toJson());
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
	
	@RequestMapping(value = "/validate", method = RequestMethod.POST)
	public ResponseEntity<String> vaildateAppKey(
			HttpServletRequest request
			, @RequestBody final AuthRequest authRequest) {
		String txId = txId();
		Result result = null;
		TraceContext parent = null;
		Span newSpan = null;
		Span newTrace = null;
		String traceId = request.getHeader("X-B3-TraceId");
		String spanId = request.getHeader("X-B3-SpanId");
		if (traceId == null || spanId == null) {
			newTrace = tracer.newTrace();
		    parent = newTrace.context();
		    newTrace.name("auth.basic.validate");
		    newTrace.start();
		    
		} else {
			parent = TraceContext.newBuilder()
	            .traceId(Long.parseUnsignedLong(traceId, 16))
	            .spanId(Long.parseUnsignedLong(spanId, 16))
	            .build();
		}
	    newSpan = this.tracer.newChild(parent);
	    newSpan.name("auth.basic.validate");
	    newSpan.start();
		newSpan.tag("Headers", getHeaders(request));
		try {
			AuthToken token = AuthUtil.checkToken(authRequest.getToken());
			if ("BASIC".equals(token.getType()) == false) {
				throw new OpenapiException();
			}
			String appKeyString = token.getAppKey();
			log.info("appKeyString==========>"+appKeyString);
			String decAppKeyString = CryptoUtil.decrypt(appKeyString, seed);
			log.info("decAppKeyString==========>"+decAppKeyString);
			result = new Result(txId, Result.OK).putValue("isOK", true);
			newSpan.tag("response", result.toJson());

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
