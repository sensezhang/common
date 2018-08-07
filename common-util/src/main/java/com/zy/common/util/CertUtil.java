package com.zy.common.util;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * @Author liurh <liurh@zy.com.cn>
 * @Date 2018/3/14
 */
public class CertUtil {

  /*
   * 获取X509对象类型证书
   * @param certBin 证书byte数组
   * @return X509格式证书
   */
  public static X509Certificate getX509Cert(byte[] certBin) throws CertificateException {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certBin));
  }

  public static String getX509CnName(String dnValue) throws InvalidNameException {
    LdapName ldapName = new LdapName(dnValue);
    for (Rdn rdn : ldapName.getRdns()) {
      if (rdn.getType().equals("CN")) {
        return rdn.getValue().toString();
      }
    }
    throw new InvalidNameException("Not found CN in X500Name");
  }

  /*
   * 获取证书颁发者
   * @param certBin 证书byte数组
   * @return String
   */
  public static String getX509Issuer(byte[] certBin) throws InvalidNameException, CertificateException {
    return getX509CnName(getX509Cert(certBin).getIssuerX500Principal().getName());
  }

  /*
   * 获取证书颁发者
   * @param x509Cert X509格式证书
   * @return String
   */
  public static String getX509Issuer(X509Certificate x509Cert) throws InvalidNameException {
    return getX509CnName(x509Cert.getIssuerX500Principal().getName());
  }

  /*
   * 获取证书使用者
   * @param x509Cert X509格式证书
   * @return String
   */
  public static String getX509Subject(byte[] certBin) throws InvalidNameException, CertificateException {
    return getX509CnName(getX509Cert(certBin).getSubjectX500Principal().getName());
  }

  /*
   * 获取证书使用者
   * @param x509Cert X509格式证书
   * @return String
   */
  public static String getX509Subject(X509Certificate x509Cert) throws InvalidNameException {
    return getX509CnName(x509Cert.getSubjectX500Principal().getName());
  }

}
