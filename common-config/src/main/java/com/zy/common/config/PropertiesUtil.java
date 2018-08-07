package com.zy.common.config;

import static com.zy.common.config.EncPropertyPlaceholderConfigurer.aesEcbDec;
import static com.zy.common.config.EncPropertyPlaceholderConfigurer.partKeyPrefixData;

import com.zy.common.util.EmailLogger;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertiesUtil {
  private static Logger log = LoggerFactory.getLogger(PropertiesUtil.class);
  
  private String propertiesFileName = null;
  private Properties props = new Properties();
  
  private byte[] encKeyBytes;
  
  public PropertiesUtil(String propertiesFileName) {
    try {
      this.propertiesFileName = propertiesFileName;
      init();
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      EmailLogger.error(ex.getMessage(), ex);
    }
  }
  
  private URL getStorageConfigURI() {
    ClassLoader loader = PropertiesUtil.class.getClassLoader();
    return loader.getResource(getPropertiesFile());
  }
  
  private void init() throws IOException {
    URL configFileURI = getStorageConfigURI();
    props.load(configFileURI.openStream());
  }
  
  public String getProperty(String key, String defaultValue) {
    return props.getProperty(key, defaultValue);
  }
  
  private String getPropertiesFile() {
    return propertiesFileName;
  }
  
  public void setPropertiesFile(String filename) {
    propertiesFileName = filename;
  }
  
  /**
   * 将加密后的配置解密并返回
   * 
   * @param key
   * @param defaultValue
   * @return
   */
  public String getDecProperty(String key, String defaultValue) {
    if (encKeyBytes == null) {// 初始化密钥
      String partKeySuffixDataStr = getProperty("enc.part.key.str", "");
      if (!StringUtils.hasText(partKeySuffixDataStr)) {
        log.warn(propertiesFileName + "文件中未配置enc.part.key.str字段，如有需要解密的字段将无法正确解密");
        return getProperty(key, defaultValue);
      }
      byte[] partKeySuffixData = Base64.decodeBase64(partKeySuffixDataStr);
      // 组装密钥
      byte[] encKeyBytes = new byte[partKeyPrefixData.length + partKeySuffixData.length];
      System.arraycopy(partKeyPrefixData, 0, encKeyBytes, 0, partKeyPrefixData.length);
      System.arraycopy(partKeySuffixData, 0, encKeyBytes, partKeyPrefixData.length,
          partKeySuffixData.length);
      this.encKeyBytes = encKeyBytes;
    }
    // 加密配置的前缀，以此为前缀的内容才会执行解密
    String encDataPrefix = getProperty("enc.prefix.str", "##01##");
    String value = getProperty(key, defaultValue);
    if (value != null && value.startsWith(encDataPrefix)) {// 对密文进行解密
      String encVal = value.substring(encDataPrefix.length(), value.length());
      value = new String(aesEcbDec(encKeyBytes, Base64.decodeBase64(encVal), key));
    }
    return value;
  }
}
