package com.rubberjam.netty.rest.message;

import java.io.Serializable;

import com.google.protobuf.Message;

public interface RESTMessage<T extends Message> extends Serializable {

	T getMessage();
	
}
