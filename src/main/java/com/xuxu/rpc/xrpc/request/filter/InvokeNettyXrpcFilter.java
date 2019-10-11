package com.xuxu.rpc.xrpc.request.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.context.XrpcRequestContext;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.filter.AbstractRequestXrpcFilter;
import com.xuxu.rpc.xrpc.filter.chain.XrpcFilterChain;
import com.xuxu.rpc.xrpc.netty.NettyClient;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.ResponseEntity;
import com.xuxu.rpc.xrpc.response.XrpcResponse;
import com.xuxu.rpc.xrpc.rigister.Rigister;

/**
 * netty-client调用
 * @author xuxu
 *
 */
public class InvokeNettyXrpcFilter implements AbstractRequestXrpcFilter{
	
	Logger logger =LoggerFactory.getLogger(InvokeNettyXrpcFilter.class);
    
	private Rigister rigsiter;
	
	public InvokeNettyXrpcFilter() {
		rigsiter=XrpcRequestContext.getRigister();
	}

	@Override
	public void doFilter(XrpcFilterChain chain, XrpcRequest request, XrpcResponse response) throws Exception {
		ResponseEntity responseEntity=null;
		Exception exception=null;
		try {
			responseEntity = NettyClient.invoke(request);		
		}  catch(Exception e) {
			exception=e;
		}
		if(exception!=null) {	
			//删除地址信息
			rigsiter.getRigisterInfo().removeInfo(request.getMethodKey(), request.getHostInfo());
			//快速失败
			throw exception;
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
