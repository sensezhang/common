/*===================================================================
 * 北京深思数盾科技有限公司
 * 日期：2017年5月23日 下午3:24:56
 * 作者：jiangtao
 * 版本：1.0.0
 * 版权：All rights reserved.
 *===================================================================
 * 修订日期           修订人               描述
 * 2017年5月23日     jiangtao	      创建
 */
package com.zy.common.noticeproxy.constants;

public enum EmailResponseCode {
  OK(0x60000000, "成功"), ILLEGAL_PARAMTER(0x66000002, "参数错误"), UNKNOW(0x66FFFFFF, "未知错误");

  private EmailResponseCode(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  private int code;
  private String desc;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public static boolean isSuccess(int code) {
    return code == OK.code;
  }
}
