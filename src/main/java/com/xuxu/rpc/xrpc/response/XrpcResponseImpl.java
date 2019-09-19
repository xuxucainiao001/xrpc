package com.xuxu.rpc.xrpc.response;

import java.io.Serializable;

/**
 *  XrpcResponse默认实现
 * @author xuxu
 *
 */
public class XrpcResponseImpl implements  XrpcResponse,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8483084295025519462L;
	
	private Object result;
	
	private Throwable throwable;

	@Override
	public void setResult(Object result) {
		this.result=result;
		
	}

	@Override
	public void setThrowable(Throwable throwable) {
		this.throwable=throwable;
		
	}

	@Override
	public boolean hasException() {
		return throwable!=null;
	}

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public Throwable getThrowable() {
		return this.throwable;
	}

	@Override
	public String toString() {
		return "XrpcResponseImpl [result=" + result + ", throwable=" + throwable + "]";
	}
	
	
}
