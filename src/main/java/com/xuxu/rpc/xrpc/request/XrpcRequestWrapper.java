package com.xuxu.rpc.xrpc.request;

import com.xuxu.rpc.xrpc.annotations.XrpcClient;
import com.xuxu.rpc.xrpc.info.HostInfo;

public class XrpcRequestWrapper implements XrpcRequest{
	
	private XrpcRequest xrpcRequest;
	
	public XrpcRequestWrapper(XrpcRequest xrpcRequest) {
		this.xrpcRequest=xrpcRequest;
	}

	@Override
	public Object[] getRequestParams() {
		return xrpcRequest.getRequestParams();
	}

	@Override
	public String getRequestKey() {
		return xrpcRequest.getRequestKey();
	}

	@Override
	public HostInfo getHostInfo() {
		return xrpcRequest.getHostInfo();
	}

	@Override
	public XrpcClient getMateDate() {
		return xrpcRequest.getMateDate();
	}

	@Override
	public Integer getRequestId() {
		return xrpcRequest.getRequestId();
	}

	@Override
	public void setHostInfo(HostInfo hostInfo) {
		xrpcRequest.setHostInfo(hostInfo);	
	}
	
}