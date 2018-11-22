package com.jyj.video.jyjplayer.subtitle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;


/**
 * Created by zhengjiayang on 2017/12/7.
 */

public class DownLoadingPage extends RelativeLayout{

    private TextView mCancelTv;
    private TextView mDownLoadingTv;

    private PageClickListener mListener;

    public DownLoadingPage(Context context) {
        this(context, null);
    }

    public DownLoadingPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownLoadingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context){
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_downloading, null);

        mCancelTv = (TextView)rootView.findViewById(R.id.cancel_download);
        mDownLoadingTv = (TextView) rootView.findViewById(R.id.downloading_tv);
        mDownLoadingTv.setText(getResources().getString(R.string.downloading));

        mCancelTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DownLoadFileUtil.mIsCancel = true;
                if(mListener != null){
                    mListener.cancelDownLoad();
                }
            }
        });

        addView(rootView);
    }


    public void setPageClickListener(PageClickListener mListener) {
        this.mListener = mListener;
    }

    public interface PageClickListener{
        void cancelDownLoad();
    }
}
