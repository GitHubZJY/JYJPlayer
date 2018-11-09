package com.zjyang.base.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.callback.IHeaderCallBack;
import com.zjyang.base.R;

/**
 * Created by 74215 on 2018/4/27.
 */

public class RefreshViewHeader extends LinearLayout implements IHeaderCallBack {
    private TextView mHintTextView;

    public RefreshViewHeader(Context context) {
        super(context);
        setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public RefreshViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_refresh_header, this);
        mHintTextView = (TextView) findViewById(R.id.refresh_header_tv);
    }

    public void setRefreshTime(long lastRefreshTime) {
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onStateNormal() {
        mHintTextView.setText(R.string.xrefreshview_header_hint_normal);
    }

    @Override
    public void onStateReady() {
        mHintTextView.setText(R.string.xrefreshview_header_hint_ready);
    }

    @Override
    public void onStateRefreshing() {
        mHintTextView.setText(R.string.xrefreshview_header_hint_refreshing);
    }

    @Override
    public void onStateFinish(boolean success) {
        mHintTextView.setText(success ? R.string.xrefreshview_header_hint_loaded : R.string.xrefreshview_header_hint_loaded_fail);
    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY, int deltaY) {
        //
    }

    @Override
    public int getHeaderHeight() {
        return getMeasuredHeight();
    }
}
