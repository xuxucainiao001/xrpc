package com.xuxu.rpc.xrpc.netty;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.info.HostInfo;
import com.xuxu.rpc.xrpc.request.RequestEntity;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.ResponseEntity;
import com.xuxu.rpc.xrpc.utils.XrpcSerializerUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
	
	private static Logger logger=LoggerFactory.getLogger(NettyClient.class);
	
	private static ConcurrentMap<Integer, ResponseEntity> responseMap =new ConcurrentHashMap<>();
	
	private static ConcurrentMap<HostInfo,Channel> invokeMap=new ConcurrentHashMap<>();
	
	private static NettyClientInvokeHandler nettyClientInvokeHandler=new NettyClientInvokeHandler(responseMap);
	
	private static AtomicInteger requestIdGenerater=new AtomicInteger(0) ;
	
	private NettyClient() {}
	
	private static void createNew(HostInfo hp){
		Channel channel=null;
		EventLoopGroup group = new NioEventLoopGroup(2);
		try {
			Bootstrap bootstrap = new Bootstrap();
			channel=bootstrap.group(group)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.TCP_NODELAY, true)
					//十秒没有找到服务器则抛出异常
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {						
							ch.pipeline()
							  .addLast(new NettyClientEncoderHandler())
							  .addLast(new NettyClientDecoderHandler())
							  .addLast(nettyClientInvokeHandler);
						}
						
					}).connect(hp.getHost().trim(), hp.getPort()).sync().channel();
			//注册连接
		}catch(Exception e) {
			logger.error("创建NettyClient服务发生异常：{}",e);
			group.shutdownGracefully();
			throw new XrpcRuntimeException(ExceptionEnum.E0020);
		}
		if(channel!=null&&channel.isOpen()) {			
			invokeMap.put(hp,channel);
		}
	}
	
	public static int invoke(XrpcRequest xrpcRequest) {
		HostInfo hostInfo=xrpcRequest.getHostInfo();
		Channel channel=invokeMap.get(hostInfo);
		if(channel==null||!channel.isOpen()) {
			createNew(hostInfo);
		}
		int requestId=requestIdGenerater.incrementAndGet();		
		RequestEntity requestEntity=xrpcRequest.newRequestEntity();
		requestEntity.setRequestId(requestId);
		invokeMap.get(hostInfo).writeAndFlush(requestEntity);
		return requestId;
	}
	
	public static ResponseEntity getResult(int requestId){
		ResponseEntity responseEntity=null;
		try {
			responseEntity = nettyClientInvokeHandler.getXrpcResponse(requestId);
		} catch (Exception e) {
			logger.error("获取调用结果异常：{}",e);
		}
		return responseEntity;
	}

}

class NettyClientEncoderHandler extends  ChannelOutboundHandlerAdapter{
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientEncoderHandler.class);
	
	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		RequestEntity requestEntity=(RequestEntity)msg;
		logger.info("NettyClient发送的对象：{}",requestEntity);	
		byte[] bytes=XrpcSerializerUtils.serialize(requestEntity);		
        ctx.writeAndFlush(Unpooled.copiedBuffer(bytes));
    }
	
}

class NettyClientDecoderHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientDecoderHandler.class);
	 
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		    ByteBuf byteBuf =(ByteBuf)msg;
		 	ResponseEntity responseEntity=XrpcSerializerUtils.deserialize(byteBuf.array());
			logger.info("NettyClient收到消息：{}" ,responseEntity);
			ctx.fireChannelRead(responseEntity);
	}
	
}


class NettyClientInvokeHandler  extends ChannelInboundHandlerAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientInvokeHandler.class);
	
	private ConcurrentMap<Integer, ResponseEntity> responseMap;

	
	public NettyClientInvokeHandler(ConcurrentMap<Integer, ResponseEntity> responseMap) {
		this.responseMap=responseMap;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {	
		 logger.info("NettyClientChannel Linked Success...");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ResponseEntity responseEntity=(ResponseEntity)msg;
			logger.info("NettyClient收到消息：{}" , responseEntity);
			responseMap.put(responseEntity.getRequestId(), responseEntity);
			synchronized (this) {
				notifyAll();
			}			
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("NettyClient发生异常：{}", cause);
		//关闭连接
		ctx.channel().close();
	}
	
	public synchronized ResponseEntity getXrpcResponse(Integer requestId) throws InterruptedException {
		int i=0;
		while(responseMap.get(requestId)==null) {
			i++;
			synchronized(this) {
				//5秒没有获取结果，抛出异常
				this.wait(5000);
			}
			if(i>2) {
				logger.error("NettyClient调用超时！requestId:{}",requestId);
				throw new XrpcRuntimeException(ExceptionEnum.E0021);
			}
		}
		return responseMap.get(requestId);
	} 

}