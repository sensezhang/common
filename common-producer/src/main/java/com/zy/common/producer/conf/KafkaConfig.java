package com.zy.common.producer.conf;

import java.util.Properties;

/**
 * Created by zhangwb on 2018/4/10.
 */
public class KafkaConfig extends LogConfig {

  private Properties properties;

  public KafkaConfig(Properties properties){
    this.properties = properties;
  }

  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }
}
