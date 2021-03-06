package com.zy.common.noticeproxy.assist;

import com.zy.common.config.PropertiesUtil;

public class SdkPropUtil {
  private static final String FILENAME = "common-noticeproxy.properties";
  private static PropertiesUtil prop = new PropertiesUtil(FILENAME);

  private SdkPropUtil() {
  }

  /**
   * 
   * @param key.
   * @param defultVal.
   * @return str.
   */
  public static String getProperty(String key, String defultVal) {
    return prop.getDecProperty(key, defultVal);
  }
}
