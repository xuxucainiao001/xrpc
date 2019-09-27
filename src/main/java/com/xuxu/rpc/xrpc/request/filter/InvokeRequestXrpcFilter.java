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
public class InvokeRequestXrpcFilter implements AbstractRequestXrpcFilter{
	
	Logger logger =LoggerFactory.getLogger(InvokeRequestXrpcFilter.class);
    
	private Rigister rigsiter;
	
	public InvokeRequestXrpcFilter() {
		rigsiter=XrpcRequestContext.getRigister();
	}

	@Override
	public void doFilter(XrpcFilterChain chain, XrpcRequest request, XrpcResponse response) {
		ResponseEntity responseEntity=null;
		Exception exception=null;
		try {
			responseEntity = NettyClient.invoke(request);		
		}  catch(Exception e) {
			exception=e;
		}
		if(exception!=null) {
			logger.error("方法调用发生异常：{}",exception);
			//连接异常删除已经注册的异常地址节点
			if(exception instanceof XrpcRuntimeException
					&&((XrpcRuntimeException) exception).getExceptionEmun().equals(ExceptionEnum.E0020)) {
				rigsiter.getRigisterInfo().removeInfo(request.getMethodKey(), request.getHostInfo());
			}	
			//快速失败
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
