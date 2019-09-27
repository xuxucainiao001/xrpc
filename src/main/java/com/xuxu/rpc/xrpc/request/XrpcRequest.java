package com.xuxu.rpc.xrpc.request;

import com.xuxu.rpc.xrpc.annotations.XrpcConsumer;
import com.xuxu.rpc.xrpc.info.HostInfo;

public interface XrpcRequest {
	
    String getMethodKey();
    
    public Object[] getRequestParams(); 
	
	HostInfo getHostInfo();
	
	public XrpcConsumer getMataDate();
	
	void setHostInfo(HostInfo hostInfo);
	
	RequestEntity newRequestEntity();
	
	
	
	
	
	
		
}
