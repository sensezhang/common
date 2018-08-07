package com.zy.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * MD5工具类
 *
 * @author liyj
 */
public class MD5Util {

    private static Logger logger = LoggerFactory.getLogger(MD5Util.class);

    private MD5Util() {
    }

    public static String string2MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
           logger.error("md5 error: str="+inStr, e);
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * MD5
     *
     * @param data 需要MD5的数据
     * @return
     */
    public static String MD5(byte[] data) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(data);
            return HexUtil.byte2HexStr(md5Bytes, md5Bytes.length);
        } catch (Exception e) {
            logger.error("", e);
            return "";
        }
    }

    /**
     * 获取文件的 MD5 值
     * @param fullPath
     */
    public static String fileMD5(String fullPath) {

        try {
            return DigestUtils.md5Hex(new FileInputStream(fullPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
