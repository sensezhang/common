package com.zy.common.http;

import com.zy.common.http.assist.HttpConnectionManagerV2;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * httpcomponents 自4.3版本后有较大改动，包括httpclient和连接池的用法，该连接池连接调用类根据4.3的改动重新实现.
 * 
 * @author zhonglj . .
 */
public class HttpConnectPoolInvokeV2 {
  private static final String CHARSET_UTF8_NAME = "UTF-8";
  private static final Charset CHARSET_UTF8 = Charset.forName(CHARSET_UTF8_NAME);
  private static final ContentType TEXT_PLAIN_UTF8 = ContentType.create("text/plain", CHARSET_UTF8);
  private static final Logger logger = LoggerFactory.getLogger(HttpConnectPoolInvokeV2.class);
  
  /**
   * 带附件以part方式发送http请求
   * 
   * @param url
   *          请求地址
   * @param strParams
   *          字符串参数map
   * @param byteParams
   *          字节型参数map
   * @return
   * @throws Exception
   * @throws IOException
   * @throws ClientProtocolException
   */
  public static String sendHttpRequest(String url, HashMap<String, String> strParams,
      HashMap<String, byte[]> byteParams) throws Exception {
    int code = -1;
    CloseableHttpClient client = null;
    HttpPost post = null;
    HttpEntity reqEntity = null;
    CloseableHttpResponse response = null;
    try {
      client = HttpConnectionManagerV2.getHttpClient();
      post = new HttpPost(url);
      post.setConfig(HttpConnectionManagerV2.getRequestConfig());
      MultipartEntityBuilder reqEntityBuilder = MultipartEntityBuilder.create();
      // 字符串参数
      if (strParams != null && strParams.size() > 0) {
        Iterator<String> strParamIter = strParams.keySet().iterator();
        while (strParamIter.hasNext()) {
          String key = strParamIter.next();
          String value = strParams.get(key);
          reqEntityBuilder.addPart(key, new StringBody(value, TEXT_PLAIN_UTF8));
        }
      }
      // 字节型参数
      if (byteParams != null && byteParams.size() > 0) {
        Iterator<String> byteParamIter = byteParams.keySet().iterator();
        while (byteParamIter.hasNext()) {
          String key = byteParamIter.next();
          byte[] bts = byteParams.get(key);
          ByteArrayBody postBody = new ByteArrayBody(bts, key);
          reqEntityBuilder.addPart(key, postBody);
        }
      }
      reqEntity = reqEntityBuilder.build();
      post.setEntity(reqEntity);
      response = client.execute(post);
      code = response.getStatusLine().getStatusCode();
      HttpEntity entity = response.getEntity();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, CHARSET_UTF8);
      } else {
        // 消费掉内容
        EntityUtils.consume(entity);
        throw new Exception("http return code=" + code);
      }
    } catch (Exception ex) {
      throw new Exception("发生异常，请检查连接、参数是否正确", ex);
    } finally {
      if (reqEntity != null) {
        try {
          EntityUtils.consume(reqEntity);
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
        }
      }
      if (response != null) {
        try {
          response.close();
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
        }
      }
    }
  }
  public static String sendHttpRequestGet(String url, HashMap<String, String> strParams) throws Exception {
    return sendHttpRequestGet(url, strParams, null);
  }
  
  public static String sendHttpRequestGet(String url, HashMap<String, String> strParams, HashMap<String, String> headers) throws Exception {

    CloseableHttpClient client = HttpConnectionManagerV2.getHttpClient();
    HttpGet get = null;
    CloseableHttpResponse response = null;
    try {
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      // 字符串参数
      if (strParams != null && strParams.size() > 0) {
        Iterator<String> strParamIter = strParams.keySet().iterator();
        while (strParamIter.hasNext()) {
          String key = strParamIter.next();
          String value = strParams.get(key);
          params.add(new BasicNameValuePair(key, value));
        }
      }
      
      String paramURLStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8) );
