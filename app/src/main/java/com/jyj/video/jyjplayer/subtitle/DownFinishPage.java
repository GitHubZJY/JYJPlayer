package com.jyj.video.jyjplayer.subtitle;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.zjyang.base.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhengjiayang on 2017/12/7.
 */

public class DownFinishPage extends RelativeLayout{

    private TextView mNumberTv;
    private ImageView mDownResultIv;
    private TextView mResultStartTv;
    private TextView mBottomTv;

    private Handler mHandler;
    private Timer timer =new Timer();
    private int mTimeCount=4;
    private boolean mTimerEndFlag = true;

    private String mFilePath;

    private PageClickListener mListener;

    private boolean mIsSuccess;
    private String mSuccessStr;
    private String mFailStr;

    public DownFinishPage(Context context) {
        this(context, null);
    }

    public DownFinishPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownFinishPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(final Context context){
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_down_finish, null);

        mNumberTv = rootView.findViewById(R.id.number_tv);
        mDownResultIv = rootView.findViewById(R.id.down_result_iv);
        mResultStartTv = rootView.findViewById(R.id.down_result_start_tv);
        mBottomTv = rootView.findViewById(R.id.bottom_tv);

        final ForegroundColorSpan successSpan = new ForegroundColorSpan(Color.parseColor("#43cfff"));
        final ForegroundColorSpan failSpan = new ForegroundColorSpan(Color.parseColor("#f53f2a"));

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        if(mTimeCount <= 1) {
                            mTimerEndFlag=false;
                            if(mIsSuccess){
                                LogUtil.d("zjy", "当前自动应用字幕路径："+mFilePath);
                                SRTUtils.parseSrt(mFilePath, false);
                            }else{
                                if(mListener != null){
                                    mListener.clickBack();
                                }
                            }
                        }
                        else {
                            mTimeCount--;
                            SpannableStringBuilder builder;
                            if(mIsSuccess){
                                mResultStartTv.setText(mSuccessStr +" "+mTimeCount+" s");
                                builder = new SpannableStringBuilder(mResultStartTv.getText().toString());
                                builder.setSpan(successSpan, builder.length() - 3, builder.length() - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else{
                                mResultStartTv.setText(mFailStr +" "+mTimeCount+" s");
                                builder = new SpannableStringBuilder(mResultStartTv.getText().toString());
                                builder.setSpan(failSpan, builder.length() - 3, builder.length() - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            mResultStartTv.setText(builder);
                            //mNumberTv.setTextColor(mIsSuccess ? Color.parseColor("#43cfff") : Color.parseColor("#f53f2a"));
                            //mNumberTv.setText(" "+mTimeCount+"");
                        }
                        break;
                }
                super.handleMessage(msg);
            }
        };

        addView(rootView);


    }

    public void setPageStatus(boolean isSuccess){
        mTimerEndFlag = true;
        mTimeCount = 4;
        mIsSuccess = isSuccess;
        if(isSuccess){
            mDownResultIv.setBackgroundResource(R.drawable.illustration_success);
            mResultStartTv.setText(getResources().getString(R.string.download_sub_success));
            mSuccessStr = getResources().getString(R.string.download_sub_success);
            mBottomTv.setText(getResources().getString(R.string.confirm));
            mBottomTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        mTimerEndFlag = false;
                        SRTUtils.parseSrt(mFilePath, false);
                        mListener.clickConfirm();
                    }
                }
            });
        }else{
            mDownResultIv.setBackgroundResource(R.drawable.illustration_fail);
            mResultStartTv.setText(getResources().getString(R.string.download_sub_failed));
            mFailStr = getResources().getString(R.string.download_sub_failed);
            mBottomTv.setText(getResources().getString(R.string.back));
            mBottomTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        mListener.clickBack();
                    }
                }
            });
        }
    }

    public void setDownLoadFile(String filePath){
        mFilePath = filePath;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(mTimerEndFlag){
                    mHandler.sendEmptyMessage(0);
                }
            }
        },0,1000);
    }

    public void setPageClickListener(PageClickListener mListener) {
        this.mListener = mListener;
    }

    public interface PageClickListener{
        void clickBack();
        void clickConfirm();
    }
}
