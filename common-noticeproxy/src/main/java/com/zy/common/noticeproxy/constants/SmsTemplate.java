package com.zy.common.noticeproxy.constants;

/**
 * Created by limeng on 2017/8/1.
 */
public enum SmsTemplate {

    NEW_DEV_AUTH_CODE("newDevAuthCode","开发者注册验证码 ：验证码：xxx，您正在注册深思云开发者账号，注意保密"),
    NEW_KID_ADM_ACTIVE_URL("newKidAdmActiveUrl","添加子管理员激活url ：您已注册成为 xxx 开发者中心的管理员，请激活 : xxx ，激活码 xxx"),
    ADMIN_ADD_KID_ADMIN("adminAddKidAdmin","添加子管理员通知 : 您已注册成为 xxx 开发者中心的管理员"),
    SUPER_ADM_CHG_ACTIVE_URL("superAdmChgActiveUrl","超级管理员变更激活url ：您即将成为 xxx 开发者中心的超级管理员，请激活验证"),
    SUPER_ADMIN_CHANGE_WARN("superAdminChangeWarn","超级管理员变更提示短信 ：您正在发起深思云平台超级管理员变更操作，如非本人发起，请及时联系深思云平台"),
    SUPER_ADMIN_CHANGE_COMP("superAdminChangeComp","超级管理员变更完成通知 ：您发起的变更超级管理员操作已完成，目前的超级管理员为 ：xxx"),
    NEW_USER_ACTIVE_URL("newUserActiveUrl","新用户激活url ：您已注册成为 xxx 的深思云用户以使用其许可系统，请激活 xxx ，激活码 xxx"),
    DEV_ADD_NEW_USER("devAddNewUser","添加为新用户通知 ：您已注册成为 xxx 的深思云用户以使用其许可系统，请用手机号登陆，初始密码为xxx,为了账号安全，请及时修改密码"),
    NEW_USER_AUTH_CODE("newUserAuthCode","用户注册验证码 ：验证码: xxx ，您正在注册深思云用户，注意保密"),
    FIND_PASSWORD_AUTH_CODE("findPasswordAuthCode","找回密码验证码 ：您正在找回密码，注意保密"),
    BIND_CELL_AUTH_CODE("bindCellAuthCode","绑定手机验证码 "),
    NORMAL_AUTH_CODE("normalAuthCode","通用验证码 ");

    public String name,desc;
    SmsTemplate(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
