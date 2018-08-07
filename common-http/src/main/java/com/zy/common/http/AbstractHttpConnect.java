package com.zy.common.http;

import java.util.HashMap;
import java.util.Map;

import com.zy.common.http.assist.SdkPropUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpConnect {
  protected static final Logger logger = LoggerFactory.getLogger(HttpConnection.class);
  
  protected static final int httpReadTimeout =
      Integer.valueOf(SdkPropUtil.getProperty("http.read.timeout", "60")) * 1000;
  protected static final int httpConnectTimeout =
      Integer.valueOf(SdkPropUtil.getProperty("http.connect.timeout", "2")) * 1000;
  protected String url;
  protected StringBuilder param = new StringBuilder();
  protected Map<String, String> headers = new HashMap<String, String>();
  protected String HttpMeThod = "POST";
  
  public abstract String sendRequest();
  
  public abstract AbstractHttpConnect appendParam(String key, String value);
  
  public abstract AbstractHttpConnect addHeaders(String key, String value);
  
  protected String getUrl() {
    return url;
  }
  
  protected StringBuilder getParam() {
    return param;
  }
  
  protected void setUrl(String url) {
    this.url = url;
  }
  
  public String getHttpMeThod() {
    return HttpMeThod;
  }
  
  public void setHttpMeThod(String httpMeThod) {
    HttpMeThod = httpMeThod;
  }
}
