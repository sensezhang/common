package com.zy.cloud1;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/3/30.
 */
public class HbaseUtilsTest {

  @Test
  public void testCreateTable() {
    HBaseUtils.createTable("mytest", "myfc1", "myfc2", "myfc3");
    HBaseUtils.close();
  }

  @Test
  public void testSave() {
    HBaseUtils.save("mytest", "2", "myfc1", "sex", "men");
    HBaseUtils.save("mytest", "2", "myfc1", "name", "xiaoming");
    HBaseUtils.save("mytest", "2", "myfc1", "age", "32");
    HBaseUtils.save("mytest", "2", "myfc2", "sex", "woman");
    HBaseUtils.save("mytest", "2", "myfc2", "name", "xiaohong");
    HBaseUtils.save("mytest", "2", "myfc2", "age", "23");
  }

  @Test
  public void testGet(){
    String name = HBaseUtils.byGet("mytest", "2", "myfc1", "name");
    System.out.println("name:"+name);
    assertEquals("xiaoming", name);
  }

  @Test
  public void testGet2(){
    Map<String, String> map = HBaseUtils.byGet("mytest", "2", "myfc1");
    System.out.println("resuly:"+ map);
    assertNotNull(map);
  }

  @Test
  public void testGet3(){
    Map<String ,Map<String,String>> map = HBaseUtils.byGet("mytest","2");
    System.out.println("result" + map);
    assertNotNull(map);
  }
  @Test
  public void testDropTable() {
    HBaseUtils.dropTable("mytest");
  }
}
