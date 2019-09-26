package com.xuxu.rpc.xrpc.context;

import java.util.concurrent.ConcurrentHashMap;

import com.xuxu.rpc.xrpc.XrpcServer;
import com.xuxu.rpc.xrpc.info.MethodInfo;
import com.xuxu.rpc.xrpc.proxy.ServerProxy;
import com.xuxu.rpc.xrpc.rigister.Rigister;

public class XrpcResponseContext {

	// method缓存
	private static final ConcurrentHashMap<String, MethodInfo> methodInfoCache = new ConcurrentHashMap<>();
	
	private static ServerProxy serverProxy;
	
	private static Rigister rigister;
	
	private static XrpcServer xrpcServer;

	private XrpcResponseContext() {
	}

	// 注入method缓存
	public static void rigisterMethodCache(String methodKey, MethodInfo methodInfo) {
		methodInfoCache.put(methodKey, methodInfo);
	}

	// 获取method缓存
	public static MethodInfo getMethodCache(String methodKey) {
		return methodInfoCache.get(methodKey);
	}
	
	public static synchronized void rigisterSeverProxy(ServerProxy serverProxy) {
		XrpcResponseContext.serverProxy=serverProxy;
	}
	
	public static ServerProxy getServerProxy() {
		return serverProxy;
	}

	public static void setRigister(Rigister rigister) {
		XrpcResponseContext.rigister=rigister;		
	}
	
	public static Rigister getRigister() {
		return XrpcResponseContext.rigister;		
	}
	
	public static void setXrpcServer(XrpcServer xrpcServer) {
		XrpcResponseContext.xrpcServer=xrpcServer;
	}
	
	public static XrpcServer getXrpcServer() {
		return XrpcResponseContext.xrpcServer;
	}

}
