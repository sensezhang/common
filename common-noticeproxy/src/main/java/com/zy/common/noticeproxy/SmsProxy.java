package com.zy.common.noticeproxy;

import com.alibaba.fastjson.JSONObject;
import com.zy.cloud.coreservice.sdk.auth.utils.http.AuthSdkHttpRequest;
import com.zy.common.noticeproxy.assist.SdkPropUtil;
import com.zy.common.noticeproxy.bean.MailContent;
import com.zy.common.noticeproxy.constants.SmsTemplate;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by limeng on 2017/8/1.
 */
public class SmsProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsProxy.class);
    private static final String SMS_SERVER_ADDRESS = SdkPropUtil
            .getProperty("sms.server.address", "https://sms.zyyun.com/zy/sms/");
    private static final String PHONE_MESSAGE_DEBUG = SdkPropUtil
            .getProperty("phone.message.debug", "false");
    static final String DEBUG_SEND_EMAIL = SdkPropUtil
            .getProperty("debug.send.email", "");

    public static void main(String[] args) {
        sendMsg("13717737261", SmsTemplate.BIND_CELL_AUTH_CODE, new String[]{"8080fadf"});
    }

    public static void sendMsg(String phone, String template, String[] msg, Integer vendor) {
        if (Objects.equals("true", PHONE_MESSAGE_DEBUG)) {
            sendDebugMail(phone, template, msg);
            return;
        }
        JSONObject params = new JSONObject();
        params.put("telnum", phone);
        params.put("msgs", msg);
        params.put("msgFlag", template);
        params.put("vendor", vendor);
        AuthSdkHttpRequest post = new AuthSdkHttpRequest(SMS_SERVER_ADDRESS + "sendMsg");
        StringEntity entity = new StringEntity(params.toString(), ContentType.APPLICATION_JSON);
        String result;
        try {
            result = post.post(entity);
            verityResult(result, params.toString());
        } catch (Exception e) {
            LOGGER.error("发送短信抛出异常，短信内容：{}", params.toString());
            LOGGER.error(e.getMessage(),e);
        }
    }

    public static void sendMsg(String phone, String template, String[] msg) {
        if (Objects.equals("true", PHONE_MESSAGE_DEBUG)) {
            sendDebugMail(phone, template, msg);
            return;
        }
        JSONObject params = new JSONObject();
        params.put("telnum", phone);
        params.put("msgs", msg);
        params.put("msgFlag", template);
        AuthSdkHttpRequest post = new AuthSdkHttpRequest(SMS_SERVER_ADDRESS + "sendMsg");
        StringEntity entity = new StringEntity(params.toString(), ContentType.APPLICATION_JSON);
        String result;
        try {
            result = post.post(entity);
            verityResult(result, params.toString());
        } catch (Exception e) {
            LOGGER.error("发送短信抛出异常，短信内容：{}", params.toString());
            LOGGER.error(e.getMessage(),e);
        }
    }

    /**
     * @param phone    手机号
     * @param msg      消息内容
     * @param template 模板名称
     * @throws Exception
     */
    public static void sendMsg(String phone, SmsTemplate template, String[] msg) {
        sendMsg(phone, template.name, msg);
    }

    private static void sendDebugMail(String phone, String template, String[] msg) {
        MailContent mail = new MailContent();
        String content = "给手机：" + phone + "发送的内容：";
        for (String ms : msg) {
            content += ms + ",";
        }
        content += "<br>短信模板标识：" + template;
        mail.setContent(content);
        mail.setTitle("深思云平台云短信通知:" + phone + "-" + template);
        mail.setMailTo(DEBUG_SEND_EMAIL);
        try {
            MailProxy.sendSimpleMail(mail, DEBUG_SEND_EMAIL);
        } catch (Exception e) {
            LOGGER.error("发送短信调试邮件失败，邮件内容", mail.toString());
            LOGGER.error(e.getMessage(),e);
        }
    }

    /**
     * @param result 1-success, 0-failure, 2-error
     * @throws Exception
     */
    private static void verityResult(String result, String content) {
        if (result == null) {
            LOGGER.error("短信发送返回结果为空,短信内容：{}", content);
        } else {
            JSONObject json = JSONObject.parseObject(result);
            int code = json.getIntValue("status");
            if (code != 1) {
                LOGGER.warn("发送短信返回码错误,返回码为:{},短信内容：{}", code, content);
            }
        }
    }
}
