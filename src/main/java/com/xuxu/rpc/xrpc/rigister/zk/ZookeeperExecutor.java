package com.xuxu.rpc.xrpc.rigister.zk;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	
	private ScheduledExecutorService excutor;

	private volatile boolean stop = true;

	private RigisterInfo rigisterInfo;

	public ZookeeperExecutor(String hostPort) {
		this.rigisterInfo = new RigisterInfo();
		this.hostPort = hostPort;
		//初始化定时器任务
		excutor=Executors.newSingleThreadScheduledExecutor();
	}
	
	/**
	 * 定时器任务同步节点数据  每120秒
	 */
	private void autoSyncNode() {
		excutor.scheduleWithFixedDelay(this::syncNodeDate, 0, 120, TimeUnit.SECONDS);
	}

	private synchronized void start() {
		try {
			zk = new ZooKeeper(hostPort, 10000, null);
			// 注册监听器
			zk.register(new WatcherImpl(zk));
			// 启动时，同步创建主节点
			createMainNode();
			// 同步节点数据
			autoSyncNode();
			logger.info("启动zookeeper成功！");
			stop = false;
			// 启动完成打开锁
			this.notifyAll();
			await();
		} catch (IOException e) {
			close();
			logger.error("启动zookeeper失败：{}", e);
		}
	}

	/**
	 * 同步节点数据
	 */
	public synchronized void syncNodeDate() {

		List<String> children = null;
		try {
			children = zk.getChildren(XRPC_ROOT_NODE, true);
		} catch (Exception e) {
			logger.error("获取zk节点数据失败，节点：{}，异常：{}", XRPC_ROOT_NODE, e);
			return;
		}
		logger.debug("zookeeper所有节点：{}", children);
		for (String child : children) {
			String paths = XRPC_ROOT_NODE + "/" + child;
			try {
				List<String> hps = zk.getChildren(paths, true);
				hps.forEach(hp -> rigisterInfo.putInfo(child, hp));
			} catch (Exception e) {
				logger.error("获取zk节点数据失败，节点：{}，异常：{}", paths, e);
			}
		}
	}

	private void createMainNode() {
		try {
			Stat stat = zk.exists(XRPC_ROOT_NODE, true);
			if (stat == null) {
				zk.create(XRPC_ROOT_NODE, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			logger.info("xrpc再zookeeper的主节点已经存在！");
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
	public synchronized void rigister(String methodInfo, String hostPort) {
		try {
			while (zk == null || stop) {
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

	/**
	 * 阻止zookeeper停止
	 */
	private synchronized void await() {
		while (!stop) {
			try {
				this.wait();
			} catch (Exception e) {
				logger.error("zookeeper运行发生中断异常：{}", e);
			}
		}
	}

	/**
	 * zookeeper关闭
	 */
	public synchronized void close() {
		try {
			zk.close();
		} catch (Exception e) {
			logger.error("zookeeper停止发生中断异常：{}", e);
		}
		stop = true;
		this.notifyAll();
	}

	public boolean started() {
		return !this.stop;
	}

	@Override
	public void run() {
		start();
	}

}

class WatcherImpl implements Watcher {

	private Logger logger = LoggerFactory.getLogger(WatcherImpl.class);

	private ZooKeeper zk;

	public WatcherImpl(ZooKeeper zk) {
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
					if (ZookeeperExecutor.XRPC_ROOT_NODE.equals(hostAndport) || "".equals(hostAndport)) {
						return;
					}
					String[] nodes = watchedEvent.getPath().split("/");
					String methodInfo = nodes[nodes.length - 2];
					logger.info("创建新的节点：{}", methodInfo);
				} catch (Exception e) {
					logger.error("获取节点数据异常：{}", e);
				}
			} else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
				// 发生节点改变
				try {
					byte[] data = zk.getData(watchedEvent.getPath(), true, null);
					String hostAndport = new String(data);
					if ("".equals(hostAndport)) {
						return;
					}
					logger.info("节点发生变化：{}", watchedEvent.getPath());
				} catch (Exception e) {
					logger.error("获取节点数据异常：{}", e);
				}
			}
			// ...还可以继续监听其它事件类型
		}
	}
}
