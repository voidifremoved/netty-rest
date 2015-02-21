package com.rubberjam.netty.rest.util;

import io.netty.handler.codec.http.HttpRequest;

public interface RESTSessionKeyValidator {

	String getSessionKey(HttpRequest request);

	boolean isValid(HttpRequest request, String apiKey, String sessionKey);

}
