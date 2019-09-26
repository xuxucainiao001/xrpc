package com.xuxu.rpc.xrpc.request;

import com.xuxu.rpc.xrpc.annotations.XrpcClient;
import com.xuxu.rpc.xrpc.info.HostInfo;

public interface XrpcRequest {
	
	Object[] getRequestParams();
	
	String getRequestKey();
	
	HostInfo getHostInfo();
	
	void setHostInfo(HostInfo hostInfo);
	
	XrpcClient getMateDate();
	
	Integer getRequestId();
	
	
		
}
