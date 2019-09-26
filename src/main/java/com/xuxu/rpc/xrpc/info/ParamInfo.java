package com.xuxu.rpc.xrpc.info;

public class ParamInfo{
	
	
	private Class<?> paramType;
	
	private String paramName;
	
	public Class<?> getParamType() {
		return paramType;
	}

	public void setParamType(Class<?> paramType) {
		this.paramType = paramType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	@Override
	public String toString() {
		return "ParamInfo [paramType=" + paramType.getSimpleName() + ", paramName=" + paramName + "]";
	}   
}
