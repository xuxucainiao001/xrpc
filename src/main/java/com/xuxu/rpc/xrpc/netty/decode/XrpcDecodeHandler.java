package com.xuxu.rpc.xrpc.netty.decode;

import java.util.List;

import com.xuxu.rpc.xrpc.utils.XrpcSerializerUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class XrpcDecodeHandler extends ByteToMessageDecoder {
	

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		 //在读取前标记readerIndex
        in.markReaderIndex();
       //读取头部
        int length = in.readInt();
        if (in.readableBytes() < length) {
       //消息不完整，无法处理，将readerIndex复位
            in.resetReaderIndex();
            return;
        }
        //吧content数据读到contestBytes字节数组中
        byte[] contestBytes=new byte[length];
        ByteBuf bf=in.readBytes(length);
        bf.readBytes(contestBytes);
        //对象反序列化
        out.add(XrpcSerializerUtils.deserialize(contestBytes)) ;    		
	}
	
}
