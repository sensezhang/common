package com.zy.common.http;

/**
 * Created by Administrator on 2017/8/30.
 */
public class TestStatic {

  public static String str = "lkjqoiugeriqb ikqh";
  static {
    System.out.println("--------------");
    str="11111";
  }

  public static String stirng = "ewqewqeqw";
  public static String getStr(){
    return str;
  }
}
