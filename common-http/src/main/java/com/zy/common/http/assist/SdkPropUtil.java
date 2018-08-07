package com.zy.common.http.assist;

import com.zy.common.config.PropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdkPropUtil {
  private static Logger logger = LoggerFactory.getLogger(SdkPropUtil.class);
  private static final String FILENAME = "common-http.properties";
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

  /**
   * 
   * @param key
   * @param defultVal
   * @return str.
   */
  public static Integer getInt(String key, int defultVal) {
    String val = prop.getDecProperty(key, String.valueOf(defultVal));
    Integer intval = defultVal;
    if (val != null && val.length() > 0) {
      intval = Integer.parseInt(val);
    }
    return intval;
  }


  /**
   * 
   * @return int.
   */
  public static Integer getHttpConnectionPoolMax() {
    int defaultMax = 20;
    try {
      defaultMax = getInt("http.connection.pool.max", defaultMax);
    } catch (Exception ex) {
      logger.error("SdkPropUtil.getHttpConnectionPoolMax() error:" + ex.getMessage(), ex);
    }
    return defaultMax;
  }

}
