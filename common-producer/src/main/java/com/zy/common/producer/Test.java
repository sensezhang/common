package com.zy.common.producer;

import com.zy.common.producer.conf.KafkaConfig;
import com.zy.common.producer.conf.LogHubConfig;
import com.zy.common.producer.kafka.KafkaProducer;
import com.zy.common.producer.loghub.LogHubProducer;
import com.zy.common.producer.service.LogProducer;

import java.util.Properties;

/**
 * Created by zhangwb on 2018/4/10.
 */
public class Test {
  private static LogProducer logProducer;
  static {
    LogHubConfig logConfig = new LogHubConfig();
    logConfig.setAccessKey("LRu5C6Bp3wHAy43n7p9DJdW5vYUR5W");
    logConfig.setAccessKeyId("ryac1mqiO2eObj8r");
    logConfig.setEndPonit("cn-beijing.log.aliyuncs.com");
    logConfig.setProject("test-north2");
    logConfig.setLogStore("test-1st");
    logProducer = new LogHubProducer(logConfig);
  }
  public static void main(String[] args) {

    /******************lohhub test**********************/
//    for(int i = 0; i<100; i++) {
//      send("120.79.185.189,RT,licenseKeepalive,13043446616,106a2748d29f490eb55fd6d9fdcf3fe5,0800000000000239,2018-04-10 15:25:56,31"+i);
//      System.out.println("---------------------"+i);
//    }
    /******************lohhub test end**********************/
    /******************kafka send test**********************/
    Properties properties = new Properties();
    properties.put("metadata.broker.list","10.10.9.35:9092,10.10.9.36:9092,10.10.9.37:9092");
    properties.put("partitioner.class","com.zy.common.producer.kafka.SamplePartition");

    /**
     * ack机制
     * 0 which means that the producer never waits for an acknowledgement from the broker
     * 1 which means that the producer gets an acknowledgement after the leader replica has received the data
     * -1 The producer gets an acknowledgement after all in-sync replicas have received the data
     */
    properties.put("request.required.acks", "1");
    // 消息发送类型 同步/异步
    properties.put("producer.type", "sync");
    // 指定message序列化类，默认kafka.serializer.DefaultEncoder
    properties.put("serializer.class", "kafka.serializer.StringEncoder");
    KafkaConfig kafkaConfig = new KafkaConfig(properties);
    LogProducer kafkaProducer = new KafkaProducer(kafkaConfig);
    kafkaProducer.setLog("120.79.185.189,RT,licenseKeepalive,13043446616,106a2748d29f490eb55fd6d9fdcf3fe5,0800000000000239,2018-04-10 15:25:56,31");
    kafkaProducer.setTopic("my-topic");
    kafkaProducer.send();
    /******************kafka test end**********************/
  }

  public static void send(String s){
    logProducer.setLog(s);
    logProducer.setTopic("");
    logProducer.send();
  }
}
