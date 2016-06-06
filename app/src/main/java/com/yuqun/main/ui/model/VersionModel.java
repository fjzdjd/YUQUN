package com.yuqun.main.ui.model;

/**
 * 版本更新
 *
 * @author
 */
public class VersionModel {
    private String AndroidVersonNum;
    private String androidPath;
    private String AndroidIsNeed;

    public String getAndroidVersonNum() {
        return AndroidVersonNum;
    }

    public void setAndroidVersonNum(String androidVersonNum) {
        AndroidVersonNum = androidVersonNum;
    }

    public String getAndroidPath() {
        return androidPath;
    }

    public void setAndroidPath(String androidPath) {
        this.androidPath = androidPath;
    }

    public String getAndroidIsNeed() {
        return AndroidIsNeed;
    }

    public void setAndroidIsNeed(String androidIsNeed) {
        AndroidIsNeed = androidIsNeed;
    }
}
