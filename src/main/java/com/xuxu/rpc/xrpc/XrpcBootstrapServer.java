package com.xuxu.rpc.xrpc;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;

/**
 * provider
 * @author xuxu
 *
 */
public class XrpcBootstrapServer extends XrpcBootstrap{
    
	
	public XrpcBootstrapServer(XrpcConfiguration con) {
		super(con);
	}
	
	@Override
	public void start() {
		
	}

	@Override
	public XrpcResponse callMethod(XrpcRequest xrcRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
