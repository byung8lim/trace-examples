package com.lbh.openapi.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import brave.Span;
import brave.Tracer;
import brave.propagation.CurrentTraceContext.Scope;
import brave.propagation.TraceContext;
import ch.qos.logback.classic.Logger;
import reactor.core.publisher.Mono;

@Component
public class GlobalGatewayFilter implements GlobalFilter, Ordered {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(GlobalGatewayFilter.class);

	@Value("${spring.application.name}")
	protected String applicationName;
	
	@Autowired
	Tracer tracer;
	
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startStmp = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();
        Span span = tracer.newTrace().name(exchange.getRequest().getPath().toString()).start();
        TraceContext context = span.context();
        String traceId = context.traceIdString();
        String spanId = context.spanIdString();
        try {
            Set<String> names = request.getHeaders().keySet();
            Map<String,String> headers = new HashMap<>();
            for (String name : names) {
            	log.info("Header setting - "+name+" => "+request.getHeaders().get(name));
            	headers.put(name, request.getHeaders().get(name).get(0));
            }
            span.tag("headers", toJson(headers));
            exchange.getRequest().mutate().header("X-B3-TraceId", traceId).build();
            exchange.getRequest().mutate().header("X-B3-SpanId", spanId).build();
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                   }));

        } finally {
        	span.finish();
        }

    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
    
	String toJson(Object obj) {
		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyyMMddHHmmss").create();
		return gson.toJson(obj);
	}
}
