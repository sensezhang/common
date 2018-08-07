package com.zy.common.lock.redis;

import com.zy.common.lock.DistributedLock;
import com.zy.common.lock.Lock;
import com.zy.common.redis.assist.CustomPool;
import com.zy.common.redis.assist.RedisSentinelPoolHolder;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.TimeZone;


/**
 * Created by Administrator on 2016/11/17.
 * 获取锁，不维护jedisPool的开和关
 */
public class RedisDistributedLock extends DistributedLock {

    /*不维护开关*/
//    private JedisPool jedisPool;

    private CustomPool customPool;
    
    public RedisDistributedLock(String lockName) {
    	
    	CustomPool customPool = RedisSentinelPoolHolder.getInstance();
    	if(customPool == null) {
    		throw new RuntimeException("pool can not be null");
    	}
    	this.customPool = customPool;
    	lock = new Lock();
    	lock.lockName = LOCK_PREFIX + lockName;
    }
    public RedisDistributedLock(String lockName, int expireTime) {
//    	this(lockName);
    	CustomPool customPool = RedisSentinelPoolHolder.getInstance();
    	if(customPool == null) {
    		throw new RuntimeException("pool can not be null");
    	}
    	this.customPool = customPool;
    	lock = new Lock();
    	lock.lockName = LOCK_PREFIX + lockName;
    	this.setTimeout(expireTime);
    }
    

    /**
     * 获取锁，获取不到则重试
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean lock() throws InterruptedException {
        int count = 0;
        while (!this.lock.isLock) {
            if (tryLock()) {
                break;
            }
            if (count == this.retryCount) {
                break;
            }
            count++;
            if (super.getRetryIdle() > 0) {
                Thread.sleep(super.getRetryIdle());
            }
        }
        if (lock.isLock) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 尝试获取锁，获取不到立即返回false
     *
     * @return
     */
    @Override
    public boolean tryLock() {
        //可重入
    	
        if (lock.isLock && lock.threadName.equals(Thread.currentThread().getName())) {
            lock.lockCount++;
            return true;
        }

//        Jedis jedis = jedisPool.getResource();
        Jedis jedis = this.getResource();
        long l = jedis.setnx(this.lock.lockName, "");

        if (l == 0) {
            releaseResource(jedis);
            return false;
        } else {
            jedis.expire(this.lock.lockName, super.getTimeout() / 1000);
            lock.ctime = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).getTime().getTime();
            lock.isLock = true;
            lock.threadName = Thread.currentThread().getName();
            lock.lockCount++;
            releaseResource(jedis);
            return true;
        }
    }

//    private static final String LOCK_SUCCESS= "OK";
//    private static final Long RELEASE_SUCCESS = 1L;
//    private static final String SET_IF_NOT_EXIST="NX";
//    private static final String SET_WITH_EXPIRE_TIME= "PX";
    
    
    /**
     * 释放锁
     *
     * @return
     */
    @Override
    public boolean unLock() {
        //重入锁，lock/unlock成对出现
        if (this.lock.isLock && lock.lockCount > 1) {
            lock.lockCount--;
            return true;
        }
        if (this.lock.isLock) {
//            Jedis jedis = jedisPool.getResource();
        	Jedis jedis = this.getResource();
            jedis.del(this.lock.lockName);
            //重置锁状态
            this.lock.isLock = false;
            this.lock.lockCount = 0;
            this.lock.threadName = null;
            this.lock.ctime = null;
            releaseResource(jedis);
            return true;
        }
        return false;
    }
    

    /**
     * 获取锁名，即redis的key值
     *
     * @return
     */
    public String getLockName() {
        return this.lock.lockName;
    }

    /**
     * 获取锁的创建时间
     *
     * @return
     */
    public Long getLockCtime() {
        return this.lock.ctime;
    }

    /**
     * 获取锁是否被获取
     *
     * @return
     */
    public boolean isLock() {
        return this.lock.isLock;
    }

    /**
     * 获取当前线程加锁的次数
     *
     * @return
     */
    public int getLockedCount() {
        return this.lock.lockCount;
    }

    /**
     * 释放redis资源,放入jedisPool中
     *
     * @param jedis
     */
    /*private void releaseResource(Jedis jedis) {
        if (jedis == null) {
            return;
        } else {
            jedisPool.returnBrokenResource(jedis);
        }
    }*/
    private void releaseResource(Jedis jedis) {
    	if(jedis == null) {
    		return;
    	} else {
			this.customPool.returnResource(jedis);
		}
    }
    
    private Jedis getResource() {
    	return this.customPool.getResource();
    }

}
