package com.zy.common.util;

import java.nio.charset.Charset;
import java.util.UUID;

public abstract class UUIDGenerator {
  
  /**
   * private constructor
   */
  private UUIDGenerator() {
  }
  
  public static byte[] generateBytes() {
    return generate().getBytes(Charset.forName("UTF-8"));
  }
  
  public static String generate() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
  
  public static void main(String[] args) {
  	System.out.println(UUIDGenerator.generate());
  	System.out.println(UUIDGenerator.generateBytes());
  }
}