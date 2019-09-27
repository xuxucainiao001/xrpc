package com.xuxu.rpc.xrpc.netty.decode;

import org.apache.commons.lang.ArrayUtils;

import com.xuxu.rpc.xrpc.utils.ByteUtils;
import com.xuxu.rpc.xrpc.utils.XrpcSerializerUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class XrpcEncodeHandler<T> extends MessageToByteEncoder<Object>{

	@Override
	@SuppressWarnings("unchecked")
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		T t = (T) msg;
		byte[] content=XrpcSerializerUtils.serialize(t);
		int length=content.length;
		//对象字节数组长度+对象字节数组
		byte[] requestBytes=ArrayUtils.addAll(ByteUtils.intToByteArray(length),content);
        out.writeBytes(requestBytes);		
	}

}
