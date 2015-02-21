package com.rubberjam.netty.rest;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.rubberjam.netty.rest.message.DefaultRESTMessage;
import com.rubberjam.netty.rest.message.TestMessages;
import com.rubberjam.netty.rest.request.RESTRequest;
import com.rubberjam.netty.rest.response.RESTResponse;

public class TestService {

	public ListenableFuture<RESTResponse<TestMessages.TestResponse>> end(RESTRequest request, TestMessages.TestRequest message) {
		return Futures.immediateFuture(new RESTResponse<TestMessages.TestResponse>(new DefaultRESTMessage<TestMessages.TestResponse>(TestMessages.TestResponse.newBuilder()
				.setApplication(message.getApplication())
				.setId(message.getId())
				.build())));
	}
	
}
