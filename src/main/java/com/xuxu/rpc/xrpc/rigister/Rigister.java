package com.xuxu.rpc.xrpc.rigister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxu.rpc.xrpc.constants.XrpcConstant;
import com.xuxu.rpc.xrpc.info.HostInfo;
import com.xuxu.rpc.xrpc.info.RigisterInfo;
import com.xuxu.rpc.xrpc.rigister.zk.ZookeeperExecutor;

/**
 * 注册中心
 * 
 * @author xuxu
 *
 */
public abstract class Rigister {
	
	private static final Logger logger=LoggerFactory.getLogger(Rigister.class);

	public static Rigister open(String rigisterUrl) {
		return new ZooKeeperRigisterImpl(rigisterUrl);
	}

	public static Rigister open(String rigisterType, String rigisterUrl) {
		if (XrpcConstant.RIGISTER_TYPE_ZOOKEEPER.equals(rigisterType)) {
			return new ZooKeeperRigisterImpl(rigisterUrl);
		}
		logger.info("无效的注册中心类型：{}，启用默认的注册中心zookeeper",rigisterType);
		return open(rigisterUrl);
	}

	public abstract void rigisterInfo(String methodInfo, HostInfo hostInfo);

	public abstract void close();

	public abstract RigisterInfo getRigisterInfo();

}

class ZooKeeperRigisterImpl extends Rigister {

	private ZookeeperExecutor excutor;

	protected ZooKeeperRigisterImpl(String rigisterUrl) {
		excutor = new ZookeeperExecutor(rigisterUrl);
		new Thread(excutor).start();
	}

	@Override
	public void rigisterInfo(String methodInfo, HostInfo hostInfo) {
		excutor.rigister(methodInfo, hostInfo.transferToString());
	}

	@Override
	public void close() {
		excutor.close();
	}

	@Override
	public RigisterInfo getRigisterInfo() {
		return excutor.getRigisterInfo();
	}

}
