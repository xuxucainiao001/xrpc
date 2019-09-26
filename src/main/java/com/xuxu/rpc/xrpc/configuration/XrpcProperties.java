package com.xuxu.rpc.xrpc.configuration;

public class XrpcProperties {
	
	private String proxyClassName;

	private boolean openServer=false;
		
	private boolean openClient=false;
	
	private String rigisterUrl;
	
	private String rigisterType;	
	
	private String routeStrategy;
	

	public String getRouteStrategy() {
		return routeStrategy;
	}

	public void setRouteStrategy(String routeStrategy) {
		this.routeStrategy = routeStrategy;
	}

	public String getRigisterType() {
		return rigisterType;
	}

	public void setRigisterType(String rigisterType) {
		this.rigisterType = rigisterType;
	}

	public String getRigisterUrl() {
		return rigisterUrl;
	}

	public void setRigisterUrl(String rigisterUrl) {
		this.rigisterUrl = rigisterUrl;
	}

	public String getProxyClassName() {
		return proxyClassName;
	}

	public void setProxyClassName(String proxyClassName) {
		this.proxyClassName = proxyClassName;
	}

	public boolean isOpenServer() {
		return openServer;
	}

	public void setOpenServer(boolean openServer) {
		this.openServer = openServer;
	}

	public boolean isOpenClient() {
		return openClient;
	}

	public void setOpenClient(boolean openClient) {
		this.openClient = openClient;
	}	
}
