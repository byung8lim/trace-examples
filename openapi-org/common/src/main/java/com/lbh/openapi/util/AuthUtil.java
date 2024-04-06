package com.lbh.openapi.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lbh.openapi.domain.AuthToken;
import com.lbh.openapi.exception.OpenapiException;
import com.lbh.openapi.exception.StatusCode;

import ch.qos.logback.classic.Logger;

public class AuthUtil {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(AuthUtil.class);
	private static final String AUTHORIZATION_PREFIX="Bearer";

	public static String getToken(HttpServletRequest request) throws OpenapiException {
		String authorization = request.getHeader("Authorization");
		if (authorization == null || authorization == "") {
			throw new OpenapiException("Authorization header not defined").setCode(StatusCode.UNAUTHORIZED.code);
		}
		
		if (!authorization.startsWith(AUTHORIZATION_PREFIX)) {
			throw new OpenapiException("Illegal Authorization format").setCode(StatusCode.UNAUTHORIZED.code);
		}
		
		if (authorization.length() <= (AUTHORIZATION_PREFIX.length()+1)) {
			throw new OpenapiException("Illegal Authorization format - too short").setCode(StatusCode.UNAUTHORIZED.code);
		}
		return authorization.substring(AUTHORIZATION_PREFIX.length()+1);
	}
	
	public static AuthToken checkToken(HttpServletRequest request) throws OpenapiException {
		String token = getToken(request);		
		return AuthToken.fromArray(fromToken(token));
	}
	
	public static AuthToken checkToken(String authString) throws OpenapiException {
		return AuthToken.fromArray(fromToken(authString));
	}
	
	public static String[] fromToken(String token) {
		return new String(Base64.decodeBase64(token.getBytes())).split(":");
	}

	public static String toToken(String...strings) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			sb.append(strings[i]);
			if (i < (strings.length-1)) {
				sb.append(":");
			}
		}
		return Base64.encodeBase64String(sb.toString().getBytes());
	}

	
	public static HttpHeaders setAuthorizationHeader(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", AUTHORIZATION_PREFIX+" "+token);
		return headers;
	}
	
	public static HttpHeaders addHeader(String key, String value, HttpHeaders headers) {
		headers.add(key, value);
		return headers;
	}
	
	public static String getErrorFromString (String txid, String str) throws OpenapiException {
		String retString = null;
		try {
	    	JsonParser jsonParser = new JsonParser();
	    	JsonObject jResponse = (JsonObject)jsonParser.parse(str);
	    	log.info("after parsing : "+jResponse.toString());
	    	if (jResponse == null || jResponse.isJsonNull()) {
	    		throw new OpenapiException("NO_JSON_RESPONSE").setCode(StatusCode.UNAUTHORIZED.code);
	    	}
	    	JsonObject result = jResponse.get("result").getAsJsonObject();
	    	log.info("result json : "+result.toString());
	    	retString = result.get("message").getAsString();
			
		} catch (Exception e) {
			if (e instanceof OpenapiException) {
				throw e;
			} else {
				throw new OpenapiException(e.getClass().getSimpleName()+",message:"+e.getMessage()).setCode(StatusCode.UNAUTHORIZED.code);
			}
		}
		return retString;
	}
	
	public static boolean validateFromString(String txid, String str) throws OpenapiException {
		boolean ret = false;
		try {
	    	JsonParser jsonParser = new JsonParser();
	    	JsonObject jResponse = (JsonObject)jsonParser.parse(str);
	    	log.info("after parsing : "+jResponse.toString());
	    	if (jResponse == null || jResponse.isJsonNull()) {
	    		throw new OpenapiException("NO_JSON_RESPONSE").setCode(StatusCode.UNAUTHORIZED.code);
	    	}
	    	JsonObject result = jResponse.get("result").getAsJsonObject();
	    	log.info("result json : "+result.toString());
	    	ret = result.get("isOK").getAsBoolean();
		} catch (Exception e) {
			if (e instanceof OpenapiException) {
				throw e;
			} else {
				throw new OpenapiException(e.getClass().getSimpleName()+",message:"+e.getMessage()).setCode(StatusCode.UNAUTHORIZED.code);
			}
		}
		return ret;
	}
}
