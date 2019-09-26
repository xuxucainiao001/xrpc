package com.xuxu.rpc.xrpc.request;

import com.xuxu.rpc.xrpc.annotations.XrpcClient;
import com.xuxu.rpc.xrpc.info.HostInfo;

public interface XrpcRequest {
	
	Object[] getRequestParams();
	
	String getRequestKey();
	
    XrpcClient getMateDate();
	
	HostInfo getHostInfo();
	
	void setHostInfo(HostInfo hostInfo);
	
	RequestEntity newRequestEntity();
	
	
	
	
		
}
