package com.jyj.video.jyjplayer.download;

/**
 * @author zjyang
 * @date 17-12-16
 */

public interface ErrorCode {


    int REQUEST_FAILURE = 0; // 请求失败
    int NET_ERROR = 1; // 网络错误
    int ALREADY_EXIST = 2; // 已经下载
    int CANCEL = 3; // 取消
    int SAVE_ERROR = 4; // 存储失败
    int URL_NULL = 5;
    int IS_DOWNLOADING = 6;
    int NOT_INS_URL = 7;
    int INVALID_URL = 8;
    int CHECK_FAILED = 9;
}
