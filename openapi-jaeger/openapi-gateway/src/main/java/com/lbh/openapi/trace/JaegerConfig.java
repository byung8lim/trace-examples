package com.lbh.openapi.trace;

import org.apache.thrift.transport.TTransportException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.Logger;
import io.opentracing.Tracer;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.spi.Sender;
import io.jaegertracing.thrift.internal.senders.UdpSender;


@Configuration
public class JaegerConfig {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(JaegerConfig.class);
	
	@Value("${opentracing.jaeger.service-name}")
	private String serviceName;
	@Value("${opentracing.jaeger.udp-sender.host}")
	private String jaegerHost;
	@Value("${opentracing.jaeger.udp-sender.port}")
	private int jaegerPort;
	
    @Bean
    public Tracer initTracer() throws TTransportException {
    	Tracer tracer = null;
    	try {
	    	Sender sender = new UdpSender(jaegerHost, jaegerPort, 0);
	        RemoteReporter reporter = new RemoteReporter.Builder()
	                .withSender(sender)
	                .build();
	        tracer = new JaegerTracer.Builder(serviceName)
	        		.withReporter(reporter)
	                .withSampler(new ConstSampler(true))
	                .build();
    	} catch(TTransportException e) {
    		log.error("FAILED to build JaegerTrace", e);
    		throw e;
    	}
    	if (log.isInfoEnabled()) {
    		log.info("JaegerTracer serviceName:"+serviceName+",jaegerHost:"+jaegerHost+",jaegerPort"+jaegerPort);
    	}
    	return tracer;
    }

}
