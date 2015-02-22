package com.rubberjam.netty.rest.registry;

public interface ServiceLookup {

	<T> T lookup(Class<T> type, String name);
	
}
