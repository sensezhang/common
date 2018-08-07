package com.zy.common.http.assist;

import org.apache.http.impl.conn.PoolingClientConnectionManager;

/**
 * 对超时的不可用连接进行关闭
 * 
 * @author liyongjian
 *
 */
public class IdleConnectionMonitorThread extends Thread {
  private PoolingClientConnectionManager connMgr;
  private volatile boolean shutdown;
  
  public IdleConnectionMonitorThread(PoolingClientConnectionManager connMgr) {
    super();
    this.connMgr = connMgr;
  }
  
  @Override
  public void run() {
    try {
      while (!shutdown) {
        synchronized (this) {
          wait(30000);
          // 关闭超时连接
          connMgr.closeExpiredConnections();
          // 可选，关闭连接，在此不进行关闭
          // 关闭空闲时间大于30s的
          // connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
        }
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public void shutdown() {
    shutdown = true;
    synchronized (this) {
      notifyAll();
    }
  }
}
