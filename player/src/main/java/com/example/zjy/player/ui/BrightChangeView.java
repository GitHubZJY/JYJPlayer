package com.example.zjy.player.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.zjy.player.R;
import com.example.zjy.player.utils.ScreenUtils;

import static android.view.Gravity.CENTER;

/**
 * Created by zhengjiayang on 2017/11/6.
 * 亮度进度展示
 */

public class BrightChangeView extends LinearLayout{

//    private static final int VOLUME_LEVEL_1 = 1;
//    private static final int VOLUME_LEVEL_2 = 2;
//    private static final int VOLUME_LEVEL_3 = 3;
//    private static final int VOLUME_LEVEL_OFF = 0;
    private int DEFAULT_WIDTH;
    private int DEFAULT_HEIGHT;
    private ProgressBar mProgressBar;
    private ImageView mIconIv;

    public BrightChangeView(Context context) {
        this(context, null);
    }

    public BrightChangeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrightChangeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        setOrientation(VERTICAL);
        setGravity(CENTER);
        DEFAULT_WIDTH = ScreenUtils.dip2px(context, 150);
        DEFAULT_HEIGHT = ScreenUtils.dip2px(context, 150);
        setBackgroundResource(R.drawable.bg_volumn_controll);
        setPadding(ScreenUtils.dip2px(context, 18),ScreenUtils.dip2px(context, 18),ScreenUtils.dip2px(context, 18),ScreenUtils.dip2px(context, 18));

        mIconIv = new ImageView(context);
        mIconIv.setBackgroundResource(R.drawable.brightness);
        addView(mIconIv);
        mIconIv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setBackgroundColor(Color.parseColor("#33ffffff"));
        ClipDrawable progressColor = new ClipDrawable(new ColorDrawable(Color.WHITE), Gravity.LEFT,ClipDrawable.HORIZONTAL);
        mProgressBar.setProgressDrawable(progressColor);
        //mProgressBar.setProgress(50);
        mProgressBar.setMax(100);
        addView(mProgressBar);
        LayoutParams progressParams = (LayoutParams)mProgressBar.getLayoutParams();
        progressParams.width = ScreenUtils.dip2px(context, 90);
        progressParams.height = ScreenUtils.dip2px(context, 4);
        progressParams.topMargin = ScreenUtils.dip2px(context, 8);
//        progressParams.addRule(ALIGN_PARENT_BOTTOM);
//        progressParams.addRule(CENTER_HORIZONTAL);
        mProgressBar.setLayoutParams(progressParams);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public void setBrightProgress(int precent){
        mProgressBar.setProgress(precent);
//        if(precent > 0 && precent <= 33){
//            setVolumeLevel(VOLUME_LEVEL_1);
//        }else if(precent > 33 && precent <= 66){
//            setVolumeLevel(VOLUME_LEVEL_2);
//        }else if(precent > 66 && precent <= 100){
//            setVolumeLevel(VOLUME_LEVEL_3);
//        }else if(precent == 0){
//            setVolumeLevel(VOLUME_LEVEL_OFF);
//        }
    }

//    public void setVolumeLevel(int type){
//        switch (type){
//            case VOLUME_LEVEL_1:
//                mIconIv.setBackgroundResource(R.drawable.volume_1);
//                break;
//            case VOLUME_LEVEL_2:
//                mIconIv.setBackgroundResource(R.drawable.volume_2);
//                break;
//            case VOLUME_LEVEL_3:
//                mIconIv.setBackgroundResource(R.drawable.volume_3);
//                break;
//            case VOLUME_LEVEL_OFF:
//                mIconIv.setBackgroundResource(R.drawable.volume_off);
//                break;
//        }
//    }
}
