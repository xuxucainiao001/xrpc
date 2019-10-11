package com.xuxu.rpc.xrpc.stub;

import java.util.concurrent.Future;

import com.xuxu.rpc.xrpc.request.RequestEntity;
import com.xuxu.rpc.xrpc.response.ResponseEntity;

public interface ClientStub extends Stub<RequestEntity,Future<ResponseEntity>>{
	
	Future<ResponseEntity> doInvoke(RequestEntity requestEntity)  throws InterruptedException;

}
