/*===================================================================
 * 北京深思数盾科技有限公司
 * 日期：2015年8月17日 下午5:14:56
 * 作者：jiangtao
 * 版本：1.0.0
 * 版权：All rights reserved.
 *===================================================================
 * 修订日期           修订人               描述
 * 2015年8月17日     jiangtao	      创建
 */
package com.zy.common.util;

import java.security.SecureRandom;

/**
 * @author jiangt
 */
public class RandomUtil {
	private static SecureRandom random = new SecureRandom();
	
	
	 public static byte[] genrate128Random(){
		 byte[] bytes = new byte[128];
		 random.nextBytes(bytes);
		 return bytes;
	 }
	 
	 public static byte[] genrate16Random(){
		 byte[] bytes = new byte[16];
		 random.nextBytes(bytes);
		 return bytes;
	 }
	 
	 public static byte[] genrate256Random(){
		 byte[] bytes = new byte[256];
		 random.nextBytes(bytes);
		 return bytes;
	 }
	 
	 public static byte[] genrate32Random(){
		 byte[] bytes = new byte[32];
		 random.nextBytes(bytes);
		 return bytes;
	 }
	 public static byte[] generate4Random(){
		 byte[] bytes = new byte[4];
		 random.nextBytes(bytes);
		 return bytes;
	 }
}
