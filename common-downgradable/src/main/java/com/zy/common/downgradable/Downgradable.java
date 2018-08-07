package com.zy.common.downgradable;

import com.baidu.disconf.client.common.annotations.DisconfItem;
import com.zy.common.redis.RedisUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public abstract class Downgradable {
  private static final Logger logger = LoggerFactory.getLogger(Downgradable.class);
  private static Timer timer; 
  private static final String DOWNGRADING_DISCONF_KEY = "downgrading";
  
  private static final String DOWNGRADING_LOCK_KEY = "LOCK_";
  private static final String DOWNGRADING_KEY_MAP_REDIS_KEY = "DOWNGRADING_KEY_MAP";
  
  public static final Long ONE_DAY_MILLIS = TimeUnit.DAYS.toMillis(1L);
  public static final Integer DISCONF_UNLOADED = -1;
  
  private Integer downgrading = DISCONF_UNLOADED;
  
  public Downgradable() {
    // 开启定时任务：每天24点开始周期执行
    synchronized (this) {
      if (timer == null) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new CheckDowngradingKeyMapTask(), getDealyTime(), ONE_DAY_MILLIS);
      }
    }
  }
  
  /**
   * 对降级的Key进行持久化，更新到数据库
   * @param key 降级的Key
   */
  protected abstract void persistDowngradingKey(String key);
  
  /**
   * 获取未持久化Key，延长超时时间
   * @param key 降级的Key
   */
  public void flushDowngradingKey(String key) {
    if (logger.isInfoEnabled()) {
      logger.info("==Downgradable.flushDowngradingKey({})", key);
    }
    RedisUtil.expire(key, Integer.parseInt(RedisUtil.Constants.REDIS_OVERTIME_MAX));
  }
  
  /**
   * 获取未持久化Key的列表
   * @return List<String> 未持久化Key的列表
   */
  public List<String> getDowngradingKeyMap() {
    List<String> list = null;
    String downgradingMapKey = getDowngradingMapKey();
    if (RedisUtil.keyExist(downgradingMapKey)) {
      Set<String> values = RedisUtil.getSetValues(downgradingMapKey);
      list = new ArrayList<String>(values);
    } else {
      list = new ArrayList<String>();
    }
    return list;
  }
  
  /**
   *  向未持久化Key的集合中添加
   * @param key 降级的Key
   */
  public void putIntoDowngradingKeyMap(String key) {
    if (logger.isInfoEnabled()) {
      logger.info("++Downgradable.putIntoDowngradingKeyMap({})", key);
    }
    RedisUtil.addSetValue(getDowngradingMapKey(), key);
    // 当key被添加时，对key进行一次解锁操作，避免有遗留的未解锁，导致不能持久化
    unlock(key);
  }
  
  /**
   * 从未持久化Key的集合中移除
   * @param key 降级的Key
   */
  public void removeFromDowngradingKeyMap(String key) {
    if (logger.isInfoEnabled()) {
      logger.info("--Downgradable.removeFromDowngradingKeyMap({})", key);
    }
    RedisUtil.removeValueInSet(getDowngradingMapKey(), key);
    // 当key被移除时，说明该key已经被持久化了，同时解锁该key
    unlock(key);
  }
  
  class CheckDowngradingKeyMapTask extends TimerTask {
    @Override
    public void run() {
      if (downgrading == DISCONF_UNLOADED) {
        return;
      }
      checkDowngradingKeyMap();
    }
  }
  
  @DisconfItem(key = DOWNGRADING_DISCONF_KEY)
  public Integer getDowngrading() {
    return downgrading;
  }
  
  public void checkDowngradingKeyMap() {
    if (logger.isInfoEnabled()) {
      logger.info("Check downgrading key map task started.");
    }
    // 获取由于降级未持久化的Key的列表
    List<String> unpersistentKeys = getDowngradingKeyMap();
    // 如果列表长度大于0，说明有未持久化的key， 需要处理
    if (unpersistentKeys.size() > 0) {
      Iterator<String> it = unpersistentKeys.iterator();
      String unpersistentKey = null;
      // 如果系统处于降级
      if (getDowngrading().equals(DowngradeSwitch.OPEN.getCode())) {
        // 遍历unpersistentKeys
        while(it.hasNext()) {
          unpersistentKey = it.next();
          // 延长未持久化Key的超时时间
          flushDowngradingKey(unpersistentKey);
        }
      } else {
        // 遍历unpersistentKeys
        while(it.hasNext()) {
          unpersistentKey = it.next();
          // 持久化Redis对象到数据库
          // 先对此key进行加锁，只有获取锁，才能进行持久化
          if (lock(unpersistentKey)) {
            persistDowngradingKey(unpersistentKey);
            // 持久化后，将此key从Map中移除
            removeFromDowngradingKeyMap(unpersistentKey);
          }
        }
      }
    }
    if (logger.isInfoEnabled()) {
      logger.info("Check downgrading key map task stopped.");
    }
  }

  public void setDowngrading(Integer downgrading) {
    this.downgrading = downgrading;
    if (this.downgrading == DowngradeSwitch.CLOSE.getCode()) {
      checkDowngradingKeyMap();
    }
  }
  
  /**
   * 获取距离24点的剩余时间毫秒数
   * @return 距离24点的剩余时间毫秒数
   */
  public Long getDealyTime() {
    // 距离24点的剩余毫秒数 = 一天的时间戳 - (utc当前时间毫秒数 + 东8时差毫秒数)%一天的时间戳
    return ONE_DAY_MILLIS  - (System.currentTimeMillis() + Calendar.getInstance().getTimeZone().getRawOffset()) % ONE_DAY_MILLIS;
  }
  
  protected abstract String getDowngradingMapKeyPrefix();
  
  private String getDowngradingMapKey() {
    return getDowngradingMapKeyPrefix() + DOWNGRADING_KEY_MAP_REDIS_KEY;
  }
  
  private String getVersionKey(String key) {
    return getDowngradingMapKeyPrefix() + DOWNGRADING_LOCK_KEY + key;
  }
  
  private boolean lock(String key) {
    Long version = RedisUtil.incr(getVersionKey(key));
    if (version != null && version.equals(1L)) {
      return true;
    } else {
      return false;
    }
  }
  
  private void unlock(String key) {
    String versionKey = getVersionKey(key);
    String version = RedisUtil.get(versionKey);
    if (version != null) {
      RedisUtil.del(versionKey);
    }
  }
  public static void main(String[] args) {
    System.out.println((ONE_DAY_MILLIS  - (System.currentTimeMillis() + Calendar.getInstance().getTimeZone().getRawOffset()) % ONE_DAY_MILLIS)/3600000);
  }
}
