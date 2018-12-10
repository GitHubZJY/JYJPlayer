package com.jyj.video.jyjplayer.subtitle;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import com.example.zjy.player.ui.VideoFrame;
import com.example.zjy.player.ui.YPlayerView;
import com.jyj.video.jyjplayer.event.PlaySettingCloseEvent;
import com.jyj.video.jyjplayer.event.SubtitleAsyncEvent;
import com.jyj.video.jyjplayer.subtitle.bean.SRT;
import com.jyj.video.jyjplayer.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by zhengjiayang on 2017/10/25.
 * SRT字幕文件解析工具类
 */

public class SRTUtils {

    static TreeMap<Integer, SRT> srt_map;

    /**
     * 解析SRT字幕文件
     *
     * @param srtPath
     * 字幕路径
     */
    public static void parseSrt(String srtPath, boolean isAuto) {
        FileInputStream inputStream = null;
        try {
            Log.d("zimu", "start parse");
            inputStream = new FileInputStream(srtPath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("zimu", "not found");
            return;// 有异常，就没必要继续下去了
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(
                inputStream));
        String line = null;
        TreeMap<Integer, SRT> srt_map_new = new TreeMap<Integer, SRT>();
        StringBuffer sb = new StringBuffer();
        int key = 0;
        try {
            while ((line = br.readLine()) != null) {
                Log.d("zimu", "parse ing");
                if (!line.equals("")) {
                    sb.append(line).append("@");
                    continue;
                }

                String[] parseStrs = sb.toString().split("@");
                // 该if为了适应一开始就有空行以及其他不符格式的空行情况
                if (parseStrs.length < 3) {
                    sb.delete(0, sb.length());// 清空，否则影响下一个字幕元素的解析
                    continue;
                }

                SRT srt = new SRT();
                // 解析开始和结束时间
                String timeTotime = parseStrs[1];
                int begin_hour = Integer.parseInt(timeTotime.substring(0, 2));
                int begin_mintue = Integer.parseInt(timeTotime.substring(3, 5));
                int begin_scend = Integer.parseInt(timeTotime.substring(6, 8));
                int begin_milli = Integer.parseInt(timeTotime.substring(9, 12));
                int beginTime = (begin_hour * 3600 + begin_mintue * 60 + begin_scend)
                        * 1000 + begin_milli;
                int end_hour = Integer.parseInt(timeTotime.substring(17, 19));
                int end_mintue = Integer.parseInt(timeTotime.substring(20, 22));
                int end_scend = Integer.parseInt(timeTotime.substring(23, 25));
                int end_milli = Integer.parseInt(timeTotime.substring(26, 29));
                int endTime = (end_hour * 3600 + end_mintue * 60 + end_scend)
                        * 1000 + end_milli;

                // System.out.println("开始:" + begin_hour + ":" + begin_mintue +
                // ":"
                // + begin_scend + ":" + begin_milli + "=" + beginTime
                // + "ms");
                // System.out.println("结束:" + end_hour + ":" + end_mintue + ":"
                // + end_scend + ":" + end_milli + "=" + endTime + "ms");
                // 解析字幕文字
                String srtBody = "";
                // 可能1句字幕，也可能2句及以上。
                for (int i = 2; i < parseStrs.length; i++) {
                    srtBody += parseStrs[i] + "\n";
                }
                // 删除最后一个"\n"
                srtBody = srtBody.substring(0, srtBody.length() - 1);
                Log.d("zimu", srtBody);
                // 设置SRT
                srt.setBeginTime(beginTime);
                srt.setEndTime(endTime);
                srt.setSrtBody(new String(srtBody.getBytes(), "UTF-8"));
                // 插入队列
                srt_map_new.put(key, srt);
                key++;
                sb.delete(0, sb.length());// 清空，否则影响下一个字幕元素的解析
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.d("zimu", "execption");
            e.printStackTrace();
        }
        Log.d("zimu", "parse end");

        if(srt_map_new == null || srt_map_new.size() == 0){
            PlaySettingCloseEvent event = new PlaySettingCloseEvent();
            event.setPanelType(PlaySettingCloseEvent.SUBTITLE_PANEL);
            EventBus.getDefault().post(event);
            EventBus.getDefault().post(new SubtitleAsyncEvent(false, isAuto));
        }else{
            srt_map = srt_map_new;
            PlaySettingCloseEvent event = new PlaySettingCloseEvent(srtPath);
            event.setPanelType(PlaySettingCloseEvent.SUBTITLE_PANEL);
            EventBus.getDefault().post(event);
            EventBus.getDefault().post(new SubtitleAsyncEvent(true, isAuto));
        }
        //关闭菜单

    }

    public static void showSRT(TextView tvSrt, VideoFrame vv) {
        boolean isFind = false;
        if(srt_map == null){
            return;
        }
        int currentPosition = vv.getCurrentPosition();
        Iterator<Integer> keys = srt_map.keySet().iterator();
        //通过while循环遍历比较
        while (keys.hasNext()) {
            Integer key = keys.next();
            SRT srtbean = srt_map.get(key);
            if (currentPosition > srtbean.getBeginTime()
                    && currentPosition < srtbean.getEndTime()) {
                //Html.fromHtml可以解析出字幕内容的格式
                Spanned srtBodyHtml = Html.fromHtml(srtbean
                        .getSrtBody());
                tvSrt.setText(srtBodyHtml);
                isFind = true;
                break;//找到后就没必要继续遍历下去，节约资源
            }
        }
        if(!isFind){
            tvSrt.setText("");
        }
    }

    /**
     * 从该目录中寻找与视频名相同的字幕文件的路径
     * @param folderUrl
     * @param videoName
     * @return
     */
    public static String getRandomSrtInFolder(String folderUrl, String videoName){
        String result = "";
        File specItemDir = new File(folderUrl);
        if(!specItemDir.exists()){
            specItemDir.mkdir();
        }
        if(!specItemDir.exists()){

        }else {
            //取出文件列表：
            final File[] files = specItemDir.listFiles();
            if(files == null){
                return "";
            }
            for(int i=0; i<files.length; i++){
                if(files[i].getName().equals(videoName)){
                    //视频文件本身
                }else{
                    String fileName = FileUtils.getReallyFileName(files[i].getName());
                    if(fileName.equals(FileUtils.getReallyFileName(videoName)) && files[i].getName().endsWith("srt")){
                        //名字完全相同，表示是该视频的字幕文件
                        result = files[i].getPath();
                    }
                }
            }
        }
        return result;
    }

    public static void clear(){
        if(srt_map != null){
            srt_map = null;
        }
    }

}
