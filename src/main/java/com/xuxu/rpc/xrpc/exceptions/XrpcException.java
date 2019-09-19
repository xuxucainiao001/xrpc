package com.xuxu.rpc.xrpc.exceptions;

import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;

public class XrpcException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4702789518713695366L;
	
	private final ExceptionEnum exceptionEnum;
	
	public XrpcException(ExceptionEnum exceptionEnum) {
		this.exceptionEnum=exceptionEnum;
	}
		
	public ExceptionEnum getExceptionEmun() {
		return this.exceptionEnum;
	}

}
