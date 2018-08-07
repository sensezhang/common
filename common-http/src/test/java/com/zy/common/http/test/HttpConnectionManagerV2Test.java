package com.zy.common.http.test;

import com.zy.common.http.assist.HttpConnectionManagerV2;
import com.zy.common.http.assist.SdkPropUtil;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpConnectionManagerV2Test {
  private static final String TEST_URI = "http://10.10.9.44:18102/ping.do";
  private static final int THREAD_COUNT = 30;
  private static final long PROCESS_ALIVE_TIME = 30L * 60 * 1000;
  private static int httpConnectTimeout =
      Integer.parseInt(SdkPropUtil.getProperty("http.connect.timeout", "2")) * 1000;
  private static int httpReadTimeout =
      Integer.parseInt(SdkPropUtil.getProperty("http.read.timeout", "60")) * 1000;
  private static ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
  
  public static void main(String[] args) {
    for (int i = 0; i < THREAD_COUNT; i++) {
      executorService.execute(new HttpTask());
    }
    
    try {
      Thread.sleep(PROCESS_ALIVE_TIME);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("process over ...");
    System.exit(0);
  }
  
  private static class HttpTask implements Runnable {
    
    @Override
    public void run() {
      while (true) {     
        HttpEntity respEntity = null;
        CloseableHttpResponse response = null;
        try {
          CloseableHttpClient httpClient = HttpConnectionManagerV2.getHttpClient();
          HttpGet httpGet = new HttpGet(TEST_URI);
          /*
           * setConnectTimeout:设置http连接超时，单位毫秒.
           * setSocketTimeout:设置http读写超时，单位毫秒.
           */
          RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(httpConnectTimeout)
              .setSocketTimeout(httpReadTimeout).build();
          httpGet.setConfig(requestConfig);
          response = httpClient.execute(httpGet);
          respEntity = response.getEntity();
          int httpStatusCode = response.getStatusLine().getStatusCode();
          if (httpStatusCode == 200) {
            String respContent = EntityUtils.toString(respEntity, "UTF-8");
            System.out.println(Thread.currentThread().getId() + "===>" + respContent);
          } else {
            // http返回错误，也要消费掉HttpEntity.
            EntityUtils.consume(respEntity);
            throw new Exception("Http Request failed! Http Status Code:" + httpStatusCode);
          }
        } catch (ClientProtocolException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (response != null) {
            try {
              response.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
    
  }
}
