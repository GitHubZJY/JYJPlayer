package com.zjyang.base.bean;

/**
 * Created by zhengjiayang on 2018/9/18.
 */

public class ThemeInfo {
    private int themeId;
    private String themeColor;
    private String textColor;

    public ThemeInfo(int themeId, String themeColor, String textColor) {
        this.themeId = themeId;
        this.themeColor = themeColor;
        this.textColor = textColor;
    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}
