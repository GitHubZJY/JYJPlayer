package com.jyj.video.jyjplayer.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatRadioButton;

import com.jyj.video.jyjplayer.R;
import com.zjyang.base.utils.ShapeUtils;


/**
 * @author zhengjiayang
 * @date 16-6-29
 */
public class CustomRadioButton extends AppCompatRadioButton {
    public CustomRadioButton(Context context) {
        super(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isChecked()) {
            super.setButtonDrawable(ShapeUtils.drawColor(getResources().getDrawable(R.drawable.single_selected), Color.BLACK));
        } else {
            super.setButtonDrawable(ShapeUtils.drawColor(getResources().getDrawable(R.drawable.single_unselected), Color.BLACK));
        }
        super.onDraw(canvas);
    }

}
