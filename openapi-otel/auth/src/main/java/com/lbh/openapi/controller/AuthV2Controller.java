package com.lbh.openapi.controller;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import ch.qos.logback.classic.Logger;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lbh.common.domain.Result;
import com.lbh.common.util.CommonUtil;
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
		Span span = getSpan("auth", "AppKeys");
		Scope scope = span.makeCurrent();
		setReqHeaders(span, request);
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
		} finally {
			setResult(span, result);
			if (null != scope) {
				scope.close();
			}
			if (null != span) {
				span.end();
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
		Span span = getSpan("auth", "create_appkey");
		Scope scope = span.makeCurrent();
		setReqHeaders(span, request);
		try {
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
		} finally {
			setResult(span, result);
			if (null != scope) {
				scope.close();
			}
			if (null != span) {
				span.end();
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
		Span span = getSpan("auth", "token_validation");
		Scope scope = span.makeCurrent();
		setReqHeaders(span, request);
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
		} finally {
			setResult(span, result);
			if (null != scope) {
				scope.close();
			}
			if (null != span) {
				span.end();
			}
		}
		return new ResponseEntity<String>(result.toJson(), result.status());
	}
	
	private void setReqHeaders(Span span, HttpServletRequest request) {
		Enumeration keys = request.getHeaderNames();
		AttributesBuilder builder = Attributes.builder();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			builder.put(key, request.getHeader(key));
			
		}
		span.addEvent("headers", builder.build());
	}
	
	private void setResult(Span span, Result result) {
		AttributesBuilder builder = Attributes.builder();
		builder.put("txId", result.getTxId());
		builder.put("code", String.valueOf(result.getCode()));

		Set<String> keys = result.getResult().keySet();
		for (String key : keys) {
			builder.put(key, CommonUtil.toJson(result.getResult().get(key)));
		}
		span.addEvent("result", Attributes.builder().build());
	}
	
	private Span getSpan(String traceName, String spanName) {
		Tracer tracer = GlobalOpenTelemetry.getTracer(traceName);
		Span currentSpan = Span.current();
		return tracer.spanBuilder(spanName).startSpan();
	}
}
