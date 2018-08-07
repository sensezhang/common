package com.zy.common.util;


public class HexStringUtil {
	/**
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		if (src == null || src.length == 0) {
			return null;
		}
    StringBuilder stringBuffer = new StringBuilder(src.length * 2);
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xff;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuffer.append(0);
			}
			stringBuffer.append(hv);
		}
		return stringBuffer.toString();
	}
	
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString ==null) {
			return null;
		}
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	
	private static byte charToByte(char c) {
		byte b =  (byte) "0123456789ABCDEF".indexOf(Character.toUpperCase(c));
		if(b<0){
			throw new RuntimeException("invalid hex string");
		}
		return b;
	}
	public static void main(String[] args) {
		byte[] bs= HexStringUtil.hexStringToBytes("636F6D2E73656E73652E64657663656E746572636F6D2E73656E73652E64657663656E746572636F6D2E73656E73652E64657663656E746572");
		System.out.println(new String(bs));
	}
}
