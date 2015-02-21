package com.rubberjam.netty.rest.annotation;

public enum HttpMethod {

	GET,
	POST,
	PUT,
	DELETE;
	
	public io.netty.handler.codec.http.HttpMethod toNettyHttpMethod() {
		return io.netty.handler.codec.http.HttpMethod.valueOf(name());
	}
	
}
