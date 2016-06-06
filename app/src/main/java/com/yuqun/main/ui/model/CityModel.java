package com.yuqun.main.ui.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/11 0011.
 */
public class CityModel implements Serializable{
    private String ID;
    private String PY;
    private String FirstLetter;
    private String CityName;

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getPY() {
        return PY;
    }

    public void setPY(String PY) {
        this.PY = PY;
    }

    public String getFirstLetter() {
        return FirstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        FirstLetter = firstLetter;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}

