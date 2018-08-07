package com.zy.common.lock.redis;

import com.zy.common.lock.DistributedLock;
import com.zy.common.lock.Lock;
import com.zy.common.redis.assist.CustomPool;
import com.zy.common.redis.assist.RedisSentinelPoolHolder;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by Administrator on 2016/11/17.
 * 获取锁，不维护jedisPool的开和关
 * 使用jedis的set方法新特性，实现原子操作，避免系统异常崩溃造成的死锁问题
 * 注：可能存在lua脚本执行效率较低的问题（未验证）
 */
public class RedisLuaDistributedLock extends DistributedLock {

  private static final String LOCK_SUCCESS = "OK";
  private static final Long RELEASE_SUCCESS = 1L;
  private static final String SET_IF_NOT_EXIST = "NX";
  private static final String SET_WITH_EXPIRE_TIME = "PX";

  private CustomPool customPool;
  private String redisValue;

  public RedisLuaDistributedLock(String lockName) {

    CustomPool customPool = RedisSentinelPoolHolder.getInstance();
    if (customPool == null) {
      throw new RuntimeException("pool can not be null");
    }
    this.customPool = customPool;
    lock = new Lock();
    lock.lockName = LOCK_PREFIX + lockName;
    this.redisValue = UUID.randomUUID().toString().replaceAll("-", "");
  }

  public RedisLuaDistributedLock(String lockName, int expireTime) {
    CustomPool customPool = RedisSentinelPoolHolder.getInstance();
    if (customPool == null) {
      throw new RuntimeException("pool can not be null");
    }
    this.customPool = customPool;
    lock = new Lock();
    lock.lockName = LOCK_PREFIX + lockName;
    this.setTimeout(expireTime);
    this.redisValue = UUID.randomUUID().toString().replaceAll("-", "");
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
   * 尝试获取锁，如果获取不到，立即返回false
   *
   * @return
   */
  public boolean tryLock() {
    final Thread current = Thread.currentThread();
    if (isLock() && lock.getExclusiveOwnerThread() == current) {
      lock.lockCount++;
      return true;
    }

    Jedis jedis = this.getResource();
		try {
			String result = jedis.set(this.getLockName(), this.getRedisValue(), SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME,
					this.getTimeout());
			if (LOCK_SUCCESS.equals(result)) {
				lock.isLock = true;
				lock.threadName = current.getName();
				lock.setExclusiveOwnerThread(current);
				lock.lockCount++;
				return true;
			}
			return false;
		} catch (Exception e) {
//			throw e;
			return false;
		} finally {
			
			releaseResource(jedis);
		}
  }

  /**
   * 可重入锁的解锁
   *
   * @return
   */
  @Override
  public boolean unLock() {
    if (this.lock.isLock && lock.lockCount > 1 && this.lock.getExclusiveOwnerThread() == Thread
            .currentThread()) {
      lock.lockCount--;
      return true;
    }
    if (this.lock.isLock && this.lock.getExclusiveOwnerThread() == Thread.currentThread()) {
      Jedis jedis = this.getResource();
      String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

			try {
				Object result = jedis.eval(luaScript, Collections.singletonList(this.getLockName()),
						Collections.singletonList(this.getRedisValue()));
				if (RELEASE_SUCCESS.equals(result)) {
					this.lock.isLock = false;
					this.lock.lockCount = 0;
					this.lock.threadName = null;
					this.lock.setExclusiveOwnerThread(null);
					return true;
				}
			} catch (Exception e) {
//				throw e;
				
			} finally {
				releaseResource(jedis);
			}
    }
    return false;
  }

  /**
   * 尝试获取锁
   */
  @Deprecated
  private boolean tryLockNoDeadLock() {
    //可重入
    if (lock.isLock && lock.threadName.equals(Thread.currentThread().getName())) {
      lock.lockCount++;
      return true;
    }
    Jedis jedis = this.getResource();
    long currentTime = System.currentTimeMillis();
    long newExpireTime = System.currentTimeMillis() + timeout;
    long ret = jedis.setnx(this.lock.lockName, String.valueOf(newExpireTime));
    if (ret == 0) {//数据已经被锁定，判断是否已经超时
      String oldExpireTimeString = jedis.get(this.lock.lockName);
      Long oldExpireTime = Long.valueOf(oldExpireTimeString);
      if (oldExpireTime < currentTime) {//锁已经失效
        String currentExpireTimeString = jedis
                .getSet(this.lock.lockName, String.valueOf(newExpireTime));
        Long currentExpireTime = new Long(currentExpireTimeString);
        if (currentExpireTime.longValue() == oldExpireTime.longValue()) {
          lock.ctime = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).getTime().getTime();
          lock.isLock = true;
          lock.threadName = Thread.currentThread().getName();
          lock.lockCount++;
          releaseResource(jedis);
          return true;
        } else {
          return false;
        }
      } else {//锁未失效
        return false;
      }
    } else {
      lock.ctime = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).getTime().getTime();
      lock.isLock = true;
      lock.threadName = Thread.currentThread().getName();
      lock.lockCount++;
      releaseResource(jedis);
      return true;
    }
  }

  @Deprecated
  private boolean unLockNoDeadLock() {
    //得到key对应的time ，与当前的时间做判断
    if (this.lock.isLock && lock.lockCount > 1) {
      lock.lockCount--;
      return true;
    }
    if (this.lock.isLock) {
      Jedis jedis = this.getResource();
      String currentExpireTimeString = jedis.get(this.lock.lockName);
      if (currentExpireTimeString == null) {
        this.lock.isLock = false;
        this.lock.lockCount = 0;
        this.lock.threadName = null;
        this.lock.ctime = null;
        releaseResource(jedis);
        return true;
      } else {//lockName对应的时间设置存在
        Long currentExpireTime = Long.valueOf(currentExpireTimeString);
        long my = 0;//当前线程设置的锁过期时间
        if (my == currentExpireTime.longValue() &&
                currentExpireTime.longValue() > System.currentTimeMillis()) {//自己的锁未超时
          //删除节点，释放锁
          this.lock.isLock = false;
          this.lock.lockCount = 0;
          this.lock.threadName = null;
          this.lock.ctime = null;
          releaseResource(jedis);
          jedis.del(lock.lockName);
          return true;
        } else {
          //自己设置的锁，已经超时
          this.lock.isLock = false;
          this.lock.lockCount = 0;
          this.lock.threadName = null;
          this.lock.ctime = null;
          releaseResource(jedis);
          return false;
        }
      }
    }

    return false;
  }

  /**
   * 释放锁
   *
   * @return
   */
    /*@Override
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
    }*/
    
    /* @Override
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
    }*/
  private String getRedisValue() {
    return redisValue;
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
  //    public void setLock(boolean )

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
    if (jedis == null) {
      return;
    } else {
      this.customPool.returnResource(jedis);
    }
  }

  private Jedis getResource() {
    return this.customPool.getResource();
  }

}
