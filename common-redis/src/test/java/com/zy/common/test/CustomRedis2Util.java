package com.zy.common.test;

import com.alibaba.fastjson.JSONObject;
import com.zy.common.redis.assist.CustomPool;
import com.zy.common.redis.assist.RedisCustomPoolHolder;
import com.zy.common.util.EmailLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class CustomRedis2Util {

  private static Logger logger = LoggerFactory.getLogger(CustomRedis2Util.class);

  public static CustomPool getPool() {
    return RedisCustomPoolHolder.getInstance("custom-redis2.properties");
  }

  private static void returnResource(CustomPool pool, Jedis jedis) {
    if (jedis != null) {
      pool.returnResource(jedis);
    }
  }

  /**
   * @param key.
   * @return bol.
   */
  public static boolean keyExist(String key) {
    boolean value = false;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.exists(key);
    } catch (Exception ex) {
      logger.error("RedisUtil.keyExist(String key) error:" + ex.getMessage(), ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * @param key.
   * @return bol.
   */
  public static boolean keyExist(byte[] key) {
    boolean value = false;
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.exists(key);
    } catch (Exception ex) {
      logger.error("RedisUtil.keyExist(byte[] key) error:" + ex.getMessage(), ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  public static Set<String> keys(String pattern) {
    Set<String> keys = null;
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      keys = jedis.keys(pattern);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
    return keys;
  }

  public static Set<byte[]> keys(byte[] pattern) {
    Set<byte[]> keys = null;
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      keys = jedis.keys(pattern);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
    return keys;
  }

  /**
   * @param key.
   * @return long.
   */
  public static long getListLength(String key) {
    long value = 0;
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.llen(key);
    } catch (Exception ex) {
      logger.error("RedisUtil.getListLength error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * @param key.
   * @return bol.
   */
  public static String get(String key) {
    String value = null;
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.get(key);
    } catch (JedisConnectionException ex) {
      try {
        value = jedis.get(key);
      } catch (Exception e1) {
        logger.error("RedisUtil.get(String key) error:" + e1.getMessage(), e1);
        throw new RuntimeException(e1);
      }
    } catch (Exception ex) {
      logger.error("RedisUtil.get(String key) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * @param key.
   * @return bs.
   */
  public static byte[] get(byte[] key) {
    byte[] value = null;
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.get(key);
    } catch (JedisConnectionException ex) {
      try {
        value = jedis.get(key);
      } catch (Exception e1) {
        logger.error("RedisUtil.get(byte[] key) error:" + e1.getMessage(), e1);
        throw new RuntimeException(e1);
      }
    } catch (Exception ex) {
      logger.error("RedisUtil.get(byte[] key) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * 获取有序集合中的start-end的成员
   *
   * @param key.
   * @param start.
   * @param end.
   * @return set bs.
   */
  public static Set<byte[]> get(byte[] key, int start, int end) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      return jedis.zrevrange(key, start, end);
    } catch (Exception ex) {
      logger.error("RedisUtil.get(byte[] key, int start, int end) error:" + ex.getMessage(), ex);
      return null;
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param key.
   * @return obj.
   */
  public static Object getObject(String key, Class<?> clazz) {
    String jsonString = get(key);
    if (jsonString == null) {
      return null;
    }

    return JSONObject.parseObject(jsonString, clazz);
  }

  /**
   * @param key.
   */
  public static void set(String key, Object value) {
    String jsonString = value == null ? null : JSONObject.toJSONString(value);
    set(key, jsonString);
  }

  /**
   * @param key val.
   */
  public static void set(String key, String value) {
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.set(key, value);
    } catch (Exception ex) {
      logger.error("RedisUtil.set(String key, String value) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param key.
   * @param value.
   */
  public static void set(byte[] key, byte[] value) {
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.set(key, value);
    } catch (Exception ex) {
      logger.error("RedisUtil.set(byte[] key, byte[] value) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  public static void setex(String key, Object value, int seconds) {
    String jsonString = value == null ? null : JSONObject.toJSONString(value);
    setex(key, jsonString, seconds);
  }

  /**
   * @param key.
   * @param value.
   * @param seconds.
   */
  public static void setex(String key, String value, int seconds) {
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.setex(key, seconds, value);
    } catch (Exception ex) {
      logger.error(
              "RedisUtil.setex(String key, String value, int seconds) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param key.
   * @param value.
   * @param seconds.
   */
  public static void setex(byte[] key, byte[] value, int seconds) {
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.setex(key, seconds, value);
    } catch (Exception ex) {
      logger.error(
              "RedisUtil.setex(byte[] key, byte[] value, int seconds) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param key.
   * @param seconds.
   */
  public static void expire(String key, int seconds) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.expire(key, seconds);
    } catch (Exception ex) {
      logger.error("RedisUtil.expire(String key, int seconds) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param key.
   * @param seconds.
   */
  public static void expire(byte[] key, int seconds) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.expire(key, seconds);
    } catch (Exception ex) {
      logger.error("RedisUtil.expire(byte[] key, int seconds) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param key.
   */
  public static void del(String key) {
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.del(key);
    } catch (Exception ex) {
      logger.error("RedisUtil.del(String key) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param key.
   */
  public static void del(byte[] key) {
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.del(key);
    } catch (Exception ex) {
      logger.error("RedisUtil.del(byte[] key) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * 删除有序集合中的成员
   *
   * @param key.
   * @param members.
   */
  public static void del(byte[] key, byte[]... members) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.zrem(key, members);
    } catch (Exception ex) {
      logger.error("RedisUtil.del(byte[] key, byte[]... members) error:" + ex.getMessage(), ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param key.
   * @param value.
   */
  public static void push(String key, String value) {
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.lpush(key, value);
    } catch (Exception ex) {
      logger.error("RedisUtil.push(String key, String value) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param key.
   * @param values.
   */
  public static void push(String key, List<String> values) {
    CustomPool pool = null;
    Jedis jedis = null;
    int length = values.size();
    String[] vs = new String[length];
    for (int i = 0; i < length; i++) {
      vs[i] = values.get(i);
    }

    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.lpush(key, vs);
    } catch (Exception ex) {
      logger.error("RedisUtil.push(String key, List<String> values) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param key.
   * @return str.
   */
  public static String pop(String key) {
    String value = null;
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.rpop(key);
    } catch (Exception ex) {
      logger.error("RedisUtil.pop(String key) error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * @param key.
   * @param members.
   */
  public static void addSetValue(String key, String... members) {
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.sadd(key, members);
    } catch (Exception ex) {
      logger.error("RedisUtil.addSetValue error:" + ex.getMessage(), ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @param
   * @return setstr.
   */
  public static Set<String> getSetValues(String key) {
    Set<String> value = null;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.smembers(key);
    } catch (Exception ex) {
      logger.error("RedisUtil.getSetValues error:" + ex.getMessage(), ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * Set the specified hash field to the specified value.
   * <p>
   * If key does not exist, a new key holding a hash is created.
   * <p>
   * <b>Time complexity:</b> O(1)
   *
   * @param key
   * @param field
   * @param value
   * @return If the field already exists, and the HSET just produced an update of the value, 0 is
   * returned, otherwise if a new field is created 1 is returned.
   */
  public static Long hSet(String key, String field, String value) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      return jedis.hset(key, field, value);
    } catch (Exception ex) {
      logger.error("RedisUtil.hSet error:" + ex.getMessage(), ex);
      EmailLogger.distinctError(EmailLogger.DAY, 100, "RedisUtil.hSet error:", ex);
      return -1l;
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * Retrieve the values associated to the specified fields.
   * <p>
   * If some of the specified fields do not exist, nil values are returned. Non existing keys are
   * considered like empty hashes.
   * <p>
   * <b>Time complexity:</b> O(N) (with N being the number of fields)
   *
   * @param key
   * @param fields
   * @return Multi Bulk Reply specifically a list of all the values associated with the specified
   * fields, in the same order of the request.
   */
  public static List<String> hmGet(String key, String... fields) {
    List<String> value = null;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.hmget(key, fields);
    } catch (Exception ex) {
      logger.error("RedisUtil.hmGet error:" + ex.getMessage(), ex);
      EmailLogger.distinctError(EmailLogger.DAY, 100, "RedisUtil.hmGet error:", ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * Retrieve the values associated to the specified fields.
   * <p>
   * If some of the specified fields do not exist, nil values are returned. Non existing keys are
   * considered like empty hashes.
   * <p>
   * <b>Time complexity:</b> O(N) (with N being the number of fields)
   *
   * @param key
   * @param fields
   * @return Multi Bulk Reply specifically a list of all the values associated with the specified
   * fields, in the same order of the request.
   */
  public static List<byte[]> hmGet(byte[] key, byte[]... fields) {
    List<byte[]> value = null;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.hmget(key, fields);
    } catch (Exception ex) {
      logger.error("RedisUtil.hmGet error:" + ex.getMessage(), ex);
      EmailLogger.distinctError(EmailLogger.DAY, 100, "RedisUtil.hmGet error:", ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * If key holds a hash, retrieve the value associated to the specified field.
   * <p>
   * If the field is not found or the key does not exist, a special 'nil' value is returned.
   * <p>
   * <b>Time complexity:</b> O(1)
   *
   * @param key
   * @param field
   * @return Bulk reply
   */
  public static String hGet(String key, String field) {
    String value = null;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.hget(key, field);
    } catch (Exception ex) {
      logger.error("RedisUtil.hGet error:" + ex.getMessage(), ex);
      EmailLogger.distinctError(EmailLogger.DAY, 100, "RedisUtil.hGet error:", ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * If key holds a hash, retrieve the value associated to the specified field.
   * <p>
   * If the field is not found or the key does not exist, a special 'nil' value is returned.
   * <p>
   * <b>Time complexity:</b> O(1)
   *
   * @param key
   * @param field
   * @return Bulk reply
   */
  public static byte[] hGet(byte[] key, byte[] field) {
    byte[] value = null;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.hget(key, field);
    } catch (Exception ex) {
      logger.error("RedisUtil.hGet error:" + ex.getMessage(), ex);
      EmailLogger.distinctError(EmailLogger.DAY, 100, "RedisUtil.hGet error:", ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * Pop a value from a list, push it to another list and return it; or block until one is available
   *
   * @param srcKey
   * @param desKey
   * @param timeout
   * @return the element
   */
  public static String brpoplpush(String srcKey, String desKey, int timeout) {
    String value = null;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.brpoplpush(srcKey, desKey, timeout);
    } catch (Exception ex) {
      logger.error("RedisUtil.brpoplpush error:" + ex.getMessage(), ex);
      EmailLogger.distinctError(EmailLogger.DAY, 100, "RedisUtil.brpoplpush error:", ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }


  /**
   * Pop a value from a list, push it to another list and return it;
   *
   * @param srcKey
   * @param desKey
   * @return the element
   */
  public static String rpoplpush(String srcKey, String desKey) {
    String value = null;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      value = jedis.rpoplpush(srcKey, desKey);
    } catch (Exception ex) {
      logger.error("RedisUtil.brpoplpush error:" + ex.getMessage(), ex);
      EmailLogger.distinctError(EmailLogger.DAY, 100, "RedisUtil.brpoplpush error:", ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }


  /**
   * @param key.
   * @param member .
   * @return bol.
   * @doc 判断是否存在于key对应的set集合中
   */
  public static boolean isExistInSet(String key, String member) {
    boolean result = false;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      result = jedis.sismember(key, member);
    } catch (Exception ex) {
      logger.error("RedisUtil.isExistInSet error:" + ex.getMessage(), ex);
    } finally {
      returnResource(pool, jedis);
    }
    return result;
  }

  /**
   * @param key.
   * @param members.
   * @return bol.
   * @doc 判断是否存在于key对应的set集合中
   */
  public static long removeValueInSet(String key, String... members) {
    long result = 0L;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      result = jedis.srem(key, members);
    } catch (Exception ex) {
      logger.error("RedisUtil.removeValueInSet error:" + ex.getMessage(), ex);
    } finally {
      returnResource(pool, jedis);
    }
    return result;
  }

  /**
   * @param key.
   * @param members.
   * @return bol.
   * @doc 移除有序集合key中的元素
   */
  public static long removeValueInSortSet(final byte[] key, final byte[]... members) {
    long result = 0L;
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      result = jedis.zrem(key, members);
    } catch (Exception ex) {
      logger.error("RedisUtil.removeValueInSortSet error:" + ex.getMessage(), ex);
    } finally {
      returnResource(pool, jedis);
    }
    return result;
  }

  /**
   * @param key.
   * @param count.
   * @return list str.
   */
  public static List<String> pops(String key, int count) {
    List<String> value = new ArrayList<String>();
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      for (int i = 0; i < count; i++) {
        String result = jedis.rpop(key);
        if (result != null) {
          value.add(result);
        } else {
          break;
        }
      }
    } catch (Exception ex) {
      logger.error("RedisUtil.pops error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
    return value;
  }

  /**
   * @param channel.
   * @param message.
   */
  public static void publish(String channel, String message) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.publish(channel, message);
    } catch (Exception ex) {
      logger.error("RedisUtil.publish error:" + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * 有序集合中添加成员
   *
   * @param key.
   * @param scoreMembers.
   */
  public static void sortAdd(byte[] key, Map<byte[], Double> scoreMembers) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.zadd(key, scoreMembers);
    } catch (Exception ex) {
      logger.error("RedisUtil.sortAdd error:" + ex.getMessage(), ex);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * 有序集合中的成员总数
   *
   * @param key.
   * @param members.
   */
  public static long getCount(byte[] key) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      return jedis.zcard(key);
    } catch (Exception ex) {
      logger.error("RedisUtil.getCount error:" + ex.getMessage(), ex);
      return 0L;
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * 将key存储的数值+1.
   *
   * @param
   * @return 加1后的数值.
   */
  public static Long incr(String key) {
    CustomPool pool = null;
    Jedis jedis = null;
    Long result = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      result = jedis.incr(key);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      returnResource(pool, jedis);
    }
    return result;
  }

  /**
   * 将key存储的数值-1.
   *
   * @param
   * @return 减1后的数值.
   */
  public static Long decr(String key) {
    CustomPool pool = null;
    Jedis jedis = null;
    Long result = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      result = jedis.decr(key);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      returnResource(pool, jedis);
    }
    return result;
  }

  /**
   * @desc 添加hash
   * @author zhangwb
   * @date 2017/2/22 12:45
   * @version 1.0
   */
  public static void addMap(String key, Map<String, String> map, int seconds) {
    CustomPool pool = null;
    Jedis jedis = null;

    try {
      pool = getPool();
      jedis = pool.getResource();
      jedis.hmset(key, map);
      if (seconds > 0) {
        jedis.expire(key, seconds);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @desc 获取hash
   * @author zhangwb
   * @date 2017/2/22 12:46
   * @version 1.0
   */
  public static Map<String, String> getMap(String key) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      return jedis.hgetAll(key);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      returnResource(pool, jedis);
    }
  }

  /**
   * @desc 存在返回0 不存在返回1
   * @author zhangwb
   * @date 2017/4/24 15:12
   * @version 1.0
   */
  public static Long setnx(String key, String value, int expireTime) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      Long ret = jedis.setnx(key, value);
      if (ret == 1 && expireTime > 0) {
        jedis.expire(key, expireTime);
      }
      return ret;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (pool != null && jedis != null) {
        returnResource(pool, jedis);
      }
    }
  }

  public static Long ttl(String key) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      Long ret = jedis.ttl(key);
      return ret;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (pool != null && jedis != null) {
        returnResource(pool, jedis);
      }
    }
  }

  public static Long ttl(byte[] key) {
    CustomPool pool = null;
    Jedis jedis = null;
    try {
      pool = getPool();
      jedis = pool.getResource();
      Long ret = jedis.ttl(key);
      return ret;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (pool != null && jedis != null) {
        returnResource(pool, jedis);
      }
    }
  }

  public static class Constants {
    public static final int REDIS_OVERTIME_ONE_HOUR = 60 * 60;
    public static final String REDIS_OVERTIME_MAX = String.valueOf(30 * 24 * 60 * 60);
  }
}
