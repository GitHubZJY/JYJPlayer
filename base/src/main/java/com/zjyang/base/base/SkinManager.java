package com.zjyang.base.base;

import android.graphics.Color;

import com.zjyang.base.bean.ThemeInfo;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 74215 on 2018/9/15.
 * 换肤管理类
 */

public class SkinManager {

    public static final int YELLOW_THEME = 1;
    public static final int BLUE_THEME = 2;
    public static final int RED_THEME = 3;
    public static final int ORANGE_THEME = 4;
    public static final int GREEN_THEME = 5;
    public static final int PINK_THEME = 6;

    public static final String YELLOW = "#ffD600";
    public static final String BLUE = "#00c1de";
    public static final String RED = "#ff6600";
    public static final String ORANGE = "#FE9D01";
    public static final String GREEN = "#66C2AE";
    public static final String PINK = "#ffc0cb";

    public final List<ThemeInfo> themeList = new ArrayList<>();

    private int primaryColor = Color.parseColor("#00c1de");
    private int primaryTextColor = Color.parseColor("#000000");

    private static final String CUR_THEME_SP = "cur_theme_sp";

    private static SkinManager instance;

    public static SkinManager getInstance(){
        if(instance == null){
            instance = new SkinManager();
        }
        return instance;
    }

    public void init(){
        int curTheme = SpUtils.obtain().getInt(CUR_THEME_SP, -1);
        if(curTheme != -1){
            toggleTheme(curTheme);
        }
        themeList.clear();
        themeList.add(new ThemeInfo(YELLOW_THEME, YELLOW, "#000000"));
        themeList.add(new ThemeInfo(BLUE_THEME, BLUE, "#ffffff"));
        themeList.add(new ThemeInfo(RED_THEME, RED, "#ffffff"));
        themeList.add(new ThemeInfo(ORANGE_THEME, ORANGE, "#ffffff"));
        themeList.add(new ThemeInfo(GREEN_THEME, GREEN, "#ffffff"));
        themeList.add(new ThemeInfo(PINK_THEME, PINK, "#ffffff"));
    }

    public int getCurTheme(){
        int curTheme = SpUtils.obtain().getInt(CUR_THEME_SP, 1);
        return curTheme;
    }

    public boolean isDefaultTheme(){
        if(getCurTheme() == BLUE_THEME){
            return true;
        }
        return false;
    }

    public int getPrimaryTextColor() {
        return primaryTextColor;
    }

    public void setPrimaryTextColor(int primaryTextColor) {
        this.primaryTextColor = primaryTextColor;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void toggleTheme(int theme){
        switch (theme){
            case YELLOW_THEME:
                SpUtils.obtain().save(CUR_THEME_SP, YELLOW_THEME);
                setPrimaryColor(Color.parseColor(YELLOW));
                setPrimaryTextColor(Color.parseColor("#000000"));
                break;
            case BLUE_THEME:
                SpUtils.obtain().save(CUR_THEME_SP, BLUE_THEME);
                setPrimaryColor(Color.parseColor(BLUE));
                setPrimaryTextColor(Color.parseColor("#ffffff"));
                break;
            case RED_THEME:
                SpUtils.obtain().save(CUR_THEME_SP, RED_THEME);
                setPrimaryColor(Color.parseColor(RED));
                setPrimaryTextColor(Color.parseColor("#ffffff"));
                break;
            case ORANGE_THEME:
                SpUtils.obtain().save(CUR_THEME_SP, ORANGE_THEME);
                setPrimaryColor(Color.parseColor(ORANGE));
                setPrimaryTextColor(Color.parseColor("#ffffff"));
                break;
            case GREEN_THEME:
                SpUtils.obtain().save(CUR_THEME_SP, GREEN_THEME);
                setPrimaryColor(Color.parseColor(GREEN));
                setPrimaryTextColor(Color.parseColor("#ffffff"));
                break;
            case PINK_THEME:
                SpUtils.obtain().save(CUR_THEME_SP, PINK_THEME);
                setPrimaryColor(Color.parseColor(PINK));
                setPrimaryTextColor(Color.parseColor("#ffffff"));
                break;
        }
    }

    public int getCurThemeIndexInList(){
        for (int i=0; i<themeList.size(); i++){
            LogUtil.d("SkinManager", ""+themeList.get(i).getThemeId() + ", " + getCurTheme());
            if(themeList.get(i).getThemeId() == getCurTheme()){
                return i;
            }
        }
        return 0;
    }


    public List<ThemeInfo> getThemeList() {
        return themeList;
    }
}
