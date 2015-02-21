package com.rubberjam.netty.rest;

import io.netty.bootstrap.ServerBootstrap;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTServerFactory {

	private static final Logger LOG = LoggerFactory.getLogger(RESTServerFactory.class);

	public static final int PORT_RANGE = 100;

	private ExecutorService mainExecutor;
	
	private ExecutorService workerExecutor;

	private RESTHandler handler;
	
	private ErrorHandler errorHandler;
	
	private int port;

	private boolean reuseAddress;

	private boolean keepAlive = true;

	private boolean tcpNoDelay = true;

	private ServerBootstrap bootstrap;

	public void setMainExecutor(ExecutorService mainExecutor) {
		this.mainExecutor = mainExecutor;
	}

	public void setWorkerExecutor(ExecutorService workerExecutor) {
		this.workerExecutor = workerExecutor;
	}

	public RESTServerFactory withPort(int port) {
		this.port = port;
		return this;
	}

	public RESTServerFactory withReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
		return this;
	}

	public RESTServerFactory withKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}

	public RESTServerFactory withTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
		return this;
	}

	public RESTServerFactory withBootstrap(ServerBootstrap bootstrap) {
		this.bootstrap = bootstrap;
		return this;
	}

	
	
	public RESTServerFactory withHandler(RESTHandler handler) {
		this.handler = handler;
		return this;
	}

	public RESTServerFactory withErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		return this;
	}

	public RESTServer build() {
		RESTServer r = new RESTServer(port, keepAlive, handler, errorHandler);
		return r;
		
		
	}
}
