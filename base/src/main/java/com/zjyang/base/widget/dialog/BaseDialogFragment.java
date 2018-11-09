package com.zjyang.base.widget.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.ScreenUtils;

/**
 * Created by zhengjiayang on 2018/7/16.
 */

public class BaseDialogFragment extends DialogFragment {

    public static final String TAG = "BaseDialogFragment";

    private DialogCallBack mDialogCallBack;
    private OnDialogCancelListener mCancelListener;
    //弹框宽度与屏幕宽度的比例（0~1）
    private double mDialogWidthScale;
    //弹框弹出时背景透明度
    private float mBgAlpha = 0.5f;

    public static BaseDialogFragment create(boolean cancelable){
        //默认弹框宽度占屏幕宽度比例90%
        return create(cancelable, 0.9);
    }

    /**
     *
     * @param cancelable
     * @param widthScale 弹框宽度与屏幕宽度的比例（0~1）
     * @return
     */
    public static BaseDialogFragment create(boolean cancelable, double widthScale){
        BaseDialogFragment instance = new BaseDialogFragment();
        instance.setCancelable(cancelable);
        instance.mDialogWidthScale = widthScale;
        LogUtil.d(TAG, "create");
        return instance;
    }

    public static BaseDialogFragment create(DialogCallBack call, boolean cancelable, OnDialogCancelListener cancelListener){
        BaseDialogFragment instance = new BaseDialogFragment();
        instance.setCancelable(cancelable);
        instance.mCancelListener = cancelListener;
        instance.mDialogCallBack = call;
        LogUtil.d(TAG, "create");
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(mDialogCallBack == null){
            return super.onCreateDialog(savedInstanceState);
        }
        return mDialogCallBack.getDialog(getActivity());
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
            windowParams.width = (int) (ScreenUtils.getsScreenWidth() * mDialogWidthScale);
            windowParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            windowParams.dimAmount = mBgAlpha;
            window.setAttributes(windowParams);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        LogUtil.d(TAG, "onDismiss");
        if (mCancelListener != null) {
            mCancelListener.onCancel();
        }
    }

    public void setDialogCallBack(DialogCallBack dialogCallBack) {
        this.mDialogCallBack = dialogCallBack;
    }

    public void setCancelListener(OnDialogCancelListener cancelListener) {
        this.mCancelListener = cancelListener;
    }

    public interface DialogCallBack {
        Dialog getDialog(Context context);
    }

    public interface OnDialogCancelListener {
        void onCancel();
    }
}
