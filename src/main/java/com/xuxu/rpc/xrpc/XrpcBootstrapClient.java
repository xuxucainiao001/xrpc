package com.xuxu.rpc.xrpc;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;

/**
 * consumer
 * @author xuxu
 *
 */
public class XrpcBootstrapClient extends XrpcBootstrap{
	
    
	
	public XrpcBootstrapClient(XrpcConfiguration con) {
		super(con);
	}


	@Override
	public  void start() {
		
	}


	@Override
	public XrpcResponse callMethod(XrpcRequest xrcRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}
