package com.yuqun.main.ui.model;

/**
 * 奖金流水
 * Created by Administrator on 2016/3/11.
 */
public class FoundsModel {
    private String ID;
    private String UserID;
    private String TaskID;
    private String AmountMoney;
    private String OccurredDate;
    private String FundsType;
    private String FundsTypeDesc;
    private String State;
    private String StateDesc;
    private String TaskTitle;
    private String OrderCode;
    private String AmountMoneyStr;

    public String getAmountMoneyStr() {
        return AmountMoneyStr;
    }

    public void setAmountMoneyStr(String amountMoneyStr) {
        AmountMoneyStr = amountMoneyStr;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getTaskID() {
        return TaskID;
    }

    public void setTaskID(String taskID) {
        TaskID = taskID;
    }

    public String getAmountMoney() {
        return AmountMoney;
    }

    public void setAmountMoney(String amountMoney) {
        AmountMoney = amountMoney;
    }

    public String getOccurredDate() {
        return OccurredDate;
    }

    public void setOccurredDate(String occurredDate) {
        OccurredDate = occurredDate;
    }

    public String getFundsType() {
        return FundsType;
    }

    public void setFundsType(String fundsType) {
        FundsType = fundsType;
    }

    public String getFundsTypeDesc() {
        return FundsTypeDesc;
    }

    public void setFundsTypeDesc(String fundsTypeDesc) {
        FundsTypeDesc = fundsTypeDesc;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getStateDesc() {
        return StateDesc;
    }

    public void setStateDesc(String stateDesc) {
        StateDesc = stateDesc;
    }

    public String getTaskTitle() {
        return TaskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        TaskTitle = taskTitle;
    }

    public String getOrderCode() {
        return OrderCode;
    }

    public void setOrderCode(String orderCode) {
        OrderCode = orderCode;
    }

    @Override
    public String toString() {
        return "FoundsModel{" +
                "ID='" + ID + '\'' +
                ", UserID='" + UserID + '\'' +
                ", TaskID='" + TaskID + '\'' +
                ", AmountMoney='" + AmountMoney + '\'' +
                ", OccurredDate='" + OccurredDate + '\'' +
                ", FundsType='" + FundsType + '\'' +
                ", FundsTypeDesc='" + FundsTypeDesc + '\'' +
                ", State='" + State + '\'' +
                ", StateDesc='" + StateDesc + '\'' +
                ", TaskTitle='" + TaskTitle + '\'' +
                ", OrderCode='" + OrderCode + '\'' +
                '}';
    }
}
