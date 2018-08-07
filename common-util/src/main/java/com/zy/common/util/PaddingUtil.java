/*===================================================================
 * 北京深思数盾科技有限公司
 * 日期：2015年8月3日 下午5:01:53
 * 作者：jiangtao
 * 版本：1.0.0
 * 版权：All rights reserved.
 *===================================================================
 * 修订日期           修订人               描述
 * 2015年8月3日     jiangtao	      创建
 */
package com.zy.common.util;

/**
 * @author jiangt
 */
public class PaddingUtil {

	public static byte[] padding16(byte[] data) {
		int length = data.length;
		byte[] result;
		if (length % 16 == 0) {
			result = data;
		} else {
			int paddingLength = 16 - length % 16;
			byte[] paddingData = new byte[length + paddingLength];
			System.arraycopy(data, 0, paddingData, 0, length);
			result = paddingData;
		}
		return result;
	}
}
