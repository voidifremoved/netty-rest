package com.rubberjam.netty.rest;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;

import org.springframework.util.ReflectionUtils;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Message.Builder;
import com.rubberjam.netty.rest.message.RESTMessage;
import com.rubberjam.netty.rest.message.TestMessages;
import com.rubberjam.netty.rest.registry.MessageBuilder;
import com.rubberjam.netty.rest.registry.RESTEndpoint;
import com.rubberjam.netty.rest.registry.RESTEndpointRegistry;
import com.rubberjam.netty.rest.request.RESTRequest;
import com.rubberjam.netty.rest.util.RESTRequestIdGenerator;

public class TestServer {

	public static void main(String[] args) {
		ListeningExecutorService ex = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
		ListeningScheduledExecutorService schex = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1));
		
		RESTEndpointRegistry registry = new RESTEndpointRegistry();
		TestService svc = new TestService();
		Method m = ReflectionUtils.findMethod(TestService.class, "end", RESTRequest.class, TestMessages.TestRequest.class);
		
		RESTEndpoint endpoint = new RESTEndpoint(svc, m, HttpMethod.GET, new MessageBuilder() {
			
			@Override
			public Builder newRequest() {
				return TestMessages.TestRequest.newBuilder();
			}
		});
		
		registry.addEndpoint("test", "end", endpoint);
		
		RESTServer server = new RESTServerFactory().withPort(10066).withHandler(
				new RESTHandler(registry, new RESTRequestIdGenerator() {
					
					@Override
					public String generateRequestId(HttpRequest request) {
						return "TEST";
					}
				}, ex, schex)).withErrorHandler(new ErrorHandler() {
			
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		}).build();
		server.run();
	}

}
