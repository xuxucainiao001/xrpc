package com.xuxu.rpc.xrpc.proxy;

import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;
/**
 * 服务端统一调用代理
 * @author xuxu
 *
 */
public interface ServerProxy {
	
	XrpcResponse invoke(XrpcRequest xrpcRequest);
		
}
