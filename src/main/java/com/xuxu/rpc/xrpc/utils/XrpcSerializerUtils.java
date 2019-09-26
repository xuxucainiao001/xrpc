package com.xuxu.rpc.xrpc.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;

/*
 * Hessian 序列化
 */
public class XrpcSerializerUtils {

	private static Logger logger = LoggerFactory.getLogger(XrpcSerializerUtils.class);

	private XrpcSerializerUtils() {
	}

	public static <T> byte[] serialize(T obj) {
		byte[] bytes = null;
		// 1、创建字节输出流
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		HessianOutput hessianOutput = new HessianOutput(bos);
		try {
			// 注意，obj 必须实现Serializable接口
			hessianOutput.writeObject(obj);
			bytes = bos.toByteArray();
		} catch (IOException e) {
			logger.error("Xrpc序列化异常");
			throw new XrpcRuntimeException(ExceptionEnum.E0022);
		}
		return bytes;
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] data) {
		if (data == null) {
			return null;
		}
		// 1、将字节数组转换成字节输入流
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		HessianInput hessianInput = new HessianInput(bis);
		Object object = null;

		try {
			object = hessianInput.readObject();
		} catch (IOException e) {
			logger.error("Xrpc反序列化异常");
			throw new XrpcRuntimeException(ExceptionEnum.E0023);
		}
		return (T) object;
	}
}
