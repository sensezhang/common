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



import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;


public class RedisSentinelPoolHolder {
  private RedisSentinelPoolHolder() {
  }
  
  private static class PoolHoledr {
    // private static redis.clients.util.Pool pool;
    private static CustomPool customPool;
    
    static {
      boolean sentinelMode =
          "sentinel".equalsIgnoreCase(SdkPropUtil.getProperty("redis.mode", "sentinel"));
      // 可选值： sentinel 、 aliyun
      
      int maxIdle = Integer.parseInt(SdkPropUtil.getProperty("redis.maxidle", "200")); // 最大空闲连接数
      int maxTotal = Integer.parseInt(SdkPropUtil.getProperty("redis.maxtotal", "300")); // 最大连接数
      int maxWaitMillis = Integer.parseInt(SdkPropUtil.getProperty("redis.maxwaitmillis", "300"));
      int timeout = Integer.parseInt(SdkPropUtil.getProperty("redis.timeout", "3000"));
      
      if (sentinelMode) { // 哨兵模式
        String hosts = SdkPropUtil.getProperty("redis.address", "127.0.0.1:26379");
        
        
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
        
        String masterName = SdkPropUtil.getProperty("redis.mastername", "mymaster");
        String password = SdkPropUtil.getProperty("redis.sentinepassword", null);
        
        JedisSentinelPool pool =
            new JedisSentinelPool(masterName, sentinels, poolConfig, timeout, password);
        
        customPool = new CustomPool(pool);
      } else { // 阿里云模式
        String host = SdkPropUtil.getProperty("redis.host", "127.0.0.1");
        int port = Integer.parseInt(SdkPropUtil.getProperty("redis.port", "6379"));
        String password = SdkPropUtil.getProperty("redis.password", null);
        
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        JedisPool pool = new JedisPool(poolConfig, host, port, timeout, password);
        customPool = new CustomPool(pool);
      }
    }
  }
  
  public static CustomPool getInstance() {
    return PoolHoledr.customPool;
  }
}
