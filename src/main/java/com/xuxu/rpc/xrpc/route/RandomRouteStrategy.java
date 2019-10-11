package com.xuxu.rpc.xrpc.route;

import java.util.List;
import java.util.Random;

import com.xuxu.rpc.xrpc.info.HostInfo;

/**
  *   随机路由策略
 * @author xuxu
 *
 */
public class RandomRouteStrategy implements RouteStrategy {
	
	private Random rd=new Random();

	@Override
	public synchronized HostInfo route(List<HostInfo> list) {		
		int next=rd.nextInt(list.size());
		return list.get(next);
	}

}
