package com.zy.common.noticeproxy;

import com.alibaba.fastjson.JSONObject;
import com.zy.common.http.HttpConnectPoolInvoke;
import com.zy.common.http.assist.HttpConnectionManager;
import com.zy.common.noticeproxy.assist.SdkPropUtil;
import com.zy.common.noticeproxy.bean.MailContent;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 调用邮件服务的工具类
 */
public class MailProxy {
  private static final Logger LOGGER = LoggerFactory.getLogger(MailProxy.class);
  private static final String MAIL_SERVER_ADDRESS = SdkPropUtil
      .getProperty("mail.server.address", "mail.zy.com.cn").concat("/sendMail");
  private static final String MAIL_SERVER_FILE_ADRESS = SdkPropUtil
      .getProperty("mail.server.address", "mail.zy.com.cn").concat("/sendMailByFile");

  private static final String DEBUG_SEND_EMAIL = SdkPropUtil.getProperty("debug.send.email", "");

  static void sendSimpleMail(MailContent mail) {
    String title = mail.getTitle();
    if (!"".equals(DEBUG_SEND_EMAIL)) {// 测试开发环境 发送到指定邮箱
      mail.setTitle(title + "-" + mail.getMailTo());
      sendSimpleMail(mail, DEBUG_SEND_EMAIL);
    }
    mail.setTitle(title);
    sendSimpleMail(mail, mail.getMailTo());
  }

  // 测试 开发环境发送短信时需要发送到邮箱 需要调用此方法 故为public 程序中不要使用此方法
  public static void sendSimpleMail(MailContent mail, String email) {
    HashMap<String, String> params = new HashMap<>();
    params.put("email", email);
    if (mail.getCopyTo() != null) {
      params.put("ccemail", mail.getCopyTo());
    }
    params.put("title", mail.getTitle());
    params.put("mailContent", mail.getContent());
    String response = null;
    try {
      response = HttpConnectPoolInvoke.sendHttpRequest(MAIL_SERVER_ADDRESS, params);
    } catch (Exception e) {
      LOGGER.error("send mail error:", e);
    }
    if (StringUtils.isBlank(response)) {
      LOGGER.warn("send mail error : " + mail.getContent() + "->" + mail.getMailTo());
    }
    try {
      JSONObject jsonObject = JSONObject.parseObject(response);
      int code = jsonObject.getInteger("code");
    } catch (Exception e) {
      LOGGER.error("send mail error:", e);
    }

  }

  static void sendAttachMail(MailContent mail) {
    String title = mail.getTitle();
    if (!"".equals(DEBUG_SEND_EMAIL)) {// 测试开发环境 发送到指定邮箱
      mail.setTitle(title + "-" + mail.getMailTo());
      sendAttachMail(mail, DEBUG_SEND_EMAIL);
    }
    mail.setTitle(title);
    sendAttachMail(mail, mail.getMailTo());
  }

  private static void sendAttachMail(MailContent mail, String email) {
    HttpClient client = null;
    HttpPost post = null;
    MultipartEntity reqEntity = null;
    try {
      client = HttpConnectionManager.getHttpClient();
      post = new HttpPost(MAIL_SERVER_FILE_ADRESS);
      reqEntity = new MultipartEntity();
      // 字符串参数
      reqEntity.addPart("email", new StringBody(email, Charset.forName("UTF-8")));
      if (mail.getCopyTo() != null) {
        reqEntity.addPart("ccemail", new StringBody(mail.getCopyTo(), Charset.forName("UTF-8")));
      }
      reqEntity.addPart("title", new StringBody(mail.getTitle(), Charset.forName("UTF-8")));
      reqEntity.addPart("mailContent", new StringBody(mail.getContent(), Charset.forName("UTF-8")));
      // 字节型参数
      Map<String, byte[]> byteParams = mail.getAttachments();
      if (byteParams != null && byteParams.size() > 0) {
        Iterator<String> byteParamIter = byteParams.keySet().iterator();
        while (byteParamIter.hasNext()) {
          String key = byteParamIter.next();
          byte[] bts = byteParams.get(key);
          ByteArrayBody postBody = new ByteArrayBody(bts, key);
          reqEntity.addPart(key, postBody);
        }
      }
      post.setEntity(reqEntity);
      HttpResponse response = client.execute(post);
      int code = response.getStatusLine().getStatusCode();
      HttpEntity entity = response.getEntity();
      if (code >= 200 && code < 400) {
        String body1 = EntityUtils.toString(entity, Charset.forName("UTF-8"));
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("访问邮件服务-->sendCertMail  result:" + body1);
      } else {
        EntityUtils.consume(entity);
        throw new IllegalArgumentException("请检查连接是否正确，http return code=" + code);
      }
    } catch (Exception e) {
      if (post != null)
        post.abort();
      LOGGER.error("注册用户发送邮件失败", e);
    }
  }
}
