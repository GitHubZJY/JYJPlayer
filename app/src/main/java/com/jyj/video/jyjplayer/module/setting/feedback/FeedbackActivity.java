package com.jyj.video.jyjplayer.module.setting.feedback;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.ui.StatusLoadingDialog;
import com.jyj.video.jyjplayer.ui.StatusLoadingView;
import com.jyj.video.jyjplayer.utils.MailService;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.utils.HandlerUtils;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.ToastUtils;
import com.zjyang.base.widget.dialog.BaseDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zhengjiayang on 2018/11/26.
 */

public class FeedbackActivity extends BaseActivity {

    private Unbinder unbinder;

    private ActionBar mActionBar;

    @BindView(R.id.et_email)
    EditText mEmailEd;
    @BindView(R.id.et_feedback_content)
    EditText mContentEd;

    StatusLoadingDialog mLoadingDialog;

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
            mActionBar.setTitle(getResources().getString(R.string.feedback));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_send:
                final String email = mEmailEd.getText().toString();
                final String content = mContentEd.getText().toString();
                if(TextUtils.isEmpty(email)){
                    ToastUtils.showToast(this, getResources().getString(R.string.contact_empty_tip));
                    return true;
                }
                if(TextUtils.isEmpty(content)){
                    ToastUtils.showToast(this, getResources().getString(R.string.content_empty_tip));
                    return true;
                }
                mLoadingDialog = StatusLoadingDialog.create(false);
                mLoadingDialog.show(getFragmentManager(), "loading");
                HandlerUtils.postThread(new Runnable() {
                    @Override
                    public void run() {
                        MailService.send(email, content, new MailService.SendMailListener() {
                            @Override
                            public void sendFinish() {
                                HandlerUtils.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLoadingDialog.loadSuccess();
                                    }
                                });
                                HandlerUtils.postDelay(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLoadingDialog.dismiss();
                                    }
                                }, 1000);
                            }

                            @Override
                            public void sendFail() {
                                HandlerUtils.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLoadingDialog.loadFail();

                                    }
                                });
                                HandlerUtils.postDelay(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLoadingDialog.dismiss();
                                    }
                                }, 1000);
                            }
                        });
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
