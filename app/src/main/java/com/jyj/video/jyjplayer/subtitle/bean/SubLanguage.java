package com.jyj.video.jyjplayer.subtitle.bean;

/**
 * Created by zhengjiayang on 2017/12/10.
 */

public class SubLanguage {

    private String languageId;
    private String languageName;
    private String isoCode;

    public SubLanguage() {
    }

    public SubLanguage(String languageId, String languageName, String isoCode) {
        this.languageId = languageId;
        this.languageName = languageName;
        this.isoCode = isoCode;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }
}
