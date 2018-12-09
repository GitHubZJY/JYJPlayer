package com.jyj.video.jyjplayer.module.setting.about;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.jyj.video.jyjplayer.R;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;

/**
 * Created by 74215 on 2018/12/9.
 */

public class VersionDescribeActivity extends BaseActivity{


    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_descirbe);

        initActionBar();
    }

    public void initActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(getResources().getString(R.string.version_describe));
        }
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
    public BasePresenter createPresenter() {
        return null;
    }
}
