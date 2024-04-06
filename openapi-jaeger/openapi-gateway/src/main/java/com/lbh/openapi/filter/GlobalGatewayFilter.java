package com.lbh.openapi.filter;

import java.util.Iterator;
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

import ch.qos.logback.classic.Logger;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.tag.Tags;
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
        Span span = tracer.buildSpan(request.getPath().toString()).start();
        try (Scope scope = tracer.scopeManager().activate(span)) {
            Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
            Tags.HTTP_URL.set(span, request.getPath().toString());
            Tags.HTTP_METHOD.set(span, request.getMethod().toString());
            HttpHeaders headers = new HttpHeaders();
            HttpHeaderInjectAdapter carrier = new HttpHeaderInjectAdapter(headers);
            tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, carrier);
            Set<String> names = headers.keySet();
            for (String name : names) {
            	log.info("Header setting - "+name+" => "+headers.get(name));
                exchange.getRequest().mutate().header(name, headers.get(name).get(0)).build();
            }
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
    
	String toJson(Object obj) {
		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyyMMddHHmmss").create();
		return gson.toJson(obj);
	}
}
