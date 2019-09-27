package com.xuxu.rpc.xrpc;

import java.util.HashMap;
import java.util.Map;

public class MethodImpl implements Method{

	@Override
	public Map<String, Object> call(Integer i, String str) {
		//throw new XrpcRuntimeException(ExceptionEnum.E0001);
	    Map<String,Object> hashMap=new HashMap<>();
	     hashMap.put(str,i);
		return hashMap;
	}
	
}