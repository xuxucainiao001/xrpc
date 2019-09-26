package com.xuxu.rpc.xrpc.request.filter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.context.XrpcRequestContext;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.filter.AbstractRequestXrpcFilter;
import com.xuxu.rpc.xrpc.filter.chain.XrpcFilterChain;
import com.xuxu.rpc.xrpc.info.HostInfo;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;
import com.xuxu.rpc.xrpc.rigister.Rigister;
import com.xuxu.rpc.xrpc.route.RouteStrategy;

/**
 * 路由过滤器
 * @author xuxu
 *
 */
public class HostAndPortRequestXrpcFilter implements AbstractRequestXrpcFilter{
	
	private Logger logger=LoggerFactory.getLogger(HostAndPortRequestXrpcFilter.class);
	
	private RouteStrategy routeStrategy;
	
	public HostAndPortRequestXrpcFilter() {
		//获取路由策略
		routeStrategy=XrpcRequestContext.getRouteStrategyFactory().getRouteStrategy();
	}

 
	@Override
	public void doFilter(XrpcFilterChain chain, XrpcRequest request, XrpcResponse response) {
		//获取注册中心
		Rigister rigister=XrpcRequestContext.getRigister();
		List<HostInfo> hostList=rigister.getRigisterInfo().getInfo(request.getRequestKey());
		if(hostList==null) {
			logger.error("没有获得方法的地址信息：{}",request.getRequestKey());
			throw new XrpcRuntimeException(ExceptionEnum.E0017);
		}
		HostInfo hostInfo=routeStrategy.route(hostList);
		logger.info("路由结果：{}",hostInfo);
		request.setHostInfo(hostInfo);
	}
	
	@Override
	public int getOrder() {
		return 1;
	}
}
