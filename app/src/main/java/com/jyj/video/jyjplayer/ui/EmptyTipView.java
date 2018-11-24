package com.jyj.video.jyjplayer.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.zjyang.base.base.SkinManager;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.ShapeUtils;

/**
 * Created by 74215 on 2018/11/24.
 */

public class EmptyTipView extends FrameLayout{

    private TextView mRefreshBtn;

    public EmptyTipView(@NonNull Context context) {
        this(context, null);
    }

    public EmptyTipView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyTipView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_empty_tip, this);
        mRefreshBtn = rootView.findViewById(R.id.refresh_btn);
        mRefreshBtn.setBackground(ShapeUtils.getRoundRectDrawable(DrawUtils.dp2px(8), SkinManager.getInstance().getPrimaryColor()));
        mRefreshBtn.setVisibility(VISIBLE);
        mRefreshBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.clickRefresh();
                }
            }
        });
    }

    private ClickEmptyListener mListener;

    public void setClickEmptyListener(ClickEmptyListener mListener) {
        this.mListener = mListener;
    }

    public interface ClickEmptyListener{
        void clickRefresh();
    }
}
