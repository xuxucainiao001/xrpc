package com.xuxu.rpc.xrpc.stub;

public interface Stub<T,R> {
	
	 R doInvoke(T t)  throws InterruptedException;

}