//      post.setEntity(new UrlEncodedFormEntity(params, CHARSET_UTF8_NAME));
      System.out.println(paramURLStr);
      get = new HttpGet(url+"?"+paramURLStr);
      get.setConfig(HttpConnectionManagerV2.getRequestConfig());
      
      // 添加header
      if (headers != null && headers.size() > 0) {
        Iterator<String> headerIter = headers.keySet().iterator();
        while (headerIter.hasNext()) {
          String key = headerIter.next();
          String value = headers.get(key);
          get.setHeader(key, value);
        }
      }
      
      response = client.execute(get);
      
      //得到响应体
      HttpEntity entity = response.getEntity();
      int code = response.getStatusLine().getStatusCode();
      if (code >= 200 && code < 400) {
        return EntityUtils.toString(entity, CHARSET_UTF8);
      } else {
        // 消费掉内容
        EntityUtils.consume(entity);
        throw new Exception("http return code=" + code);
      }
    } catch (Exception ex) {
      throw new Exception("发生异常，请检查连接、参数是否正确", ex);
    } finally {
      if (response != null) {
        try {
          response.close();
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
        }
      }
    }
  
  }
  /**
   * 不带附件方式发送http请求
   * 
   * @param url
   * @param strParams
   *          参数。
   * @return
   * @throws Exception
   */
  public static String sendHttpRequest(String url, HashMap<String, String> strParams)
      throws Exception {
    CloseableHttpClient client = HttpConnectionManagerV2.getHttpClient();
    HttpPost post = null;
    CloseableHttpResponse response = null;
    try {
      post = new HttpPost(url);
      post.setConfig(HttpConnectionManagerV2.getRequestConfig());
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      // 字符串参数
      if (strParams != null && strParams.size() > 0) {
        Iterator<String> strParamIter = strParams.keySet().iterator();
        while (strParamIter.hasNext()) {
          String key = strParamIter.next();
          String value = strParams.get(key);
          params.add(new BasicNameValuePair(key, value));
        }
      }
      post.setEntity(new UrlEncodedFormEntity(params, CHARSET_UTF8_NAME));
      response = client.execute(post);
      HttpEntity entity = response.getEntity();
      int code = response.getStatusLine().getStatusCode();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, CHARSET_UTF8);
      } else {
        // 消费掉内容
        EntityUtils.consume(entity);
        throw new Exception("http return code=" + code);
      }
    } catch (Exception ex) {
      throw new Exception("发生异常，请检查连接、参数是否正确", ex);
    } finally {
      if (response != null) {
        try {
          response.close();
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
        }
      }
    }
  }
  
  /**
   * 带附件以part方式发送http请求
   * 
   * @param url
   *          请求地址
   * @param strParams
   *          字符串参数map
   * @param byteParams
   *          字节型参数map
   * @param headers
   *          header信息
   * @return
   * @throws Exception
   * @throws IOException
   * @throws ClientProtocolException
   */
  public static String sendHttpRequestHeaders(String url, HashMap<String, String> strParams,
      HashMap<String, byte[]> byteParams, HashMap<String, String> headers) throws Exception {
    int code = -1;
    CloseableHttpClient client = null;
    HttpPost post = null;
    HttpEntity reqEntity = null;
    CloseableHttpResponse response = null;
    try {
      client = HttpConnectionManagerV2.getHttpClient();
      post = new HttpPost(url);
      post.setConfig(HttpConnectionManagerV2.getRequestConfig());
      MultipartEntityBuilder reqEntityBuilder = MultipartEntityBuilder.create();
      // 字符串参数
      if (strParams != null && strParams.size() > 0) {
        Iterator<String> strParamIter = strParams.keySet().iterator();
        while (strParamIter.hasNext()) {
          String key = strParamIter.next();
          String value = strParams.get(key);
          reqEntityBuilder.addPart(key, new StringBody(value, TEXT_PLAIN_UTF8));
        }
      }
      // 字节型参数
      if (byteParams != null && byteParams.size() > 0) {
        Iterator<String> byteParamIter = byteParams.keySet().iterator();
        while (byteParamIter.hasNext()) {
          String key = byteParamIter.next();
          byte[] bts = byteParams.get(key);
          ByteArrayBody postBody = new ByteArrayBody(bts, key);
          reqEntityBuilder.addPart(key, postBody);
        }
      }
      // 添加header
      if (headers != null && headers.size() > 0) {
        Iterator<String> headerIter = headers.keySet().iterator();
        while (headerIter.hasNext()) {
          String key = headerIter.next();
          String value = headers.get(key);
          post.setHeader(key, value);
        }
      }
      reqEntity = reqEntityBuilder.build();
      post.setEntity(reqEntity);
      response = client.execute(post);
      code = response.getStatusLine().getStatusCode();
      HttpEntity entity = response.getEntity();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, CHARSET_UTF8);
      } else {
        // 消费掉内容
        EntityUtils.consume(entity);
        throw new Exception("http return code=" + code);
      }
    } catch (Exception ex) {
      throw new Exception("发生异常，请检查连接、参数是否正确", ex);
    } finally {
      if (reqEntity != null) {
        try {
          EntityUtils.consume(reqEntity);
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
        }
      }
      if (response != null) {
        try {
          response.close();
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
        }
      }
    }
  }
  
  /**
   * 不带附件方式发送http请求
   * 
   * @param url
   * @param strParams
   * @param headers
   *          header信息
   * @return
   * @throws Exception
   */
  public static String sendHttpRequestHeaders(String url, HashMap<String, String> strParams,
      Map<String, String> headers) throws Exception {
    CloseableHttpClient client = HttpConnectionManagerV2.getHttpClient();
    HttpPost post = null;
    CloseableHttpResponse response = null;
    try {
      post = new HttpPost(url);
      post.setConfig(HttpConnectionManagerV2.getRequestConfig());
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      // 字符串参数
      if (strParams != null && strParams.size() > 0) {
        Iterator<String> strParamIter = strParams.keySet().iterator();
        while (strParamIter.hasNext()) {
          String key = strParamIter.next();
          String value = strParams.get(key);
          params.add(new BasicNameValuePair(key, value));
        }
      }
      // 添加header
      if (headers != null && headers.size() > 0) {
        Iterator<String> headerIter = headers.keySet().iterator();
        while (headerIter.hasNext()) {
          String key = headerIter.next();
          String value = headers.get(key);
          post.setHeader(key, value);
        }
      }
      post.setEntity(new UrlEncodedFormEntity(params, CHARSET_UTF8_NAME));
      response = client.execute(post);
      HttpEntity entity = response.getEntity();
      int code = response.getStatusLine().getStatusCode();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, CHARSET_UTF8);
      } else {
        // 消费掉内容
        EntityUtils.consume(entity);
        throw new Exception("http return code=" + code);
      }
    } catch (Exception ex) {
      throw new Exception("发生异常，请检查连接、参数是否正确", ex);
    } finally {
      if (response != null) {
        try {
          response.close();
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
        }
      }
    }
  }
  
  /**
   * POST字节数据的方法。
   * 
   * @param url
   * @param data
   *          字节数据.
   * @param headers
   *          头信息.
   * @return
   * @throws ClientProtocolException
   * @throws IOException
   */
  public static String sendHttpPostBytesRequest(String url, byte[] data,
      Map<String, String> headers) throws ClientProtocolException, IOException {
    CloseableHttpClient client = HttpConnectionManagerV2.getHttpClient();
    HttpPost post = null;
    CloseableHttpResponse response = null;
    try {
      post = new HttpPost(url);
      post.setConfig(HttpConnectionManagerV2.getRequestConfig());
      // 添加header
      if (headers != null && headers.size() > 0) {
        Iterator<String> headerIter = headers.keySet().iterator();
        while (headerIter.hasNext()) {
          String key = headerIter.next();
          String value = headers.get(key);
          post.setHeader(key, value);
        }
      }
      ByteArrayEntity requestEntity = new ByteArrayEntity(data);
      post.setEntity(requestEntity);
      response = client.execute(post);
      int code = response.getStatusLine().getStatusCode();
      HttpEntity entity = response.getEntity();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, CHARSET_UTF8);
      } else {
        EntityUtils.consume(entity);
        throw new IllegalArgumentException("请检查连接是否正确，http return code=" + code);
      }
    } catch (Exception ex) {
      throw new IllegalArgumentException("请检查连接是否正确", ex);
    } finally {
      if (response != null) {
        try {
          response.close();
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
        }
      }
    }
  }
  
  /**
   * 发送http post请求
   * 
   * @param url
   * @param jsonStr
   *          参数。
   * @return
   * @throws Exception
   */
  public static String sendHttpPostRequest(String url, String jsonStr)
      throws Exception {
    CloseableHttpClient client = HttpConnectionManagerV2.getHttpClient();
    HttpPost post = null;
    CloseableHttpResponse response = null;
    try {
      post = new HttpPost(url);
      post.setConfig(HttpConnectionManagerV2.getRequestConfig());
      // 1. 设置请求类型为JSON
      post.setHeader("Content-Type", "application/json");
      // 2. 设置Body
      post.setEntity(new StringEntity(jsonStr, "UTF-8"));
      response = client.execute(post);
      HttpEntity entity = response.getEntity();
      int code = response.getStatusLine().getStatusCode();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, CHARSET_UTF8);
      } else {
        // 消费掉内容
        EntityUtils.consume(entity);
        throw new Exception("http return code=" + code);
      }
    } catch (Exception ex) {
      throw new Exception("发生异常，请检查连接、参数是否正确", ex);
    } finally {
      if (response != null) {
        try {
          response.close();
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
        }
      }
    }
  }
}
