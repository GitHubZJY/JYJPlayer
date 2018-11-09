package com.jyj.video.jyjplayer.module.local;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.manager.FolderListPicManager;
import com.jyj.video.jyjplayer.module.folderdetail.FolderDetailActivity;
import com.jyj.video.jyjplayer.utils.TypefaceUtil;

import java.util.List;

/**
 * Created by 74215 on 2018/11/3.
 */

public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderListViewHolder>{

    private List<FolderInfo> mFolderList;
    private Context mContext;

    public FolderListAdapter(Context mContext, List<FolderInfo> mFolderList) {
        this.mFolderList = mFolderList;
        this.mContext = mContext;
    }

    @Override
    public FolderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_list, parent, false);
        return new FolderListViewHolder(view);
    }


    @Override
    public void onBindViewHolder(FolderListViewHolder holder, final int position) {
        holder.mFolderNameTv.setTypeface(TypefaceUtil.getDefaultTypeface(mContext));
        holder.mVideoNumTv.setTypeface(TypefaceUtil.getDefaultTypeface(mContext));
        holder.mFolderNameTv.setText(mFolderList.get(position).getName());
        holder.mVideoNumTv.setText(mFolderList.get(position).getVideoCnt() + "个视频");
        List<VideoInfo> videoInfos = mFolderList.get(position).getVideoList();
        for(int i=0; i<videoInfos.size(); i++){
            VideoInfo videoInfo = videoInfos.get(i);
            if(i == 0){
                final Bitmap icon = FolderListPicManager.loadVideoIcon(videoInfo);//VideoUtil.createVideoThumbnail(videoInfo.getPath());if (icon != null) {
                holder.mFolderIv.setImageBitmap(icon);
            }else if(i == 1){
                holder.mSecondIv.setVisibility(View.VISIBLE);
                final Bitmap icon = FolderListPicManager.loadVideoIcon(videoInfo);//VideoUtil.createVideoThumbnail(videoInfo.getPath());if (icon != null) {
                holder.mSecondIv.setImageBitmap(icon);
            }else if(i == 2){
                holder.mThirdIv.setVisibility(View.VISIBLE);
                final Bitmap icon = FolderListPicManager.loadVideoIcon(videoInfo);//VideoUtil.createVideoThumbnail(videoInfo.getPath());if (icon != null) {
                holder.mThirdIv.setImageBitmap(icon);
            }
        }
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FolderDetailActivity.start(mContext, mFolderList.get(position).getPath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFolderList == null ? 0 : mFolderList.size();
    }

    public class FolderListViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout mRootView;
        private ImageView mThirdIv;
        private ImageView mSecondIv;
        private ImageView mFolderIv;
        private TextView mFolderNameTv;
        private TextView mVideoNumTv;

        public FolderListViewHolder(View itemView) {
            super(itemView);
            mRootView = (RelativeLayout) itemView.findViewById(R.id.root_view);
            mFolderIv = (SimpleDraweeView) itemView.findViewById(R.id.folder_iv);
            mSecondIv = (SimpleDraweeView) itemView.findViewById(R.id.second_iv);
            mThirdIv = (SimpleDraweeView) itemView.findViewById(R.id.third_iv);
            mFolderNameTv = (TextView) itemView.findViewById(R.id.folder_name);
            mVideoNumTv = (TextView) itemView.findViewById(R.id.video_num_tv);
        }
    }
}
