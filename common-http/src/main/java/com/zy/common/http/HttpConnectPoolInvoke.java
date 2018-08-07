package com.zy.common.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zy.common.http.assist.HttpConnectionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectPoolInvoke {
  private static final Logger logger = LoggerFactory.getLogger(HttpConnectPoolInvoke.class);
  
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
    HttpClient client = null;
    HttpPost post = null;
    MultipartEntity reqEntity = null;
    try {
      client = HttpConnectionManager.getHttpClient();
      post = new HttpPost(url);
      reqEntity = new MultipartEntity();
      // 字符串参数
      if (strParams != null && strParams.size() > 0) {
        Iterator<String> strParamIter = strParams.keySet().iterator();
        while (strParamIter.hasNext()) {
          String key = strParamIter.next();
          String value = strParams.get(key);
          reqEntity.addPart(key, new StringBody(value, Charset.forName("UTF-8")));
        }
      }
      // 字节型参数
      if (byteParams != null && byteParams.size() > 0) {
        Iterator<String> byteParamIter = byteParams.keySet().iterator();
        while (byteParamIter.hasNext()) {
          String key = byteParamIter.next();
          byte[] bts = byteParams.get(key);
          ByteArrayBody postBody = new ByteArrayBody(bts, key);
          reqEntity.addPart(key, postBody);
        }
      }
      post.setEntity(reqEntity);
      HttpResponse response = client.execute(post);
      code = response.getStatusLine().getStatusCode();
      HttpEntity entity = response.getEntity();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, Charset.forName("UTF-8"));
      } else {
        // 消费掉内容
        EntityUtils.consume(entity);
        throw new Exception("http return code=" + code);
      }
    } catch (Exception ex) {
      // 终止连接
      if (post != null) {
        post.abort();
      }
      throw new Exception("发生异常，请检查连接、参数是否正确", ex);
    } finally {
      try {
        if (reqEntity != null) {
          EntityUtils.consume(reqEntity);
        }
      } catch (Exception ex) {
        logger.error(ex.getMessage(), ex);
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
    HttpClient client = HttpConnectionManager.getHttpClient();
    HttpPost post = null;
    try {
      post = new HttpPost(url);
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
      post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
      HttpResponse response = client.execute(post);
      HttpEntity entity = response.getEntity();
      int code = response.getStatusLine().getStatusCode();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, Charset.forName("UTF-8"));
      } else {
        // 消费掉内容
        EntityUtils.consume(entity);
        throw new Exception("http return code=" + code);
      }
    } catch (Exception ex) {
      if (post != null) {
        post.abort();
      }
      throw new Exception("发生异常，请检查连接、参数是否正确", ex);
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
    HttpClient client = null;
    HttpPost post = null;
    MultipartEntity reqEntity = null;
    try {
      client = HttpConnectionManager.getHttpClient();
      post = new HttpPost(url);
      reqEntity = new MultipartEntity();
      // 字符串参数
      if (strParams != null && strParams.size() > 0) {
        Iterator<String> strParamIter = strParams.keySet().iterator();
        while (strParamIter.hasNext()) {
          String key = strParamIter.next();
          String value = strParams.get(key);
          reqEntity.addPart(key, new StringBody(value, Charset.forName("UTF-8")));
        }
      }
      // 字节型参数
      if (byteParams != null && byteParams.size() > 0) {
        Iterator<String> byteParamIter = byteParams.keySet().iterator();
        while (byteParamIter.hasNext()) {
          String key = byteParamIter.next();
          byte[] bts = byteParams.get(key);
          ByteArrayBody postBody = new ByteArrayBody(bts, key);
          reqEntity.addPart(key, postBody);
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
      post.setEntity(reqEntity);
      HttpResponse response = client.execute(post);
      code = response.getStatusLine().getStatusCode();
      HttpEntity entity = response.getEntity();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, Charset.forName("UTF-8"));
      } else {
        // 消费掉内容
        EntityUtils.consume(entity);
        throw new Exception("http return code=" + code);
      }
    } catch (Exception ex) {
      // 终止连接
      if (post != null) {
        post.abort();
      }
      throw new Exception("发生异常，请检查连接、参数是否正确", ex);
    } finally {
      try {
        if (reqEntity != null) {
          EntityUtils.consume(reqEntity);
        }
      } catch (Exception ex) {
        logger.error(ex.getMessage(), ex);
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
    HttpClient client = HttpConnectionManager.getHttpClient();
    HttpPost post = null;
    try {
      post = new HttpPost(url);
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
      post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
      HttpResponse response = client.execute(post);
      HttpEntity entity = response.getEntity();
      int code = response.getStatusLine().getStatusCode();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, Charset.forName("UTF-8"));
      } else {
        // 消费掉内容
        EntityUtils.consume(entity);
        throw new Exception("http return code=" + code);
      }
    } catch (Exception ex) {
      if (post != null) {
        post.abort();
      }
      throw new Exception("发生异常，请检查连接、参数是否正确", ex);
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
    HttpClient client = HttpConnectionManager.getHttpClient();
    HttpPost post = null;
    try {
      post = new HttpPost(url);
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
      HttpResponse response = client.execute(post);
      int code = response.getStatusLine().getStatusCode();
      HttpEntity entity = response.getEntity();
      if (code == HttpStatus.SC_OK) {
        return EntityUtils.toString(entity, Charset.forName("UTF-8"));
      } else {
        EntityUtils.consume(entity);
        throw new IllegalArgumentException("请检查连接是否正确，http return code=" + code);
      }
    } catch (Exception ex) {
      if (post != null) {
        post.abort();
      }
      throw new IllegalArgumentException("请检查连接是否正确", ex);
    }
  }
}
