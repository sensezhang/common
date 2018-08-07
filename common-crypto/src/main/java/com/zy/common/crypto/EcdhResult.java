/*===================================================================
 * 北京深思数盾科技有限公司
 * 日期：2015年8月17日 下午5:42:31
 * 作者：jiangtao
 * 版本：1.0.0
 * 版权：All rights reserved.
 *===================================================================
 * 修订日期           修订人               描述
 * 2015年8月17日     jiangtao	      创建
 */
package com.zy.common.crypto;

import java.security.spec.ECPoint;

/**
 * @author jiangt
 */
public class EcdhResult {
  private byte[] secret;
  private ECPoint point;
  
  public EcdhResult(byte[] secret, ECPoint point) {
    this.secret = secret;
    this.point = point;
  }
  
  public byte[] getSecret() {
    return secret;
  }
  
  public ECPoint getPoint() {
    return point;
  }
}
