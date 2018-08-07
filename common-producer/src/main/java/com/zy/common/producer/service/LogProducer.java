package com.zy.common.producer.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhangwb on 2018/4/10.
 */
public abstract class LogProducer {

  protected String log;

  protected String topic;

  protected static ExecutorService pool = Executors.newCachedThreadPool();

  public abstract void send();

  public String getLog() {
    return log;
  }

  public void setLog(String log) {
    this.log = log;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }
}
