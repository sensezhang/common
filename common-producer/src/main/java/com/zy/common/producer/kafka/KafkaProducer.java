package com.zy.common.producer.kafka;

import com.zy.common.producer.conf.KafkaConfig;
import com.zy.common.producer.conf.LogConfig;
import com.zy.common.producer.service.LogProducer;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * Created by zhangwb on 2018/4/10.
 */
public class KafkaProducer extends LogProducer {

  private Producer<String, String> producer;

  public KafkaProducer(LogConfig logConfig) {

    ProducerConfig producerConfig = new ProducerConfig(((KafkaConfig) logConfig).getProperties());
    producer = new Producer<>(producerConfig);
  }

  @Override
  public void send() {
    producer.send(new KeyedMessage<>(topic, log));
  }
}
