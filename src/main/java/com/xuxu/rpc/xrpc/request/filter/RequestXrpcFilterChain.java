package com.xuxu.rpc.xrpc.request.filter;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.exceptions.XrpcRuntimeException;
import com.xuxu.rpc.xrpc.exceptions.eumn.ExceptionEnum;
import com.xuxu.rpc.xrpc.filter.AbstractRequestXrpcFilter;
import com.xuxu.rpc.xrpc.filter.chain.XrpcFilterChain;
import com.xuxu.rpc.xrpc.request.XrpcRequest;
import com.xuxu.rpc.xrpc.response.XrpcResponse;
import com.xuxu.rpc.xrpc.utils.XrpcUtils;

public class RequestXrpcFilterChain implements XrpcFilterChain {
	
	private static Logger logger=LoggerFactory.getLogger(RequestXrpcFilterChain.class);
	
	private static final String DEFAULT_FILTER_PACKAGE="com.xuxu.rpc.xrpc.request.filter";

	private static Set<AbstractRequestXrpcFilter> filters;

	private Iterator<AbstractRequestXrpcFilter> itrs = null;

	public RequestXrpcFilterChain() {
		synchronized (RequestXrpcFilterChain.class) {
			init();
		}
		
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (RequestXrpcFilterChain.filters == null) {
			try {
				loadfilters();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				logger.error("加载Xrpc请求拦截器异常：{}",e);
				throw new XrpcRuntimeException(ExceptionEnum.E0007);
			}
		}
		this.itrs=filters.iterator();
	}

	/**
	 * 加载请求过滤器
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundExceptio 
	 */
	private static void loadfilters() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		RequestXrpcFilterChain.filters=new TreeSet<>((f1,f2)-> f1.getOrder()-f2.getOrder());
		String currentClassPath = XrpcUtils.getCurrentClassPath(RequestXrpcFilterChain.class);
		String filtersDir=currentClassPath+DEFAULT_FILTER_PACKAGE.replaceAll("\\.", "/");
		File dir = new File(filtersDir);
		for (File file : dir.listFiles()) {
            String filterName=DEFAULT_FILTER_PACKAGE+"."+file.getName().substring(0,file.getName().indexOf('.'));
			Class<?> clazz=Class.forName(filterName);
			if(AbstractRequestXrpcFilter.class.isAssignableFrom(clazz)&&!clazz.isInterface()&&!Modifier.isAbstract(clazz.getModifiers())) {
				AbstractRequestXrpcFilter filter=(AbstractRequestXrpcFilter)clazz.newInstance();
				filters.add(filter);
				logger.info("完成加载Xrpc请求过滤器：{}",filter.getClass());
			}
		}
	}

	@Override
	public void doChain(XrpcRequest request,XrpcResponse response) throws Exception {
		if (itrs.hasNext()) {
			itrs.next().doFilter(this, request,response);
		}
	}

}


