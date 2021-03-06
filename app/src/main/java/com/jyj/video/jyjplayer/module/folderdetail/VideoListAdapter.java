package com.jyj.video.jyjplayer.module.folderdetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.manager.FolderListPicManager;
import com.jyj.video.jyjplayer.manager.VideoPlayDataManager;
import com.jyj.video.jyjplayer.module.fullscreen.EnlargeWatchActivity;
import com.jyj.video.jyjplayer.utils.TimeUtils;
import com.jyj.video.jyjplayer.utils.TypefaceUtil;
import com.jyj.video.jyjplayer.utils.VideoUtil;
import com.zjyang.base.utils.HandlerUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 74215 on 2018/11/3.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder>{

    private List<VideoInfo> mVideoList;
    private Context mContext;
    private ExecutorService cachedThreadPool = Executors.newFixedThreadPool(3);

    public VideoListAdapter(Context mContext, List<VideoInfo> mFolderList) {
        this.mVideoList = mFolderList;
        this.mContext = mContext;
    }

    @Override
    public VideoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false);
        return new VideoListViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final VideoListViewHolder holder, int position) {
        final VideoInfo videoInfo = mVideoList.get(position);
        holder.mVideoNameTv.setTypeface(TypefaceUtil.getDefaultTypeface(mContext));
        holder.mVideoNameTv.setText((Html.fromHtml(videoInfo.getDisplayName())));
        holder.mVideoTimeTv.setTypeface(TypefaceUtil.getDefaultTypeface(mContext));
        holder.mVideoTimeTv.setText(VideoUtil.switchDurationFormat(videoInfo.getDuration()));
        holder.mVideoIv.setTag(videoInfo.getPath());

        if (cachedThreadPool.isShutdown()) {
            return;
        }
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                final Bitmap icon = FolderListPicManager.loadVideoIcon(videoInfo);//VideoUtil.createVideoThumbnail(videoInfo.getPath());
                HandlerUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        if (holder.mVideoIv.getTag().equals(videoInfo.getPath())) {
                            if (icon != null) {
                                holder.mVideoIv.setImageBitmap(icon);
                            }
                        }
                    }
                });
            }
        });


        //final Bitmap icon = FolderListPicManager.loadVideoIcon(videoInfo);//VideoUtil.createVideoThumbnail(videoInfo.getPath());if (icon != null) {
        //holder.mVideoIv.setImageBitmap(icon);
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoPlayDataManager.getInstance().setCurPlayVideoInfo(videoInfo);
                EnlargeWatchActivity.start(mContext);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoList == null ? 0 : mVideoList.size();
    }

    public class VideoListViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout mRootView;
        private ImageView mVideoIv;
        private TextView mVideoNameTv;
        private TextView mVideoTimeTv;

        public VideoListViewHolder(View itemView) {
            super(itemView);
            mRootView = (RelativeLayout) itemView.findViewById(R.id.root_view);
            mVideoIv = (SimpleDraweeView) itemView.findViewById(R.id.video_iv);
            mVideoNameTv = (TextView) itemView.findViewById(R.id.video_name);
            mVideoTimeTv = (TextView) itemView.findViewById(R.id.video_time_tv);
        }
    }
}
