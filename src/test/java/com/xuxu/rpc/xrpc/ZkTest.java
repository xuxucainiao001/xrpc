package com.xuxu.rpc.xrpc;

import com.xuxu.rpc.xrpc.info.HostInfo;
import com.xuxu.rpc.xrpc.rigister.Rigister;

public class ZkTest {

	public static void main(String[] args) throws Exception {
		String rigisterUrl = "39.107.67.13:2181";
		Rigister rigister = Rigister.open(rigisterUrl);
		rigister.rigisterInfo("inteface1", new HostInfo("127.0.0.1",80));
		rigister.rigisterInfo("inteface2", new HostInfo("127.0.0.1",90));
		rigister.rigisterInfo("inteface2", new HostInfo("127.0.0.2",60));
		rigister.rigisterInfo("inteface1", new HostInfo("127.0.0.1",80));
		System.out.println(rigister.getRigisterInfo());
	}

}


