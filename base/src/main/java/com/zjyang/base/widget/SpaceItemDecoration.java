package com.zjyang.base.widget;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zjyang.base.utils.DrawUtils;

/**
 * Created by zhengjiayang on 2018/7/17.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int columnNum;
    private int orientation;

    public SpaceItemDecoration(int space, int columnNum, int orientation) {
        this.space = space;
        this.columnNum = columnNum;
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = space;
        outRect.bottom = space;
        outRect.right = space;
        int index = parent.getChildLayoutPosition(view);
        if(index == 0 || index == 1){
            //outRect.top = space;
        }
        if(orientation == LinearLayoutManager.HORIZONTAL){
            if(index == 0){
                outRect.left = DrawUtils.dp2px(16);
            }
            return;
        }
        if (index % columnNum != 0) {
            //outRect.left = 0;
            outRect.left = 0;
        }
    }

}
