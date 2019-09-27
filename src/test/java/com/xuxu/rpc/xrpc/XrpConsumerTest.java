package com.xuxu.rpc.xrpc;


import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.configuration.XrpcProperties;
import com.xuxu.rpc.xrpc.proxy.BeanProxyFactory;
import com.xuxu.rpc.xrpc.proxy.ClientBeanProxy;

public class XrpConsumerTest {
	
	
	public static void main(String[] args) throws Exception {
		
		XrpcProperties pro=new XrpcProperties();
		String rigisterUrl="39.107.67.13:2181";
		pro.setRigisterUrl(rigisterUrl);
		pro.setOpenClient(true);
		XrpcConfiguration con=new XrpcConfiguration(pro);
		con.initialize();
		BeanProxyFactory factory=con.getBeanProxyFactory();
		ClientBeanProxy clientBeanProxy=factory.getClientBeanProxy();
		Method m=clientBeanProxy.createXrpcClientProxy(Method.class,null);
		try {
			System.out.println(m.call(1,"2"));
		}catch(Exception e) {
			
		}
		try {
			System.out.println(m.call(1,"3"));
		}catch(Exception e) {
			
		}
		try {
			System.out.println(m.call(1,"4"));
		}catch(Exception e) {
			
		}
		try {
			System.out.println(m.call(1,"5"));
		}catch(Exception e) {
			
		}	
	}

}






