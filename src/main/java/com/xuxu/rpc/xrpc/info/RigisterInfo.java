package com.xuxu.rpc.xrpc.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class RigisterInfo {
	
	private ConcurrentHashMap<String, Set<HostInfo>> rigisterInfoMap;
	
	private Logger logger=LoggerFactory.getLogger(RigisterInfo.class);
	
	public RigisterInfo() {
		rigisterInfoMap=new ConcurrentHashMap<>();
	}
	
	public void putInfo(String methodInfo,String hostPort) {
		Set<HostInfo> hostAndPortSet=rigisterInfoMap.get(methodInfo);
		if(CollectionUtils.isEmpty(hostAndPortSet)) {
			hostAndPortSet=new CopyOnWriteArraySet<>();
		}
		HostInfo hostInfo=new HostInfo(hostPort);
		hostAndPortSet.add(hostInfo);		
		rigisterInfoMap.put(methodInfo,hostAndPortSet);
		logger.debug("注册接口方法：{}，注册地址：{}",methodInfo,hostPort);
	}
	
	public List<HostInfo> getInfo(String methodInfo) {
		Set<HostInfo> hostAndPortSet=rigisterInfoMap.get(methodInfo);
		if(CollectionUtils.isEmpty(hostAndPortSet)) {
			return Collections.emptyList();
		}
		return new ArrayList<>(hostAndPortSet);
	}
	
	public void removeInfo(String methodInfo,HostInfo hostInfo) {
		Set<HostInfo> hostAndPortSet=rigisterInfoMap.get(methodInfo);
		if(!CollectionUtils.isEmpty(hostAndPortSet)) {
			hostAndPortSet.remove(hostInfo);
		}		
	}
	
	
	@Override
	public String toString() {
		return rigisterInfoMap.toString();
	}

}

