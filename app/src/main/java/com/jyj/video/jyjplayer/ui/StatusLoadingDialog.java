package com.jyj.video.jyjplayer.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jyj.video.jyjplayer.R;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.ScreenUtils;
import com.zjyang.base.utils.ShapeUtils;
import com.zjyang.base.widget.dialog.BaseDialogFragment;

/**
 * Created by 74215 on 2018/11/30.
 */

public class StatusLoadingDialog extends DialogFragment{

    public static final String TAG = "StatusLoadingDialog";

    private StatusLoadingView mLoadingView;

    /**
     *
     * @param cancelable
     * @param widthScale 弹框宽度与屏幕宽度的比例（0~1）
     * @return
     */
    public static StatusLoadingDialog create(boolean cancelable){
        StatusLoadingDialog instance = new StatusLoadingDialog();
        instance.setCancelable(cancelable);
        LogUtil.d(TAG, "create");
        return instance;
    }


    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart");
        Dialog dialog = getDialog();
        if (dialog != null) {
            //在5.0以下的版本会出现白色背景边框，若在5.0以上设置则会造成文字部分的背景也变成透明
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                //目前只有这两个dialog会出现边框
                if(dialog instanceof ProgressDialog || dialog instanceof DatePickerDialog) {
                    getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            }
            Window window = getDialog().getWindow();
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.width = DrawUtils.dp2px(144);
            windowParams.height = DrawUtils.dp2px(144);
            windowParams.dimAmount = 0.5f;
            window.setAttributes(windowParams);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading_status, container);
        view.setBackground(ShapeUtils.getRoundRectDrawable(DrawUtils.dp2px(10), Color.WHITE));
        mLoadingView = view.findViewById(R.id.loading_view);
        mLoadingView.loadLoading();
        return view;
    }

    public void loadSuccess(){
        mLoadingView.loadSuccess();
    }

    public void loadFail(){
        mLoadingView.loadFailure();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mLoadingView.loadLoading();
    }
}
