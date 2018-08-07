package com.zy.common.crypto;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Ecc 签名/验签
 * 
 * @author jiangt
 *
 */
public class EccAlgorithm {
  private static final String ALGORITHM = "EC";
  private static final String SIGNTURE_ALGORITHM = "SHA256withECDSA";
  private static KeyFactory keyFactory = null;
  
  /**
   * 签名
   * 
   * @param data
   *          签名数据
   * @return
   * 
   */
  public static byte[] sign(byte[] data, PrivateKey key) {
    byte[] sign = null;
    try {
      Signature signature = Signature.getInstance(SIGNTURE_ALGORITHM);
      signature.initSign(key);
      signature.update(data);
      sign = signature.sign();
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (SignatureException e) {
      throw new RuntimeException(e);
    }
    return sign;
  }
  
  /**
   * 验签
   * 
   * @param sign
   *          签名数据
   * @param data
   *          验签数据
   * @return
   */
  public static boolean verify(byte[] sign, byte[] data, PublicKey key) {
    boolean result = false;
    try {
      Signature signature = Signature.getInstance(SIGNTURE_ALGORITHM);
      signature.initVerify(key);
      signature.update(data);
      result = signature.verify(sign);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (SignatureException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
  
  /**
   * 从文件系统中读取公钥 读取添加zy格式公钥，需要去掉前12字节zy数据 公钥格式：X509EncodedKeySpec
   * 
   * @param keyStorePath
   *          公钥文件地址
   * @see X509EncodedKeySpec
   * @return
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PublicKey getzyPublicKey(String keyStorePath)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    FileInputStream fis = new FileInputStream(keyStorePath);
    int length = fis.available();
    byte[] publicKeyStream = new byte[length];
    fis.read(publicKeyStream);
    fis.close();
    
    return getzyPublicKey(publicKeyStream);
  }
  
  public static PublicKey getzyPublicKey(byte[] publicKeyStream)
      throws InvalidKeySpecException, NoSuchAlgorithmException
  
  {
    int length = publicKeyStream.length;
    byte[] publicKeyRealStream = new byte[length - 12];
    System.arraycopy(publicKeyStream, 12, publicKeyRealStream, 0, length - 12);
    return getPublicKey(publicKeyRealStream);
  }
  
  /**
   * 从文件系统中读取公钥 公钥格式：X509EncodedKeySpec
   * 
   * @param keyStorePath
   *          公钥文件地址
   * @see X509EncodedKeySpec
   * @return
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PublicKey getPublicKey(String keyStorePath)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    FileInputStream fis = new FileInputStream(keyStorePath);
    int length = fis.available();
    byte[] publicKeyStream = new byte[length];
    fis.read(publicKeyStream);
    fis.close();
    return getPublicKey(publicKeyStream);
  }
  
  /**
   * 从数据流中读取公钥 公钥格式：X509EncodedKeySpec
   * 
   * @see X509EncodedKeySpec
   * @param publicKeyStream
   * @return
   * @throws InvalidKeySpecException
   * @throws NoSuchAlgorithmException
   */
  public static PublicKey getPublicKey(byte[] publicKeyStream)
      throws InvalidKeySpecException, NoSuchAlgorithmException {
    if (publicKeyStream == null) {
      return null;
    }
    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyStream);
    return getKeyFactory().generatePublic(publicKeySpec);
  }
  
  /**
   * 从文件系统中读取私钥 私钥格式：PKCS8EncodedKeySpec
   * 
   * @param keyStorePath
   * @return
   * @see PKCS8EncodedKeySpec
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PrivateKey getPrivateKey(String keyStorePath)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    FileInputStream fis = new FileInputStream(keyStorePath);
    int length = fis.available();
    byte[] privateKeyStream = new byte[length];
    fis.read(privateKeyStream);
    fis.close();
    return getPrivateKey(privateKeyStream);
  }
  
  /**
   * 从数据流中读取私钥 私钥格式：PKCS8EncodedKeySpec
   * 
   * @param privateKeyStream
   * @return
   * @see PKCS8EncodedKeySpec
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PrivateKey getPrivateKey(byte[] privateKeyStream)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyStream);
    return getKeyFactory().generatePrivate(privateKeySpec);
  }
  
  private static KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
    if (keyFactory == null) {
      keyFactory = KeyFactory.getInstance(ALGORITHM);
    }
    return keyFactory;
  }
  
}
