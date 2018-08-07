package com.zy.common.http;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class HttpsConnection extends AbstractHttpConnect {
  
  public HttpsConnection(String url) {
    setUrl(url);
  }
  
  @Override
  public HttpsConnection appendParam(String key, String value) {
    if (param.length() != 0) {
      param.append("&");
    }
    try {
      param.append(key).append("=").append(URLEncoder.encode(value, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      logger.error(e.getMessage(), e);
    }
    return this;
  }
  
  @Override
  public String sendRequest() {
    HttpsURLConnection connection = null;
    InputStream in = null;
    try {
      SSLContext sslContext = SSLContextFactory.getInstance();
      URL url = new URL(getUrl());
      connection = (HttpsURLConnection) url.openConnection();
      connection.setSSLSocketFactory(sslContext.getSocketFactory());
      connection.setConnectTimeout(httpConnectTimeout);
      connection.setReadTimeout(httpReadTimeout);
      connection.setDoOutput(true);
      connection.setRequestMethod(getHttpMeThod());
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
      if (code != 200) {
        in = connection.getErrorStream();
      } else {
        in = connection.getInputStream();
      }
      BufferedReader breader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
      String result = breader.readLine();
      return result;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (ProtocolException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          logger.error(e.getMessage(), e);
        }
      }
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
  
  @Override
  public AbstractHttpConnect addHeaders(String key, String value) {
    headers.put(key, value);
    return this;
  }
}
