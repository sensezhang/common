/**===================================================================
 * 北京深思数盾科技有限公司
 * 日期：2015年10月15日 上午10:17:34
 * 作者：jiangtao
 * 版本：1.0.0
 * 版权：All rights reserved.
 *===================================================================
 * 修订日期           修订人               描述
 * 2015年10月15日     jiangtao 创建
 */

package com.zy.common.redis.assist;

import com.zy.common.config.PropertiesUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

public class RedisCustomPoolHolder {
  
  private static class PoolHoledr {
    private static HashMap<String, CustomPool> poolMap;
    static {
      poolMap = new HashMap<String, CustomPool>();
    }
  }
  
  public static CustomPool getCustomPool(String filePath) {
    // 1. 初始化PropertiesUtil
    PropertiesUtil prop = new PropertiesUtil(filePath);
    boolean sentinelMode = "sentinel"
        .equalsIgnoreCase(prop.getDecProperty("redis.mode", "sentinel"));

    // 2. 获取配置信息
    String hosts = prop.getDecProperty("redis.address", "");
    String masterName = prop.getDecProperty("redis.mastername", "mymaster");
    String sentinelPassword = prop.getDecProperty("redis.sentinepassword", null);

    String host = prop.getDecProperty("redis.host", "");
    int port = Integer.parseInt(prop.getDecProperty("redis.port", "6379"));
    String password = prop.getDecProperty("redis.password", "");
    int maxIdle = Integer.parseInt(prop.getDecProperty("redis.maxidle", "200"));
    int maxTotal = Integer.parseInt(prop.getDecProperty("redis.maxtotal", "300"));
    int maxWaitMillis = Integer.parseInt(prop.getDecProperty("redis.maxwaitmillis", "300"));
    int timeout = Integer.parseInt(prop.getDecProperty("redis.timeout", "3000"));

    // 3. 构建Redis连接池
    if (sentinelMode) {
      return buildSentinelRedisPool(hosts, masterName, sentinelPassword, maxIdle, maxTotal,
          maxWaitMillis, timeout);
    } else {
      return buildAliyunRedisPool(host, password, port, maxIdle, maxTotal,
          maxWaitMillis, timeout);
    }
  }

  private static CustomPool buildSentinelRedisPool(String hosts, String masterName, String password,
      int maxIdle, int maxTotal, int maxWaitMillis, int timeout) {
    Set<String> sentinels = new HashSet<String>();
    String[] host = hosts.split(",");
    for (int i = 0; i < host.length; i++) {
      sentinels.add(host[i]);
    }
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(maxTotal);
    poolConfig.setMaxIdle(maxIdle);
    poolConfig.setMaxWaitMillis(maxWaitMillis);
    poolConfig.setTestOnBorrow(true);

    JedisSentinelPool pool = new JedisSentinelPool(masterName, sentinels, poolConfig, timeout,
        password);

    return new CustomPool(pool);
  }

  private static CustomPool buildAliyunRedisPool(String host, String password, int port, int maxIdle, int maxTotal,
      int maxWaitMillis, int timeout) {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(maxTotal);
    poolConfig.setMaxIdle(maxIdle);
    poolConfig.setMaxWaitMillis(maxWaitMillis);
    poolConfig.setTestOnBorrow(false);
    poolConfig.setTestOnReturn(false);
    JedisPool pool = new JedisPool(poolConfig, host, port, timeout, password);
    return new CustomPool(pool);
  }
  
  public static CustomPool getInstance(String filePath) {
    if (PoolHoledr.poolMap.get(filePath) == null) {
      synchronized (PoolHoledr.class) {
        if (PoolHoledr.poolMap.get(filePath) == null) {
          CustomPool customPool = getCustomPool(filePath);
          PoolHoledr.poolMap.put(filePath, customPool);
        }
      }
    }
    return PoolHoledr.poolMap.get(filePath);
  }
}
