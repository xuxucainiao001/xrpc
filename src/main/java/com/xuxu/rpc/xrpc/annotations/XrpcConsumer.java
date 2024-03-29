package com.xuxu.rpc.xrpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.xuxu.rpc.xrpc.constants.XrpcConstant;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XrpcConsumer {
	
	String groupName() default XrpcConstant.DEFAULT_GROUP;

}
