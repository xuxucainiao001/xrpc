package com.xuxu.rpc.xrpc.request.filter;

import com.xuxu.rpc.xrpc.filter.AbstractRequestXrpcFilter;
import com.xuxu.rpc.xrpc.filter.chain.XrpcFilterChain;
import com.xuxu.rpc.xrpc.netty.NettyClient;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;

/**
 * netty-client调用
 * @author xuxu
 *
 */
public class InvokeRequestXrpcFilter implements AbstractRequestXrpcFilter{


	@Override
	public void doFilter(XrpcFilterChain chain, XrpcRequest request, XrpcResponse response) {
		int requestId=NettyClient.invoke(request);
		XrpcResponse invkeResponse= NettyClient.getResult(requestId);
		response.setResult(invkeResponse.getResult()); 
		response.setThrowable(invkeResponse.getThrowable());
		chain.doChain(request, response);
	}
	
	@Override
	public int getOrder() {
		return 2;
	}
}
