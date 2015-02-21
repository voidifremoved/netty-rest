package com.rubberjam.netty.rest.request;

import com.rubberjam.netty.rest.EncodingType;
import com.rubberjam.netty.rest.message.RESTMessage;

public final class RESTRequest {

	private final EncodingType encodingType;

	private final String serviceName;

	private final String serviceMethod;

	private final String apiKey;

	private final String sessionKey;

	private final String requestId;

	private final String clientVersion;

	private final long hash;

	private final long startTime;

	public RESTRequest(EncodingType encodingType, String serviceName, String serviceMethod, String apiKey, String sessionKey,
			String requestId, String clientVersion, long hash, long startTime) {
		super();
		this.encodingType = encodingType;
		this.serviceName = serviceName;
		this.serviceMethod = serviceMethod;
		this.apiKey = apiKey;
		this.sessionKey = sessionKey;
		this.requestId = requestId;
		this.clientVersion = clientVersion;
		this.hash = hash;
		this.startTime = startTime;
	}

	public EncodingType getEncodingType() {
		return encodingType;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceMethod() {
		return serviceMethod;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public String getRequestId() {
		return requestId;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public long getHash() {
		return hash;
	}

	
	public long getStartTime() {
		return startTime;
	}




	public static class RESTRequestBuilder {
		private EncodingType encodingType;
		private String serviceName;
		private String serviceMethod;
		private String apiKey;
		private String sessionKey;
		private String requestId;
		private String clientVersion;
		private long hash;
		private long startTime;

		public RESTRequestBuilder withEncodingType(EncodingType encodingType) {
			this.encodingType = encodingType;
			return this;
		}

		public RESTRequestBuilder withServiceName(String serviceName) {
			this.serviceName = serviceName;
			return this;
		}

		public RESTRequestBuilder withServiceMethod(String serviceMethod) {
			this.serviceMethod = serviceMethod;
			return this;
		}

		public RESTRequestBuilder withApiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public RESTRequestBuilder withSessionKey(String sessionKey) {
			this.sessionKey = sessionKey;
			return this;
		}

		public RESTRequestBuilder withRequestId(String requestId) {
			this.requestId = requestId;
			return this;
		}

		public RESTRequestBuilder withClientVersion(String clientVersion) {
			this.clientVersion = clientVersion;
			return this;
		}

		public RESTRequestBuilder withHash(long hash) {
			this.hash = hash;
			return this;
		}

		public RESTRequestBuilder withStartTime(long startTime) {
			this.startTime = startTime;
			return this;
		}



		public RESTRequest build() {
			return new RESTRequest(encodingType, serviceName, serviceMethod, apiKey, sessionKey, requestId, clientVersion, hash, startTime);
		}
	}

	public static <T extends RESTMessage> RESTRequestBuilder newBuilder() {
		return new RESTRequestBuilder();
	}

}