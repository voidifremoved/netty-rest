package com.rubberjam.netty.rest.util;

import io.netty.handler.codec.http.HttpRequest;

public interface RESTSignatureValidator {

	long getRequestHash(HttpRequest request);

	boolean isValid(HttpRequest request, String apiKey, String sessionKey, long hash);

}
