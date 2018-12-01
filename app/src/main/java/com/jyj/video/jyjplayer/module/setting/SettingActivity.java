package com.jyj.video.jyjplayer.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.module.setting.feedback.FeedbackActivity;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.widget.BaseSettingItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 74215 on 2018/11/11.
 */

public class SettingActivity extends BaseActivity{

    private Unbinder unbinder;

    private ActionBar mActionBar;

    @BindView(R.id.feedback_item)
    BaseSettingItem mFeedbackItem;

    @BindView(R.id.decode_iv)
    ImageView mDecodeIv;
    @BindView(R.id.auto_play_iv)
    ImageView mAutoIv;

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        unbinder = ButterKnife.bind(this);
        initActionBar();
        mDecodeIv.setBackground(getResources().getDrawable(com.zjyang.base.R.drawable.bg_radius_border));
        mAutoIv.setBackground(getResources().getDrawable(com.zjyang.base.R.drawable.bg_radius_border));

    }

    public void initActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle("设置");
        }
    }

    @OnClick(R.id.feedback_item)
    void clickFeedBack(){
        Intent intent = new Intent(SettingActivity.this, FeedbackActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder != null){
            unbinder.unbind();
        }
    }
}
