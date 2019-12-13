package com.xuxu.rpc.xrpc.netty;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.context.XrpcRequestContext;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.info.HostInfo;
import com.xuxu.rpc.xrpc.netty.decode.XrpcDecodeHandler;
import com.xuxu.rpc.xrpc.netty.encode.XrpcEncodeHandler;
import com.xuxu.rpc.xrpc.request.RequestEntity;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.ResponseEntity;
import com.xuxu.rpc.xrpc.response.XrpcResponseFuture;
import com.xuxu.rpc.xrpc.stub.ClientStub;

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
	
	private static ConcurrentMap<HostInfo,ClientStub> subMap=new ConcurrentHashMap<>();
	
	private static AtomicInteger requestIdGenerater=new AtomicInteger(0) ;
	
	private static EventLoopGroup group = new NioEventLoopGroup(2);	
	
	private NettyClient() {}
	
	public static ClientStub createStub(HostInfo hp) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group);
		NettyClientInvokeHandler nettyClientInvokeHandler = new NettyClientInvokeHandler(bootstrap, hp);
		subMap.put(hp, nettyClientInvokeHandler);
		return nettyClientInvokeHandler;
	}
	
	public static void removeStub(HostInfo hp) {
		subMap.remove(hp);
	}

	public static ResponseEntity invoke(XrpcRequest xrpcRequest) throws InterruptedException, ExecutionException {
		HostInfo hostInfo=xrpcRequest.getHostInfo();
		ClientStub stub=subMap.get(hostInfo);
		//双重判断减少锁竞争
		if(stub==null) {
		    synchronized (hostInfo){
		    	if(subMap.get(hostInfo)==null) {
			        stub=createStub(hostInfo);
		    	}
		    }
		}		
		int requestId=requestIdGenerater.incrementAndGet();		
		RequestEntity requestEntity=xrpcRequest.newRequestEntity();
		requestEntity.setRequestId(requestId);
		Future<ResponseEntity> future=stub.doInvoke(requestEntity);
		ResponseEntity responseEntity=future.get();
		if(responseEntity==null) {
			throw new XrpcRuntimeException(ExceptionEnum.E0020);
		}
		return responseEntity;
	}

}


class NettyClientInvokeHandler  extends SimpleChannelInboundHandler<ResponseEntity> implements ClientStub{
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientInvokeHandler.class);
    
	private Bootstrap bootstrap;
	
	private Channel channel;
	
	private HostInfo hp;
	
	public NettyClientInvokeHandler(Bootstrap bootstrap,HostInfo hp) {					
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
		NettyClient.removeStub(hp);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ResponseEntity responseEntity) throws Exception {
		logger.info("NettyClient收到消息：{}" , responseEntity);
		//返回结果放入全局map中
		XrpcRequestContext.RESPOSNE_MAP.get(responseEntity.getRequestId()).setResult(responseEntity);			
	}

	@Override
	public  Future<ResponseEntity> doInvoke(RequestEntity requestEntity) throws InterruptedException{		
		XrpcResponseFuture future=new XrpcResponseFuture(requestEntity);
		XrpcRequestContext.RESPOSNE_MAP.put(requestEntity.getRequestId(), future);		
		while(channel==null) {
			synchronized (this) {
			  this.wait(2000);
			}
		}			
		this.channel.writeAndFlush(requestEntity);
		return future;
	}


}