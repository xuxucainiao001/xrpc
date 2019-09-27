package com.xuxu.rpc.xrpc;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.configuration.XrpcProperties;
import com.xuxu.rpc.xrpc.proxy.BeanProxyFactory;
import com.xuxu.rpc.xrpc.proxy.ServerBeanProxy;

public class XrpcProviderTest2 {
	
	
	public static void main(String[] args) throws Exception {
		
		XrpcProperties pro=new XrpcProperties();
		String rigisterUrl="39.107.67.13:2181";
		pro.setRigisterUrl(rigisterUrl);
		pro.setOpenServer(true);
		pro.setServerPort(8777);
		XrpcConfiguration con=new XrpcConfiguration(pro);
		con.initialize();
		BeanProxyFactory factory=con.getBeanProxyFactory();
		ServerBeanProxy serverBeanProxy=factory.getServerBeanProxy();	
		MethodImpl mi=new MethodImpl();
		serverBeanProxy.createXrpcServerProxy(mi, null);
	}

}






