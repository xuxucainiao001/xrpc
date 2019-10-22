package com.xuxu.rpc.xrpc.configuration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.XrpcServer;
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

	private static final String DEFAULT_BEAN_PROXY_FACTORY_NAME = "com.xuxu.rpc.xrpc.proxy.JdkBeanProxyFactory";
	
	public XrpcConfiguration() {
           
	}
	
	public void initialize() {
		 //打开注册中心
		 openRigisterCenter();
		 //注册路由策略
		 rigisterRouteStrategyFactory();
		 //打开服务端代理服务
		 openXrpcServer();
		 //向上下文中注册自己
		 if(xrpcProperties.isOpenClient()) {
			 XrpcRequestContext.setConfiguration(this);
		 }
		 if(xrpcProperties.isOpenServer()) {
			 XrpcResponseContext.setConfiguration(this);
		 }
		 
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
	 * 创建XrpcServer服务
	 * 
	 * @return
	 */
	private synchronized void openXrpcServer(){
		if (xrpcProperties.isOpenServer()) {
			XrpcServer xrpcServer=XrpcServer.open(xrpcProperties.getServerPort());
			XrpcResponseContext.setXrpcServer(xrpcServer);
		} 
	}
	
	/**
	   * 获取路由工厂
	 */
	private void rigisterRouteStrategyFactory() {
    	XrpcRequestContext.setRouteStrategyFactory(new RouteStrategyFactoryImpl(this));
    }
}
