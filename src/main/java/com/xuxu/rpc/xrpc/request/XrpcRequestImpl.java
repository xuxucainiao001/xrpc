package com.xuxu.rpc.xrpc.request;

import com.xuxu.rpc.xrpc.annotations.XrpcConsumer;
import com.xuxu.rpc.xrpc.info.HostInfo;

/**
 * XrpcRequest默认实现
 * @author xuxu
 *
 */
public class XrpcRequestImpl implements XrpcRequest{

	private XrpcConsumer xrpcConsumer;

	private Object[] requestParams;
	
	private String methodKey;
	
	private HostInfo hostInfo;
		

	public XrpcRequestImpl(XrpcConsumer xrpcConsumer, Object[] requestParams, String methodKey) {
		this.xrpcConsumer = xrpcConsumer;
		this.requestParams = requestParams;
		this.methodKey =methodKey;

	}

	@Override
	public Object[] getRequestParams() {
		return this.requestParams;
	}

	@Override
	public HostInfo getHostInfo() {
		return hostInfo;
	}
	
	@Override
	public void setHostInfo(HostInfo hostInfo) {
		this.hostInfo= hostInfo;		
	}
	
	public XrpcConsumer getMataDate() {
		return this.xrpcConsumer;
	}
	
	@Override
	public RequestEntity newRequestEntity() {
		RequestEntity entity=new RequestEntity();
		entity.setMethodKey(methodKey);
		entity.setParam(getRequestParams());
		return entity;
	}

	@Override
	public String getMethodKey() {
		return this.methodKey;
	}

	

}
