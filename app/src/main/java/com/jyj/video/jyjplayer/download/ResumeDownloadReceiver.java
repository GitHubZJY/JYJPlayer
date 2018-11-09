package com.jyj.video.jyjplayer.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import com.jyj.video.jyjplayer.constant.SpConstant;
import com.jyj.video.jyjplayer.download.film.FilmDownLoadManager;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

public class ResumeDownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent != null) {
//            Log.d("ResumeDownloadReceiver", "StartServiceReceiver" + intent.getAction());
//        } else {
//            Log.d("ResumeDownloadReceiver", "StartServiceReceiver intent == null");
//        }
        if (intent == null) {
            return;
        }

        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info == null) {
                return;
            }
            //如果当前的网络连接成功并且网络连接可用
            if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI
                        || info.getType() == ConnectivityManager.TYPE_MOBILE
                        || info.getType() == ConnectivityManager.TYPE_VPN) {
                    Log.i("ResumeDownloadReceiver", getConnectionType(info.getType()) + "-连上");
                    boolean hasUnSupportVideo = SpUtils.obtain(SpConstant.SETTING).getBoolean(SpConstant.HAS_UNSUPPORT_VIDEO, false);
                    if(hasUnSupportVideo){
                        //IjkLibDownLoadHelper.getInstance().continueDownLoad();
                    }
                }
                if(info.getType() == ConnectivityManager.TYPE_WIFI){
                    LogUtil.e("ResumeDownloadReceiver", "当前连接上wifi");
                    FilmDownLoadManager.getInstance(context).continueAutoPauseTaskList();
                    FilmDownLoadManager.getInstance(context).autoRetryFailTask(FilmDownLoadManager.WIFI_CONNECT_RETRY);
                }
            } else {
                Log.i("ResumeDownloadReceiver", getConnectionType(info.getType()) + "-断开");

                if (info.getType() == ConnectivityManager.TYPE_WIFI
                        || info.getType() == ConnectivityManager.TYPE_MOBILE
                        || info.getType() == ConnectivityManager.TYPE_VPN) {
                    //IjkLibDownLoadHelper.getInstance().pause();
                    //EventBus.getDefault().post(new NetStateChangeEvent());
                }
                if(info.getType() == ConnectivityManager.TYPE_WIFI){
                    LogUtil.e("ResumeDownloadReceiver", "当前断开wifi");
                    FilmDownLoadManager.getInstance(context).pauseAll();
                }
            }
        }

    }

    private String getConnectionType(int type) {
        String connType = "" + type;
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "MOBILE";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI";
        } else if (type == ConnectivityManager.TYPE_VPN) {
            connType = "VPN";
        }
        return connType;
    }
}

