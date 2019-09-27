package com.xuxu.rpc.xrpc.route;

import java.util.List;

import com.xuxu.rpc.xrpc.info.HostInfo;

/**
  *   轮询路由策略
 * @author xuxu
 *
 */
public class PollRouteStrategy implements RouteStrategy {
	
	private int next=0;

	@Override
	public HostInfo route(List<HostInfo> list) {
		if(++next>list.size()-1) {
			next=0;
		}
		return list.get(next);
	}

}
