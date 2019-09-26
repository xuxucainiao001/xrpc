package com.xuxu.rpc.xrpc;

import com.xuxu.rpc.xrpc.netty.NettyServer;

/**
 * XrpcServer
 * 
 * @author xuxu
 *
 */
public interface XrpcServer {

	public static XrpcServer open(int port) {
		NettyXrpcServer nettyXrpcServer = new NettyXrpcServer();
		nettyXrpcServer.start(port);
		return nettyXrpcServer;
	}

	 void start(int port);
		
}

/**
 * Netty的实现
 * 
 * @author xuxu
 *
 */
class NettyXrpcServer implements XrpcServer {

	private volatile boolean isOpen = false;

	private NettyServer nettyServer;

	@Override
	public synchronized void start(int port) {
		if (!isOpen) {
			nettyServer = new NettyServer(port);
			nettyServer.open();
			isOpen = true;
		}
	}

	public synchronized void close() {
		if (isOpen) {
			nettyServer.closeNettyServer();
			isOpen = false;
		}

	}

}