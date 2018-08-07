package com.zy.common.http.assist;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLContext;

/**
 * httpcomponents 自4.3版本后有较大改动，包括httpclient和连接池的用法，该连接池类根据4.3的改动重新实现.
 * 
 * @author zhonglj . .
 */
public class HttpConnectionManagerV2 {
  private static final Logger logger =
      LoggerFactory.getLogger(HttpConnectionManagerV2.class);
  /**
   * 连接超时时间
   */
  private static int httpConnectTimeout = 2 * 1000;
  /**
   * 读取超时时间
   */
  private static int httpReadTimeout = 60 * 1000; 
  private static int maxTotalConnections = 20; 
  private static RequestConfig requestConfig = RequestConfig.custom()
      .setConnectTimeout(httpConnectTimeout).setSocketTimeout(httpReadTimeout).build(); 
  private static PoolingHttpClientConnectionManager cm; 
  private static IdleConnectionMonitorThreadV2 idleConnMonitor;
  
  private static void init() {
    try {
      httpConnectTimeout =
          Integer.parseInt(SdkPropUtil.getProperty("http.connect.timeout", "2")) * 1000;
      httpReadTimeout =
          Integer.parseInt(SdkPropUtil.getProperty("http.read.timeout", "60")) * 1000;  
      requestConfig = RequestConfig.custom().setConnectTimeout(httpConnectTimeout)
          .setSocketTimeout(httpReadTimeout).build();
      maxTotalConnections =
          Integer.parseInt(SdkPropUtil.getProperty("httpClient.max.connect.size", "20")); 
      logger.info("HttpConnectionManagerV2 loaded config...httpConnectTimeout:" + httpConnectTimeout + ", httpReadTimeout"
          + httpReadTimeout + ", maxTotalConnections:" + maxTotalConnections);
      
      SSLContext sslcontext = SSLContexts.createSystemDefault();
      SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
          sslcontext, new String[] { "TLSv1", "SSLv3" }, null,
          SSLConnectionSocketFactory.getDefaultHostnameVerifier());
      
      Registry<ConnectionSocketFactory> socketFactoryRegistry =
          RegistryBuilder.<ConnectionSocketFactory> create()
              .register("http", PlainConnectionSocketFactory.INSTANCE)
              .register("https", sslConnectionSocketFactory)
              .build();
      cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
      cm.setMaxTotal(maxTotalConnections);
      
      idleConnMonitor = new IdleConnectionMonitorThreadV2(cm);
      idleConnMonitor.start();
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
  }
  
  private static class HttpConnectionManagerHolder {
    static CloseableHttpClient httpClient;
    
    static {
      init();
      httpClient = HttpClients.custom().setConnectionManager(cm).build();
    }
  }
  
  /**
   * 返回HttpClient实例. 注意：连接超时、读写超时等配置需要调用方自己设置，连接池不再管理此类设置.
   * 
   * @return 返回HttpClient实例.
   */
  public static CloseableHttpClient getHttpClient() {
    return HttpConnectionManagerHolder.httpClient;
  }
  
  /**
   * 返回RequestConfig,这里默认配置了连接超时和读取超时，配置值为配置文件锁配置.
   * @return
   */
  public static RequestConfig getRequestConfig() {
    return requestConfig;
  }
  
  /**
   * 返回配置文件中配置的连接超时时间.
   * @return
   */
  public static int getHttpConnectTimeout() {
    return httpConnectTimeout;
  }
  
  /**
   * 返回配置文件中配置的读取超时时间.
   * @return
   */
  public static int getHttpReadTimeout() {
    return httpReadTimeout;
  }
  
  /**
   * 关闭连接池.一般不需要主动调用此接口去关闭连接池.
   */
  public static void close() {
    if (HttpConnectionManagerHolder.httpClient != null) {
      try {
        HttpConnectionManagerHolder.httpClient.close();
      } catch (IOException ex) {
        logger.error(ex.getMessage(), ex);
      }
    }
    if (idleConnMonitor != null) {
      idleConnMonitor.shutdown();
    }
    if (logger.isInfoEnabled()) {
      logger.info(HttpConnectionManagerV2.class.getName() + " closed!");
    }
  }
  
  
  
  
  /**
   * Test and Sample
   * 
   * @param args
   *          .
   */
  public static void main(String[] args) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    System.out.println("begin time:" + sdf.format(new Date()));
    
    HttpEntity respEntity = null;
    CloseableHttpResponse response = null;
    try {
      CloseableHttpClient httpClient = getHttpClient();
      HttpGet httpGet = new HttpGet("http://10.10.9.44:18102/ping.do");
      /*
       * setConnectTimeout:设置http连接超时，单位毫秒. setSocketTimeout:设置http读写超时，单位毫秒.
       */
      RequestConfig requestConfig =
          RequestConfig.custom().setConnectTimeout(2 * 1000).setSocketTimeout(60 * 1000).build();
      httpGet.setConfig(requestConfig);
      response = httpClient.execute(httpGet);
      respEntity = response.getEntity();
      int httpStatusCode = response.getStatusLine().getStatusCode();
      if (httpStatusCode == 200) {
        String respContent = EntityUtils.toString(respEntity, "UTF-8");
        System.out.println(respContent);
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
    
    System.out.println("end time:" + sdf.format(new Date()));
  }
}
