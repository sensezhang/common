package com.zy.common.http.assist;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpConnectionManager {
  private static HttpParams httpParams;
  private static PoolingClientConnectionManager cm;
  /**
   * 最大连接数
   */
  private static int maxTotalConnections = 20;
  /**
   * 连接超时时间
   */
  private static int httpConnectTimeout = 2 * 1000;
  /**
   * 读取超时时间
   */
  private static int httpReadTimeout = 60 * 1000;
  
  private static void initManager() {
    maxTotalConnections =
        Integer.parseInt(SdkPropUtil.getProperty("httpClient.max.connect.size", "20"));
    httpConnectTimeout =
        Integer.parseInt(SdkPropUtil.getProperty("http.connect.timeout", "2")) * 1000;
    httpReadTimeout =
        Integer.parseInt(SdkPropUtil.getProperty("http.read.timeout", "60")) * 1000;
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
    // 创建TrustManager
    X509TrustManager xtm = new X509TrustManager() {
      public void checkClientTrusted(X509Certificate[] chain, String authType)
          throws CertificateException {
      }
      
      public void checkServerTrusted(X509Certificate[] chain, String authType)
          throws CertificateException {
      }
      
      public X509Certificate[] getAcceptedIssuers() {
        return null; // return new java.security.cert.X509Certificate[0];
      }
    };
    X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
      @Override
      public void verify(String host, SSLSocket ssl) throws IOException {

      }

      @Override
      public void verify(String host, X509Certificate cert) throws SSLException {

      }

      @Override
      public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {

      }

      @Override
      public boolean verify(String s, SSLSession sslSession) {
        return true;
      }
    };
    try {
      // TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
      SSLContext ctx = SSLContext.getInstance("SSL");
      // 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
      ctx.init(null, new TrustManager[] { xtm }, null);
      // 创建SSLSocketFactory
      SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
      socketFactory.setHostnameVerifier(hostnameVerifier);
      schemeRegistry.register(new Scheme("https", 443, socketFactory));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    
    cm = new PoolingClientConnectionManager(schemeRegistry);
    cm.setMaxTotal(maxTotalConnections);
    cm.setDefaultMaxPerRoute(maxTotalConnections);
    
    httpParams = new BasicHttpParams();
    httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, httpConnectTimeout);
    httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, httpReadTimeout);
    // 检测连接是否可用
    httpParams.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, true);
    // 启动超时进行关闭扫描
    IdleConnectionMonitorThread td = new IdleConnectionMonitorThread(cm);
    td.start();
  }
  
  private static class HttpConnectionManagerHolder {
    static DefaultHttpClient defaultHttpClient;
    static {
      initManager();
      defaultHttpClient = new DefaultHttpClient(cm, httpParams);
    }
  }
  
  public static HttpClient getHttpClient() {
    return HttpConnectionManagerHolder.defaultHttpClient;
  }
  
  public int getMaxTotalConnections() {
    return maxTotalConnections;
  }
}
