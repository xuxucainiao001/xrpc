package com.xuxu.rpc.xrpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodImpl implements Method{

	@Override
	public Map<String, Object> call(Integer i, String str) {
		//throw new XrpcRuntimeException(ExceptionEnum.E0001);
	    Map<String,Object> hashMap=new HashMap<>();
	     hashMap.put(str,i);
		return hashMap;
	}

	@Override
	public List<String> work(String[] strs) {
		return new ArrayList<>(Arrays.asList(strs));
	}
	
}