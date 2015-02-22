package com.rubberjam.netty.rest;

import java.util.Arrays;

import org.fest.assertions.Assertions;
import org.junit.Test;

import com.google.common.util.concurrent.ListenableFuture;
import com.rubberjam.netty.rest.message.DefaultRESTMessage;
import com.rubberjam.netty.rest.message.TestMessages;
import com.rubberjam.netty.rest.registry.AnnotationRESTEndpointBuilder;
import com.rubberjam.netty.rest.registry.MapServiceLookup;
import com.rubberjam.netty.rest.registry.RESTEndpoint;
import com.rubberjam.netty.rest.registry.RESTEndpointRegistry;
import com.rubberjam.netty.rest.response.RESTResponse;

public class TestAnnotationRESTEndpointBuilder {
	
	@Test
	public void testBuilding() throws Exception {

		TestService svc = new TestService();
		AnnotationRESTEndpointBuilder builder = new AnnotationRESTEndpointBuilder();
		RESTEndpointRegistry registry = new RESTEndpointRegistry();
		MapServiceLookup lookup = new MapServiceLookup();
		lookup.add(svc);
		
		builder.setPackages(Arrays.asList(TestService.class.getPackage().getName()));
		builder.setRegistry(registry);
		builder.setLookup(lookup);
		
		builder.build();
		
		RESTEndpoint restEndpoint = registry.getRESTEndpoint("test", "end");
		ListenableFuture<RESTResponse<TestMessages.TestResponse>> invoke = restEndpoint.invoke(null, new DefaultRESTMessage<TestMessages.TestRequest>(
				TestMessages.TestRequest.newBuilder().setApplication("app").setId("id").build()));
		RESTResponse<TestMessages.TestResponse> restResponse = invoke.get();
		Assertions.assertThat(restResponse.getResponse().getMessage().getId()).isEqualTo("id");
		Assertions.assertThat(restResponse.getResponse().getMessage().getApplication()).isEqualTo("app");
	}
	
}
