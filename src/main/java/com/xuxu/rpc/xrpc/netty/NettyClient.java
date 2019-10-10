package com.xuxu.rpc.xrpc.netty;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.info.HostInfo;
import com.xuxu.rpc.xrpc.netty.decode.XrpcDecodeHandler;
import com.xuxu.rpc.xrpc.netty.encode.XrpcEncodeHandler;
import com.xuxu.rpc.xrpc.request.RequestEntity;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.ResponseEntity;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
	
	private static Logger logger=LoggerFactory.getLogger(NettyClient.class);
	
	private static ConcurrentMap<Integer, ResponseEntity> responseMap =new ConcurrentHashMap<>();
	
	private static ConcurrentMap<HostInfo,NettyClientInvokeHandler> invokeMap=new ConcurrentHashMap<>();
	
	private static AtomicInteger requestIdGenerater=new AtomicInteger(0) ;
	
	private static EventLoopGroup group = new NioEventLoopGroup(2);	
	
	private NettyClient() {}
	
	private synchronized static NettyClientInvokeHandler createNew(HostInfo hp) {

		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group);
		NettyClientInvokeHandler nettyClientInvokeHandler = new NettyClientInvokeHandler(responseMap, bootstrap, hp);
		invokeMap.put(hp, nettyClientInvokeHandler);
		return nettyClientInvokeHandler;

	}

	public static ResponseEntity invoke(XrpcRequest xrpcRequest) throws InterruptedException {
		HostInfo hostInfo=xrpcRequest.getHostInfo();
		NettyClientInvokeHandler handler=invokeMap.get(hostInfo);
		if(handler==null) {
			handler=createNew(hostInfo);
		}
		int requestId=requestIdGenerater.incrementAndGet();		
		RequestEntity requestEntity=xrpcRequest.newRequestEntity();
		requestEntity.setRequestId(requestId);
        handler.sendRequest(requestEntity);
		//获取返回结果		
		long invokeStart=System.currentTimeMillis();
		while(responseMap.get(requestId)==null) {		
			synchronized(hostInfo) {
				//5秒没有获取结果，重新判断
				hostInfo.wait(5000);
			}
			//10秒超时
			if(System.currentTimeMillis()-invokeStart>10000) {
				logger.error("NettyClient调用超时！requestId:{}",requestId);
				throw new XrpcRuntimeException(ExceptionEnum.E0021);
			}
		}
		return responseMap.get(requestId);
	}

}


class NettyClientInvokeHandler  extends SimpleChannelInboundHandler<ResponseEntity> {
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientInvokeHandler.class);
	
	private ConcurrentMap<Integer, ResponseEntity> responseMap;
    
	private Bootstrap bootstrap;
	
	private Channel channel;
	
	private HostInfo hp;
	
	public NettyClientInvokeHandler(ConcurrentMap<Integer, ResponseEntity> responseMap,
			Bootstrap bootstrap,
			HostInfo hp) {
		this.responseMap=responseMap;
		this.bootstrap=bootstrap;
		this.hp=hp;
		start(this);
	}
	
	/**
	   * 创建Netty客户端
	 */
	private void start(NettyClientInvokeHandler nettyClientInvokeHandler) {
		
		try {
			bootstrap
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.TCP_NODELAY, true)
					//十秒没有找到服务器则抛出异常
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {						
							ch.pipeline()
							  .addLast(new XrpcEncodeHandler<RequestEntity>())
							  .addLast(new XrpcDecodeHandler())
							  .addLast(nettyClientInvokeHandler);
						}
						
					}).connect(hp.getHost().trim(), hp.getPort()).sync();
		}catch(Exception e) {			
			logger.error("创建NettyClient服务发生异常：{}",e);
			throw new XrpcRuntimeException(ExceptionEnum.E0020);
		}	
	}
	
	@Override
	public synchronized void channelActive(ChannelHandlerContext ctx) throws Exception {	
		 logger.info("NettyClient Linked Success...");
		 this.channel=ctx.channel();
		 this.notifyAll();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("NettyClient发生异常：{}", cause);
		//关闭连接
		this.channel.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ResponseEntity responseEntity) throws Exception {
		logger.info("NettyClient收到消息：{}" , responseEntity);
		responseMap.put(responseEntity.getRequestId(), responseEntity);
		synchronized (hp) {
			hp.notifyAll();
		}				
	}
	
	public void sendRequest(RequestEntity requestEntity) throws InterruptedException {
		synchronized (this) {
			while(this.channel==null) {
				this.wait();
			}
			if(!this.channel.isOpen()) {
				start(this);
			}
		}		
		this.channel.writeAndFlush(requestEntity);
	}
	

}