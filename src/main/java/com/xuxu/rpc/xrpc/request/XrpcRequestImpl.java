package com.xuxu.rpc.xrpc.request;

import com.xuxu.rpc.xrpc.annotations.XrpcConsumer;
import com.xuxu.rpc.xrpc.info.HostInfo;
import com.xuxu.rpc.xrpc.info.MethodInfo;

/**
 * XrpcRequest默认实现
 * @author xuxu
 *
 */
public class XrpcRequestImpl implements XrpcRequest{

	private XrpcConsumer xrpcConsumer;

	private Object[] requestParams;

	private String requestKey;
	
	private HostInfo hostInfo;
	

	public XrpcRequestImpl(XrpcConsumer xrpcConsumer, Object[] requestParams, MethodInfo mi) {
		this.xrpcConsumer = xrpcConsumer;
		this.requestParams = requestParams;
		requestKey = mi.getMethodKey();

	}

	@Override
	public Object[] getRequestParams() {
		return this.requestParams;
	}


	@Override
	public XrpcConsumer getMateDate() {
		return this.xrpcConsumer;
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
