package com.jyj.video.jyjplayer.subtitle.bean;

/**
 * Created by zhengjiayang on 2017/12/7.
 */

public class SubTitleFileInfo {

    private String subTitleFileId;
    private String subTitleFileName;

    private boolean isSelect;

    public SubTitleFileInfo() {
    }

    public SubTitleFileInfo(String subTitleFileId, String subTitleFileName, boolean isSelect) {
        this.subTitleFileId = subTitleFileId;
        this.subTitleFileName = subTitleFileName;
        this.isSelect = isSelect;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getSubTitleFileId() {
        return subTitleFileId;
    }

    public void setSubTitleFileId(String subTitleFileId) {
        this.subTitleFileId = subTitleFileId;
    }

    public String getSubTitleFileName() {
        return subTitleFileName;
    }

    public void setSubTitleFileName(String subTitleFileName) {
        this.subTitleFileName = subTitleFileName;
    }
}
