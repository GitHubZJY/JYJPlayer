package com.jyj.video.jyjplayer.constant;

/**
 * @author denglongyun
 * @date 17-10-20
 */

public interface InfoType {

    int TITLE = 0;
    int DIRECTORY = 1; // 文件夹 默认类型
    int VIDEO = 2;
    int IMAGE = 4;
    int SIDECAR = 5;



    enum InsDataType {
        GraphSidecar, GraphImage, GraphVideo
    }
}
