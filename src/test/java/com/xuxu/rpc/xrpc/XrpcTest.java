package com.xuxu.rpc.xrpc;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.configuration.XrpcProperties;
import com.xuxu.rpc.xrpc.proxy.BeanProxyFactory;
import com.xuxu.rpc.xrpc.proxy.ClientBeanProxy;
import com.xuxu.rpc.xrpc.proxy.ServerBeanProxy;

public class XrpcTest {
	
	
	public static void main(String[] args) throws Exception {
		
		XrpcProperties pro=new XrpcProperties();
		String rigisterUrl="39.107.67.13:2181";
		pro.setRigisterUrl(rigisterUrl);
		pro.setOpenClient(true);
		pro.setOpenServer(true);
		XrpcConfiguration con=new XrpcConfiguration(pro);
		con.init();
		BeanProxyFactory factory=con.getBeanProxyFactory();
		ClientBeanProxy clientBeanProxy=factory.getClientBeanProxy();
		ServerBeanProxy serverBeanProxy=factory.getServerBeanProxy();	
		Method m=clientBeanProxy.createXrpcClientProxy(Method.class,null);
		MethodImpl mi=new MethodImpl();
		serverBeanProxy.createXrpcServerProxy(mi, null);
		Thread.sleep(10000);
		System.out.println(m.call(1,"2"));
		System.out.println(m.call(2,"3"));	
	}

}






