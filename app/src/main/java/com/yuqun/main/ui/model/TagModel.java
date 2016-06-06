package com.yuqun.main.ui.model;

/**
 * 标签model
 * Created by Administrator on 2016/3/14.
 */
public class TagModel {
    private String ID;
    private String Name;
    private String TagType;
    private String DelFlag;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTagType() {
        return TagType;
    }

    public void setTagType(String tagType) {
        TagType = tagType;
    }

    public String getDelFlag() {
        return DelFlag;
    }

    public void setDelFlag(String delFlag) {
        DelFlag = delFlag;
    }

    @Override
    public String toString() {
        return "TagModel{" +
                "ID='" + ID + '\'' +
                ", Name='" + Name + '\'' +
                ", TagType='" + TagType + '\'' +
                ", DelFlag='" + DelFlag + '\'' +
                '}';
    }
}
