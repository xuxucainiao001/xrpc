package com.xuxu.rpc.xrpc.response;

public interface XrpcResponse {
	
	public void setResult(Object result);
	
	public void setThrowable(Throwable error);
	
	public boolean hasException();
	
	public Object getResult();
	
	public Throwable getThrowable();
	
	public Integer getRequestId();
	
	
	
}
