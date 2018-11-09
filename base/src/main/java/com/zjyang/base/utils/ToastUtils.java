package com.zjyang.base.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 74215 on 2018/5/26.
 */

public class ToastUtils {

    public static void showToast(Context context, String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
