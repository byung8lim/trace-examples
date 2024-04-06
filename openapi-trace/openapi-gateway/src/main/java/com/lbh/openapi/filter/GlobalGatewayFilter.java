package com.lbh.openapi.filter;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.lbh.openapi.domain.TraceData;
import com.lbh.openapi.util.TraceUtil;

import ch.qos.logback.classic.Logger;
import reactor.core.publisher.Mono;

@Component
public class GlobalGatewayFilter implements GlobalFilter, Ordered {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(GlobalGatewayFilter.class);

	@Value("${spring.application.name}")
	protected String applicationName;
	
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startStmp = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();
        String spanId = TraceUtil.getSpanId();
        TraceData trace = TraceUtil.getTraceData("TRACE", spanId);
        String traceId = trace.getTraceId();
        trace.setStartLog(startStmp)
        	.setServiceName(applicationName)
        	.setMethod(request.getMethod().toString())
        	.setUrl(request.getURI().toString());
        exchange.getRequest().mutate().header(TraceUtil.TRACE_HEADER, traceId+","+spanId).build();

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                	
                    long endStmp = System.currentTimeMillis();
                    trace.setStopLog(endStmp)
                    	.setHttpCode(exchange.getResponse().getStatusCode().toString())
                    	.setLogMsg(null);
                    TraceUtil.remove(trace.getTraceType(), spanId);
                    log.trace(trace.toString());
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
