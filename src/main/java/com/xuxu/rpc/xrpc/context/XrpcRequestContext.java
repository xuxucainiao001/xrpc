package com.xuxu.rpc.xrpc.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.info.MethodInfo;
import com.xuxu.rpc.xrpc.response.XrpcResponseFuture;
import com.xuxu.rpc.xrpc.rigister.Rigister;
import com.xuxu.rpc.xrpc.route.RouteStrategyFactory;

/**
 * Xrpc请求上下文全局对象
 * @author xuxu
 *
 */
public class XrpcRequestContext {
	
	public static final ConcurrentMap<Integer, XrpcResponseFuture> RESPOSNE_MAP =new ConcurrentHashMap<>();
		
	private XrpcRequestContext() {}
	
	private static Rigister rigister;
	
	private static RouteStrategyFactory routeStrategyFactory;
	
	private static XrpcConfiguration con;
	
	//methodInfo 缓存
	private static final ConcurrentHashMap<Class<?>, MethodInfo> methodInfoCache=new ConcurrentHashMap<>();
	
	//注入methodInfo缓存
	public static void rigisterMethodInfoCache(Class<?> intf,MethodInfo methodInfo) {
		 methodInfoCache.put(intf, methodInfo);
	}
	
	//获取methodInfo缓存
	public static MethodInfo getMethodInfoCache(Class<?> intf) {
		return methodInfoCache.get(intf);
	}
	
	//设置注册中心
	public static void setRigister(Rigister rigister) {
		XrpcRequestContext.rigister=rigister;		
	}
	
	public static Rigister getRigister() {
		return XrpcRequestContext.rigister;		
	}

	public static void setRouteStrategyFactory(RouteStrategyFactory routeStrategyFactory) {
		XrpcRequestContext.routeStrategyFactory=routeStrategyFactory;
		
	}
	
	public static RouteStrategyFactory getRouteStrategyFactory() {
		return XrpcRequestContext.routeStrategyFactory;
	}
	
	public static void setConfiguration(XrpcConfiguration con) {
		XrpcRequestContext.con=con;
	}
	
	public static XrpcConfiguration getConfiguration() {
		return XrpcRequestContext.con;
	}
	
}
