package com.zy.common.http;

import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author jiangt
 */
public class FileX509TrustMamager implements X509TrustManager {
  
  /**
   * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
   *      java.lang.String)
   */
  @Override
  public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    x509TrustManager.checkClientTrusted(arg0, arg1);
  }
  
  /**
   * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
   *      java.lang.String)
   */
  @Override
  public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    x509TrustManager.checkServerTrusted(arg0, arg1);
    
  }
  
  /**
   * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
   */
  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return x509TrustManager.getAcceptedIssuers();
  }
  
  private X509TrustManager x509TrustManager;
  
  public FileX509TrustMamager(byte[] cer, String password) throws Exception {
    KeyStore ks = KeyStore.getInstance("JKS");
    ks.load(new ByteArrayInputStream(cer),
        StringUtils.isBlank(password) ? null : password.toCharArray());
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
    tmf.init(ks);
    TrustManager tms[] = tmf.getTrustManagers();
    for (TrustManager tm : tms) {
      if (tm instanceof X509TrustManager) {
        x509TrustManager = (X509TrustManager) tm;
        return;
      }
    }
    throw new Exception("Could`t initialize");
  }
}
