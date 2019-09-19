package com.xuxu.rpc.xrpc.info;

import java.util.Objects;

public class HostInfo {

	private String host;

	private int port;

	public HostInfo(String hostPort) {
		String[] hostAndport = hostPort.split(":");
		host = hostAndport[0];
		port = Integer.parseInt(hostAndport[1]);
	}

	public HostInfo(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String transferToString() {
		return host + ":" + port;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HostInfo) {
			return Objects.equals(((HostInfo) obj).transferToString(), this.transferToString());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return transferToString().hashCode();
	}
	
	@Override
	public String toString() {
		return this.transferToString();
	}

}
