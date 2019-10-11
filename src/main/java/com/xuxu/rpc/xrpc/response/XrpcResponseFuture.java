package com.xuxu.rpc.xrpc.response;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.context.XrpcRequestContext;
import com.xuxu.rpc.xrpc.request.RequestEntity;

public class XrpcResponseFuture implements Future<ResponseEntity> {

	private Logger logger = LoggerFactory.getLogger(XrpcResponseFuture.class);

	private volatile boolean isDone = false;

	private volatile boolean isCancelled = false;

	private ResponseEntity result;
	
    private RequestEntity requestEntity;

	private Lock lock = new ReentrantLock();

	private Condition con = lock.newCondition();
	
	public XrpcResponseFuture(RequestEntity requestEntity) {
		this.requestEntity=requestEntity;
	}

	public void setResult(ResponseEntity result) {
		lock.lock();
		if (!isCancelled && !isDone) {
			this.result = result;
		}
		con.signal();
		lock.unlock();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		lock.lock();
		isCancelled = true;
		isDone = true;
		con.signal();
		lock.unlock();
		return isCancelled;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public ResponseEntity get() throws InterruptedException, ExecutionException {
		lock.lock();
		try {
			while (result == null) {
				boolean flag=con.await(10,TimeUnit.SECONDS);
				if(!flag) {
					//删除调用缓存
					XrpcRequestContext.RESPOSNE_MAP.remove(requestEntity.getRequestId());
					logger.error("方法调用超时,请求参数：{}",requestEntity);
					return result;
				}
				if (isCancelled) {
					logger.debug("方法调用取消,请求参数：{}",requestEntity);
					return result;
				}
			}
		} catch (Exception e) {
			logger.error("获取ResponseEntity发生异常:{}", e);
		} finally {
			lock.unlock();
		}
		return result;
	}

	@Override
	public ResponseEntity get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		lock.lock();
		try {
			while (result == null) {
				if (con.await(timeout, unit) || isCancelled) {
					break;
				}
			}
		} catch (Exception e) {
			logger.error("获取ResponseEntity发生异常:{}", e);
		} finally {
			lock.unlock();
		}
		return result;
	}

}
