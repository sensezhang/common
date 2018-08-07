package com.zy.quartz;

import com.zy.common.config.PropertiesUtil;
import com.zy.common.lock.redis.RedisLuaDistributedLock;
import com.zy.common.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Created by Administrator on 2018/1/22.
 * 1.使用redis作为任务调度中心，采用了redis的自动过期与分布式锁特性
 * 2.每个服务器的ip加项目名作为每台服务的唯一别名
 * 3.通过redis中对应的key值中的value来判断执行的是哪台服务器
 * 4.每次执行任务之前判定下redis中的quartz_prefix_是否为空，如果为空，则设置当前ip进去，设置
 * 一定时间的有效期，并执行定时任务；如果不为空，判断是否与本机ip相同，相同则执行定时任务，否则跳过
 * 5.设置有效期是为了某台机器发生故障时能够进行故障转移
 */
public class QuartzCluster {

  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private static String QUARTZ_PREFIX = "quartz_prefix_";
  private static int MAX_TIMEOUT = 2;

  private String lockName;
  private volatile boolean QUARTZ_STATUS = false;

  static {
    PropertiesUtil properties = new PropertiesUtil("common-quartz.properties");
    QUARTZ_PREFIX = properties.getProperty("quartz.prefix", "quartz_prefix_");
    MAX_TIMEOUT = Integer.parseInt(properties.getProperty("max.timeout", "2"));
  }

  public QuartzCluster(String lockName) {
    this.lockName = lockName;
  }

  public boolean getCheckStatus() {
    return QUARTZ_STATUS;
  }

  /**
   * @desc 每秒都需要刷新，分布式定时任务检查节点执行状态
   * @author zhangwb
   * @date 2018/1/22 17:59
   * @version 1.0
   */
  public void checkStatus() {
    RedisLuaDistributedLock distributedLock = new RedisLuaDistributedLock(lockName, 1);
    try {
      boolean lock = distributedLock.lock();
      if (!lock) {
        return;
      }
      String ip = InetAddress.getLocalHost().getHostAddress();
      // 获取服务器上的工作ip
      String currentIp = RedisUtil.get(QUARTZ_PREFIX + lockName);
      if (currentIp == null) {
        RedisUtil.setex(QUARTZ_PREFIX + lockName, ip, MAX_TIMEOUT);
        QUARTZ_STATUS = true;
        return;
      }
      //如果是当前服务器，则返回true
      if (currentIp.equals(ip)) {
        RedisUtil.setex(QUARTZ_PREFIX + lockName, ip, MAX_TIMEOUT);
        QUARTZ_STATUS = true;
        return;
      } else {
        QUARTZ_STATUS = false;
        return;
      }
    } catch (Exception e) {
      logger.error("异常：" + e);
      QUARTZ_STATUS = false;
      return;
    } finally {
      distributedLock.unLock();
    }
  }
}
