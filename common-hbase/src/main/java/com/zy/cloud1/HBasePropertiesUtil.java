package com.zy.cloud1;

import com.zy.common.config.PropertiesUtil;

/**
 * Created by Administrator on 2018/3/30.
 */
public class HBasePropertiesUtil {

  private HBasePropertiesUtil() {
  }

  private static PropertiesUtil propertiesUtil;

  private static final String PROPERTIES_NAME = "hbase.properties";

  static {
    propertiesUtil = new PropertiesUtil(PROPERTIES_NAME);
  }

  public static String getStr(String key, String defaultValue) {
    return propertiesUtil.getProperty(key, defaultValue);
  }
}
