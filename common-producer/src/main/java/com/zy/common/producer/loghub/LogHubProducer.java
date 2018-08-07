package com.zy.common.producer.loghub;

import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.producer.ProjectConfig;
import com.zy.common.producer.conf.LogConfig;
import com.zy.common.producer.conf.LogHubConfig;
import com.zy.common.producer.service.LogProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Vector;

/**
 * Created by zhangwb on 2018/4/10.
 */
public class LogHubProducer extends LogProducer {


  private static Logger logger = LoggerFactory.getLogger(LogHubProducer.class);

  private static String IP;

  private String project;

  private String logStore;
  private String endPoint;
  private String accessKeyId;
  private String accessKey;

  private com.aliyun.openservices.log.producer.LogProducer producer;

  static {
    try {
      IP = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      logger.error("获取本地IP失败......");
    }
  }

  public LogHubProducer(LogConfig logConfig) {

    LogHubConfig logHubConfig = (LogHubConfig) logConfig;
    this.accessKey = logHubConfig.getAccessKey();
    this.accessKeyId = logHubConfig.getAccessKeyId();
    this.project = logHubConfig.getProject();
    this.endPoint = logHubConfig.getEndPonit();
    this.logStore = logHubConfig.getLogStore();
    producer = new com.aliyun.openservices.log.producer.LogProducer(logHubConfig.getProducerConfig());
    // 添加project配置
    producer.setProjectConfig(new ProjectConfig(project, endPoint, accessKeyId, accessKey));
  }

  @Override
  public void send() {
    final Vector<LogItem> logGroup = new Vector<>();
    LogItem logItem = new LogItem((int) (new Date().getTime() / 1000));
    logItem.PushBack("content", log);
    logGroup.add(logItem);
    pool.execute(() -> producer.send(project, logStore, topic, IP, logGroup,
            new CallBack(project, logStore, topic, IP, null, logGroup, producer)));
  }

  public void setLogStore(String logStore){
    this.logStore = logStore;
  }
}
