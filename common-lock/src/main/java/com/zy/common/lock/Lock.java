package com.zy.common.lock;

public class Lock {

    /*锁名我*/
    public String lockName;
    /*锁的创建时间*/
    public Long ctime;

    public volatile boolean isLock = false;

    public String threadName;
    /*同一个线程获取锁的次数*/
    public int lockCount;
    
    private transient Thread exclusiveOwnerThread;

		public Thread getExclusiveOwnerThread() {
			return exclusiveOwnerThread;
		}

		public void setExclusiveOwnerThread(Thread exclusiveOwnerThread) {
			this.exclusiveOwnerThread = exclusiveOwnerThread;
		}
}
