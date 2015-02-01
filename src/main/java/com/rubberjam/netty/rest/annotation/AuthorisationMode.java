package com.rubberjam.netty.rest.annotation;

public enum AuthorisationMode {

	NONE,
	API_KEY,
	SESSION_KEY,
	REQUEST_HASH,
	RESPONSE_HASH
}
