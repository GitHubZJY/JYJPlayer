package com.jyj.video.jyjplayer.module.setting.widget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.jyj.video.jyjplayer.manager.SpManager;
import com.jyj.video.jyjplayer.ui.CustomRadioButton;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.ScreenUtils;
import com.zjyang.base.utils.ShapeUtils;

/**
 * Created by zhengjiayang on 2018/12/2.
 */

public class DecodeSwitchDialog extends DialogFragment{


    public static final String TAG = "DecodeSwitchDialog";

    private CustomRadioButton mHardDecode;
    private CustomRadioButton mSoftDecode;


    /**
     *
     * @param cancelable
     * @return
     */
    public static DecodeSwitchDialog create(boolean cancelable){
        DecodeSwitchDialog instance = new DecodeSwitchDialog();
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
            windowParams.width = (int) (ScreenUtils.getsScreenWidth() * 0.8);
            windowParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            windowParams.dimAmount = 0.5f;
            window.setAttributes(windowParams);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_decode_menu, container);
        view.setBackground(ShapeUtils.getRoundRectDrawable(DrawUtils.dp2px(10), Color.WHITE));
        mHardDecode = (CustomRadioButton) view.findViewById(R.id.hard_decode);
        mSoftDecode = (CustomRadioButton) view.findViewById(R.id.soft_decode);

        if (SpManager.getInstance().getIsHardwareDecoding()) {
            mHardDecode.performClick();
        } else {
            mSoftDecode.performClick();
        }

        mHardDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpManager.getInstance().setIsHardwareDecoding(true);
                dismiss();
            }
        });
        mSoftDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpManager.getInstance().setIsHardwareDecoding(false);
                dismiss();
            }
        });


        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(callback != null){
            callback.dismiss();
        }
    }

    DialogCallback callback;

    public void setDialogCallback(DialogCallback callback) {
        this.callback = callback;
    }

    public interface DialogCallback {
        void dismiss();
    }
}
