package com.xuxu.rpc.xrpc.request;

import com.xuxu.rpc.xrpc.annotations.XrpcClient;

public interface XrpcRequest {
	
	Object[] getRequestParams();
	
	String getRequestKey();
	
	String[] getServerIps();
	
	XrpcClient getMateDate();
		
}
