package com.xuxu.rpc.xrpc.request;

import com.xuxu.rpc.xrpc.annotations.XrpcClient;
import com.xuxu.rpc.xrpc.info.HostInfo;
import com.xuxu.rpc.xrpc.info.MethodInfo;

/**
 * XrpcRequest默认实现
 * @author xuxu
 *
 */
public class XrpcRequestImpl implements XrpcRequest{

	private XrpcClient xrpcClient;

	private Object[] requestParams;

	private String requestKey;
	
	private HostInfo hostInfo;
	

	public XrpcRequestImpl(XrpcClient xrpcClient, Object[] requestParams, MethodInfo mi) {
		this.xrpcClient = xrpcClient;
		this.requestParams = requestParams;
		requestKey = mi.getMethodKey();

	}

	@Override
	public Object[] getRequestParams() {
		return this.requestParams;
	}


	@Override
	public XrpcClient getMateDate() {
		return this.xrpcClient;
	}

	@Override
	public String getRequestKey() {
		return requestKey;
	}

	@Override
	public HostInfo getHostInfo() {
		return hostInfo;
	}
	
	@Override
	public void setHostInfo(HostInfo hostInfo) {
		this.hostInfo= hostInfo;		
	}

	@Override
	public RequestEntity newRequestEntity() {
		RequestEntity entity=new RequestEntity();
		entity.setMethodKey(getRequestKey());
		entity.setParam(getRequestParams());
		return entity;
	}

}
