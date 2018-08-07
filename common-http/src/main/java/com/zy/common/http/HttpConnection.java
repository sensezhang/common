package com.zy.common.http;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Set;

public class HttpConnection extends AbstractHttpConnect {
  
  public HttpConnection(String connectionUrl) {
    setUrl(connectionUrl);
  }
  
  @Override
  public AbstractHttpConnect appendParam(String key, String value) {
    if (param.length() != 0) {
      param.append("&");
    }
    try {
      param.append(key).append("=").append(URLEncoder.encode(value, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return this;
  }
  
  @Override
  public String sendRequest() {
    HttpURLConnection connection = null;
    InputStream in = null;
    BufferedReader breader = null;
    try {
      URL url = new URL(getUrl());
      
      connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod(getHttpMeThod());
      connection.setConnectTimeout(httpConnectTimeout);
      connection.setReadTimeout(httpReadTimeout);
      if (!headers.isEmpty()) {
        Set<String> keys = headers.keySet();
        for (String key : keys) {
          String value = headers.get(key);
          connection.addRequestProperty(key, value);
        }
      }
      if (StringUtils.isNotBlank(param.toString())) {
        OutputStream out = null;
        try {
          out = connection.getOutputStream();
          out.write(param.toString().getBytes());
          out.flush();
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        } finally {
          try {
            if (out != null) {
              out.close();
            }
          } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
          }
        }
      }
      int code = connection.getResponseCode();
      if (code >= 200 && code < 400) {
        in = connection.getInputStream();
      } else {
        in = connection.getErrorStream();
      }
      breader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
      String result = breader.readLine();
      return result;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (ProtocolException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception ex) {
        logger.error(ex.getMessage(), ex);
      }
      try {
        if (breader != null) {
          breader.close();
        }
      } catch (Exception ex) {
        logger.error(ex.getMessage(), ex);
      }
      try {
        if (connection != null) {
          connection.disconnect();
        }
      } catch (Exception ex) {
        logger.error(ex.getMessage(), ex);
      }
    }
  }
  
  @Override
  public AbstractHttpConnect addHeaders(String key, String value) {
    headers.put(key, value);
    return this;
  }
  
  public static void main(String[] args) {
//    HttpConnection connection =
//        new HttpConnection("http://ip.zyyun.com/query.do?ip=10.10.9.36");
//    connection.setHttpMeThod("GET");
////    connection.appendParam("version", "2");
//    System.out.println(connection.sendRequest());
    System.out.println(TestStatic.stirng);
  }
}
