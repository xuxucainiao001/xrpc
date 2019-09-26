package com.xuxu.rpc.xrpc.netty;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.info.HostInfo;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.request.XrpcRequestWrapper;
import com.xuxu.rpc.xrpc.response.XrpcResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
	
	private static Logger logger=LoggerFactory.getLogger(NettyClient.class);
	
	private static ConcurrentMap<Integer, XrpcResponse> responseMap =new ConcurrentHashMap<>();
	
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
							ch.pipeline().addLast().addLast(nettyClientInvokeHandler);
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
		invokeMap.get(hostInfo).writeAndFlush(new XrpcRequestWrapper(xrpcRequest) {
			@Override
			public Integer getRequestId() {
				return requestId;
			}
			
		});
		return requestId;
	}
	
	public static XrpcResponse getResult(int requestId){
		XrpcResponse response=null;
		try {
			response = nettyClientInvokeHandler.getXrpcResponse(requestId);
		} catch (Exception e) {
			logger.error("获取调用结果异常：{}",e);
		}
		return response;
	}

}



class NettyClientInvokeHandler  extends ChannelInboundHandlerAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientInvokeHandler.class);
	
	private ConcurrentMap<Integer, XrpcResponse> responseMap;

	
	public NettyClientInvokeHandler(ConcurrentMap<Integer, XrpcResponse> responseMap) {
		this.responseMap=responseMap;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {	
		 logger.info("NettyClientChannel Linked Success...");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			XrpcResponse respone=(XrpcResponse)msg;
			logger.info("NettyClient收到消息：{}" , respone);
			responseMap.put(respone.getRequestId(), respone);
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
	
	public synchronized XrpcResponse getXrpcResponse(Integer requestId) throws InterruptedException {
		int i=0;
		while(responseMap.get(requestId)==null) {
			i++;
			synchronized(this) {
				//10秒没有获取结果，抛出异常
				this.wait(5000);
			}
			if(i>2) {
				logger.error("创建NettyClient调用超时！requestId:{}",requestId);
				throw new XrpcRuntimeException(ExceptionEnum.E0021);
			}
		}
		return responseMap.get(requestId);
	} 

}