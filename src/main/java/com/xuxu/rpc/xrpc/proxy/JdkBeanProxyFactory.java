package com.xuxu.rpc.xrpc.proxy;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;

/**
 * jdk代理工厂
 * 
 * @author xuxu
 *
 */
public class JdkBeanProxyFactory implements BeanProxyFactory {

	private XrpcConfiguration con;

	@Override
	public void setXrpcConfiguration(XrpcConfiguration con) {
		this.con = con;
	}

	@Override
	public ClientBeanProxy getClientBeanProxy() {
		if (con.getXrpcProperties().isOpenClient()) {
			return new JdkClientBeanProxy();
		}
		throw new XrpcRuntimeException(ExceptionEnum.E0004);

	}

	@Override
	public ServerBeanProxy getServerBeanProxy() {
		if (con.getXrpcProperties().isOpenServer()) {			
			return new JdkServerBeanProxy();			
		}
		throw new XrpcRuntimeException(ExceptionEnum.E0005);
	}
}
