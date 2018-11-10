package com.jyj.video.jyjplayer.utils;

import com.zjyang.base.utils.LogUtil;

/**
 * Created by 74215 on 2018/11/10.
 */

public class SuffixUtils {

    public static final String TAG = "SuffixUtils";

    private static String[] mVideoSuffix = new String[]{"mp4", "3g2", "mkv", "avi", "3gp", "webm","3gp2","3gpp","4k",
            "amv","asf","divx","dv","dvavi","f4v","flv","gvi","gxf","iso","m1v","m2t","m2ts","m2v","m4v","mats",
            "mov","mp2","mp2v","mp4v","mpe","mpeg","mpeg1","mpeg2","mpeg4","mpg","mpv2","mts","mtv","mxf","mxg",
            "navi","ndivx","nsv","nuv","ogm","ogx","ps","ra","ram","rec","rm","rmvb","vdat","vob","vro","tak","tod",
            "ts","wm","wmv","wtv","xesc","xvid"};


    public static String isMatchVideoFile(String url){
        for(String suffix : mVideoSuffix){
            if((url.toLowerCase()).contains(suffix.toLowerCase())){
                return suffix;
            }
        }
        LogUtil.d(TAG, "该链接并不包含视频格式");
        return "";
    }
}
