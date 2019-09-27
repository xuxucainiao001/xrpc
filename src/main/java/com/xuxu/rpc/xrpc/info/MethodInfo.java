package com.xuxu.rpc.xrpc.info;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import com.xuxu.rpc.xrpc.annotations.XrpcConsumer;
import com.xuxu.rpc.xrpc.annotations.XrpcProvider;
import com.xuxu.rpc.xrpc.constants.XrpcConstant;

public class MethodInfo {

	private ParamInfo[] paramInfos;

	private Class<?> returnType;

	private String methodName;

	private Class<?>[] exceptionTypes;

	private String interfaceName;

	private Method method;
	
	private Object intfImpl;

	private String methodKey;

	private String groupName;

	/**
	 * 客户端构造函数
	 * 
	 * @param method 代理的方法
	 * @param params 代理的方法参数对象
	 * @param intf   代理方法所属的接口
	 */
	public MethodInfo(Method method, Class<?> intf, XrpcConsumer xrpcConsumer) {
		init(method, intf);
		if (xrpcConsumer == null) {
			this.groupName = XrpcConstant.DEFAULT_GROUP;
		} else {
			this.groupName = xrpcConsumer.groupName();
		}
		// 构建方法信息
		buildkey();
	}

	/**
	 * 服务端构造函数
	 * 
	 * @param method
	 * @param intf
	 * @param xrpcServer
	 */
	public MethodInfo(Method method, Class<?> intf, Object intfImpl,XrpcProvider xrpcServer) {
		init(method, intf);
		if (xrpcServer == null) {
			this.groupName = XrpcConstant.DEFAULT_GROUP;
		} else {
			this.groupName = xrpcServer.groupName();
		}
		this.intfImpl=intfImpl;
		// 构建方法信息
		buildkey();
	}

	private void init(Method method, Class<?> intf) {
		// 判断是否是无参数类型
		Parameter[] parameters = method.getParameters();
		if (parameters == null || parameters.length == 0) {
			returnType = null;
			methodName = "";
			paramInfos = null;
		} else {
			this.paramInfos = new ParamInfo[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				ParamInfo paramInfo = new ParamInfo();
				paramInfo.setParamType(parameters[i].getType());
				paramInfo.setParamName(parameters[i].getName());
				paramInfos[i] = paramInfo;
			}
		}
		this.returnType = method.getReturnType();
		this.methodName = method.getName();
		this.exceptionTypes = method.getExceptionTypes();
		this.method = method;
		this.interfaceName = intf.getName();
	}

	private void buildkey() {
		StringBuilder sb = new StringBuilder();
		sb.append(groupName);
		sb.append(".");
		sb.append(interfaceName);
		sb.append(".");
		sb.append(methodName);
		sb.append("(");
		if (paramInfos != null) {
			for (int i = 0; i < paramInfos.length; i++) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(paramInfos[i].getParamType().getName());
			}
		}
		sb.append(")");
		methodKey = sb.toString();
	}

	public String getMethodKey() {
		return methodKey;
	}

	public Class<?> getReturnType() {
		return returnType;
	}
    
	//获取方法
	public Method getMethod() {
		return method;
	}
	
	//获取接口实现类对象
	public Object getIntfImpl() {
		return intfImpl;
	}

	@Override
	public String toString() {
		return "MethodInfo [paramInfos=" + Arrays.toString(paramInfos) + ", returnType=" + returnType + ", methodName="
				+ methodName + ", exceptionTypes=" + Arrays.toString(exceptionTypes) + ", interfaceName="
				+ interfaceName + ", method=" + method + ", intfImpl=" + intfImpl + ", methodKey=" + methodKey
				+ ", groupName=" + groupName + "]";
	}

	

}
