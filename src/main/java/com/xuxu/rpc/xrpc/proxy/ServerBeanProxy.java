package com.xuxu.rpc.xrpc.proxy;

import com.xuxu.rpc.xrpc.annotations.XrpcProvider;

public interface ServerBeanProxy {
	
	public ServerProxy createXrpcServerProxy(Object intfImpl,XrpcProvider xrpcProvider);
    
}
