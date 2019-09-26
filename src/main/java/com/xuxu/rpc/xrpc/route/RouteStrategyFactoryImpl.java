package com.xuxu.rpc.xrpc.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;

public class RouteStrategyFactoryImpl implements RouteStrategyFactory {
	
	private Logger logger=LoggerFactory.getLogger(RouteStrategyFactoryImpl.class);
	
	private static final String DEFAULT_ROUTE_STRATEGY="com.xuxu.rpc.xrpc.route.PollRouteStrategy";
	
	private XrpcConfiguration con;
	
	public RouteStrategyFactoryImpl(XrpcConfiguration con) {
		this.con=con;
	}
		
	private RouteStrategy getRouteStrategy(String strategyClassName) {
		  try {
			return (RouteStrategy) Class.forName(strategyClassName).newInstance();
		} catch (Exception e) {
			logger.error("创建路由策略发生异常：{}",e);
			throw new XrpcRuntimeException(ExceptionEnum.E0018);
		}
	}
	
	public RouteStrategy getRouteStrategy() {
		String routeStrategy=con.getXrpcProperties().getRouteStrategy();
		if(routeStrategy!=null) {
			return getRouteStrategy(con.getXrpcProperties().getRouteStrategy());
		}		
		return getRouteStrategy(DEFAULT_ROUTE_STRATEGY);
	}
	

}
