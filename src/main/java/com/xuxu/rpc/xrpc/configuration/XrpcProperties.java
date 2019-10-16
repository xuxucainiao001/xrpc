package com.xuxu.rpc.xrpc.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.xuxu.rpc.xrpc.constants.XrpcConstant;

@ConfigurationProperties(prefix = XrpcProperties.PREX)
public class XrpcProperties {

	public static final String PREX = "com.xuxu.xrpc";

	private String proxyClassName;

	private boolean openServer = false;

	private boolean openClient = true;

	private String rigisterUrl;

	private String rigisterType;

	private String routeStrategy;

	private int serverPort = XrpcConstant.SERVER_PORT_DEFAULT;

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

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
