package com.xuxu.rpc.xrpc.utils;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.configuration.XrpcConfiguration;
import com.xuxu.rpc.xrpc.context.XrpcRequestContext;
import com.xuxu.rpc.xrpc.context.XrpcResponseContext;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.info.HostInfo;

public class XrpcUtils {
	
	public static final Logger logger=LoggerFactory.getLogger(XrpcUtils.class);
		
	private XrpcUtils() {}
	
	//获取当前class所在路径
	public static String getCurrentClassPath(Class<?> clazz) {
		String jarFilePath = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();  
		try {
			return java.net.URLDecoder.decode(jarFilePath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("URL解码异常：{}",e);
			throw new XrpcRuntimeException(ExceptionEnum.E0010);
		} 
	}
	
	public static HostInfo localHostInfo() {
		try {
			InetAddress ip4 = InetAddress.getLocalHost();
			XrpcConfiguration con=XrpcRequestContext.getConfiguration();
			if(con==null) {
				con=XrpcResponseContext.getConfiguration();
			}
			return new HostInfo(ip4.getHostAddress(),con.getXrpcProperties().getServerPort());
		} catch (UnknownHostException e) {
			logger.error("获取本机地址失败：{}",e);
			throw new XrpcRuntimeException(ExceptionEnum.E0016);
		}
	} 
	
	

}
