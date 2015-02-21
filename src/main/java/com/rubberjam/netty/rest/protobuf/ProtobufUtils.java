package com.rubberjam.netty.rest.protobuf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.googlecode.protobuf.format.JsonFormat;
import com.rubberjam.netty.rest.EncodingType;

public class ProtobufUtils {

	private static final String DEFAULT_CHARSET = "UTF-8";

	private ProtobufUtils() {

	}

	public static <T extends Message> T readInto(Builder builder, byte[] in, EncodingType mode) {
		if (mode == EncodingType.BINARY) {
			return readBinary(builder, new ByteArrayInputStream(in));
		}
		return readText(builder, in, mode);
	}

	public static <T extends Message> T readInto(Builder builder, InputStream in, EncodingType mode) {
		if (mode == EncodingType.BINARY) {
			return readBinary(builder, in);
		}
		return readText(builder, in, mode);
	}

	private static <T> T readBinary(Builder builder, InputStream in) {
		try {
			builder.mergeFrom(in);
			return (T)builder.build();
		} catch (IOException e) {
			throw new ProtobufException("Building from binary failed : " + builder.getDescriptorForType().getName(), e);
		}
	}

	private static <T extends Message> T readText(Builder builder, byte[] in, EncodingType mode) {
		return readText(builder, new String(in), mode);
	}

	private static <T extends Message> T readText(Builder builder, InputStream in, EncodingType mode) {
		try {
			String body = FileCopyUtils.copyToString(new InputStreamReader(in));
			return readText(builder, body, mode);
		} catch (IOException e) {
			throw new ProtobufException("Building from text failed : " + builder.getDescriptorForType().getName(), e);
		}
	}

	private static <T extends Message> T readText(Builder builder, String in, EncodingType mode) {
		if (in == null) {
			return null;
		}
		if (!StringUtils.hasText(in)) {
			return (T)builder.getDefaultInstanceForType();
		}

		if (mode == EncodingType.JSON) {
			try {
				JsonFormat.merge(in, builder);
				return (T)builder.build();
			} catch (JsonFormat.ParseException e) {
				throw new ProtobufException("Building from text failed : " + builder.getDescriptorForType().getName(), e);
			}
		}
		throw new ProtobufException("Unsupported text type : " + builder.getDescriptorForType().getName() + ":" + mode);

	}

	public static void write(Message object, OutputStream out, EncodingType mode) {
		if (object != null) {
			try {
				if (mode == EncodingType.BINARY) {
					object.writeTo(out);
				} else if (mode == EncodingType.JSON) {
					FileCopyUtils.copy(JsonFormat.printToString(object), new OutputStreamWriter(out, DEFAULT_CHARSET));
				} else {
					throw new ProtobufException("Unrecognised encoding mode:" + mode);
				}
				out.flush();
				return;
			} catch (IOException e) {
				throw new ProtobufException("Writing protobuf message " + object + " failed: ", e);
			}
		}
		throw new ProtobufException("Writing null protobuf message failed");
	}

	public static String writeToString(Message object, EncodingType mode) {
		if (object != null) {
			if (mode == EncodingType.BINARY) {
				return object.toByteString().toString();
			} else if (mode == EncodingType.JSON) {
				return JsonFormat.printToString(object);
			} else {
				throw new ProtobufException("Unrecognised encoding mode:" + mode);
			}
		}
		throw new ProtobufException("Writing null protobuf message failed");
	}

}
