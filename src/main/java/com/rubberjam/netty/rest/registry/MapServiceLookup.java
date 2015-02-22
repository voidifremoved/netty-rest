package com.rubberjam.netty.rest.registry;

import java.util.HashMap;
import java.util.Map;

public class MapServiceLookup implements ServiceLookup {

	private Map<String, Object> map = new HashMap<>();
	
	@Override
	public <T> T lookup(Class<T> type, String name) {
		return (T)map.get(name);
	}

	public void add(Object o) {
		String name = AnnotationRESTEndpointBuilder.getServiceName(o.getClass());
		map.put(name, o);
	}

}
