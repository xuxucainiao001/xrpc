package com.xuxu.rpc.xrpc.filter;

import com.xuxu.rpc.xrpc.filter.chain.XrpcFilterChain;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;

public interface XrpcFilter {
	
	void doFilter(XrpcFilterChain chain,XrpcRequest request,XrpcResponse response);

}
