package com.rubberjam.netty.rest.registry;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.rubberjam.netty.rest.message.RESTMessage;
import com.rubberjam.netty.rest.request.RESTRequest;
import com.rubberjam.netty.rest.response.RESTResponse;

public class RESTEndpoint {


	private final Object target;
	
	private final Method toInvoke;
	
	private final HttpMethod httpMethod;
	
	private final MessageBuilder messageBuilder;
	
	



	public RESTEndpoint(Object target, Method toInvoke, HttpMethod httpMethod, MessageBuilder messageBuilder) {
		super();
		this.target = target;
		this.toInvoke = toInvoke;
		this.httpMethod = httpMethod;
		this.messageBuilder = messageBuilder;
	}


	public void validate(RESTRequest restRequest, RESTMessage argument) {
		// TODO Auto-generated method stub
		
	}


	public Builder newRequest() {
		return messageBuilder.newRequest();
	}

	@SuppressWarnings("unchecked")
	public <T extends Message, R extends Message> ListenableFuture<RESTResponse<R>> invoke(RESTRequest restRequest, RESTMessage<T> argument) {
		try {
			return (ListenableFuture<RESTResponse<R>>)toInvoke.invoke(target, new Object[] {restRequest, argument.getMessage()});
		} catch (Throwable t) {
			return Futures.<RESTResponse<R>>immediateFailedFuture(t);
		}
	}



	public void addResponseHeaders(RESTResponse result, DefaultFullHttpResponse defaultFullHttpResponse) {
		// TODO Auto-generated method stub
		
	}

	
	
}
