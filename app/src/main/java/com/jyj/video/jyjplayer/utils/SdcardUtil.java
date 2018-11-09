package com.jyj.video.jyjplayer.utils;

import android.app.Activity;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.AppApplication;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.ToastUtils;

import java.io.File;

/**
 * @author denglongyun
 * @date 16-4-27
 */
public class SdcardUtil {
    // 内置sd卡路径 例如：/sdcard
    public final static String sdcardPath = SdcardUtil.getDirectoryPath("EXTERNAL_STORAGE", "/sdcard");
    // 内置sd卡路径 例如：/storage/emulated/0
    public final static String sdcardPath2 = SdcardUtil.getDirectoryPath("ANDROID_STORAGE", "/storage");
    // 外置sd卡路径 可能为空
    public final static String extSdcardPath = System.getenv("SECONDARY_STORAGE");
    // 在Enviroment类的源码中获得sd卡路径其实也是通过 System.getnv() 方法来实现的，如隐藏的方法:


    /**
     * sdcard head
     */
    public final static String SDCARD = Environment.getExternalStorageDirectory().getPath();

    public static final String SDCARD_DIR = "/sdcard" /*getStorageAddress()*/;

    public static final String APP_DIR = SDCARD_DIR + "/goplayer";
    public static final String PRIVACY_DIR = "/.privacy.power";

    public static final String CRASH_REPORT_PATH = APP_DIR + "/log/";

    public static final String DOWNLOAD_FILM_SAVE_FOLDER_NAME = "download_film";
    public static final String DOWNLOAD_SAVE_PATH = APP_DIR + "/download/";
    public static final String DOWNLOAD_FILM_SAVE_PATH = APP_DIR + "/"+DOWNLOAD_FILM_SAVE_FOLDER_NAME+"/";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};


    static File getDirectory(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? new File(defaultPath) : new File(path);
    }

    static String getDirectoryPath(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? defaultPath : path;
    }

    public static String getStorageAddress() {
        // 如果默认的sd能写，直接写
        // 是否有权限
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // sd卡未装入

        }
        if (canWrite(SDCARD)) {
            return SDCARD;
        }
        // 如果不能写入
        String address = StorageAddressUtil.getSuitableStorage();
        return address;
    }

    /**
     * <br>功能简述: 判断该目录能否写入
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param path
     * @return
     */
    public static boolean canWrite(String path) {
        File file = new File(path);

        // 判断file能否读取写入，并且不是一个 USB drive
        // 分开写用于debug
        if (file != null) {
            if (file.isDirectory() && file.canRead() && file.canWrite()) {
                if (file.listFiles() != null) {
                    if (file.listFiles().length > 0) {
                        return true;
                    }
                }
            }
        }

        file = null;
        return false;
    }

    /**
     * 指定路径文件是否存在
     *
     * @param filePath
     * @return
     * @author huyong
     */
    public static boolean isFileExist(String filePath) {
        boolean result = false;
        try {
            File file = new File(filePath);
            result = file.exists();
            file = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 测目录是否存在，不存在则创建
     *
     * @param strFolder
     * @return
     */
    public static boolean isFolderExistsWithCreate(String strFolder) {
        boolean result = false;
        try {
            File file = new File(strFolder);

            if (!file.exists()) {
                if (file.mkdir()) {
                    result = true;
                } else {
                    result = false;
                }
            }
            result = true;
            file = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean hasSdcardPermissions() {
        try {
            //检测是否有写的权限
            if (StorageAddressUtil.canWrite2(SdcardUtil.SDCARD_DIR)) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                return true;
            } else {
                LogUtil.e("dly", "SDCard不可读写");
                ToastUtils.showToast(AppApplication.getContext(), AppApplication.getContext().getResources().getString(R.string.can_not_read_sd));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
//            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
