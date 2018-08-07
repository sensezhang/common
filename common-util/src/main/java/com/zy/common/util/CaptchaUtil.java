package com.zy.common.util;

//import com.google.code.kaptcha.Producer;
//import com.google.code.kaptcha.util.Config;
//import com.zy.common.redis.RedisUtil;
//import com.zy.usercenter.constants.ErrorCode;
//import com.zy.usercenter.constants.RedisKeyConstants;
//import com.zy.usercenter.exception.CommonException;
//import com.zy.usercenter.util.Base64ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 验证码（图片、短信）生产、验证辅助类
 * Created by zhanghao on 2017/6/20.
 */
public class CaptchaUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaUtil.class);
  private static final int VALIDATE_TIME = 10 * 60;//验证码有效时间

  //图片验证码相关的属性
  //    private static Properties props = new Properties();
  //    private static Producer captchaProducer;

   /* static {
        ImageIO.setUseCache(false);

        Config config1 = new Config(props);
        captchaProducer = config1.getProducerImpl();
    }*/

  /**
   * 获取图片验证码
   * @return 唯一的uuid，验证码内容存放在Redis中
   * @throws CommonException
   */
    /*public static final String getImageCaptcha() throws CommonException {
        try {
            String capText = captchaProducer.createText();
            BufferedImage bi = captchaProducer.createImage(capText);
            //如果生成的图片不为空
            if (bi != null) {
                String captchaId = UUID.randomUUID().toString();
                String base64Image = Base64ImageUtils.encodeImgageToBase64(bi);
                //一个验证码在缓存中只保存2分钟
                RedisUtil.setex(RedisKeyConstants.CAPTCHA_CODE_PREFIX + captchaId, capText + "-" + base64Image,
                        VALIDATE_TIME);
                return captchaId;
            }else{
                if(LOGGER.isInfoEnabled()){
                    LOGGER.info("生成图片验证码出错：");
                }
                throw new CommonException(ErrorCode.UNKOWN.code(), ErrorCode.UNKOWN.desc());
            }
        } catch (Exception e) {
            LOGGER.error("生成图片验证码出错：", e);
            throw new CommonException(ErrorCode.UNKOWN.code(), ErrorCode.UNKOWN.desc());
        }
    }*/

  /**
   *
   * @param captchaId
   * @return
   * @throws CommonException
   */
    /*public static final byte[] getImageCaptcha(String captchaId, boolean isBase64) throws CommonException {
        String base64ImageString = RedisUtil.get(RedisKeyConstants.CAPTCHA_CODE_PREFIX + captchaId);
        if (base64ImageString != null) {
            try{
                int index = base64ImageString.indexOf("-");
                base64ImageString = base64ImageString.substring(index + 1);
                return isBase64 ? base64ImageString.getBytes("UTF-8") : Base64ImageUtils.decodeBase64ToBytes(base64ImageString);
            } catch (Exception e) {
                LOGGER.error("base64 image captch error:", e);
                throw new CommonException(ErrorCode.UNKOWN.code(), ErrorCode.UNKOWN.desc());
            }
        }else{
            throw new CommonException(ErrorCode.CAPTCHA_IS_NULL.code(), ErrorCode.CAPTCHA_IS_NULL.desc());
        }
    }*/

  /**
   * 验证用户填写的图片验证码是否和图片上一致
   *
   * @param captchaId       图片验证码唯一id
   * @param captchaText     用户输入的验证码
   * @return
   */
    /*public static void validateImageCaptcha(String captchaId, String captchaText) throws CommonException {
        String base64ImageString = RedisUtil.get(RedisKeyConstants.CAPTCHA_CODE_PREFIX + captchaId);
        if (base64ImageString != null) {
            int index = base64ImageString.indexOf("-");
            //图片对应的真实验证码
            String capTextConfirm = base64ImageString.substring(0, index);
            if (!captchaText.toLowerCase().equals(capTextConfirm.toLowerCase())) {

                LOGGER.error("captchaId:"+captchaId+", captchaText:"+captchaText);

                throw  new CommonException(ErrorCode.CAPTCHA_IS_ERROR.code(), ErrorCode.CAPTCHA_IS_ERROR.desc());
            }
            RedisUtil.del(RedisKeyConstants.CAPTCHA_CODE_PREFIX + captchaId);
            RedisUtil.setex(RedisKeyConstants.CAPTCHA_CODE_RELATION_PREFIX  + captchaId, "true", VALIDATE_TIME);
        }else {
            throw new CommonException(ErrorCode.VERIFY_CODE.code(), "exception.captchautil.validateimagecaptcha.109");
        }
    }*/

   /* public static void validateCaptchaRelation (String account,String captchaId) throws CommonException {

        if (StringUtils.isBlank(account)) {
           throw new CommonException(ErrorCode.ILLEGAL_PARAMTER.code(), ErrorCode.ILLEGAL_PARAMTER.desc());
        }
        String key = RedisKeyConstants.CAPTCHA_CODE_RELATION_PREFIX + captchaId;
        boolean exist = RedisUtil.keyExist(key);
        if (exist) {
            //验证成功删除此缓存
            String cache = RedisUtil.get(key);
            if(!cache.equals("true")){
                throw new CommonException(ErrorCode.CAPTCHA_IS_ERROR.code(), ErrorCode.CAPTCHA_IS_ERROR.desc());
            }
            RedisUtil.del(key);
            return;
        } else {
            throw new CommonException(ErrorCode.CAPTCHA_IS_NULL.code(), ErrorCode.CAPTCHA_IS_NULL.desc());
        }

    }*/

  private static char[] numeral = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
  private static char[] allChar = { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'A', 'b', 'B',
          'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h', 'H',
          'I', 'J', 'j', 'k', 'K', 'L', 'm', 'M', 'n', 'N', 'P', 'p', 'q', 'Q', 'r', 'R', 's', 'S',
          't', 'T', 'u', 'U', 'v', 'V', 'w', 'W', 'x', 'X', 'y', 'Y', 'z', 'Z' };

  /**
   * 生产随机字符串
   *
   * @param length
   * @param isContainLetter 是否包含字母
   * @return
   */
  private static String createStr(int length, boolean isContainLetter) {
    StringBuilder randomCaptcha = new StringBuilder();
    Random random = new Random();
    if (isContainLetter) {
      for (int i = 0; i < length; i++) {
        randomCaptcha.append(allChar[random.nextInt(allChar.length)]);
      }
    } else {
      for (int i = 0; i < length; i++) {
        randomCaptcha.append(numeral[random.nextInt(numeral.length)]);
      }
    }
    return randomCaptcha.toString();
  }

  /**
   * 获取短信验证码
   *
   * @return
   */
  public static final String getPhoneOrEmailPin() {
    String pin = createStr(6, Boolean.FALSE);
    //        RedisUtil.setex(RedisKeyConstants.CAPTCHA_CODE_PREFIX+phone+":"+type, pin, VALIDATE_TIME);
    return pin;
  }

  /**
   * 校验短信验证码
   * @param phone
   * @param pin
   * @param module 功能模块
   * @return
   * @throws CommonException
   */
    /*public static final void validatePhonePin(String phone,String pin, String module) throws CommonException {
        String key = RedisKeyConstants.CAPTCHA_CODE_PREFIX + phone + ":" + module;
        String cachePin = RedisUtil.get(key);
        if(cachePin==null){
            throw new CommonException(ErrorCode.PHONE_CODE_ERROR.code(), ErrorCode.PHONE_CODE_ERROR.desc());
        } else if(!cachePin.equals(pin)){
            throw new CommonException(ErrorCode.PHONE_CODE_ERROR.code(), ErrorCode.PHONE_CODE_ERROR.desc());
        } else {//缓存phone和pin的关系 方便验证码的异步校验
            RedisUtil.del(key);
            RedisUtil.setex(key, "true", VALIDATE_TIME);
        }
    }*/

  /**
   *校验手机号是否已经验证了验证码
   * @param phone
   * @param actionType
   * @return
   */
   /* public static final void validatePhone(String phone, String actionType) throws CommonException{
        String key = RedisKeyConstants.CAPTCHA_CODE_PREFIX + phone + ":" + actionType;
        String valid = RedisUtil.get(key);
        if(valid == null || !valid.equals("true")){
            throw new CommonException(ErrorCode.PHONE_CODE_TIMEOUT.code(), ErrorCode.PHONE_CODE_TIMEOUT.desc());
        }
        RedisUtil.del(key);
    }

    public static void delPhonePin(String phone, String actionType){
        RedisUtil.del(RedisKeyConstants.CAPTCHA_CODE_PREFIX + phone + ":" +actionType);
    }*/
  /**
   * 计算同一手机号发送的短信验证码次数
   * @param phone
   * @param actionType
   */
    /*public static int cachePinCountCalc(String phone, String actionType) {
        String key = RedisKeyConstants.ACCOUNT_SEND_CODE_COUNT_KEY + actionType + phone;

        boolean exist = RedisUtil.keyExist(key);
        if (exist) {
            int count = Integer.parseInt(RedisUtil.get(key));
            count++ ;
            RedisUtil.setex(key, count, VALIDATE_TIME);
            return count;
        } else {
            RedisUtil.setex(key, 1, VALIDATE_TIME);
            return 1;
        }
    }*/

  /**
   * 获取同一手机号发送的短信验证码的次数
   *
   * @param
   * @param
   * @return
   */
   /* public static int getPinCount(String phone, String actionType) {
        String key = RedisKeyConstants.ACCOUNT_SEND_CODE_COUNT_KEY + actionType + phone;
        boolean exist = RedisUtil.keyExist(key);
        if (exist) {
            int count = Integer.parseInt(RedisUtil.get(key));
            return count;
        } else {
            return 0;
        }
    }*/
  public static void main(String[] args) {
        /*String pin = "039742";
        final String[] strings = {pin};
        for (String string : strings) {
            System.out.println(string);
            System.out.println( Arrays.toString(strings));
        }*/
    String cap = CaptchaUtil.getPhoneOrEmailPin();
    System.out.println(cap);
  }

}

