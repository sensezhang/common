package com.zy.common.noticeproxy.bean;

import java.util.Map;

/**
 * Created by limeng on 2017/8/1.
 */
public class MailContent {

    private String mailTo;
    private String title;
    private String copyTo;
    private String content;
    private Map<String,byte[]> attachments;

    /**
     * @return the mailTo
     */
    public String getMailTo() {
        return mailTo;
    }

    /**
     * @param mailTo the mailTo to set
     */
    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the copyTo
     */
    public String getCopyTo() {
        return copyTo;
    }

    /**
     * @param copyTo the copyTo to set
     */
    public void setCopyTo(String copyTo) {
        this.copyTo = copyTo;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
    public void setAttachments(Map<String, byte[]> attachments){
        this.attachments = attachments;
    }
    public Map<String,byte[]> getAttachments(){
        return this.attachments;
    }

    @Override
    public String toString() {
        return "MailContent{" +
                "mailTo='" + mailTo + '\'' +
                ", title='" + title + '\'' +
                ", copyTo='" + copyTo + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
