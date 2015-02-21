package com.rubberjam.netty.rest.protobuf;

public class ProtobufException extends RuntimeException {

	public ProtobufException() {
		super();
	}

	public ProtobufException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ProtobufException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtobufException(String message) {
		super(message);
	}

	public ProtobufException(Throwable cause) {
		super(cause);
	}

	
	
}
