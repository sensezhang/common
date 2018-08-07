package com.zy.common.producer.conf;

import com.aliyun.openservices.log.producer.ProducerConfig;

/**
 * Created by zhangwb on 2018/4/10.
 */
public class LogHubConfig extends LogConfig{

  private String project;
  private String endPonit;
  private String logStore;
  private String accessKeyId;
  private String accessKey;

  private ProducerConfig producerConfig = new ProducerConfig();

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public String getEndPonit() {
    return endPonit;
  }

  public void setEndPonit(String endPonit) {
    this.endPonit = endPonit;
  }

  public String getLogStore() {
    return logStore;
  }

  public void setLogStore(String logStore) {
    this.logStore = logStore;
  }

  public String getAccessKeyId() {
    return accessKeyId;
  }

  public void setAccessKeyId(String accessKeyId) {
    this.accessKeyId = accessKeyId;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public ProducerConfig getProducerConfig() {
    return producerConfig;
  }

  public void setProducerConfig(ProducerConfig producerConfig) {
    this.producerConfig = producerConfig;
  }
}
