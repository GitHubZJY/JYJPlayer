package com.jyj.video.jyjplayer.download;

/**
 * @author denglongyun
 * @date 17-12-14
 */

public interface Constant {

    interface Status {

        int NONE = 0x0000; //无状态
        int START = 0x0001; //准备下载
        int PROGRESS = 0x0002; //下载中
        int PAUSE = 0x0004; //暂停
        int RESUME = 0x0008; //继续下载
        int CANCEL = 0x0012; //取消
        int RESTART = 0x0016; //重新下载
        int FINISH = 0x0020; //下载完成
        int ALL_FINISH = 0x0041; //全部下载完成
        int ERROR = 0x0024; //下载出错
        int WAIT = 0x0028; //等待中
        int DESTROY = 0x0032; //释放资源
        int AUTO_PAUSE = 0x0044; //自动暂停
        int RETRY = 0x0048; //自动重试

        int CHECK_URL_SUCCESS = 0x0036;
        int CHECK_URL_FAILED = 0x0040;

    }

    interface CallbackType {
        int ON_START = 1;
        int ON_PAUSE = 2;
        int ON_CANCEL = 3;
        int ON_PROGRESS = 4;
        int ON_FINISH = 5;
        int ON_WAIT = 6;
        int ON_DESTROY = 7;
        int ON_ALREADY_EXIST = 8;
        int ON_INVALID_URL = 9;
        int ON_NOT_INS_URL = 10;
        int ON_CHECK_SUCCESS = 11;
        int ON_EEROR = 12;
        int ON_ALL_FINISH = 13;
        int ON_RETRY = 14;
    }

}
