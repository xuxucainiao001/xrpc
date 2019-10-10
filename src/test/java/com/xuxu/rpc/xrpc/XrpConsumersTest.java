package com.xuxu.rpc.xrpc;


import java.util.concurrent.CountDownLatch;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.configuration.XrpcProperties;
import com.xuxu.rpc.xrpc.proxy.BeanProxyFactory;
import com.xuxu.rpc.xrpc.proxy.ClientBeanProxy;

public class XrpConsumersTest {
	
	
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
	    CountDownLatch cdl=new CountDownLatch(1);
	    int i=600;
	    while(i>0) {
	    	int j=i;
	    	new Thread(()-> {
	    		try {
					cdl.await();
				} catch (InterruptedException e) {
					
				}
	    		System.out.println(m.call(1,""+j));
	    	}).start();
	    	i--;
	    }
	    cdl.countDown();
		
	}

}






