package com.xuxu.rpc.xrpc.proxy;

import com.xuxu.rpc.xrpc.annotations.XrpcServer;

public interface ServerBeanProxy {
	
	public ServerProxy createXrpcServerProxy(Object intfImpl,XrpcServer xprcServer);
    
}
