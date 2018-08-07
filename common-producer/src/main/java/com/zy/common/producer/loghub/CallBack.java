package com.zy.common.producer.loghub;

import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.producer.ILogCallback;
import com.aliyun.openservices.log.producer.LogProducer;
import com.aliyun.openservices.log.response.PutLogsResponse;
import com.zy.common.util.EmailLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

/**
 * Created by zhangwb on 2018/4/10.
 */
public class CallBack extends ILogCallback{

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  public String project;
  public String logstore;
  public String topic;
  public String shardHash;
  public String source;
  public Vector<LogItem> items;
  public LogProducer producer;

  public CallBack(String project, String logstore, String topic,
          String shardHash, String source, Vector<LogItem> items,
          LogProducer producer) {
    super();
    this.project = project;
    this.logstore = logstore;
    this.topic = topic;
    this.shardHash = shardHash;
    this.source = source;
    this.items = items;
    this.producer = producer;
  }

  public void onCompletion(PutLogsResponse response, LogException e) {
    if (e != null) {
      logger.error(e.GetErrorCode() + ", " + e.GetErrorMessage()
              + ", " + e.GetRequestId() + toString());
      EmailLogger.distinctError(60, 1, e.GetErrorCode() + ", " + e.GetErrorMessage()
              + ", " + e.GetRequestId() + toString(), e);
    } else {
      if ((completeIOEndTimeInMillis - callSendBeginTimeInMillis) > (producer.getProducerConfig().packageTimeoutInMS * 10)) {
        logger.info("send success, request id: "
                + response.GetRequestId() + toString());
      }
    }
  }

}
