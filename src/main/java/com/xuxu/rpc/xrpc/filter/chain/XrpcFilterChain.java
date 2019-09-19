package com.xuxu.rpc.xrpc.filter.chain;

import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;

public interface XrpcFilterChain {
		
	void doChain(XrpcRequest request,XrpcResponse response);

}
