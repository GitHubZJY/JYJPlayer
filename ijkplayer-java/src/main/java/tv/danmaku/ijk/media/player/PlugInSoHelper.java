package tv.danmaku.ijk.media.player;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Administrator on 2017/11/2.
 */

public class PlugInSoHelper implements Runnable {

    public static final String TAG = "PlugInSoHelper";

    private Context sContext;

    private static String sNativeLibraryPath;

    private static final String BASE_SO_PATH = "lib/armeabi/";

    private static final String[] SO_ARRAYS = {
            "libijkffmpeg.so",
            "libijkplayer.so",
            "libijksdl.so",
            "libscan.so"
    };

    private static boolean sIsRunning;

    private SharedPreferences sSp;
    private static final String LAST_COPY_SO_VC = "LAST_COPY_SO_VC";

    private static final String DEFAULT_SP_FILE = "default_sharePreferences"; // 默认sp 与SpConstant.DEFAULT_SP_FILE一致

    public PlugInSoHelper(Context context) {
        setup(context);
    }

    public void setup(Context context) {
        sContext = context;
        sNativeLibraryPath = context.getDir("native", Context.MODE_PRIVATE).getPath();
        sSp = sContext.getSharedPreferences(DEFAULT_SP_FILE, Context.MODE_PRIVATE);
    }

    @Override
    public void run() {
        if (sIsRunning) {
            return;
        }
        sIsRunning = true;

        long lastTime = sSp.getLong(LAST_COPY_SO_VC, 0L);
        long curTime = -1;
        PackageManager packageManager = sContext.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(sContext.getPackageName(), 0);
            curTime = packageInfo.lastUpdateTime;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        ApplicationInfo applicationInfo = sContext.getApplicationInfo();
//        File nativeLibraryFile = new File(applicationInfo.nativeLibraryDir);
//        File[] dstSoPaths = nativeLibraryFile.listFiles();
        for (String soName : SO_ARRAYS) {
//            boolean isFind = false;
//            for (File dstSoPath : dstSoPaths) {
//                Log.d("LogUtils", "for " + dstSoPath.getPath() + ", " + dstSoPath.getName());
//                if (soName.equals(dstSoPath.getName())) {
//                    isFind = true;
//                    break;
//                }
//            }
//            if (!isFind) {
                Log.d(TAG, "copySo 开始 " + soName);
                copySo(soName, applicationInfo.sourceDir, lastTime == curTime);
//            }
        }
        if (lastTime != curTime) {
            sSp.edit().putLong(LAST_COPY_SO_VC, curTime).apply();
        }
        sIsRunning = false;
        //PlugInSoHelper.loadLibrary("ijkplayer");
        //PlugInSoHelper.loadLibrary("ijksdl");
        //PlugInSoHelper.loadLibrary("ijkffmpeg");
        PlugInSoHelper.loadLibrary("ijkffmpeg");
        PlugInSoHelper.loadLibrary("ijksdl");
        PlugInSoHelper.loadLibrary("ijkplayer");
        PlugInSoHelper.loadLibrary("scan");
    }

    private void copySo(String soName, String apkPath, boolean notNewTime) {
        try {
            File srcFile = new File(sNativeLibraryPath, soName);
            Log.d(TAG, "for so !!!!" + notNewTime + "  " + soName + "  " + srcFile.getAbsolutePath());
            if (srcFile.exists() && notNewTime) {
                Log.d(TAG, "for so 已经存在!!!!");
                return;
            }

            File dstFile = new File(sNativeLibraryPath, soName + ".tmp");
            ZipFile zipFile = new ZipFile(apkPath);
            ZipEntry zipEntry = zipFile.getEntry(BASE_SO_PATH + soName);
            if (zipEntry == null /*|| zipEntry.getName() == null*/) {
                Log.d(TAG, "copySo zipEntry为空!!!! soName=" + soName + "  apkPath=" + apkPath);
                Log.d(TAG, "copySo zipEntry为空!!!! dstFile=" + dstFile);
                return;
            }

            InputStream inputStream = zipFile.getInputStream(zipEntry);

            FileOutputStream outputStream = new FileOutputStream(dstFile);
            int len;
            byte[] bs = new byte[10240];
            while ((len = inputStream.read(bs, 0, bs.length)) > 0) {
                outputStream.write(bs, 0, len);
                outputStream.flush();
            }
            inputStream.close();
            outputStream.close();
            dstFile.renameTo(srcFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadLibrary(String libname) {
        if (!TextUtils.isEmpty(sNativeLibraryPath)) {
            File file = new File(sNativeLibraryPath, "lib" + libname + ".so");
            if (file.exists()) {
                try{
                    System.load(file.getPath());
                }catch (UnsatisfiedLinkError e){
                    Log.e(TAG, "load " + libname + " error！！！");
                }
                Log.d(TAG, "load " + file.getPath());
                return;
            }
        }
        System.loadLibrary(libname);
    }

}
