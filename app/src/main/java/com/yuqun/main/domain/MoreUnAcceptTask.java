package com.yuqun.main.domain;

/**
 * 所有任务
 * Created by zzp on 2016/3/10.
 */
public class MoreUnAcceptTask {

    private String TID;
    private String CID;
    private String Title;
    private String Price;
    private String ExpectedToTake;
    private String TaskPersonLimit;
    private String TaskAcceptNum;
    private String State;
    private String TaskType;
    private String TagsString;
    private String CanFinlishUploadTimeSpan;


    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getExpectedToTake() {
        return ExpectedToTake;
    }

    public void setExpectedToTake(String expectedToTake) {
        ExpectedToTake = expectedToTake;
    }

    public String getTaskPersonLimit() {
        return TaskPersonLimit;
    }

    public void setTaskPersonLimit(String taskPersonLimit) {
        TaskPersonLimit = taskPersonLimit;
    }

    public String getTaskAcceptNum() {
        return TaskAcceptNum;
    }

    public void setTaskAcceptNum(String taskAcceptNum) {
        TaskAcceptNum = taskAcceptNum;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getTaskType() {
        return TaskType;
    }

    public void setTaskType(String taskType) {
        TaskType = taskType;
    }

    public String getTagsString() {
        return TagsString;
    }

    public void setTagsString(String tagsString) {
        TagsString = tagsString;
    }


    public String getCanFinlishUploadTimeSpan() {
        return CanFinlishUploadTimeSpan;
    }

    public void setCanFinlishUploadTimeSpan(String canFinlishUploadTimeSpan) {
        CanFinlishUploadTimeSpan = canFinlishUploadTimeSpan;
    }

    @Override
    public String toString() {
        return "MoreUnAcceptTask{" +
                "TID='" + TID + '\'' +
                ", CID='" + CID + '\'' +
                ", Title='" + Title + '\'' +
                ", Price='" + Price + '\'' +
                ", ExpectedToTake='" + ExpectedToTake + '\'' +
                ", TaskPersonLimit='" + TaskPersonLimit + '\'' +
                ", TaskAcceptNum='" + TaskAcceptNum + '\'' +
                ", State='" + State + '\'' +
                ", TaskType='" + TaskType + '\'' +
                ", TagsString='" + TagsString + '\'' +
                ", CanFinlishUploadTimeSpan='" + CanFinlishUploadTimeSpan + '\'' +
                '}';
    }
}
