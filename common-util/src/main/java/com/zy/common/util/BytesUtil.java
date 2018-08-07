package com.zy.common.util;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class BytesUtil {

	public static byte[] toBytes(int val) {
		byte[] b = new byte[4];
		for (int i = 3; i > 0; i--) {
			b[i] = (byte) val;
			val >>>= 8;
		}
		b[0] = (byte) val;
		return b;
	}

	public static byte[] toBytesSmall(int val) {
		byte[] tmp = toBytes(val);
		swap(tmp);
		return tmp;
	}

	public static int toInt(byte[] bytes) {
		int n = 0;
		for (int i = 0; i < 4; i++) {
			n <<= 8;
			n ^= bytes[i] & 0xFF;
		}
		return n;
	}

	public static int toIntFromSmall(byte[] bytes) {
		swap(bytes);
		return toInt(bytes);
	}

	public static byte[] toBytes(long val) {
		byte[] b = new byte[8];
		for (int i = 7; i > 0; i--) {
			b[i] = (byte) val;
			val >>>= 8;
		}
		b[0] = (byte) val;
		return b;
	}

	public static byte[] toBytesSmall(long val) {
		byte[] tmp = toBytes(val);
		swap(tmp);
		return tmp;
	}

	public static long toLong(byte[] bytes) {
		long l = 0;
		for (int i = 0; i < 8; i++) {
			l <<= 8;
			l ^= bytes[i] & 0xFF;
		}
		return l;
	}

	public static long toLongFromSmall(byte[] bytes) {
		swap(bytes);
		return toLong(bytes);
	}

	//
	public static byte[] toBytes(short val) {
		byte[] b = new byte[2];
		for (int i = 1; i > 0; i--) {
			b[i] = (byte) val;
			val >>>= 8;
		}
		b[0] = (byte) val;
		return b;
	}

	public static byte[] toBytesSmall(short val) {
		byte[] tmp = toBytes(val);
		swap(tmp);
		return tmp;
	}

	public static short toShort(byte[] bytes) {
		short n = 0;
		for (int i = 0; i < 2; i++) {
			n <<= 8;
			n ^= bytes[i] & 0xFF;
		}
		return n;
	}

	public static short toShortFromSmall(byte[] bytes) {
		swap(bytes);
		return toShort(bytes);
	}

	public static void swap(byte[] data) {
		int length = data.length;
		for (int i = 0; i < length / 2; i++) {
			byte tmp = data[i];
			data[i] = data[length - i - 1];
			data[length - i - 1] = tmp;
		}
	}
	
	public static List<Integer> getModuleIndexList(long modules){
		List<Integer> list = new LinkedList<Integer>();
		BitSet bs =BitSet.valueOf(BytesUtil.toBytesSmall(modules));
		for(int i=0; i <  bs.size() ;i++){
			if(bs.get(i)){
				list.add(i+1);
			}
		}
		return list;
	}
	
	public static byte[] getModuleIndexListBytes(long modules){
		byte[] bytes = new byte[0];
		BitSet bs =BitSet.valueOf(BytesUtil.toBytesSmall(modules));
		for(int i=0; i <  bs.size() ;i++){
			if(bs.get(i)){
				bytes = ArrayUtils.add(bytes, (byte)(i+1));
			}
		}
		return bytes;
	}
	
	public static long getModulesLong(List<Integer> moduleIndexList){
		if(moduleIndexList.size()>64){
			throw new RuntimeException("moduleIndexList size must <=64");
		}
		BitSet bs = new BitSet(64);
		for(int i=0; i < moduleIndexList.size() ;i++){
			int idx = moduleIndexList.get(i);
			if(idx <1 || idx>64){
				throw new RuntimeException("moduleIndex should be 1 to 64");
			}
			bs.set(idx-1); //moduleIndexList 元素的值范围为 1-64；对应 BitSet 的 0-63
		}
		byte [] bitBytes = bs.toByteArray();  //转为字节数组时，占用几个字节就返回几个
		if(bitBytes.length<8){
			bitBytes = ArrayUtils.addAll(bitBytes, new byte[8-bitBytes.length]);  //补0
		}
		return BytesUtil.toLongFromSmall(bitBytes);
	}
	
	public static long getModulesLong(byte[] moduleIndexList){
		if(moduleIndexList.length>64){
			throw new RuntimeException("moduleIndexList size must <=64");
		}
		BitSet bs = new BitSet(64);
		for(int i=0; i < moduleIndexList.length ;i++){
			if(moduleIndexList[i]<1 || moduleIndexList[i]>64){
				throw new RuntimeException("moduleIndex should be 1 to 64");
			}
			bs.set(moduleIndexList[i]-1); //moduleIndexList 元素的值范围为 1-64；对应 BitSet 的 0-63
		}
		byte [] bitBytes = bs.toByteArray();  //转为字节数组时，占用几个字节就返回几个
		if(bitBytes.length<8){
			bitBytes = ArrayUtils.addAll(bitBytes, new byte[8-bitBytes.length]);  //补0
		}
		return BytesUtil.toLongFromSmall(bitBytes);
	}

  /**
   * 根据起始位置及拷贝数量从源byte数组提取一段byte数组
   *
   * @param src byte数组提取源
   * @param pos 提取的起始位置，从0开始计算
   * @param size 提取byte个数
   * @return 新提取的byte数组
   */
  public static byte[] bytesExtractor(byte[] src, int pos, int size) {
    byte[] bytes = new byte[size];
    System.arraycopy(src, pos, bytes, 0, size);
    return bytes;
  }
}
