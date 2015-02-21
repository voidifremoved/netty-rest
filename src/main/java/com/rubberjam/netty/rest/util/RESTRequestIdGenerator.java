package com.rubberjam.netty.rest.util;

import io.netty.handler.codec.http.HttpRequest;

public interface RESTRequestIdGenerator {

	String generateRequestId(HttpRequest request);

}
