package com.jyj.video.jyjplayer.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.constant.TimeConstant;
import com.google.gson.reflect.TypeToken;
import com.zjyang.base.utils.LogUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * @author zjyang
 * @date 17-10-23
 */

public class VideoUtil {

    private static final String TAG = "VideoUtil";


    /**
     * 通过文件名判断是否为视频
     * @param fileName 文件全名
     * sdk不支持播放的类型：v8,tts,m3u8,asx,h264,swf
     */
    public static boolean isVideoFile(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        // 排序：数字->字母 ==
        return fileName.equalsIgnoreCase(".3g2")
                || fileName.equalsIgnoreCase(".3gp")
                || fileName.equalsIgnoreCase(".3gp2")
                || fileName.equalsIgnoreCase(".3gpp")
                || fileName.equalsIgnoreCase(".4k")
                || fileName.equalsIgnoreCase(".amv")
                || fileName.equalsIgnoreCase(".asf")
//                || fileName.equalsIgnoreCase(".asx")
                || fileName.equalsIgnoreCase(".avi")
                || fileName.equalsIgnoreCase(".divx")
                || fileName.equalsIgnoreCase(".dv")
                || fileName.equalsIgnoreCase(".dvavi")
                || fileName.equalsIgnoreCase(".f4v")
                || fileName.equalsIgnoreCase(".flv")
                || fileName.equalsIgnoreCase(".gvi")
                || fileName.equalsIgnoreCase(".gxf")
//                || fileName.equalsIgnoreCase(".h264")
                || fileName.equalsIgnoreCase(".iso")
                || fileName.equalsIgnoreCase(".m1v")
                || fileName.equalsIgnoreCase(".m2t")
                || fileName.equalsIgnoreCase(".m2ts")
                || fileName.equalsIgnoreCase(".m2v")
                || fileName.equalsIgnoreCase(".m4v")
//                || fileName.equalsIgnoreCase(".m3u8")
                || fileName.equalsIgnoreCase(".mats")
                || fileName.equalsIgnoreCase(".mkv")
                || fileName.equalsIgnoreCase(".mov")
                || fileName.equalsIgnoreCase(".mp2")
                || fileName.equalsIgnoreCase(".mp2v")
                || fileName.equalsIgnoreCase(".mp4")
                || fileName.equalsIgnoreCase(".mp4v")
                || fileName.equalsIgnoreCase(".mpe")
                || fileName.equalsIgnoreCase(".mpeg")
                || fileName.equalsIgnoreCase(".mpeg1")
                || fileName.equalsIgnoreCase(".mpeg2")
                || fileName.equalsIgnoreCase(".mpeg4")
                || fileName.equalsIgnoreCase(".mpg")
                || fileName.equalsIgnoreCase(".mpv2")
                || fileName.equalsIgnoreCase(".mts")
                || fileName.equalsIgnoreCase(".mtv")
                || fileName.equalsIgnoreCase(".mxf")
                || fileName.equalsIgnoreCase(".mxg")
                || fileName.equalsIgnoreCase(".navi")
                || fileName.equalsIgnoreCase(".ndivx")
                || fileName.equalsIgnoreCase(".nsv")
                || fileName.equalsIgnoreCase(".nuv")
                || fileName.equalsIgnoreCase(".ogm")
                || fileName.equalsIgnoreCase(".ogx")
                || fileName.equalsIgnoreCase(".ps")
                || fileName.equalsIgnoreCase(".ra")
                || fileName.equalsIgnoreCase(".ram")
                || fileName.equalsIgnoreCase(".rec")
                || fileName.equalsIgnoreCase(".rm")
                || fileName.equalsIgnoreCase(".rmvb")
//                || fileName.equalsIgnoreCase(".v8")
                || fileName.equalsIgnoreCase(".vdat")
                || fileName.equalsIgnoreCase(".vob")
                || fileName.equalsIgnoreCase(".vro")
//                || fileName.equalsIgnoreCase(".swf")
                || fileName.equalsIgnoreCase(".tak")
                || fileName.equalsIgnoreCase(".tod")
                || fileName.equalsIgnoreCase(".ts")
//                || fileName.equalsIgnoreCase(".tts")
                || fileName.equalsIgnoreCase(".webm")
                || fileName.equalsIgnoreCase(".wm")
                || fileName.equalsIgnoreCase(".wmv")
                || fileName.equalsIgnoreCase(".wtv")
                || fileName.equalsIgnoreCase(".xesc")
                || fileName.equalsIgnoreCase(".xvid")
                ;
    }


