package com.zy.common.lock.zookeeper;

import com.zy.common.lock.DistributedLock;

public class ZookeeperDistributedLock extends DistributedLock {

	@Override
	protected boolean lock() throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean unLock() {
		// TODO Auto-generated method stub
		return false;
	}

}
