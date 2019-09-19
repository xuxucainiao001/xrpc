package com.xuxu.rpc.xrpc.request;

import com.xuxu.rpc.xrpc.annotations.XrpcClient;

/**
 * XrpcRequest包装类
 * @author xuxu
 *
 */
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
	public String[] getServerIps() {
		return xrpcRequest.getServerIps();
	}

	@Override
	public XrpcClient getMateDate() {
		return xrpcRequest.getMateDate();
	}

	@Override
	public String getRequestKey() {
		return xrpcRequest.getRequestKey();
	}

}
