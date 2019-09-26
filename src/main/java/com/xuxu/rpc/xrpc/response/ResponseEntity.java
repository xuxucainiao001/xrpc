package com.xuxu.rpc.xrpc.response;

import java.io.Serializable;

public class ResponseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9046859308554292632L;
	
	private Throwable throwable;
	
	private Object result;
	
	private int requestId;

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	
}
