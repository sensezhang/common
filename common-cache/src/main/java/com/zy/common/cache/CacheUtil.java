package com.zy.common.cache;

import java.util.concurrent.TimeUnit;

/**
 * 内存缓存工具 自定义缓存实现，用来解决大量访问数据库和redis缓存
 * 相当于二级缓存
 * 
 * @author wzw
 */
public class CacheUtil {
  
  private static Cache<String, Object> cache = new Cache<String, Object>();
  private static final long liveTime = 30 * 60;// 默认的超时时间
  
  /**
   * @param key
   *          key
   * @param value
   *          value
   * @param time
   *          超时时间（单位为妙）
   **/
  public static void put(String key, Object value, Long time) {
    if (time == null || time <= 0) {
      cache.put(key, value, liveTime, TimeUnit.SECONDS);
    } else {
      cache.put(key, value, time, TimeUnit.SECONDS);
    }
  }
  
  // 获得
  public static Object get(String key) {
    return cache.get(key);
  }
  
  public static void main(String[] args) {
    
    User user = new User("wzw", "13243", "3333", 33);
    CacheUtil.put("user", user, 2l);
    
    CacheUtil.put("wanghuzi", true, 2l);
    
    CacheUtil.put("wanghuzi2", 123, 10l);
    
    CacheUtil.put("wanghuzi3", 345, 10l);
    
    System.out.println(((User) CacheUtil.get("user")).toString());
    try {
      Thread.sleep(5000);
      System.out.println(CacheUtil.get("user"));
      System.out.println(CacheUtil.get("wanghuzi2"));
      System.out.println(CacheUtil.get("wanghuzi3"));
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
}

class User {
  private String name;
  private String passwd;
  private String add;
  private int age;
  
  public User(String name, String passwd, String add, int age) {
    this.name = name;
    this.passwd = passwd;
    this.add = add;
    this.age = age;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getPasswd() {
    return passwd;
  }
  
  public void setPasswd(String passwd) {
    this.passwd = passwd;
  }
  
  public String getAdd() {
    return add;
  }
  
  public void setAdd(String add) {
    this.add = add;
  }
  
  public int getAge() {
    return age;
  }
  
  public void setAge(int age) {
    this.age = age;
  }
  
  public String toString() {
    return name + "|" + passwd + "|" + add + "|" + age;
  }
  
}