    /**
     * 小数点后保留2位
     */
    public static String getSizeAsStringWithUnit(final long filesize) {
        DecimalFormat format = new DecimalFormat("#.0");
        String size;
        if (filesize > TimeConstant.BYTE_1_G) {
            size = format.format((float) filesize / TimeConstant.BYTE_1_G) + "GB";
        } else if (filesize > TimeConstant.BYTE_1_M) {
            size = format.format((float) filesize / TimeConstant.BYTE_1_M) + "MB";
        } else if (filesize > TimeConstant.BYTE_1_K){
            size = format.format((float) filesize / TimeConstant.BYTE_1_K) + "KB";
        } else {
            size = filesize + "B";
        }
        return size;
    }

    /**
     * 视频时长 long -> "00:00:00"
     */
    public static String switchDurationFormat(long duration) {
        String result = "--:--:--";

        if (duration <= 0) {
            LogUtil.d(TAG, "视频时长 无 ： " + duration);
            return result;
        }
        long temp = duration;
        int h = (int) (temp / TimeConstant.ONE_HOUR);

        temp -= h * TimeConstant.ONE_HOUR;
        int m = (int) (temp / TimeConstant.ONE_MIN);

        temp -= m * TimeConstant.ONE_MIN;
        // 秒数 取顶
        int s = (int) (temp / TimeConstant.ONE_SEC);
//        temp -= s * TimeConstant.ONE_SEC;
//        if (temp > 0) {
//            s ++;
//        }
        if (h == 0 && m == 0 && s == 0) {
            s ++;
        }

        result = (h > 9 ? h : ("0" + h)) + ":" + (m > 9 ? m : ("0" + m)) + ":" + (s > 9 ? s : ("0" + s));
        LogUtil.d(TAG, duration + " -> " + result);
        return result;
    }

