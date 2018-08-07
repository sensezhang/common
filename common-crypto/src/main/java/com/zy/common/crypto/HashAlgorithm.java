package com.zy.common.crypto;

import java.security.MessageDigest;

public class HashAlgorithm {
  
  public static String md5buildString(String s) {
    char hexDigits[] =
        { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    try {
      byte[] md = md5build(s);
      int j = md.length;
      char str[] = new char[j << 1];
      int k = 0;
      for (int i = 0; i < j; i++) {
        byte b = md[i];
        str[k++] = hexDigits[b >>> 4 & 0xf];
        str[k++] = hexDigits[b & 0xf];
      }
      return new String(str);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static byte[] md5build(String s) {
    try {
      byte[] btInput = s.getBytes();
      MessageDigest mdInst = MessageDigest.getInstance("MD5");
      mdInst.update(btInput);
      return mdInst.digest();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static byte[] md5build(byte[] b) {
    try {
      MessageDigest mdInst = MessageDigest.getInstance("MD5");
      mdInst.update(b);
      return mdInst.digest();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static byte[] sha256Build(byte[] data) {
    try {
      MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
      mdInst.update(data);
      return mdInst.digest();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
  }
  
  public static byte[] sha1Build(byte[] data) {
    try {
      MessageDigest mdInst = MessageDigest.getInstance("SHA-1");
      mdInst.update(data);
      return mdInst.digest();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
  }
}
