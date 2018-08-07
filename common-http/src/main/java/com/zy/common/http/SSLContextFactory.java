package com.zy.common.http;

import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.zy.common.http.assist.SdkPropUtil;
import com.zy.common.util.FileUtil;

/**
 * 
 * @author jiangt
 *
 */
public class SSLContextFactory {
  
  private SSLContextFactory() {
  }
  
  public static SSLContext getInstance() {
    return SSLContextHolder.instance;
  }
  
  private static class SSLContextHolder {
    
    private static final String SSL_KEYSTORE = SdkPropUtil.getProperty("ssl.keystore", "");
    private static final String SSL_KEYSTORE_PASSWORD =
    		SdkPropUtil.getProperty("ssl.keystore.password", "");
    
    private static SSLContext instance;
    static {
      try {
        instance = SSLContext.getInstance("SSL", "SunJSSE");
        byte[] cer = FileUtil.readFile(SSL_KEYSTORE);
        TrustManager[] trustManagers =
            new TrustManager[] { new FileX509TrustMamager(cer, SSL_KEYSTORE_PASSWORD) };
        instance.init(null, trustManagers, new SecureRandom());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    
  }
  
}