    public static long getDuration(String mUri) {
        String duration = "0";
        FFmpegMediaMetadataRetriever mmr = null;
        try {
            mmr = new FFmpegMediaMetadataRetriever();
            if (!TextUtils.isEmpty(mUri)) {

                mmr.setDataSource(mUri);
                duration = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);

                mmr.release();
            }

            LogUtil.d(TAG, "getDuration   :[" + duration + "]" + "   :[" + mUri + "]");
            return Long.valueOf(duration);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "getDuration   :[" + duration + "]" + "   :[" + mUri + "]" + " Exception: " + e);
            return 0L;
        } catch (Throwable ex) {
            ex.printStackTrace();
            LogUtil.e(TAG, "getDuration   :[" + duration + "]" + "   :[" + mUri + "]" + " Exception: " + ex);
            return 0L;
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        FFmpegMediaMetadataRetriever mmr = null;
        try {
            mmr = new FFmpegMediaMetadataRetriever();
            if (!TextUtils.isEmpty(filePath)) {
                mmr.setDataSource(filePath);
                bitmap = mmr.getFrameAtTime(1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            LogUtil.e(TAG, "createVideoThumbnail： " + ex);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            LogUtil.e(TAG, "createVideoThumbnail： " + ex);
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
        return bitmap;
    }

    public static String getVideoName(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        String name = path;
        int i = path.lastIndexOf("/");
        if (i != -1 && path.length() > i + 1) {
            name = path.substring(i + 1);
        }
        return name;
    }

    /**
     * 获取文件类型 如 .mp4
     * @param src
     * @return 无则返回 空
     */
    public static String getFileSuffix(String src) {
        if(TextUtils.isEmpty(src)){
            return "";
        }
        String name = "";
        int i = src.lastIndexOf(".");
        if (i != -1 && src.length() > i + 1) {
            name = src.substring(i);
        }
        return name;
    }

    static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");


    public static Set<Long> getVideoDateSetFromJson(String videoJson) {
        return AppApplication.getGson().fromJson(videoJson, new TypeToken<Set<Long>>() {
        }.getType());
    }

    public static Set<String> getVideoPathSetFromJson(String videoJson) {
        return AppApplication.getGson().fromJson(videoJson, new TypeToken<Set<String>>() {
        }.getType());
    }

    /**
     * 毫秒时间格式转为“yyyyMMdd”的long
     */
    public static long time2date(long time) {
        if (time <= 0) {
            return 0;
        }
        return Long.valueOf(format.format(new Date(time)));
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     */
    public static int dateToWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        if (weekDay == Calendar.SUNDAY) {
            return 7;
        } else {
            return weekDay - 1;
        }
    }

    /**
     * 根据一个日期字符String，返回是星期几的数字代号
     */
    public static int dateStr2WeekDay(String dateStr) {
        int weekDay = -1;

        try {
            Date date = format.parse(dateStr);
            return dateToWeekDay(date);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return weekDay;
    }

    public static int getTagResId(long lastModify) {

        long tag = getLastModifyOffset(lastModify);
        LogUtil.e(TAG, "！！！！！修改时间距离今天：" + tag);

        if (tag <= 0) { // 当天
            return R.string.latest_today;
        } else if (tag < 7) { // 一周内
            return R.string.latest_week;
        } else {
            return R.string.latest_early;
        }
    }

    public static long getLastModifyOffset(long lastModify) {

        if (lastModify < 0) {
            LogUtil.e(TAG, "lastModify 小于0 取当下时间");
            return 0L;
        }

        TimeZone zone = TimeZone.getDefault();
        int time = zone.getRawOffset(); // 时区
        double todayDays = Math.ceil((System.currentTimeMillis() + time) / (double) TimeConstant.ONE_DAY);
        double startDays = Math.ceil((lastModify + time) / (double) TimeConstant.ONE_DAY);
        int days = (lastModify <= 0 ? 0 : (int) (todayDays - startDays));


        return days;
    }

    public static int time2WeekDay(long time) {
        Calendar calendar = Calendar.getInstance();
        int day = -1;

        calendar.setTimeInMillis(time);
        day = calendar.get(Calendar.DAY_OF_WEEK);

        LogUtil.e(TAG, "time2WeekDay " + day);
        if (day == Calendar.SUNDAY) {
            return 7;
        } else {
            return day - 1;
        }
    }


    /**
     * @param path 视频文件路径
     * @return 视频所在文件夹路径
     */
    public static String getParentPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        int j = path.lastIndexOf('/');
        if (j != -1) {
            String parentFolderPath = path.substring(0, j);
            if (!TextUtils.isEmpty(parentFolderPath)) {
                return parentFolderPath;
            }
        }
        return null;
    }

    public static String getRenamedNewPath(String oldPath, String newName) {
        if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newName)) {
            return null;
        }
        String newPath = oldPath;
        int i = oldPath.lastIndexOf("/");
        if (i != -1 && oldPath.length() > i + 1) {
            newPath = oldPath.substring(0, i + 1);
        }
        newPath += newName;
        return newPath;
    }

    public static void shareVideo(@NonNull Context context, @NonNull String path) {

        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("video/mp4");
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
        context.startActivity(Intent.createChooser(localIntent, "Share via"));
    }

    public static void shareVideoViaIns(@NonNull Context context, @NonNull String path) {

        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("video/mp4");
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setPackage("com.instagram.android");
        localIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
        try {
            context.startActivity(localIntent);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void shareImage(@NonNull Context context, @NonNull String path) {

        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("image/*");
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
        context.startActivity(Intent.createChooser(localIntent, "Share via"));
    }

    public static void shareImageViaIns(@NonNull Context context, @NonNull String path) {

        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("image/*");
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setPackage("com.instagram.android");
        localIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
        try {
            context.startActivity(localIntent);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void shareImages(@NonNull Context context, @NonNull ArrayList<Uri> paths) {
        if (paths == null || paths.isEmpty()) {
            return;
        }

        Intent localIntent = new Intent();
        localIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setType("image/*");
        localIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, paths);
        context.startActivity(Intent.createChooser(localIntent, "Share via"));
    }

    public static void shareImagesVideos(@NonNull Context context, @NonNull ArrayList<Uri> paths) {
        if (paths == null || paths.isEmpty()) {
            return;
        }

        Intent localIntent = new Intent();
        localIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setType("video/*;image/*"); //同时选择视频和图片
        localIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, paths);
        context.startActivity(Intent.createChooser(localIntent, "Share via"));
    }
}
