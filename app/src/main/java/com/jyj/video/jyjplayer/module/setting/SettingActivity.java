package com.jyj.video.jyjplayer.module.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.manager.SpManager;
import com.jyj.video.jyjplayer.module.setting.feedback.FeedbackActivity;
import com.jyj.video.jyjplayer.module.setting.widget.DecodeSwitchDialog;
import com.jyj.video.jyjplayer.ui.SwitchCheck;
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
    @BindView(R.id.decode_item)
    RelativeLayout mDecodeItem;
    @BindView(R.id.cur_decode_tv)
    TextView mCurDecodeTv;
    @BindView(R.id.auto_play_item)
    RelativeLayout mAutoPlayItem;
    @BindView(R.id.auto_next_switch)
    SwitchCheck mAutoNextSwitch;
    @BindView(R.id.scan_hide_item)
    RelativeLayout mScanHideItem;
    @BindView(R.id.scan_hide_switch)
    SwitchCheck mScanHideSwitch;
    @BindView(R.id.scan_hide_iv)
    ImageView mScanHideIv;

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
        mScanHideIv.setBackground(getResources().getDrawable(com.zjyang.base.R.drawable.bg_radius_border));
        mCurDecodeTv.setText(SpManager.getInstance().getIsHardwareDecoding() ? R.string.setting_decode_hard : R.string.setting_decode_soft);
        mAutoNextSwitch.setCheck(SpManager.getInstance().getAutoPlayNext());
        mScanHideSwitch.setCheck(SpManager.getInstance().getIsShowHiddenFolder());
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

    @OnClick(R.id.decode_item)
    void clickDecode(){
        DecodeSwitchDialog decodeSwitchDialog = DecodeSwitchDialog.create(true);
        decodeSwitchDialog.show(getFragmentManager(), "decode_dialog");
        decodeSwitchDialog.setDialogCallback(new DecodeSwitchDialog.DialogCallback() {
            @Override
            public void dismiss() {
                mCurDecodeTv.setText(SpManager.getInstance().getIsHardwareDecoding() ? R.string.setting_decode_hard : R.string.setting_decode_soft);
            }
        });
    }

    @OnClick(R.id.auto_play_item)
    void onAutoNextClick() {
        boolean isAutoNext = mAutoNextSwitch.isCheck();
        SpManager.getInstance().setAutoPlayNext(!isAutoNext);
        mAutoNextSwitch.setCheck(!isAutoNext);
    }

    @OnClick(R.id.scan_hide_item)
    void onShowHiddenClick() {
        boolean isShowHiddenFolder = mScanHideSwitch.isCheck();
        SpManager.getInstance().setIsShowHiddenFolder(!isShowHiddenFolder);
        mScanHideSwitch.setCheck(!isShowHiddenFolder);
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
