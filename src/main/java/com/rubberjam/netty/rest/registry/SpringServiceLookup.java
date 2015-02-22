package com.rubberjam.netty.rest.registry;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringServiceLookup implements ServiceLookup, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@Override
	public <T> T lookup(Class<T> type, String name) {
		return applicationContext.getBean(type, name);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
