package com.xuxu.rpc.xrpc.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.annotations.XrpcServer;
import com.xuxu.rpc.xrpc.context.XrpcResponseContext;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.info.MethodInfo;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;
import com.xuxu.rpc.xrpc.response.XrpcResponseImpl;
import com.xuxu.rpc.xrpc.utils.XrpcUtils;

public class JdkServerBeanProxy implements ServerBeanProxy {

	private Logger logger = LoggerFactory.getLogger(JdkServerBeanProxy.class);
	
	/**
	 * 创建服务端代理
	 */
	@Override
	public ServerProxy createXrpcServerProxy(Object intfImpl, XrpcServer xprcServer) {
		Class<?> intfImplClass = intfImpl.getClass();
		if (intfImplClass.isInterface() || Modifier.isAbstract(intfImplClass.getModifiers())) {
			throw new XrpcRuntimeException(ExceptionEnum.E0011);
		}
		// 获取接口
		Class<?>[] intfs = intfImplClass.getInterfaces();
		if (intfs == null || intfs.length == 0) {
			logger.error("服务端代理必须实现接口:{}", intfImplClass);
			throw new XrpcRuntimeException(ExceptionEnum.E0013);
		}
		if (intfs.length > 1) {
			logger.error("服务端代理不能是多接口实现:{}", intfImplClass);
			throw new XrpcRuntimeException(ExceptionEnum.E0012);
		}
        //获取接口中的方法 
		Method[] intfMethods =intfs[0].getMethods();
		//实现方法入册到XrpcResponseContext中
		for (Method m : intfMethods) {
			Method method=null;
			try {
				method = intfImplClass.getMethod(m.getName(), m.getParameterTypes());
			} catch (NoSuchMethodException | SecurityException e) {
				continue;
			}
			if(method!=null) {
				MethodInfo methodInfo = new MethodInfo(method, intfs[0], intfImpl, xprcServer);				
				//注册到注册中心上
				XrpcResponseContext.getRigister().rigisterInfo(methodInfo.getMethodKey(), XrpcUtils.localHostInfo());
				//注册到缓存里
				XrpcResponseContext.rigisterMethodCache(methodInfo.getMethodKey(), methodInfo);
			}			
		}
		//获取代理对象
		if(XrpcResponseContext.getServerProxy()==null) {
			XrpcResponseContext.rigisterSeverProxy(new SeverProxyImpl());
		}
		return XrpcResponseContext.getServerProxy();
	}

}

/**
 * 服务端代理默认实现
 * 
 * @author xuxu
 *
 */
class SeverProxyImpl implements ServerProxy {

	private Logger logger = LoggerFactory.getLogger(SeverProxyImpl.class);

	@Override
	public XrpcResponse invoke(XrpcRequest xrpcRequest) {
		
		MethodInfo invokedMethodInfo = XrpcResponseContext.getMethodCache(xrpcRequest.getRequestKey());
		logger.info("调用的MethodInfo信息：{}", invokedMethodInfo);
		Object result = null;
		Throwable exception = null;
		XrpcResponse response = new XrpcResponseImpl();
		try {
			result = invokedMethodInfo.getMethod().invoke(invokedMethodInfo.getIntfImpl(),
					xrpcRequest.getRequestParams());
		} catch (IllegalAccessException | IllegalArgumentException e) {
			logger.error("服务端调用方法发生异常：{}", e);
			exception = new RuntimeException(e);
		} catch (InvocationTargetException e) {
			logger.error("服务端调用方法发生异常：{}", e);
			exception = e.getTargetException();
		} finally {
			response.setThrowable(exception);
			response.setResult(result);
		}
		return response;

	}

}
