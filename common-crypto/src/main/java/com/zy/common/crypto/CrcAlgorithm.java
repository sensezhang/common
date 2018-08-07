/*===================================================================
 * 北京深思数盾科技有限公司
 * 日期：2015年8月21日 下午3:32:46
 * 作者：jiangtao
 * 版本：1.0.0
 * 版权：All rights reserved.
 *===================================================================
 * 修订日期           修订人               描述
 * 2015年8月21日     jiangtao	      创建
 */
package com.zy.common.crypto;

import com.zy.common.util.BytesUtil;

import java.util.zip.CRC32;

/**
 * @author jiangt
 */
public class CrcAlgorithm {
  
  public static long crc32(byte[] data) {
    CRC32 crc32 = new CRC32();
    crc32.update(data);
    return crc32.getValue();
  }
  
  public static int crc32Int(byte[] data) {
    CRC32 crc32 = new CRC32();
    crc32.update(data);
    return (int) crc32.getValue();
  }
  
  /**
   * 小尾
   * 
   * @param data
   * @return
   */
  public static byte[] crc32IntBytes(byte[] data) {
    return BytesUtil.toBytesSmall(crc32Int(data));
  }
  
  /**
   * 小尾
   * 
   * @param data
   * @return
   */
  public static byte[] crc32LongBytes(byte[] data) {
    return BytesUtil.toBytesSmall(crc32(data));
  }
}
