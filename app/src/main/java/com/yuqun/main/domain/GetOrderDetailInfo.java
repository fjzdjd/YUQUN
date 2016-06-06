package com.yuqun.main.domain;

import java.io.Serializable;

/**
 * <P>除可接受外的详细</P>
 * Created by zzp on 2016/3/15.
 */
public class GetOrderDetailInfo implements Serializable{

    private String OID;
    private String TID;
    private String UserID;
    private String OrderState;
    private String SettlementState;
    private String OrderTime;
    private String OrderIP;
    private String Comment;
    private String OrderBack;
    private String OrderBackPic;
    private String OrderCheckDate;
    private String OrderFinlishDate;
    private String CustomerName;


    public String getOID() {
        return OID;
    }

    public void setOID(String OID) {
        this.OID = OID;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getOrderState() {
        return OrderState;
    }

    public void setOrderState(String orderState) {
        OrderState = orderState;
    }

    public String getSettlementState() {
        return SettlementState;
    }

    public void setSettlementState(String settlementState) {
        SettlementState = settlementState;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public String getOrderIP() {
        return OrderIP;
    }

    public void setOrderIP(String orderIP) {
        OrderIP = orderIP;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getOrderBack() {
        return OrderBack;
    }

    public void setOrderBack(String orderBack) {
        OrderBack = orderBack;
    }

    public String getOrderBackPic() {
        return OrderBackPic;
    }

    public void setOrderBackPic(String orderBackPic) {
        OrderBackPic = orderBackPic;
    }

    public String getOrderCheckDate() {
        return OrderCheckDate;
    }

    public void setOrderCheckDate(String orderCheckDate) {
        OrderCheckDate = orderCheckDate;
    }

    public String getOrderFinlishDate() {
        return OrderFinlishDate;
    }

    public void setOrderFinlishDate(String orderFinlishDate) {
        OrderFinlishDate = orderFinlishDate;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    @Override
    public String toString() {
        return "GetOrderDetailInfo{" +
                "OID='" + OID + '\'' +
                ", TID='" + TID + '\'' +
                ", UserID='" + UserID + '\'' +
                ", OrderState='" + OrderState + '\'' +
                ", SettlementState='" + SettlementState + '\'' +
                ", OrderTime='" + OrderTime + '\'' +
                ", OrderIP='" + OrderIP + '\'' +
                ", Comment='" + Comment + '\'' +
                ", OrderBack='" + OrderBack + '\'' +
                ", OrderBackPic='" + OrderBackPic + '\'' +
                ", OrderCheckDate='" + OrderCheckDate + '\'' +
                ", OrderFinlishDate='" + OrderFinlishDate + '\'' +
                ", CustomerName='" + CustomerName + '\'' +
                '}';
    }
}
