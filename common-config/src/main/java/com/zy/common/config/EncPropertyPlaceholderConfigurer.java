package com.zy.common.config;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringValueResolver;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

/**
 * 用来解密配置文件中加密的数据，在Spring中使用，兼容3.1以上版本
 *
 * @author liyongjian
 */
public class EncPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
  
    private static Logger log = LoggerFactory.getLogger(EncPropertyPlaceholderConfigurer.class);

    public static final byte[] partKeyPrefixData = {-24, -119, -71, -17, -68, -116, -27, -126,
            -69, -23, -128, -68, -27, -112, -105, 33};

    @Override
    protected void processProperties(
            ConfigurableListableBeanFactory beanFactoryToProcess,
            Properties props) throws BeansException {
        StringValueResolver valueResolver = new EncPlaceholderResolvingStringValueResolver(props);
        this.doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    @Override
    protected String parseStringValue(String strVal, Properties props,
                                      Set<?> visitedPlaceholders) {
        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(
                placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
        PlaceholderResolver resolver = new EncPropertyPlaceholderConfigurerResolver(props);
        return helper.replacePlaceholders(strVal, resolver);
    }

    private class EncPlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final PropertyPlaceholderHelper helper;

        private final PlaceholderResolver resolver;

        public EncPlaceholderResolvingStringValueResolver(Properties props) {
            this.helper = new PropertyPlaceholderHelper(
                    placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
            this.resolver = new EncPropertyPlaceholderConfigurerResolver(props);
        }

        public String resolveStringValue(String strVal) throws BeansException {
            String value = this.helper.replacePlaceholders(strVal, this.resolver);
            String r = (value.equals(nullValue) ? null : value);
            return r;
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("address", "##01##95l/S1DaPLh3Zs6oDtx6TA==");
//		EncPlaceholderResolvingStringValueResolver resolver = new EncPlaceholderResolvingStringValueResolver();
    }

    /**
     * 处理Properties配置文件的内容
     *
     * @author liyongjian
     */
    private class EncPropertyPlaceholderConfigurerResolver implements PlaceholderResolver {
        private final Properties props;
        //部分密钥串

        private byte[] partKeySuffixData = null;

        private EncPropertyPlaceholderConfigurerResolver(Properties props) {
            //build enc key
            byte[] encKey = buildEncKey(props);
            String encPrefixStr = "enc.prefix.str";
            //要加密的字段的开始字符串，如果不配置默认##01##
            String encStr = props.getProperty(encPrefixStr);
            if (encStr == null || encStr.equals("")) {
                encStr = "##01##";
            }
            Enumeration<?> keys = props.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if (!key.equals(encPrefixStr)) {
                    String val = props.getProperty(key);
                    //开始字符串如果是encStr,就代表需要解密
                    if (val.length() >= encStr.length() &&
                            val.substring(0, encStr.length()).equals(encStr)) {
                        String encVal = val.substring(encStr.length(), val.length());
                        String dec = new String(aesEcbDec(encKey, Base64.decodeBase64(encVal), key));
                        //将解密后的值写回Properties
                        props.setProperty(key, dec);
                    }
                }
            }
            this.props = props;
        }

        public String resolvePlaceholder(String placeholderName) {
            return EncPropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName, props,
                    EncPropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK);
        }
//		/**
//		 * 解密数据
//		 * @param key 密钥
//		 * @param data 需要解密的数据
//		 * @return
//		 */
//		private byte[] aesEcbDec( byte[] key, byte[] data){
//			byte[] bts = null;
//			try{
//				SecureRandom sr = new SecureRandom();
//		        SecretKey secretKey = new SecretKeySpec(key, "AES");
//		        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//		        //用密匙初始化Cipher对象
//		        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
//		        //正式执行解密操作
//		        bts = cipher.doFinal(data);
//			}catch(Exception ex){
//				throw new RuntimeException("解密配置中的加密串失败！请检查配置是否正确。");
//			}
//	        return bts;
//		}

        /**
         * 组装密钥
         *
         * @param props
         * @return
         */
        private byte[] buildEncKey(Properties props) {
            //load part enc key
            String partKeySuffixDataStr = props.getProperty("enc.part.key.str");
            //如果不正确则抛出运行时异常
            if (partKeySuffixDataStr == null || partKeySuffixDataStr.equals("")) {
                throw new RuntimeException("配置中enc.part.key.str不能为空!");
            } else {
                partKeySuffixData = Base64.decodeBase64(partKeySuffixDataStr);
                if (partKeySuffixData.length != 16) {
                    throw new RuntimeException("配置中enc.part.key.str的值不正确!");
                }
            }
            //组装密钥
            byte[] encKeyBytes = new byte[partKeyPrefixData.length + partKeySuffixData.length];
            System.arraycopy(partKeyPrefixData, 0, encKeyBytes, 0, partKeyPrefixData.length);
            System.arraycopy(partKeySuffixData, 0, encKeyBytes, partKeyPrefixData.length,
                    partKeySuffixData.length);
            return encKeyBytes;
        }
    }

    /**
     * 解密数据
     *
     * @param key  密钥
     * @param data 需要解密的数据
     * @return
     */
    public static byte[] aesEcbDec(byte[] key, byte[] data, String propKey) {
        byte[] bts = null;
        try {
            SecureRandom sr = new SecureRandom();
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
            //正式执行解密操作
            bts = cipher.doFinal(data);
        } catch (Exception ex) {
            log.error("解密配置中的" + propKey + "失败！请检查该配置项的密文是否正确。", ex);
            // 如果解密数据失败，则退出
            System.exit(0);
        }
        return bts;
    }
}
