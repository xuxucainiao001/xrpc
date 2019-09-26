package com.xuxu.rpc.xrpc.request;

import java.io.Serializable;

import com.xuxu.rpc.xrpc.annotations.XrpcClient;
import com.xuxu.rpc.xrpc.info.HostInfo;
import com.xuxu.rpc.xrpc.info.MethodInfo;

/**
 * XrpcRequest默认实现
 * @author xuxu
 *
 */
public class XrpcRequestImpl implements XrpcRequest, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5852170375319732307L;

	private transient XrpcClient xrpcClient;

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
	public Integer getRequestId() {
		return null;
	}

	@Override
	public void setHostInfo(HostInfo hostInfo) {
		this.hostInfo= hostInfo;		
	}

}
