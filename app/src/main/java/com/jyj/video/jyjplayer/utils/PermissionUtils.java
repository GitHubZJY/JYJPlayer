package com.jyj.video.jyjplayer.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

/**
 * Created by 74215 on 2018/11/25.
 */

public class PermissionUtils {


    /**
     * 申请相机权限
     */
    public static void requestSDPermission(final Context context, final RequestPermissionCallback callback){
        AndPermission.with(context)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> data) {
                        // 用户同意授予权限
                        if(callback != null){
                            callback.onGranted();
                        }
                    }
                }).onDenied(new Action() {

            @Override
            public void onAction(List<String> permissions) {
                // 用户拒绝授予权限
                checkAndShowDialog(context, permissions);
                if(callback != null){
                    callback.onDenied();
                }
            }

        }).start();

    }

    public static void checkAndShowDialog(Context context, List<String> permissions){
        if(AndPermission.hasAlwaysDeniedPermission(context, permissions)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false)
                    .setTitle("权限申请")
                    .setMessage("我们需要使用SD权限来读取设备中的视频，否则将无法正常扫描出视频文件")
                    .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            com.zjyang.base.utils.PermissionUtils.newInstance().jumpPermissionPage();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }}


    public interface RequestPermissionCallback {
        void onGranted();
        void onDenied();
    }

}
