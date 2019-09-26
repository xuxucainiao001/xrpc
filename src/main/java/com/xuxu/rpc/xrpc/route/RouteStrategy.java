package com.xuxu.rpc.xrpc.route;

import java.util.List;

import com.xuxu.rpc.xrpc.info.HostInfo;

/**
   *   路由策略
 * @author xuxu
 *
 */
public interface RouteStrategy {
	
	public HostInfo route(List<HostInfo> list);

}
