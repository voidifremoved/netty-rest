package com.rubberjam.netty.rest;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.protobuf.Message;
import com.rubberjam.netty.rest.message.DefaultRESTMessage;
import com.rubberjam.netty.rest.message.RESTMessage;
import com.rubberjam.netty.rest.protobuf.ProtobufUtils;
import com.rubberjam.netty.rest.registry.RESTEndpoint;
import com.rubberjam.netty.rest.registry.RESTEndpointRegistry;
import com.rubberjam.netty.rest.request.RESTRequest;
import com.rubberjam.netty.rest.request.RESTRequest.RESTRequestBuilder;
import com.rubberjam.netty.rest.response.RESTResponse;
import com.rubberjam.netty.rest.util.RESTRequestIdGenerator;

/**
 * Netty channel handler for directing HTTP requests to registered REST
 * endpoints.
 *
 * @author David Hewitt
 */
@Sharable
public class RESTHandler extends SimpleChannelInboundHandler<Object> {

	/** Logging support. */
	private static final Logger LOG = LoggerFactory.getLogger(RESTHandler.class);

	/** The registry of all services. */
	private final RESTEndpointRegistry endpointRegistry;

	/** Generates request IDs. */
	private final RESTRequestIdGenerator requestIdGenerator;

	/** The executor service for async request handling. */
	private ListeningExecutorService executorService;

	/** The scheduled executor service. */
	private ListeningScheduledExecutorService scheduledExecutorService;

	public RESTHandler(RESTEndpointRegistry serviceRegistry, RESTRequestIdGenerator requestIdGenerator,
			ListeningExecutorService executorService, ListeningScheduledExecutorService scheduledExecutorService) {
		super();
		this.endpointRegistry = serviceRegistry;
		this.requestIdGenerator = requestIdGenerator;
		this.executorService = executorService;
		this.scheduledExecutorService = scheduledExecutorService;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {

		if (message instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest)message;
			final QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());

			RESTRequest restRequest = parseRequest(request, decoder);
			if (restRequest == null) {
				sendError(ctx);
			} else {

				RESTEndpoint endpoint = endpointRegistry.getRESTEndpoint(restRequest.getServiceName(), restRequest.getServiceMethod());
				RESTMessage argument = parseArgument(request, decoder, endpoint, restRequest.getEncodingType());
	
				endpoint.validate(restRequest, argument);
	
				handleRequest(ctx, endpoint, restRequest, argument);
			}
		} else {
			sendError(ctx);
		}
	}


	private static void sendError(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
		ctx.write(response);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	private void handleRequest(final ChannelHandlerContext ctx, RESTEndpoint endpoint, RESTRequest restRequest, RESTMessage argument) {
		ListenableFuture<RESTResponse> result = endpoint.invoke(restRequest, argument);
		Futures.addCallback(result, new FutureCallback<RESTResponse>() {

			@Override
			public void onSuccess(RESTResponse result) {
				if (result != null && result.getException() != null) {
					exceptionCaught(ctx, result.getException());
				} else {
					try {
						DefaultFullHttpResponse defaultFullHttpResponse = null;
						if (restRequest.getEncodingType() == EncodingType.BINARY) {
							byte[] byteArray = result.getResponse().getMessage().toByteArray();
							defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, result.getResponseStatus(),
									Unpooled.wrappedBuffer(byteArray));
						} else {
							byte[] byteArray = ProtobufUtils.writeToString(result.getResponse().getMessage(), restRequest.getEncodingType()).getBytes();
							defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, result.getResponseStatus(),
									Unpooled.wrappedBuffer(byteArray));
						}
						endpoint.addResponseHeaders(result, defaultFullHttpResponse);
						ctx.writeAndFlush(defaultFullHttpResponse);
						ctx.close();
					} catch (Throwable t) {
						exceptionCaught(ctx, t);
					}
				}

			}

			@Override
			public void onFailure(Throwable t) {
				exceptionCaught(ctx, t);
			}
		});

	}

	/**
	 * Parses the HTTP request.
	 * 
	 * @param request
	 * @param decoder
	 * @return
	 */
	private RESTRequest parseRequest(HttpRequest request, QueryStringDecoder decoder) {
		RESTRequestBuilder r = RESTRequest.newBuilder().withRequestId(requestIdGenerator.generateRequestId(request))
				.withStartTime(System.currentTimeMillis()).withClientVersion(request.headers().get("X-client-version"));

		String[] parts = StringUtils.delimitedListToStringArray(decoder.path().substring(1), "/");
		String hash = parameter(decoder, "h");

		r.withApiKey(parameter(decoder, "apiKey")).withSessionKey(parameter(decoder, "sessionKey"))
				.withHash(hash == null ? 0 : Long.valueOf(hash));
		
		if (parts.length < 3) {
			return null;
		}
		String encoding = parts[0].toUpperCase();
		
		if (!"BINARY".equals(encoding) && !"JSON".equals(encoding)) {
			return null;
		}
		r.withEncodingType(EncodingType.valueOf(encoding)).withServiceName(parts[1]).withServiceMethod(parts[2]);

		return r.build();
	}

	private String parameter(QueryStringDecoder decoder, String key) {
		return parameter(decoder, key, null);
	}

	private String parameter(QueryStringDecoder decoder, String key, String def) {
		List<String> list = decoder.parameters().get(key);
		if (list == null || list.isEmpty()) {
			return def;
		}
		return list.get(0);
	}

	private RESTMessage parseArgument(FullHttpRequest request, QueryStringDecoder decoder, RESTEndpoint endpoint, EncodingType encodingType) {
		String message = parameter(decoder, "message");

		byte[] body;
		if (message == null) {
			body = request.content().array();
		} else {
			if (encodingType == EncodingType.BINARY) {
				body = Base64Utils.decodeFromString(message);
			} else {
				body = message.getBytes();
			}
		}

		Message msg = ProtobufUtils.readInto(endpoint.newRequest(), body, encodingType);
		return new DefaultRESTMessage(msg);
	}

}
