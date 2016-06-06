package com.yuqun.main.ui.model;

/**
 * 认证结果
 * Created by Administrator on 2016/4/21.
 */
public class AuthModel {
    private String ID;
    private String UID;
    private String RealName;
    private String CertifyCode;
    private String Photo;
    private String AuthenticationState;
    private String SubmitDate;
    private String AuthDate;
    private String FailedReson;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getCertifyCode() {
        return CertifyCode;
    }

    public void setCertifyCode(String certifyCode) {
        CertifyCode = certifyCode;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getAuthenticationState() {
        return AuthenticationState;
    }

    public void setAuthenticationState(String authenticationState) {
        AuthenticationState = authenticationState;
    }

    public String getSubmitDate() {
        return SubmitDate;
    }

    public void setSubmitDate(String submitDate) {
        SubmitDate = submitDate;
    }

    public String getAuthDate() {
        return AuthDate;
    }

    public void setAuthDate(String authDate) {
        AuthDate = authDate;
    }

    public String getFailedReson() {
        return FailedReson;
    }

    public void setFailedReson(String failedReson) {
        FailedReson = failedReson;
    }

}
