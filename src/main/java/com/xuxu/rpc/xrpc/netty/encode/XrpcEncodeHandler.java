package com.xuxu.rpc.xrpc.netty.encode;

import com.xuxu.rpc.xrpc.utils.ByteUtils;
import com.xuxu.rpc.xrpc.utils.XrpcSerializerUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class XrpcEncodeHandler<T> extends MessageToByteEncoder<Object>{

	@Override
	@SuppressWarnings("unchecked")
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		T t = (T) msg;
		byte[] content=XrpcSerializerUtils.serialize(t);
		int length=content.length;
		byte[] head=ByteUtils.intToByteArray(length);
		ByteBufAllocator byteBufAllocator=ctx.alloc();
		//对象字节数组长度+对象字节数组
		CompositeByteBuf compositeByteBuf=byteBufAllocator.compositeBuffer(2);
		compositeByteBuf.addComponents(
				true, 
				byteBufAllocator.buffer().writeBytes(head),
				byteBufAllocator.buffer().writeBytes(content)
				);
		ctx.writeAndFlush(compositeByteBuf);
	}

}
