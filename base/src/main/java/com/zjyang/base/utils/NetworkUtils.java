//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zjyang.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;

import com.zjyang.base.utils.LogUtil;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

public class NetworkUtils {
    private static final String LOG_TAG = "NetworkUtils";
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_HSPAP = 15;
    public static final int TYPE_MOBILE_MMS = 2;
    public static final int TYPE_MOBILE_SUPL = 3;
    public static final int TYPE_MOBILE_DUN = 4;
    public static final int TYPE_MOBILE_HIPRI = 5;
    public static final int TYPE_WIMAX = 6;
    public static final int TYPE_BLUETOOTH = 7;
    public static final int TYPE_DUMMY = 8;
    public static final int TYPE_ETHERNET = 9;
    public static final int NETWORK_TYPE_UNKOWN = 0;
    public static final int NETWORK_TYPE_WIFI = 1;
    public static final int NETWORK_TYPE_2G = 2;
    public static final int NETWORK_TYPE_3G = 3;
    public static final int NETWORK_TYPE_4G = 4;
    public static final int NETWORK_TYPE_OTHER = 5;

    public NetworkUtils() {
    }

    public static boolean isNetworkOK(Context context) {
        boolean result = false;
        if(context != null) {
            try {
                ConnectivityManager e = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if(e != null) {
                    NetworkInfo networkInfo = e.getActiveNetworkInfo();
                    if(networkInfo != null && networkInfo.isConnected()) {
                        result = true;
                    }
                }
            } catch (NoSuchFieldError var4) {
                var4.printStackTrace();
            }
        }

        return result;
    }

    public static boolean isWifiEnable(Context context) {
        if(context == null) {
            return false;
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager != null?connectivityManager.getActiveNetworkInfo():null;
            return activeNetInfo != null && activeNetInfo.isConnected() && activeNetInfo.getType() == 1;
        }
    }

    public static int getNetworkType(Context context) {
        byte type = 0;
        ConnectivityManager connectMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if(info != null) {
            LogUtil.d(LOG_TAG, "网络类型：" + info.getType());

            switch(info.getType()) {
                case 0:
                    LogUtil.d(LOG_TAG, "手机网制类型：" + info.getSubtype());

                    switch(info.getSubtype()) {
                        case 0:
                            type = 0;
                            return type;
                        case 1:
                        case 2:
                        case 4:
                        case 7:
                            type = 2;
                            return type;
                        case 3:
                        case 5:
                        case 6:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 14:
                        case 15:
                            type = 3;
                            return type;
                        case 13:
                            type = 4;
                            return type;
                        default:
                            type = 0;
                            return type;
                    }
                case 1:
                    type = 1;
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    type = 5;
                    break;
                default:
                    type = 0;
            }
        }

        return type;
    }

    public static String buildNetworkState(Context context) {
        String ret = "unknown";
        int networkType = getNetworkType(context);
        if(networkType == 0) {
            ret = "unknown";
        } else if(networkType == 1) {
            ret = "wifi";
        } else if(networkType == 2) {
            ret = "2g";
        } else if(networkType == 3) {
            ret = "3g";
        } else if(networkType == 4) {
            ret = "4g";
        } else if(networkType == 5) {
            ret = "other";
        }

        return ret;
    }

    public static boolean isVpnConnected() {
        if(VERSION.SDK_INT >= 9) {
            try {
                Enumeration e = NetworkInterface.getNetworkInterfaces();
                if(e != null) {
                    Iterator var1 = Collections.list(e).iterator();

                    NetworkInterface intf;
                    do {
                        do {
                            do {
                                if(!var1.hasNext()) {
                                    return false;
                                }

                                intf = (NetworkInterface)var1.next();
                            } while(!intf.isUp());
                        } while(intf.getInterfaceAddresses().size() == 0);
                    } while(!"tun0".equals(intf.getName()) && !"ppp0".equals(intf.getName()));

                    return true;
                }
            } catch (Throwable var3) {
                var3.printStackTrace();
            }
        }

        return false;
    }
}
