package com.xuxu.rpc.xrpc.rigister.zk;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.info.RigisterInfo;

public class ZookeeperExecutor implements Runnable {

	public static final String XRPC_ROOT_NODE = "/xrpc";

	private Logger logger = LoggerFactory.getLogger(ZookeeperExecutor.class);

	private String hostPort;

	private ZooKeeper zk;

	private volatile boolean stop = false;

	private RigisterInfo rigisterInfo;

	public ZookeeperExecutor(String hostPort) {
		this.rigisterInfo = new RigisterInfo();
		this.hostPort = hostPort;
	}

	private void start() {
		synchronized (this) {
			try {
				zk = new ZooKeeper(hostPort, 10000, null);
				// 注册监听器
				zk.register(new WatcherImpl(zk, rigisterInfo));
				// 启动时，异步创建主节点
				createMainNode();
				logger.info("启动zookeeper成功！");
				// 启动完成打开锁
				this.notifyAll();
				await();
			} catch (IOException e) {
				close();
				logger.error("启动zookeeper失败：{}", e);
			}
		}

	}

	private void createMainNode() {
		try {
			Stat stat = zk.exists(XRPC_ROOT_NODE, true);
			if (stat == null) {
				zk.create(XRPC_ROOT_NODE, XRPC_ROOT_NODE.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			}
		} catch (Exception e) {
			logger.error("启动zookeeper创建主节点失败：{}", e);
		}
	}

	public RigisterInfo getRigisterInfo() {
		return this.rigisterInfo;
	}

	/**
	 * 注册节点
	 * 
	 * @param methodInfo
	 * @param ip
	 * @param info
	 */
	public void rigister(String methodInfo, String hostPort) {
		synchronized (this) {
			try {
				if (zk == null) {
					this.wait();
				}
				// 检查接口信息是否已经注入
				Stat stat = zk.exists(XRPC_ROOT_NODE + "/" + methodInfo, true);
				if (stat == null) {
					zk.create(XRPC_ROOT_NODE + "/" + methodInfo, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
							CreateMode.PERSISTENT);
				}
				// 检查ip信息是否已经存在，有则更新，无则创建
				stat = zk.exists(XRPC_ROOT_NODE + "/" + methodInfo + "/" + hostPort, true);
				if (stat == null) {
					zk.create(XRPC_ROOT_NODE + "/" + methodInfo + "/" + hostPort, hostPort.getBytes(),
							ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				} else {
					zk.setData(XRPC_ROOT_NODE + "/" + methodInfo + "/" + hostPort, hostPort.getBytes(), 0);
				}
			} catch (Exception e) {
				logger.error("设置节点失败：{}", e);
			}
		}

	}

	/**
	 * 阻止zookeeper停止
	 */
	private void await() {
		synchronized (this) {
			while (!stop) {
				try {
					this.wait();
				} catch (Exception e) {
					logger.error("zookeeper运行发生中断异常：{}", e);
				}
			}
		}
	}

	/**
	 * zookeeper关闭
	 */
	public void close() {
		synchronized (this) {
			try {
				zk.close();
			} catch (Exception e) {
				logger.error("zookeeper停止发生中断异常：{}", e);
			}
			stop = true;
			this.notifyAll();
		}
	}

	@Override
	public void run() {
		start();
	}

}

class WatcherImpl implements Watcher {

	private Logger logger = LoggerFactory.getLogger(WatcherImpl.class);

	private ZooKeeper zk;

	private RigisterInfo rigisterInfo;

	public WatcherImpl(ZooKeeper zk, RigisterInfo rigisterInfo) {
		this.rigisterInfo = rigisterInfo;
		this.zk = zk;
	}

	@Override
	public void process(WatchedEvent watchedEvent) {
		if (watchedEvent.getState() == Event.KeeperState.SyncConnected) { // 判断是否已连接
			if (watchedEvent.getType() == Event.EventType.NodeCreated) {
				// 发生节点创建
				try {

					byte[] data = zk.getData(watchedEvent.getPath(), true, null);
					String hostAndport = new String(data);
					if (ZookeeperExecutor.XRPC_ROOT_NODE.equals(hostAndport)||"".equals(hostAndport)) {
						return;
					}
					String[] nodes = watchedEvent.getPath().split("/");
					String methodInfo = nodes[nodes.length - 2];
					logger.info("创建新的节点：{}：{}", methodInfo, hostAndport);
					// 重新注册
					rigisterInfo.putInfo(methodInfo, hostAndport);
				} catch (Exception e) {
					logger.error("获取节点数据异常：{}", e);
				}
			} else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
				// 发生节点改变
				try {
					byte[] data = zk.getData(watchedEvent.getPath(), true, null);
					String hostAndport = new String(data);
					if (ZookeeperExecutor.XRPC_ROOT_NODE.equals(hostAndport)||"".equals(hostAndport)) {
						return;
					}
					String[] nodes = watchedEvent.getPath().split("/");
					String methodInfo = nodes[nodes.length - 2];
					
					logger.info("节点发生变化：{}：{}", watchedEvent.getPath(), hostAndport);
					// 重新注册
					rigisterInfo.putInfo(methodInfo, hostAndport);
				} catch (Exception e) {
					logger.error("获取节点数据异常：{}", e);
				}
			}

			// ...还可以继续监听其它事件类型
		}
	}
}
