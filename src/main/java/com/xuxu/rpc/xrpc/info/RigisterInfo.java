package com.xuxu.rpc.xrpc.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RigisterInfo {
	
	private ConcurrentHashMap<String, Set<HostInfo>> rigisterInfoMap;
	
	private Logger logger=LoggerFactory.getLogger(RigisterInfo.class);
	
	public RigisterInfo() {
		rigisterInfoMap=new ConcurrentHashMap<>();
	}
	
	public void putInfo(String methodInfo,String hostPort) {
		Set<HostInfo> hostAndPortSet=rigisterInfoMap.get(methodInfo);
		if(hostAndPortSet==null) {
			hostAndPortSet=new HashSet<>();
		}
		HostInfo hostInfo=new HostInfo(hostPort);
		hostAndPortSet.add(hostInfo);		
		rigisterInfoMap.put(methodInfo,hostAndPortSet);
		logger.info("注册接口方法：{}，注册地址：{}",methodInfo,hostPort);
	}
	
	public List<HostInfo> getInfo(String methodInfo) {
		Set<HostInfo> hostAndPortSet=rigisterInfoMap.get(methodInfo);
		if(hostAndPortSet==null) {
			logger.info("没有获得方法的地址信息：{}",methodInfo);	
			return Collections.emptyList();
		}
		return new ArrayList<>(hostAndPortSet);
	}
	
	@Override
	public String toString() {
		return rigisterInfoMap.toString();
	}

}

