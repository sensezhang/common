package com.zy.common.crypto;

import com.zy.common.crypto.exception.IllegalRsaPureKeySize;
import com.zy.common.util.BytesUtil;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public abstract class RsaAlgorithm {
  
  public static final String ALGORITHM = "RSA";
  public static final String SIGN_ALGORITHM = "SHA256withRSA";
  private static KeyFactory keyFactory = null;
  private static final int RSA_PURE_PUB_KEY_BIT_BYTE_SIZE = 4;
  
  /**
   * 签名算法类型
   */
  public enum SIGN_ALGORITHM_TYPE {
    SHA1withRSA(1, "SHA1withRSA"), SHA256withRSA(2, "SHA256withRSA");
    private SIGN_ALGORITHM_TYPE(int value, String name) {
      this.value = value;
      this.name = name;
    }
    
    private String name;
    
    public String getName() {
      return name;
    }
    
    private int value;
    
    public int getValue() {
      return value;
    }
    
    public static SIGN_ALGORITHM_TYPE valueOf(int value) {
      switch (value) {
        case 0:
          return SHA1withRSA;
        case 1:
          return SHA256withRSA;
        default: // return null;
          throw new IllegalArgumentException("不支持的算法");
      }
    }
  }
  
  public static byte[] encrypt(PublicKey key, byte[] data) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(data);
  }
  
  public static byte[] encrypt(PrivateKey key, byte[] data) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(data);
  }
  
  public static byte[] decrypt(PublicKey key, byte[] data) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, key);
    return cipher.doFinal(data);
  }
  
  public static byte[] decrypt(PrivateKey key, byte[] data) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, key);
    return cipher.doFinal(data);
  }
  
  /**
   * @deprecated 请调用带算法的接口
   */
  public static byte[] sign(PrivateKey key, byte[] data) {
    byte[] sign = null;
    try {
      Signature signature = Signature.getInstance(SIGN_ALGORITHM);
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
  
  public static byte[] sign(PrivateKey key, byte[] data, SIGN_ALGORITHM_TYPE algorithmType) {
    byte[] sign = null;
    try {
      Signature signature = Signature.getInstance(algorithmType.getName());
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
   * @deprecated 请调用带算法的接口
   */
  public static boolean verify(byte[] sign, byte[] data, PublicKey key) {
    boolean result = false;
    try {
      Signature signature = Signature.getInstance(SIGN_ALGORITHM);
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
  
  public static boolean verify(byte[] sign, byte[] data, PublicKey key,
      SIGN_ALGORITHM_TYPE algorithmType) {
    boolean result = false;
    try {
      Signature signature = Signature.getInstance(algorithmType.getName());
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

  /**
   * 从纯公钥构建公钥对象
   *
   * @param purePubKey 纯公钥byte数组，结构参见x200接口getKeypair返回公钥
   * @return 公钥对象
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws IllegalRsaPureKeySize
   */
  public static PublicKey getPublicKeyFromPure(byte[] purePubKey)
          throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalRsaPureKeySize {
    int cursor = 0;
    int keyBits = BytesUtil.toIntFromSmall(BytesUtil.bytesExtractor(purePubKey, cursor, RSA_PURE_PUB_KEY_BIT_BYTE_SIZE));
    cursor += RSA_PURE_PUB_KEY_BIT_BYTE_SIZE;
    int rsaMaxLength = (keyBits + 7) / 8;
    int pureKeySize = (rsaMaxLength << 1) + RSA_PURE_PUB_KEY_BIT_BYTE_SIZE;
    if (pureKeySize != purePubKey.length) {
      throw new IllegalRsaPureKeySize("Pure key size should be " + pureKeySize);
    }
    byte[] bytModules = BytesUtil.bytesExtractor(purePubKey, cursor, rsaMaxLength);
    cursor += rsaMaxLength;
    byte[] bytExponent = BytesUtil.bytesExtractor(purePubKey, cursor, rsaMaxLength);
    cursor += rsaMaxLength;
    RSAPublicKeySpec rsaPubKeySpec = new RSAPublicKeySpec(new BigInteger(bytModules), new BigInteger(bytExponent));
    PublicKey pubKey = getKeyFactory().generatePublic(rsaPubKeySpec);
    return getPublicKey(pubKey.getEncoded());
  }

  /**
   * PKCS8编码的公钥转换成PKCS1编码
   * 一般Java使用的是PKCS8，openssl使用的PKCS1
   * @param pubKey
   * @return
   * @throws IOException
   */
  public static byte[] convertPubKeyPkcs8ToPkcs1(PublicKey pubKey) throws IOException {
    byte[] pkcs8Bytes = pubKey.getEncoded();
    SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(pkcs8Bytes);
    ASN1Primitive primitive = spki.parsePublicKey();
    byte[] pkcs1Bytes = primitive.getEncoded();
    return pkcs1Bytes;
  }
  
  /**
   * PKCS8编码的私钥转换成PKCS1编码
   * 一般Java使用的是PKCS8，openssl使用的PKCS1
   * @param priKey
   * @return
   * @throws IOException 
   */
  public static byte[] convertPriKeyPkcs8ToPkcs1(PrivateKey priKey) throws IOException {
    byte[] pkcs8Bytes = priKey.getEncoded();
    PrivateKeyInfo pki = PrivateKeyInfo.getInstance(pkcs8Bytes);
    ASN1Encodable encodable = pki.parsePrivateKey();
    ASN1Primitive primitive = encodable.toASN1Primitive();
    byte[] pkcs1Bytes = primitive.getEncoded();
    return pkcs1Bytes;
  }
}
