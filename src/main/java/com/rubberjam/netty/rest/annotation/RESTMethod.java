package com.rubberjam.netty.rest.annotation;

/**
 * Used to annotate methods to indicate that they should be exposed as REST endpoints.
 *
 * @author David Hewitt
 */
public @interface RESTMethod {

	/**
	 * Which HTTP method to use when invoking this REST endpoint.
	 * @return
	 */
	HttpMethod httpMethod();

	/**
	 * The authorisation modes for the REST method.
	 * @return
	 */
	AuthorisationMode[] authorisationMode();
	
	
}
