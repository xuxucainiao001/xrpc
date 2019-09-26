package com.xuxu.rpc.xrpc;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import io.netty.util.CharsetUtil;

public class NettyClientTest {

	private static Logger logger = LoggerFactory.getLogger(NettyClientTest.class);

	public static void main(String[] args) throws Exception {
		ConcurrentHashMap<String, String> responseMap = new ConcurrentHashMap<>();
		NettyClientHandler1 nettyClientHandler = new NettyClientHandler1(responseMap);

		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootStrap = new Bootstrap();
		Channel channel=bootStrap.group(group)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
				.channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {

					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {

							@Override
							public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
									throws Exception {
								ByteBuf byteBuf = (ByteBuf) msg;
								System.out.println("客户端发出的消息为：" + byteBuf.toString(CharsetUtil.UTF_8));
								ctx.writeAndFlush(msg);
							}

						}).addLast(nettyClientHandler);
					}

				}).connect("localhost", 8080).sync().channel();
				
		logger.info("Netty客户端启动完成....");
		String requestId = UUID.randomUUID().toString();
		channel.writeAndFlush(Unpooled.copiedBuffer(requestId+":"+"a", CharsetUtil.UTF_8));

	}

}

class NettyClientHandler1  extends ChannelInboundHandlerAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientHandler1.class);
	
	private ConcurrentHashMap<String, String> responseMap;

	
	public NettyClientHandler1(ConcurrentHashMap<String, String> responseMap) {
		this.responseMap=responseMap;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		 String requestId = UUID.randomUUID().toString();
		 ctx.writeAndFlush(Unpooled.copiedBuffer(requestId+":NettyClinetHandler Linked  Success...", CharsetUtil.UTF_8));		
		 logger.info("NettyClinetHandler Linked Success...");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ByteBuf byteBuf = (ByteBuf) msg;
			String str = byteBuf.toString(CharsetUtil.UTF_8);
			System.out.println("客户端收到消息：" + str);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("发生异常：{}", cause);
	}

}

class NettyClientHandler2 extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(NettyClientHandler2.class);

	private BlockingQueue<String> request = new LinkedBlockingQueue<>();

	private ConcurrentHashMap<String, String> responseMap = new ConcurrentHashMap<>();

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String requestId = UUID.randomUUID().toString();
		 ctx.writeAndFlush(Unpooled.copiedBuffer(requestId+":NettyClinetHandler Linked  Success...", CharsetUtil.UTF_8));		
		logger.info("NettyClinetHandler Linked Success...");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		synchronized (this) {
			ByteBuf byteBuf = (ByteBuf) msg;
			String str = byteBuf.toString(CharsetUtil.UTF_8);
			System.out.println("客户端收到消息：" + str);
			String requestId = str.split(":")[0];
			String result = str.split(":")[1];
			responseMap.put(requestId, result);
			this.notifyAll();
		}
			ctx.writeAndFlush(Unpooled.copiedBuffer(request.take(), CharsetUtil.UTF_8));
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("发生异常：{}", cause);
	}

	public String sentMessage(String message) throws InterruptedException {
		synchronized (this) {
			String requestId = UUID.randomUUID().toString();
			request.put(requestId + ":" + message);
			while (responseMap.get(requestId) == null) {
				this.wait();
			}
			return responseMap.get(requestId);
		}

	}

}
