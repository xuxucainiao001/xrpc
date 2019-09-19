package com.xuxu.rpc.xrpc.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.xuxu.rpc.xrpc.XrpcBootstrapClient;
import com.xuxu.rpc.xrpc.XrpcBootstrapServer;
import com.xuxu.rpc.xrpc.context.XrpcRequestContext;
import com.xuxu.rpc.xrpc.context.XrpcResponseContext;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.proxy.BeanProxyFactory;
import com.xuxu.rpc.xrpc.rigister.Rigister;
import com.xuxu.rpc.xrpc.route.RouteStrategyFactoryImpl;

/**
 * xrpc配置和类
 * 
 * @author xuxu
 *
 */
public class XrpcConfiguration {

	private Logger logger = LoggerFactory.getLogger(XrpcConfiguration.class);

	private XrpcProperties xrpcProperties;

	private BeanProxyFactory beanProxyFactory;

	private XrpcBootstrapClient xrpcBootstrapClient;
	
	private XrpcBootstrapServer xrpcBootstrapServer;

	private static final String DEFAULT_BEAN_PROXY_FACTORY_NAME = "com.xuxu.rpc.xrpc.proxy.JdkBeanProxyFactory";
	
	public XrpcConfiguration() {
           
	}
	
	public void init() {
		 //打开注册中心
		 openRigisterCenter();
		 //注册路由策略
		 rigisterRouteStrategyFactory();
	}

	public XrpcConfiguration(XrpcProperties xrpcProperties) {
		this.xrpcProperties = xrpcProperties;
	}

	/**
	 * 获取代理器工厂
	 * 
	 * @return
	 */
	public BeanProxyFactory getBeanProxyFactory() {
		if (beanProxyFactory != null) {
			return beanProxyFactory;
		}
		this.beanProxyFactory = getBeanProxyFactoryByClassName(xrpcProperties.getProxyClassName());
		this.beanProxyFactory.setXrpcConfiguration(this);
		return beanProxyFactory;
	}

	/**
	 * 创建consumer服务
	 * 
	 * @return
	 */
	public synchronized XrpcBootstrapClient getXrpcBootstrapClient() {
		if (this.xrpcBootstrapClient != null) {
			return this.xrpcBootstrapClient;
		}
		if (xrpcProperties.isOpenClient()) {
			this.xrpcBootstrapClient = new XrpcBootstrapClient(this);
			this.xrpcBootstrapClient.start();
			return this.xrpcBootstrapClient;
		} 
			throw new XrpcRuntimeException(ExceptionEnum.E0004);
	}
	
	/**
	 * 创建provider服务
	 * 
	 * @return
	 */
	public synchronized XrpcBootstrapServer getXrpcBootstrapServer() {
		if (this.xrpcBootstrapServer != null) {
			return this.xrpcBootstrapServer;
		}
		if (xrpcProperties.isOpenServer()) {
			this.xrpcBootstrapServer = new XrpcBootstrapServer(this);
			this.xrpcBootstrapServer.start();
			return this.xrpcBootstrapServer;
		} 
			throw new XrpcRuntimeException(ExceptionEnum.E0005);
	}
	
	//获取配置信息
	public XrpcProperties getXrpcProperties() {
		return xrpcProperties;
	}

	private BeanProxyFactory getDefaultBeanProxyFactory() {
		return getBeanProxyFactoryByClassName(DEFAULT_BEAN_PROXY_FACTORY_NAME);
	}

	private BeanProxyFactory getBeanProxyFactoryByClassName(String className) {
		if (StringUtils.isEmpty(className)) {
			return getDefaultBeanProxyFactory();
		}
		try {
			return (BeanProxyFactory) Class.forName(className).newInstance();
		} catch (Exception e) {
			logger.error("{}:{},", ExceptionEnum.E0001.getMessage(), e);
			throw new XrpcRuntimeException(ExceptionEnum.E0001);
		}
	}
	
	/**
	  * 打开注册中心
	 * @return
	 */
	private void openRigisterCenter() {
		Rigister rigister= Rigister.open(xrpcProperties.getRigisterType(), xrpcProperties.getRigisterUrl());
		XrpcResponseContext.setRigister(rigister);
		XrpcRequestContext.setRigister(rigister);
	}
	
	/**
	   * 获取路由工厂
	 */
	private void rigisterRouteStrategyFactory() {
    	XrpcRequestContext.setRouteStrategyFactory(new RouteStrategyFactoryImpl(this));
    }
}
