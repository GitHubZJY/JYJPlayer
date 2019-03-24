package com.jyj.video.jyjplayer.module.download.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.download.film.DownFilmHelper;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.event.DownFilmDataChangeEvent;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.utils.TypefaceUtil;
import com.zjyang.base.utils.DrawUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 74215 on 2019/3/24.
 */

public class DownItemPopup extends PopupWindow{

    private DownLoadFilmInfo mFilmInfo;

    public DownItemPopup(final Context context) {
        super(context);
        setWidth(DrawUtils.dp2px(80));
        setHeight(DrawUtils.dp2px(80));
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_download_popup, null, false);
        TextView mRenameTv = rootView.findViewById(R.id.rename_tv);
        TextView mDeleteTv = rootView.findViewById(R.id.delete_tv);
        mRenameTv.setTypeface(TypefaceUtil.getDefaultTypeface(context));
        mDeleteTv.setTypeface(TypefaceUtil.getDefaultTypeface(context));
        mRenameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RenameDialog dialog = new RenameDialog(context);
                dialog.showDownloadDialog(mFilmInfo.getFileName(), new RenameDialog.ClickCallback() {
                    @Override
                    public void clickConfirm(String str) {
                        mFilmInfo.setFileName(str);
                        DownFilmHelper.getInstance().addOrReplace(mFilmInfo);
                        FileVideoModel.addOrReplaceDownloadFilm(mFilmInfo);
                        EventBus.getDefault().post(new DownFilmDataChangeEvent());
                    }
                });
                dismiss();
            }
        });
        mDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileVideoModel.deleteDownloadFilm(mFilmInfo.getUrl());
                EventBus.getDefault().post(new DownFilmDataChangeEvent());
                dismiss();
            }
        });
        setContentView(rootView);
    }


    public void setFilmInfo(DownLoadFilmInfo mFilmInfo) {
        this.mFilmInfo = mFilmInfo;
    }
}
