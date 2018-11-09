package com.jyj.video.jyjplayer.module.download;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyj.video.jyjplayer.R;

/**
 * Created by 74215 on 2018/11/3.
 */

public class DownLoadFragment extends Fragment {

    public static DownLoadFragment newInstance() {

        Bundle args = new Bundle();

        DownLoadFragment fragment = new DownLoadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        return view;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
