package com.rubberjam.netty.rest;

import com.rubberjam.netty.rest.request.RESTRequest;

public interface RequestValidator {

	void validate(RESTRequest request);
	
}
