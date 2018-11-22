package com.jyj.video.jyjplayer.subtitle;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.SubtitleBase;
import com.jyj.video.jyjplayer.filescan.model.bean.SubtitleInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.manager.VideoPlayDataManager;
import com.jyj.video.jyjplayer.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengjiayang on 2017/12/6.
 */

public class LocalSubListPage extends RelativeLayout{


    private ImageView mToAddSubBtn;
    private RelativeLayout mNoFoundTip;
    private TextView mCurSubPathTv;

    private RecyclerView mFileLv;
    private List<SubtitleBase> mFileList;

    private RecyclerView.Adapter<ViewHolder> mAdapter;


    private PageClickListener mListener;

    private SubtitleBase mSubtitleBase;

    public LocalSubListPage(Context context) {
        this(context, null);
    }

    public LocalSubListPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalSubListPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    public void initView(Context context){
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_local_sub_list, null);
        mFileLv = (RecyclerView) rootView.findViewById(R.id.file_lv);
        mToAddSubBtn = (ImageView) rootView.findViewById(R.id.to_add_subtitle);
        mCurSubPathTv = (TextView) rootView.findViewById(R.id.cur_subtitle_path_tv);
        mNoFoundTip = (RelativeLayout) rootView.findViewById(R.id.no_file_rlyt);
        mToAddSubBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.clickAddSubTitle();
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mFileLv.setLayoutManager(linearLayoutManager);
        mFileList = new ArrayList<>();
        mAdapter =  new RecyclerView.Adapter<ViewHolder>(){

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtitle_folder, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.setContent(position, mFileList.get(position));
            }

            @Override
            public int getItemCount() {
                return mFileList == null ? 0 : mFileList.size();
            }
        };
        mFileLv.setAdapter(mAdapter);

        addView(rootView);
    }

    public void initSubData(String videoPath){
        SubtitleInfo subtitleInfo = FileVideoModel.getSubtitleInfo(videoPath);
        if(subtitleInfo != null){
            List<SubtitleBase> unExistBaseList = new ArrayList<>();
            List<SubtitleBase> subtitleBaseList = subtitleInfo.getAllSubtitles();
            for(SubtitleBase subtitleBase : subtitleBaseList){
                File checkfile = new File(subtitleBase.getPath());
                if(!checkfile.exists()){
                    unExistBaseList.add(subtitleBase);
                }
            }
            subtitleBaseList.removeAll(unExistBaseList);
            mNoFoundTip.setVisibility(GONE);
            mFileList.addAll(subtitleBaseList);
            mSubtitleBase = subtitleInfo.getCurSubtitle();
            mCurSubPathTv.setText(FileUtils.getFileDirPath(mSubtitleBase.getPath()));
            mAdapter.notifyDataSetChanged();
        }else{
            mNoFoundTip.setVisibility(VISIBLE);
        }
    }

    public void setPageClickListener(PageClickListener mListener) {
        this.mListener = mListener;
    }

    public interface PageClickListener{
        void clickAddSubTitle();
    }


    public void setIconStatus(TextView iconTv, boolean isSelect){
        iconTv.setText("srt");
        iconTv.setTextColor(Color.parseColor("#08080e"));
        if(isSelect){
            iconTv.setBackgroundResource(R.drawable.bg_small_file_selected);
        }else{
            iconTv.setBackgroundResource(R.drawable.bg_small_file);
        }
    }


    /**
     * File viewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        RelativeLayout mItemBg;
        TextView mTitle;
        TextView mIcon;
        VideoInfo videoInfo = VideoPlayDataManager.getInstance().getCurPlayVideoInfo();

        public ViewHolder(View itemView) {
            super(itemView);
            mItemBg = (RelativeLayout) itemView.findViewById(R.id.subtitle_folder_file_name_rlyt);
            mTitle = (TextView) itemView.findViewById(R.id.subtitle_folder_file_name_tv);
            mIcon = (TextView) itemView.findViewById(R.id.subtitle_file_left_tv);

            mItemBg.setOnClickListener(this);

        }

        public void setContent(int index, final SubtitleBase file) {
            mTitle.setText(file.getName());

            if(mSubtitleBase != null && !TextUtils.isEmpty(mSubtitleBase.getName())){
                if(mSubtitleBase.getPath().equals(file.getPath())){
                    //之前已选的字幕文件
                    setIconStatus(mIcon, true);
                    mTitle.setTextColor(Color.parseColor("#35eeff"));
                }else{
                    mTitle.setTextColor(Color.parseColor("#ffffff"));
                }
            }
            setIconStatus(mIcon, false);

        }

        @Override
        public void onClick(View v) {
            // 播放
            int position = getAdapterPosition();
            SubtitleBase file = mFileList.get(position);
            //String fileName = file.getName();
            //选中srt字幕文件
            SRTUtils.parseSrt(file.getPath(), false);
        }

    }
}
