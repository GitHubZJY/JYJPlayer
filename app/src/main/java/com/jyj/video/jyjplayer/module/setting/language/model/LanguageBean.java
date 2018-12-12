package com.jyj.video.jyjplayer.module.setting.language.model;

import com.jyj.video.jyjplayer.filescan.model.bean.AbstractBean;

import java.util.Locale;

/**
 * Created by zhengjiayang on 2018/12/3.
 */

public class LanguageBean extends AbstractBean {

    Locale locale;

    public LanguageBean(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
