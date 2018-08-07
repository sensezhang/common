package com.zy.common.downgradable;

import java.util.HashMap;
import java.util.Map;

/**
 * 降级开关
 * 
 * @author liuyu
 *
 */
public enum DowngradeSwitch {
  CLOSE(0, "关闭"), OPEN(1, "开启");
  
  private static Map<Integer, DowngradeSwitch> map;
  static {
    map = new HashMap<Integer, DowngradeSwitch>();
    DowngradeSwitch[] downgradeSwitchs = DowngradeSwitch.values();
    for (DowngradeSwitch downgradeSwitch : downgradeSwitchs) {
      map.put(downgradeSwitch.getCode(), downgradeSwitch);
    }
  }
  
  public static DowngradeSwitch valueOf(int code) {
    return map.get(code);
  }
  
  private DowngradeSwitch(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }
  
  private int code;
  private String desc;
  
  public int getCode() {
    return code;
  }
  
  public String getDesc() {
    return desc;
  }
}
