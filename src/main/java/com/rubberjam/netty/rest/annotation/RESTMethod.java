package com.rubberjam.netty.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate methods to indicate that they should be exposed as REST endpoints.
 *
 * @author David Hewitt
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
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
	AuthorisationMode[] authorisationMode() default {		
		AuthorisationMode.API_KEY
	};
	
	
}
