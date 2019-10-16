package com.xuxu.rpc.xrpc;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class CuratorTest {
	
	public static void main(String[] args) throws Exception {
		
		RetryPolicy retryPolicy  = new ExponentialBackoffRetry(1000,3);
		
		CuratorFramework client = CuratorFrameworkFactory.builder()
	            .connectString("39.107.67.13:2181")
	            .sessionTimeoutMs(5000)
	            .connectionTimeoutMs(5000)
	            .retryPolicy(retryPolicy)
	            .build();
		String str=client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/a/b","Hello".getBytes());
		System.out.println(str);
	}

}
