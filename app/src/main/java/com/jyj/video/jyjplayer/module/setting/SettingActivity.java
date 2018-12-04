package com.jyj.video.jyjplayer.module.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.MainActivity;
import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.event.ToggleLanguageEvent;
import com.jyj.video.jyjplayer.manager.SpManager;
import com.jyj.video.jyjplayer.module.setting.feedback.FeedbackActivity;
import com.jyj.video.jyjplayer.module.setting.language.LanguageActivity;
import com.jyj.video.jyjplayer.module.setting.widget.DecodeSwitchDialog;
import com.jyj.video.jyjplayer.ui.SwitchCheck;
import com.jyj.video.jyjplayer.utils.DataCleanManager;
import com.jyj.video.jyjplayer.utils.LanguageUtils;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.ShapeUtils;
import com.zjyang.base.utils.ToastUtils;
import com.zjyang.base.widget.BaseSettingItem;
import com.zjyang.base.widget.dialog.BaseDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    @BindView(R.id.language_item)
    BaseSettingItem mLanguageItem;
    @BindView(R.id.clear_cache_item)
    BaseSettingItem mClearCacheItem;

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        unbinder = ButterKnife.bind(this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
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
            mActionBar.setTitle(getResources().getString(R.string.setting));
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

    @OnClick(R.id.language_item)
    void clickLanguage(){
        Intent languageIntent = new Intent(this, LanguageActivity.class);
        startActivity(languageIntent);
    }

    @OnClick(R.id.clear_cache_item)
    void clickClearCache(){
        showClearCacheDialog(getFragmentManager());
    }

    public void showClearCacheDialog(FragmentManager manager) {
        final String tipText = getResources().getString(R.string.clear_success);
        final BaseDialogFragment mClearCacheDialog = BaseDialogFragment.create(true, 0.8);
        mClearCacheDialog.setDialogCallBack(new BaseDialogFragment.DialogCallBack() {
            @Override
            public Dialog getDialog(Context context) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Base_AlertDialog);
                builder.setCancelable(false);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_clear_cache, null);
                TextView closeTv = view.findViewById(R.id.cancel_tv);
                TextView continueTv = view.findViewById(R.id.agree_tv);
                LinearLayout mBgView = view.findViewById(R.id.dialog_bg);
                TextView detailTv = view.findViewById(R.id.dialog_detail);
                //detailTv.setText(DataCleanManager.getCacheSize());
                mBgView.setBackground(ShapeUtils.getRoundRectDrawable(DrawUtils.dp2px(4), Color.WHITE));
                continueTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataCleanManager.cleanApplicationData(AppApplication.getContext());
                        ToastUtils.showToast(AppApplication.getContext(), tipText);
                        mClearCacheDialog.dismiss();
                    }
                });
                closeTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClearCacheDialog.dismiss();

                    }
                });
                builder.setView(view);
                return builder.create();
            }
        });
        mClearCacheDialog.show(manager, "clearcache");
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

    @Subscribe
    public void onToggleLanguageEvent(ToggleLanguageEvent event){
        recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder != null){
            unbinder.unbind();
        }
        EventBus.getDefault().unregister(this);
    }
}
