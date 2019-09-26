package com.xuxu.rpc.xrpc.request;

import java.io.Serializable;
import java.util.Arrays;

public class RequestEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9046852308554292632L;
	
	private String methodKey;
	
	private int requestId;
	
	private Object param[];

	public String getMethodKey() {
		return methodKey;
	}

	public void setMethodKey(String methodKey) {
		this.methodKey = methodKey;
	}

	public Object[] getParam() {
		return param;
	}

	public void setParam(Object[] param) {
		this.param = param;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return "RequestEntity [methodKey=" + methodKey + ", requestId=" + requestId + ", param="
				+ Arrays.toString(param) + "]";
	}
		
}
