package com.jyj.video.jyjplayer;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.jyj.video.jyjplayer.module.local.LocalFragment;
import com.jyj.video.jyjplayer.module.local.MainPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.main_pager)
    ViewPager mMainPager;

    private List<Fragment> mFragments;
    private LocalFragment mLocalFragment;
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
        mFragments.add(mLocalFragment);
        mPagerAdapter = new MainPagerAdapter(fm, mFragments);
        mMainPager.setAdapter(mPagerAdapter);
        mMainPager.setOffscreenPageLimit(3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
