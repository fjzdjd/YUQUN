package com.yuqun.main.ui.model;

/**消息公告
 * Created by Administrator on 2016/3/25.
 */
public class NotifyModel {
    private String ED;
    private String UserTel;
    private String Notification;
    private String Type;
    private String CreateTime;
    private String Content;

    public String getED() {
        return ED;
    }

    public void setED(String ED) {
        this.ED = ED;
    }

    public String getUserTel() {
        return UserTel;
    }

    public void setUserTel(String userTel) {
        UserTel = userTel;
    }

    public String getNotification() {
        return Notification;
    }

    public void setNotification(String notification) {
        Notification = notification;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
