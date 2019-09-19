package com.xuxu.rpc.xrpc.request;

import java.io.Serializable;

import com.xuxu.rpc.xrpc.annotations.XrpcClient;
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
	public String[] getServerIps() {
		return null;
	}

	@Override
	public XrpcClient getMateDate() {
		return this.xrpcClient;
	}

	@Override
	public String getRequestKey() {
		return requestKey;
	}

}
