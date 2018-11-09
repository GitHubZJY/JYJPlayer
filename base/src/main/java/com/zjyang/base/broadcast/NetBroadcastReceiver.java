package com.zjyang.base.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.zjyang.base.utils.NetworkUtils;

/**
 * Created by gulian on 18-4-11.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {

    private NetEvent netEvent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //检查网络状态的类型
            int netWorkState = NetworkUtils.getNetworkType(context);
            if (netEvent != null) {
                // 接口回传网络状态的类型
                netEvent.onNetChange(netWorkState);
            }
        }
    }

    public void setNetEvent(NetEvent netEvent) {
        this.netEvent = netEvent;
    }

    public interface NetEvent {
        void onNetChange(int netMobile);
    }
}
