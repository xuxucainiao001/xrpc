package com.xuxu.rpc.xrpc.proxy;

import com.xuxu.rpc.xrpc.annotations.XrpcClient;

public interface ClientBeanProxy {
	
	public <T> T createXrpcClientProxy(Class<T> intf,XrpcClient xrpcclient);
	

}
