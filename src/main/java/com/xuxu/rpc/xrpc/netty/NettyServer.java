package com.xuxu.rpc.xrpc.netty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.context.XrpcResponseContext;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.info.MethodInfo;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;
import com.xuxu.rpc.xrpc.response.XrpcResponseImpl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
	
	private static Logger logger=LoggerFactory.getLogger(NettyServer.class);
	
	private int port;
	
	private Channel channel;
	
	public NettyServer(int port) {
         this.port=port;
	}
	
	public void open() {
		
		EventLoopGroup boss = new NioEventLoopGroup(1);
		EventLoopGroup work = new NioEventLoopGroup();
		ServerBootstrap serverBoostrap = new ServerBootstrap();
		serverBoostrap.group(boss, work);
		try {
			channel=serverBoostrap
			        .childOption(ChannelOption.SO_KEEPALIVE, true)
			        .childOption(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_BACKLOG, 1024)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							ch.pipeline()						
							.addLast(new NettySeverInvokeHandler());
						}
						
					}).bind(port).sync().channel();
			logger.info("Netty Server 启动成功...");
		} catch (Exception e) {
			logger.error("Netty Server 启动失败：{}",e);
			boss.shutdownGracefully();
			work.shutdownGracefully();
		}
	}
	
	/**
	 * 关闭
	 */
	public void closeNettyServer() {
		if(channel!=null&&channel.isOpen()) {
			channel.close();
		}
	}


}


class NettySeverInvokeHandler extends ChannelInboundHandlerAdapter {
	
	private Logger logger=LoggerFactory.getLogger(NettySeverInvokeHandler.class);
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		XrpcRequest xrpcRequest = (XrpcRequest) msg;
		logger.info("服务端NettySeverInvokeHandler收到消息：{}" , xrpcRequest);
		MethodInfo methodInfo=XrpcResponseContext.getMethodCache(xrpcRequest.getRequestKey());
		Method mothod=methodInfo.getMethod();
		Object methodImplObject=methodInfo.getIntfImpl();
		Throwable invokeException=null;
		Object result =null;
		try {
			result=mothod.invoke(methodImplObject, xrpcRequest.getRequestParams());
		} catch (InvocationTargetException e) {
			invokeException=e.getTargetException();
		} catch (Exception e) {
			logger.error("Xrpc服务端方法调用发生异常！");
			throw new XrpcRuntimeException(ExceptionEnum.E0019);
		}
		XrpcResponse xrpcResponse =new XrpcResponseImpl();
		xrpcResponse.setResult(result);
		xrpcResponse.setThrowable(invokeException);
		ctx.writeAndFlush(xrpcResponse);
	}

}
