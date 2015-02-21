package com.rubberjam.netty.rest;

import io.netty.handler.codec.http.HttpRequest;

import com.rubberjam.netty.rest.registry.RESTEndpoint;
import com.rubberjam.netty.rest.registry.RESTEndpointRegistry;
import com.rubberjam.netty.rest.util.RESTRequestIdGenerator;

public class TestServer {

	public static void main(String[] args) {
		RESTEndpointRegistry registry = new RESTEndpointRegistry();
		RESTEndpoint endpoint = new RESTEndpoint(args, null, null, null)
		
		registry.addEndpoint("test", "end", null);
		
		RESTServer server = new RESTServerFactory().withPort(10066).withHandler(
				new RESTHandler(null, new RESTRequestIdGenerator() {
					
					@Override
					public String generateRequestId(HttpRequest request) {
						return "TEST";
					}
				}, null, null)).withErrorHandler(new ErrorHandler() {
			
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		}).build();
		server.run();
	}

}
