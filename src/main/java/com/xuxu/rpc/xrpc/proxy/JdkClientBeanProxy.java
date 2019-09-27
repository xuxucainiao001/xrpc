package com.xuxu.rpc.xrpc.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.annotations.XrpcClient;
import com.xuxu.rpc.xrpc.context.XrpcRequestContext;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.filter.chain.XrpcFilterChain;
import com.xuxu.rpc.xrpc.info.MethodInfo;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.request.XrpcRequestImpl;
import com.xuxu.rpc.xrpc.request.filter.RequestXrpcFilterChain;
import com.xuxu.rpc.xrpc.response.XrpcResponse;
import com.xuxu.rpc.xrpc.response.XrpcResponseImpl;

public class JdkClientBeanProxy implements ClientBeanProxy {
	
	
	@Override
	public <T> T createXrpcClientProxy(Class<T> intf, XrpcClient xrpcClient) {
		if (!intf.isInterface()) {
			throw new XrpcRuntimeException(ExceptionEnum.E0003);
		}
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return intf.cast(
				Proxy.newProxyInstance(cl, new Class<?>[] { intf }, new XrpcInvocationHandler(intf, xrpcClient)));
	}

}

/**
 * 代理类实现方法
 * 
 * @author xuxu
 *
 */
class XrpcInvocationHandler implements InvocationHandler {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Class<?> intf;

	private XrpcClient xrpcClient;

	public XrpcInvocationHandler(Class<?> intf, XrpcClient xrpcClient) {
		this.intf = intf;
		this.xrpcClient = xrpcClient;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 缓存中先获取
		MethodInfo methodInfo = XrpcRequestContext.getMethodInfoCache(intf);
		if (methodInfo == null) {
			// 创建调用方法
			methodInfo = new MethodInfo(method, intf, xrpcClient);
			XrpcRequestContext.rigisterMethodInfoCache(intf, methodInfo);
		}
		// 参数是否序列化
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				if (!(args[i] instanceof Serializable)) {
					logger.error("请求参数必须实现序列化接口：{}", args[i].getClass());
					throw new XrpcRuntimeException(ExceptionEnum.E0008);
				}
			}
		}
		// 打印每次请求方法信息
		logger.debug("请求方法信息：{}", methodInfo);
		// 构建请求调用链
		XrpcFilterChain chain = new RequestXrpcFilterChain();
		XrpcResponse response=new XrpcResponseImpl();
		XrpcRequest request=new XrpcRequestImpl(xrpcClient, args, methodInfo);
		chain.doChain(request,response);
		logger.debug("响应结果信息：{}", response);
		if(!response.hasException()) {
			return response.getResult();
		}
		throw response.getThrowable();
	}

}

