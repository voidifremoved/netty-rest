package com.rubberjam.netty.rest.registry;

import java.util.HashMap;
import java.util.Map;

public class RESTEndpointRegistry {

	private Map<String, Map<String, RESTEndpoint>> endpoints = new HashMap<>();
	
	public RESTEndpoint getRESTEndpoint(String serviceName, String serviceMethod) {
		Map<String, RESTEndpoint> map = endpoints.get(serviceName);
		if (map != null) {
			return map.get(serviceMethod);
		}
		
		return null;
	}
	
	public void addEndpoint(String service, String method, RESTEndpoint endpoint) {
		Map<String, RESTEndpoint> map = endpoints.get(service);
		if (map == null) {
			map = new HashMap<>();
			endpoints.put(service, map);
		}
		map.put(method, endpoint);
	}

}
