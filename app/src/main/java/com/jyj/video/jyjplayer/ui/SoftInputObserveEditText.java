package com.jyj.video.jyjplayer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;


import com.jyj.video.jyjplayer.event.SoftInputActionEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhengjiayang on 2017/12/12.
 * 软键盘弹出时能够监听返回键
 */

public class SoftInputObserveEditText extends EditText{

    public SoftInputObserveEditText(Context context) {
        super(context);
    }

    public SoftInputObserveEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.KEYCODE_SOFT_LEFT ){
            EventBus.getDefault().post(new SoftInputActionEvent());
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
