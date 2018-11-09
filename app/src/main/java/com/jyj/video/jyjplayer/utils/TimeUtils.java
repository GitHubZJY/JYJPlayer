package com.jyj.video.jyjplayer.utils;

import java.text.SimpleDateFormat;

/**
 * Created by 74215 on 2018/11/4.
 */

public class TimeUtils {

    public static String longToFormatTime(long time){
        if(time == -1){
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。

        return formatter.format(time);
    }
}
