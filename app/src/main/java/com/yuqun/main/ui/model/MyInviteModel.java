package com.yuqun.main.ui.model;

/**
 * Created by Administrator on 2016/3/14 0014.
 */
public class MyInviteModel {
    private String Name;
    private String Tel;
    private String RegDate;
    private String AgentNumCount;
    private String BounsMoney;


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public String getRegDate() {
        return RegDate;
    }

    public void setRegDate(String regDate) {
        RegDate = regDate;
    }

    public String getAgentNumCount() {
        return AgentNumCount;
    }

    public void setAgentNumCount(String agentNumCount) {
        AgentNumCount = agentNumCount;
    }

    public String getBounsMoney() {
        return BounsMoney;
    }

    public void setBounsMoney(String bounsMoney) {
        BounsMoney = bounsMoney;
    }

    @Override
    public String toString() {
        return "MyInviteModel{" +
                "Name='" + Name + '\'' +
                ", Tel='" + Tel + '\'' +
                ", RegDate='" + RegDate + '\'' +
                ", AgentNumCount='" + AgentNumCount + '\'' +
                ", BounsMoney='" + BounsMoney + '\'' +
                '}';
    }
}
