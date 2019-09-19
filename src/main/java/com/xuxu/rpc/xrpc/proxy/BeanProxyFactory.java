package com.xuxu.rpc.xrpc.proxy;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;

/**
  *  代理器工厂
 * @author xuxu
 *
 */
public interface BeanProxyFactory {
	
	   public ClientBeanProxy getClientBeanProxy();
	   
	   public ServerBeanProxy getServerBeanProxy();
	   
	   public void setXrpcConfiguration(XrpcConfiguration con);

}
