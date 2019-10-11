package com.xuxu.rpc.xrpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
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
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyServerTest {

	private static Logger logger = LoggerFactory.getLogger(NettyServerTest.class);

	public static void main(String[] args) throws InterruptedException {

		EventLoopGroup boss = new NioEventLoopGroup(1);
		EventLoopGroup work = new NioEventLoopGroup();
		ServerBootstrap serverBoostrap = new ServerBootstrap();
		serverBoostrap.group(boss, work);
		serverBoostrap.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_BACKLOG, 1024).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<Channel>() {

					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline()						
						.addLast(new ChannelOutBoundHandler2())
						.addLast(new ChannelOutBoundHandler1())
						.addLast(new ChannelInBoundHandler1())
						.addLast(new ChannelInBoundHandler2());
						System.out.println(ch.pipeline());
					}
				}).bind(8776).sync();
		logger.info("Netty Server 启动成功....");

	}

}

class ChannelInBoundHandler1 extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		String str = byteBuf.toString(CharsetUtil.UTF_8);
		System.out.println("服务端ChannelInBoundHandler1收到消息：" + str);
		ctx.fireChannelRead(Unpooled.copiedBuffer(str, CharsetUtil.UTF_8));
	}

}

class ChannelInBoundHandler2 extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		String str = byteBuf.toString(CharsetUtil.UTF_8);
		System.out.println("服务端ChannelInBoundHandler2收到消息：" + str);
		ctx.writeAndFlush(Unpooled.copiedBuffer(str, CharsetUtil.UTF_8));
	}

}

class ChannelOutBoundHandler1 extends ChannelOutboundHandlerAdapter {
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		String str = byteBuf.toString(CharsetUtil.UTF_8);
		System.out.println("服务端ChannelOutBoundHandler1收到的消息：" + str);
		ctx.writeAndFlush(Unpooled.copiedBuffer(str, CharsetUtil.UTF_8));
	}
}

class ChannelOutBoundHandler2 extends ChannelOutboundHandlerAdapter {
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		String str = byteBuf.toString(CharsetUtil.UTF_8);
		System.out.println("服务端ChannelOutBoundHandler2收到的消息：" + str);
		ctx.writeAndFlush(Unpooled.copiedBuffer(str, CharsetUtil.UTF_8));
	}
}
