package com.xuxu.rpc.xrpc.request.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.filter.AbstractRequestXrpcFilter;
import com.xuxu.rpc.xrpc.filter.chain.XrpcFilterChain;
import com.xuxu.rpc.xrpc.netty.NettyClient;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.ResponseEntity;
import com.xuxu.rpc.xrpc.response.XrpcResponse;

/**
 * netty-client调用
 * @author xuxu
 *
 */
public class InvokeRequestXrpcFilter implements AbstractRequestXrpcFilter{
	
	Logger logger =LoggerFactory.getLogger(InvokeRequestXrpcFilter.class);


	@Override
	public void doFilter(XrpcFilterChain chain, XrpcRequest request, XrpcResponse response) {
		ResponseEntity responseEntity=null;
		try {
			responseEntity = NettyClient.invoke(request);
		} catch (Exception e) {
			logger.error("方法调用发生异常：{}",e);
			throw new XrpcRuntimeException(ExceptionEnum.E0024);
		}
		response.setResult(responseEntity.getResult()); 
		response.setThrowable(responseEntity.getThrowable());
		chain.doChain(request, response);
	}
	
	@Override
	public int getOrder() {
		return 2;
	}
}
