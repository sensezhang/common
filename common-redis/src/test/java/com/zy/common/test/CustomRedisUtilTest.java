package com.zy.common.test;

import com.zy.common.redis.CustomRedisUtil;
import com.zy.common.redis.RedisUtil;

public class CustomRedisUtilTest {

  public static void main(String[] args) {
    System.out.println("Redis set testRedis=0："
        + (RedisUtil.setnx("testRedis", "0", 10) == 1 ? "成功" : "失败"));
    System.out.println("Redis get testRedis=" + RedisUtil.get("testRedis"));
    
    System.out.println("Redis1 set testRedis1=1："
        + (CustomRedisUtil.setnx("testRedis1", "1", 10) == 1 ? "成功" : "失败"));
    System.out.println("Redis1 get testRedis1=" + CustomRedisUtil.get("testRedis1"));

    System.out.println("Redis2 set testRedis2=2："
        + (CustomRedis2Util.setnx("testRedis2", "2", 10) == 1 ? "成功" : "失败"));
    System.out.println("Redis2 get testRedis2=" + CustomRedis2Util.get("testRedis2"));
  }
}
