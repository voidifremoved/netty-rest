package com.rubberjam.netty.rest.message;

import com.google.protobuf.Message;

public class DefaultRESTMessage<T extends Message> implements RESTMessage<T>{

	private final T message;
	
	public DefaultRESTMessage(T message) {
		super();
		this.message = message;
	}

	@Override
	public T getMessage() {
		return message;
	}

}
