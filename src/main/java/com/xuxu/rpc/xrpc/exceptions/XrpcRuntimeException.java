package com.xuxu.rpc.xrpc.exceptions;

import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;

public class XrpcRuntimeException extends RuntimeException{
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 4502789518713695366L;
	
	private final ExceptionEnum exceptionEnum;
	
	public XrpcRuntimeException(ExceptionEnum exceptionEnum) {
		this.exceptionEnum=exceptionEnum;
	}
		
	public ExceptionEnum getExceptionEmun() {
		return this.exceptionEnum;
	}
}
