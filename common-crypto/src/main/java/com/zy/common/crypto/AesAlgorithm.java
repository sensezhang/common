package com.zy.common.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class AesAlgorithm {
  
  public static final String ALGORITHM = "AES";
  public static final String ECB_ALGORITHM = "AES/ECB/PKCS5Padding";
  public static final String CBC_ALGORITHM = "AES/CBC/PKCS5Padding";
  public static final String ECB_ALGORITHM_NOPADDING = "AES/ECB/NoPadding";
  public static final String CBC_ALGORITHM_NOPADDING = "AES/CBC/NoPadding";
  public static final String PROVIDER = "BC";
  
  /**
   * 默认种子
   */
  private static final String DEFAULT_SEED = "0f22507a10bbddd07d8a3082122966e3";
  
  static {
    Security.addProvider(new BouncyCastleProvider());
  }
  
  /**
   * AES256_ECB 解密
   * 
   * @param data
   * @param key
   * @return byte[]
   * @throws Exception
   */
  public static byte[] decryptECB(byte[] data, byte[] key) throws Exception {
    Key k = buildKey(key);
    Cipher cipher = Cipher.getInstance(ECB_ALGORITHM, PROVIDER);
    cipher.init(Cipher.DECRYPT_MODE, k);
    return cipher.doFinal(data);
  }
  
  /**
   * AES256_ECB 加密
   * 
   * @param data
   * @param key
   * @return byte[]
   * @throws Exception
   */
  public static byte[] encryptECB(byte[] data, byte[] key) throws Exception {
    Key k = buildKey(key);
    Cipher cipher = Cipher.getInstance(ECB_ALGORITHM, PROVIDER);
    cipher.init(Cipher.ENCRYPT_MODE, k);
    return cipher.doFinal(data);
  }
  
  /**
   * AES256_CBC 解密
   * 
   * @param data
   * @param key
   * @param iv
   * @return byte[]
   * @throws Exception
   */
  public static byte[] decryptCBC(byte[] data, byte[] key, byte[] iv) throws Exception {
    Key k = buildKey(key);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    Cipher cipher = Cipher.getInstance(CBC_ALGORITHM, PROVIDER);
    cipher.init(Cipher.DECRYPT_MODE, k, ivParameterSpec);
    return cipher.doFinal(data);
  }
  
  /**
   * AES256_CBC 加密
   * 
   * @param data
   * @param key
   * @param iv
   * @return byte[]
   * @throws Exception
   */
  public static byte[] encryptCBC(byte[] data, byte[] key, byte[] iv) throws Exception {
    Key k = buildKey(key);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    Cipher cipher = Cipher.getInstance(CBC_ALGORITHM, PROVIDER);
    cipher.init(Cipher.ENCRYPT_MODE, k, ivParameterSpec);
    return cipher.doFinal(data);
  }
  
  //
  /**
   * AES256_ECB 解密
   * 
   * @param data
   * @param key
   * @return byte[]
   * @throws Exception
   */
  public static byte[] decryptECBNoPadding(byte[] data, byte[] key) throws Exception {
    Key k = buildKey(key);
    Cipher cipher = Cipher.getInstance(ECB_ALGORITHM_NOPADDING, PROVIDER);
    cipher.init(Cipher.DECRYPT_MODE, k);
    return cipher.doFinal(data);
  }
  
  /**
   * AES256_ECB 加密
   * 
   * @param data
   * @param key
   * @return byte[]
   * @throws Exception
   */
  public static byte[] encryptECBNoPadding(byte[] data, byte[] key) throws Exception {
    Key k = buildKey(key);
    Cipher cipher = Cipher.getInstance(ECB_ALGORITHM_NOPADDING, PROVIDER);
    cipher.init(Cipher.ENCRYPT_MODE, k);
    return cipher.doFinal(data);
  }
  
  /**
   * AES256_CBC 解密
   * 
   * @param data
   * @param key
   * @param iv
   * @return byte[]
   * @throws Exception
   */
  public static byte[] decryptCBCNoPadding(byte[] data, byte[] key, byte[] iv) throws Exception {
    Key k = buildKey(key);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    Cipher cipher = Cipher.getInstance(CBC_ALGORITHM_NOPADDING, PROVIDER);
    cipher.init(Cipher.DECRYPT_MODE, k, ivParameterSpec);
    return cipher.doFinal(data);
  }
  
  /**
   * AES256_CBC 加密
   * 
   * @param data
   * @param key
   * @param iv
   * @return byte[]
   * @throws Exception
   */
  public static byte[] encryptCBCNoPadding(byte[] data, byte[] key, byte[] iv) throws Exception {
    Key k = buildKey(key);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    Cipher cipher = Cipher.getInstance(CBC_ALGORITHM_NOPADDING, PROVIDER);
    cipher.init(Cipher.ENCRYPT_MODE, k, ivParameterSpec);
    return cipher.doFinal(data);
  }
  
  /**
   * 生成密钥
   * 
   * @param seed
   *          种子
   * @return 密钥对象
   * @throws Exception
   */
  public static byte[] initKey(String seed) throws Exception {
    // 初始化随机产生器
    SecureRandom secureRandom = new SecureRandom();
    if (seed == null) {
      seed = DEFAULT_SEED;
    }
    secureRandom.setSeed(seed.getBytes());
    KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM, PROVIDER);
    keyGenerator.init(256, secureRandom);
    SecretKey secretKey = keyGenerator.generateKey();
    return secretKey.getEncoded();
  }
  
  /**
   * 默认生成密钥
   * 
   * @return 密钥对象
   * @throws Exception
   */
  private static Key buildKey(byte[] key) throws Exception {
    return new SecretKeySpec(key, ALGORITHM);
  }
}
