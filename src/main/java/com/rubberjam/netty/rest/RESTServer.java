package com.rubberjam.netty.rest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point to standalone REST server.
 *
 * @author David Hewitt
 */
public class RESTServer implements Runnable {

	/** Logging support. */
	private static final Logger LOG = LoggerFactory.getLogger(RESTServer.class);

	private final ServerBootstrap bootstrap;

	private final EventLoopGroup bossGroup;

	private final EventLoopGroup workerGroup;

	private final int port;

	/** The primary handler for REST calls. */
	private final RESTHandler handler;
	
	/** Optional error handler to receive any port binding or bootstrap errors. */
	private final ErrorHandler errorHandler;

	/** The channel is retained after initialization, to handle graceful shutdown. */
	private Channel channel;

	/**
	 * 
	 * @param port
	 * @param keepalive
	 * @param handler
	 * @param errorHandler
	 */
	public RESTServer(int port, boolean keepalive, final RESTHandler handler, ErrorHandler errorHandler) {
		this.port = port;
		this.handler = handler;
		this.errorHandler = errorHandler;
		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();

		bootstrap = new ServerBootstrap();
		ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline()
					.addLast(new HttpRequestDecoder())
					.addLast(new HttpResponseEncoder())
					.addLast(new HttpObjectAggregator(1024 * 1024))
					.addLast(RESTServer.this.handler);
			}
		};
		
		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(channelInitializer)
				.option(ChannelOption.SO_BACKLOG, 128) // (5)
				.childOption(ChannelOption.SO_KEEPALIVE, keepalive); 


	}

	@Override
	public void run() {
		try {
			// Bind and start to accept incoming connections.
			ChannelFuture f = bootstrap.bind(port).sync();
			channel = f.channel();
			f.channel().closeFuture().sync();
		} catch (Throwable t) {
			LOG.error("Error occurred in netty server bootstrap", t);
			if (errorHandler != null) {
				errorHandler.onError(t);
			}
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public void stop() {
		if (channel != null) {
			channel.close();
		}
	}



}
