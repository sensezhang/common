package com.zy.common.http.assist;

import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * httpcomponents 自4.3版本后有较大改动，包括httpclient和连接池的用法，该连接池连接管理类根据4.3的改动重新实现.
 * @author zhonglj .
 * .
 */
public class IdleConnectionMonitorThreadV2 extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(IdleConnectionMonitorThreadV2.class);
  private final HttpClientConnectionManager connMgr;
  private volatile boolean shutdown;
  
  public IdleConnectionMonitorThreadV2(HttpClientConnectionManager connMgr) {
    super();
    this.connMgr = connMgr;
  }
  
  @Override
  public void run() {
    try {
      while (!shutdown) {
        synchronized (this) {
          wait(5000);
          // Close expired connections
          connMgr.closeExpiredConnections();
          // Optionally, close connections
          // that have been idle longer than 30 sec
          // connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
        }
      }
    } catch (InterruptedException ex) {
      logger.error(ex.getMessage(), ex);
    }
  }
  
  public void shutdown() {
    shutdown = true;
    synchronized (this) {
      notifyAll();
    }
  }
}
