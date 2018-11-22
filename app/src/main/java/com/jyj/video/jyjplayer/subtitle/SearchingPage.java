package com.jyj.video.jyjplayer.subtitle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;

/**
 * Created by zhengjiayang on 2017/12/6.
 */

public class SearchingPage extends RelativeLayout{

    private TextView mSearchingTv;


    public SearchingPage(Context context) {
        this(context, null);
    }

    public SearchingPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context){
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_searching, null);

        mSearchingTv = (TextView) rootView.findViewById(R.id.searching_tv);
        mSearchingTv.setText(getResources().getString(R.string.searching) + "...");

        addView(rootView);
    }

}
