package com.jyj.video.jyjplayer;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jyj.video.jyjplayer.module.download.DownLoadFragment;
import com.jyj.video.jyjplayer.module.download.view.AddDownLoadActivity;
import com.jyj.video.jyjplayer.module.home.widget.HomeBottomBar;
import com.jyj.video.jyjplayer.module.local.view.LocalFragment;
import com.jyj.video.jyjplayer.module.home.adapter.MainPagerAdapter;
import com.jyj.video.jyjplayer.ui.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements HomeBottomBar.TabClickListener{

    private Unbinder unbinder;

    @BindView(R.id.main_pager)
    CustomViewPager mMainPager;
    @BindView(R.id.bottom_bar)
    HomeBottomBar mBottomBar;
    @BindView(R.id.add_down_btn)
    FloatingActionButton mAddDownBtn;

    private List<Fragment> mFragments;
    private LocalFragment mLocalFragment;
    private DownLoadFragment mDownLoadFragment;
    private MainPagerAdapter mPagerAdapter;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        fm = getSupportFragmentManager();
        mFragments = new ArrayList<Fragment>();
        mLocalFragment = LocalFragment.newInstance();
        mDownLoadFragment = DownLoadFragment.newInstance();
        mFragments.add(mLocalFragment);
        mFragments.add(mDownLoadFragment);
        mPagerAdapter = new MainPagerAdapter(fm, mFragments);
        mMainPager.setAdapter(mPagerAdapter);
        mMainPager.setOffscreenPageLimit(3);

        mBottomBar.setTabClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                break;
            case R.id.action_settings:
                break;
        }
        return true;
    }

    @Override
    public boolean clickTab(int index) {
        mMainPager.setCurrentItem(index-1);
        return false;
    }

    @OnClick(R.id.add_down_btn)
    void clickAddBtn(){
        Intent intent = new Intent(MainActivity.this, AddDownLoadActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
