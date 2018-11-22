package com.jyj.video.jyjplayer.subtitle;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Created by zhengjiayang on 2017/12/6.
 */

public class DownLoadFileUtil {

    private static final String ONLINE_SUBTITLE_FOLDER = "/sdcard" +"/jyjplayer/onLine";

    public static boolean mIsCancel;


    public static String decodeBase64(String sourceStr, String fileName){
        String result = "";
        try{
            byte[] bytes = Base64.decode(sourceStr, Base64.DEFAULT);
            result = saveFileFromBytes(ungzip(bytes), fileName);
            //result = new String(ungzip(bytes), "UTF-8");
            //Log.d("zjy", "decode: " + result);
        }catch (Exception e){

        }
        return result;
    }

    public static byte[] ungzip(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }


    public static String saveFileFromBytes(byte[] b, String fileName) {
        BufferedOutputStream stream = null;
        File dir = null;
        File file = null;
        try {
            dir = new  File(ONLINE_SUBTITLE_FOLDER);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //file = new File();
            FileOutputStream fstream = new FileOutputStream(ONLINE_SUBTITLE_FOLDER+"/"+fileName);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return ONLINE_SUBTITLE_FOLDER+"/"+fileName;
    }



    public HashMap<String, String> parseResultStr(String result){
        result = result.substring(1, result.length()-1);
        String[] arr = result.split(", ");
        HashMap<String, String> map = new HashMap<>();
        for(int i=0; i<arr.length; i++){
            String[] groups = arr[i].split("=");
            map.put(groups[0], groups[1]);
        }
        return map;
    }
}
