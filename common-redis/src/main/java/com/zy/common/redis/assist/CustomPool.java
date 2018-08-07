package com.zy.common.redis.assist;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

public class CustomPool {
  private JedisSentinelPool jedisSentinelPool = null;
  private JedisPool         jedisPool         = null;
  private boolean           isSentinelMode    = false;
  
  public CustomPool(JedisSentinelPool poolInstance) {
    this.isSentinelMode = true;
    this.jedisSentinelPool = poolInstance;
  }
  
  public CustomPool(JedisPool poolInstance) {
    this.isSentinelMode = false;
    this.jedisPool = poolInstance;
  }
  
  /**
   * 
   * @return jedis.
   */
  public Jedis getResource() {
    if (isSentinelMode) {
      return jedisSentinelPool.getResource();
    } else {
      return jedisPool.getResource();
    }
  }
  
  /**
   * 
   * @param jedis.
   */
  public void returnResource(Jedis jedis) {
    if (jedis != null) {
      jedis.close();
    }
  }
}
