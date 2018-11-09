package com.zjyang.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjyang.base.R;

/**
 * Created by 74215 on 2018/7/18.
 * 封装基础设置项ItemView
 */

public class BaseSettingItem extends RelativeLayout{

    private View mBaseView;
    private View mTopLine;
    private View mBottomLine;
    private ImageView mLeftIconIv;
    private TextView mContentTv;

    private boolean mTopLineVisible;
    private boolean mBottomLineVisible;
    private int mLeftIconRes;
    private String mContentText;
    private float mBottomLineMargin;

    public BaseSettingItem(Context context) {
        this(context, null);
    }

    public BaseSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBaseView = inflater.inflate(R.layout.layout_base_item, this);

        mTopLine = mBaseView.findViewById(R.id.top_line);
        mBottomLine = mBaseView.findViewById(R.id.bottom_line);
        mLeftIconIv = mBaseView.findViewById(R.id.setting_left_icon_iv);
        mContentTv = mBaseView.findViewById(R.id.setting_name_tv);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseSettingItem);
        mTopLineVisible = typedArray.getBoolean(R.styleable.BaseSettingItem_top_line_visible, true);
        mBottomLineVisible = typedArray.getBoolean(R.styleable.BaseSettingItem_bottom_line_visible, true);
        mLeftIconRes = typedArray.getResourceId(R.styleable.BaseSettingItem_left_icon_src, -1);
        mContentText = typedArray.getString(R.styleable.BaseSettingItem_content_text);
        mBottomLineMargin = typedArray.getDimension(R.styleable.BaseSettingItem_bottom_line_margin, 0f);

        mTopLine.setVisibility(mTopLineVisible ? VISIBLE : GONE);
        mBottomLine.setVisibility(mBottomLineVisible ? VISIBLE : GONE);
        if(mLeftIconRes != -1){
            mLeftIconIv.setImageResource(mLeftIconRes);
        }
        mContentTv.setText(TextUtils.isEmpty(mContentText) ? "" : mContentText);

        RelativeLayout.LayoutParams bottomLineParams = (RelativeLayout.LayoutParams)mBottomLine.getLayoutParams();
        bottomLineParams.leftMargin = (int)mBottomLineMargin;
    }
}
