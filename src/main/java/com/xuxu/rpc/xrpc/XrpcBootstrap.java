package com.xuxu.rpc.xrpc;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;

/**
 * 启动和初始化类
 * 
 * @author xuxu
 *
 */
public abstract class XrpcBootstrap {
	
	protected XrpcConfiguration con;
	
	protected volatile boolean isStart =false;
	
	public XrpcBootstrap(XrpcConfiguration con) {
		this.con=con;
	}
	
	public abstract void start() ;
	
	public abstract XrpcResponse callMethod(XrpcRequest xrcRequest) ;
	
}
