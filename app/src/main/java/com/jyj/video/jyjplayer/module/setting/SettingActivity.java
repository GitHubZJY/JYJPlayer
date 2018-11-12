package com.jyj.video.jyjplayer.module.setting;

import android.os.Bundle;

import com.jyj.video.jyjplayer.R;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;

/**
 * Created by 74215 on 2018/11/11.
 */

public class SettingActivity extends BaseActivity{

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }
}
