package com.xuxu.rpc.xrpc.exceptions.eumn;

public enum ExceptionEnum {
	
	E0001("0001","创建Xrpc代理异常"),
	E0002("0002","XrpcConfiguration不能为null"),
	E0003("0003","Xrpc客户端只能代理接口"),
	E0004("0004","未开启Xrpc客户端"),
	E0005("0005","未开启Xrpc服务端"),
	E0006("0006","非法文件路径"),
	E0007("0007","加载Xrpc请求拦截器异常"),
	E0008("0008","参数必须实现序列化接口"),
	E0009("0009","返回的XrpceResponse不可以为null"),
	E0010("0010","URL解码异常"),
	E0011("0011","服务端代理必须为实现类"),
	E0012("0012","服务端代理不能是多接口实现"),
	E0013("0013","服务端代理必须实现接口"),
	E0014("0014","服务端调用方法发生异常"),
	E0015("0015","不支持的注册中心类型"),
	E0016("0016","获取本机地址失败"),
	E0017("0017","没有获得方法的地址信息"),
	E0018("0018","创建路由策略发生异常");
		
	private ExceptionEnum(String code,String message){
		this.code=code;
		this.message=message;
	}
	
	private String code;
	
	private String message;
	
	public String getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}

}
