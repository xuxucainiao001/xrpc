package com.xuxu.rpc.xrpc;

import java.util.Map;

public interface Method {
	Map<String,Object> call(Integer i,String str);
}
