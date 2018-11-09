package com.jyj.video.jyjplayer.constant;

/**
 * 定义时间相关的常量
 * <p>
 * Created by chensuilun on 16-7-26.
 */
public interface TimeConstant {

    long ONE_SEC = 1000L;
    long FIVE_SEC = ONE_SEC * 5;
    long ONE_MIN = ONE_SEC * 60;
    long ONE_HOUR = ONE_MIN * 60;
    long ONE_DAY = ONE_HOUR * 24;
    long THREE_MIN = ONE_MIN * 3;
    long FIVE_MIN = ONE_MIN * 5;
    long HALF_HOUR = ONE_MIN * 30;
    long TWO_HOUR = ONE_HOUR * 2;


    long BYTE_1_G = 1073741824; // 1024*1024*1024
    long BYTE_1_M = 1048576; // 1024*1024
    long BYTE_1_K = 1024;

}
