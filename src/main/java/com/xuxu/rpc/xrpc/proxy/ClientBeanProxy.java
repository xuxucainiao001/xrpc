package com.xuxu.rpc.xrpc.proxy;

import com.xuxu.rpc.xrpc.annotations.XrpcConsumer;

public interface ClientBeanProxy {
	
	public <T> T createXrpcClientProxy(Class<T> intf,XrpcConsumer xrpcConsumer);
	

}
