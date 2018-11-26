package com.jyj.video.jyjplayer.module.setting.feedback;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.utils.MailService;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.utils.HandlerUtils;
import com.zjyang.base.utils.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zhengjiayang on 2018/11/26.
 */

public class FeedbackActivity extends BaseActivity {

    private Unbinder unbinder;

    private ActionBar mActionBar;

    @BindView(R.id.et_feedback_content)
    EditText mContentEd;

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        unbinder = ButterKnife.bind(this);
        initActionBar();
    }

    public void initActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle("反馈");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_send:
                HandlerUtils.postThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            MailService.send_email("", mContentEd.getText().toString());
                        }catch (Exception e){
                            LogUtil.e("zjy", "e: " + e.toString());
                        }
                    }
                });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder != null){
            unbinder.unbind();
        }
    }

}
