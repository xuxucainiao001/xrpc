package com.xuxu.rpc.xrpc;

import java.util.concurrent.CountDownLatch;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.configuration.XrpcProperties;
import com.xuxu.rpc.xrpc.proxy.BeanProxyFactory;
import com.xuxu.rpc.xrpc.proxy.ClientBeanProxy;

public class XrpConsumersTest {

	public static void main(String[] args) throws Exception {

		XrpcProperties pro = new XrpcProperties();
		String rigisterUrl = "39.107.67.13:2181";
		pro.setRigisterUrl(rigisterUrl);
		pro.setOpenClient(true);
		XrpcConfiguration con = new XrpcConfiguration(pro);
		con.initialize();
		BeanProxyFactory factory = con.getBeanProxyFactory();
		ClientBeanProxy clientBeanProxy = factory.getClientBeanProxy();
		Method m = clientBeanProxy.createXrpcClientProxy(Method.class, null);
		multi(m);
		syc(m);	
	}
	
	private static void syc(Method m) throws InterruptedException {
		
		int i=1;
		while(i>0) {
			try {
				m.call(i--, "1");
				m.work(new String[] {"a","b"});
			}catch(Exception e) {
				
			}
			
		}
		
	}
	
	private  static void multi(Method m) {
		CountDownLatch cdl = new CountDownLatch(1);
		int i = 1;
		while (i > 0) {
			int j = i;
			new Thread(() -> {
				try {
					cdl.await();
				} catch (InterruptedException e) {

				}
				System.out.println(m.call(1, "" + j));
			}).start();
			i--;
		}
		cdl.countDown();
	}

}
