package com.zy.common.lock;

/**
 * Created by Administrator on 2016/11/19.
 */
public abstract class DistributedLock {
    /*锁的超时时间,单位为毫秒，依赖于实现*/
    protected int timeout = 5000;
    /*重试次数,默认为3,若小于0，则为无限重试*/
    protected int retryCount = 100;
    /*重试间隔，单位为毫秒；默认为500，如果小于等于0，则无时间间隔，一直重试*/
    protected int retryIdle = 50;

    protected String LOCK_PREFIX = "lock_prefix_";
    protected Lock lock;

    protected abstract boolean lock() throws InterruptedException;

    protected abstract boolean tryLock();

    protected abstract boolean unLock();

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryIdle() {
        return retryIdle;
    }

    public void setRetryIdle(int retryIdle) {
        this.retryIdle = retryIdle;
    }

    /*public static class Lock {
        锁名我
        protected String lockName;
        锁的创建时间
        protected Long ctime;

        protected volatile boolean isLock = false;

        protected String threadName;
        同一个线程获取锁的次数
        protected int lockCount;
    }*/
}
