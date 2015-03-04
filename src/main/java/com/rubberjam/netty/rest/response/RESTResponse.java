package com.rubberjam.netty.rest.response;

import io.netty.handler.codec.http.HttpResponseStatus;

import com.google.protobuf.Message;
import com.rubberjam.netty.rest.message.DefaultRESTMessage;
import com.rubberjam.netty.rest.message.RESTMessage;

/**
 * Represents a REST API response.
 *
 * @author David Hewitt
 * @param <T>
 */
public class RESTResponse<T extends Message> {
	
	private final RESTMessage<T> response;
	
	private final Throwable exception;
	
	private final HttpResponseStatus responseStatus;


	public RESTResponse(RESTMessage<T> response) {
		this(response, HttpResponseStatus.OK);
	}
	

	public RESTResponse(RESTMessage<T> response, HttpResponseStatus responseCode) {
		this(response, null, responseCode);
	}
	
	public RESTResponse(Throwable exception) {
		this(null, exception, HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}
	
	public RESTResponse(RESTMessage<T> response, Throwable exception, HttpResponseStatus responseCode) {
		super();
		this.response = response;
		this.exception = exception;
		this.responseStatus = responseCode;
	}

	public static <T extends Message> RESTResponse<T> defaultResponse(T message) {
		return new RESTResponse<T>(new DefaultRESTMessage<T>(message));
	}
	
	
	public RESTMessage<T> getResponse() {
		return response;
	}


	public Throwable getException() {
		return exception;
	}


	public HttpResponseStatus getResponseStatus() {
		return responseStatus;
	}



	
	
	
}
