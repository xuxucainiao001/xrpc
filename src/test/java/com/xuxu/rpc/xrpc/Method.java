package com.xuxu.rpc.xrpc;

import java.util.List;
import java.util.Map;

public interface Method {
	
	Map<String,Object> call(Integer i,String str);
	
	List<String> work(String[] strs);
}
