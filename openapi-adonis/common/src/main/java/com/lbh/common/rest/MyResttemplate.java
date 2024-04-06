package com.lbh.common.rest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import ch.qos.logback.classic.Logger;

@Configuration
public class MyResttemplate extends RestTemplate {
	@Value("${rest.connectionTimeout:10000}")
	private int connectionTimeout;
	
	@Value("${rest.readTimeout:10000}")
	private int readTimeout;
	
	protected final static Logger log = (Logger)LoggerFactory.getLogger(MyResttemplate.class);
	
	public MyResttemplate() {
        if (getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
        	if (log.isDebugEnabled()) {
        		log.debug("HTTP", "HttpUrlConnection is used");
        	}
            ((SimpleClientHttpRequestFactory) getRequestFactory()).setConnectTimeout(connectionTimeout);
            ((SimpleClientHttpRequestFactory) getRequestFactory()).setReadTimeout(readTimeout);
        } else if (getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory) {
        	if (log.isDebugEnabled()) {
            	log.debug("HTTP", "HttpClient is used");
        	}
            ((HttpComponentsClientHttpRequestFactory) getRequestFactory()).setReadTimeout(connectionTimeout);
            ((HttpComponentsClientHttpRequestFactory) getRequestFactory()).setConnectTimeout(readTimeout);
        }
	}
}
