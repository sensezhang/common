package com.zy.common.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public class PubUtils
{
  public static final String X509 = "X.509";
  public static final String PKCS12 = "PKCS12";

  public static boolean checkCertificate(byte[] cert, byte[] root)
    throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException{
    boolean flag = false;
    try {
      X509Certificate x509Cert = getCertificate(cert);
      X509Certificate rootCert = getCertificate(root);
      checkCert(x509Cert, rootCert.getPublicKey());
      flag = true;
    }catch (InvalidKeyException|CertificateException|NoSuchAlgorithmException|NoSuchProviderException|SignatureException e){
      e.printStackTrace();
      flag = false;
    }
    return flag;
  }

  public static boolean checkCertificateRe(byte[] cert, byte[] root)
    throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException{
    boolean flag = false;
    try {
      X509Certificate x509Cert = getCertificate(cert);
      X509Certificate rootCert = getCertificate(root);
      checkCert(x509Cert, rootCert.getPublicKey());
      flag = true;
    }catch (InvalidKeyException|CertificateException|NoSuchAlgorithmException|NoSuchProviderException|SignatureException e){
      e.printStackTrace();
      flag = false;
    }
    return flag;
  }

  public static void checkCert(X509Certificate cert, PublicKey prevPublicKey)
    throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException{
    cert.checkValidity(new Date());
    cert.verify(prevPublicKey);
  }

  public static X509Certificate getCertificate(String certificatePath){
    FileInputStream in = null;
    X509Certificate certificate = null;
    try {
      CertificateFactory certificateFactory = 
        CertificateFactory.getInstance("X.509");
      in = new FileInputStream(certificatePath);
      certificate = (X509Certificate)certificateFactory.generateCertificate(in);
    } catch (Exception ex) {
      ex.printStackTrace();
      try{
        if (in != null)
          in.close();
      }catch (Exception e){
    	  e.printStackTrace();
      }
    }
    finally{
      try
      {
        if (in != null)
          in.close(); 
      } catch (Exception e) {
    	  e.printStackTrace();
      }

    }
    return certificate;
  }

  public static X509Certificate getCertificate(byte[] certByte)
  {
    X509Certificate cert = null;
    if ((certByte == null) || (certByte.length == 0)) {
      return cert;
    }
    InputStream in = null;
    try {
      in = new ByteArrayInputStream(certByte);
      CertificateFactory certificateFactory = 
        CertificateFactory.getInstance("X.509");
      cert = (X509Certificate)certificateFactory.generateCertificate(in);
    } catch (Exception ex) {
      ex.printStackTrace();
      try{
        if (in != null)
          in.close();
      }
      catch (Exception e){
    	  e.printStackTrace();
      }
    }
    finally{
      try
      {
        if (in != null)
          in.close(); 
      } catch (Exception e) {
    	  e.printStackTrace();
      }

    }
    return cert;
  }

  public static X509Certificate getCertificate(String keyStorePath, String alias, String password)
    throws Exception
  {
    KeyStore ks = getKeyStore(keyStorePath, password);
    X509Certificate certificate = (X509Certificate)ks.getCertificate(alias);
    return certificate;
  }

  public static KeyStore getKeyStore(String keyStorePath, String password)
    throws Exception{
	FileInputStream is = null;
	KeyStore ks = null;
	try{  
	    is = new FileInputStream(keyStorePath);
	    ks = KeyStore.getInstance("PKCS12");
	    ks.load(is, password.toCharArray());
	}catch(Exception ex){
		ex.printStackTrace();
	}finally{
		if(is != null){
			is.close();
		}
	}
    return ks;
  }
}