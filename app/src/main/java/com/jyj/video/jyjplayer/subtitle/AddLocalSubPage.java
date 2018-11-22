package com.jyj.video.jyjplayer.subtitle;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.SubtitleBase;
import com.jyj.video.jyjplayer.filescan.model.bean.SubtitleInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.manager.VideoPlayDataManager;
import com.jyj.video.jyjplayer.utils.FileUtils;
import com.jyj.video.jyjplayer.utils.SdcardUtil;
import com.jyj.video.jyjplayer.utils.StorageAddressUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengjiayang on 2017/12/6.
 */

public class AddLocalSubPage extends RelativeLayout{

    private String[] sdcardList;
    private String mCurPath = "";
    private TextView mPathTv;
    private LinearLayout mFolderUpLlyt;
    private ImageView mFolderUpIv;
    private TextView mFolderTv;
    private RecyclerView mFileLv;
    private List<File> mFileList;

    private TextView mCancelTv;

    private RecyclerView.Adapter<ViewHolder> mAdapter;

    private PageClickListener mListener;

    private SubtitleBase mSubtitleBase;

    public AddLocalSubPage(Context context) {
        this(context, null);
    }

    public AddLocalSubPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddLocalSubPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context){
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_local_sub_add, null);
        mCancelTv = rootView.findViewById(R.id.cancel_add_tv);
        mPathTv = rootView.findViewById(R.id.cur_path_tv);
        mFolderUpLlyt = rootView.findViewById(R.id.folder_title_llyt);
        mFolderUpIv = rootView.findViewById(R.id.back_folder_iv);
        mFolderTv = rootView.findViewById(R.id.folder_name_tv);
        mFileLv = rootView.findViewById(R.id.file_lv);
        mFolderTv.setText(getResources().getString(R.string.folder_up));

        mCancelTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.cancelAdd();
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

        mFolderUpLlyt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sdcardList.length >= 2){
                    //双卡
                    if(mCurPath.equals(sdcardList[0])){
                        mCurPath = sdcardList[1];
                        updateFolderLv(mCurPath);
                        return;
                    }else if(mCurPath.equals(sdcardList[1])){
                        mCurPath = sdcardList[0];
                        updateFolderLv(mCurPath);
                        return;
                    }
                }
                mCurPath = FileUtils.getFileDirPath(mCurPath);
                updateFolderLv(mCurPath);
            }
        });
        addView(rootView);
    }

    public boolean isRootFolder(String path){
        if(sdcardList.length >= 2){
            //双卡
            if(path.equals(sdcardList[0]) || path.equals(sdcardList[1])){
               return false;
            }
        }else{
            if(FileUtils.getFileDirPath(path).equals(path)){
                return true;
            }
        }
        return false;
    }

    public void initFolderLv(String url){
        SubtitleInfo subtitleInfo = FileVideoModel.getSubtitleInfo(url);
        if(subtitleInfo != null){
            mSubtitleBase = subtitleInfo.getCurSubtitle();
        }

        sdcardList = StorageAddressUtil.getAllSuitableStorage();
        try{
            if(url.startsWith("http")){
                mCurPath = SdcardUtil.SDCARD_DIR;
            }else{
                mCurPath = FileUtils.getFileDirPath(url);
            }
            if(isRootFolder(mCurPath)){
                mFolderUpLlyt.setVisibility(GONE);
            }else{
                mFolderUpLlyt.setVisibility(VISIBLE);
            }
            mPathTv.setText(mCurPath);
            File specItemDir = new File(mCurPath);
            if(!specItemDir.exists()){
                specItemDir.mkdir();
            }
            if(!specItemDir.exists()){
            }else {

                //取出文件列表：
                final File[] files = specItemDir.listFiles();
                for(int i=0; i<files.length; i++){
                    if(files[i].getName().endsWith(".srt") || files[i].isDirectory()){
                        mFileList.add(files[i]);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateFolderLv(String url){
        if(isRootFolder(url)){
            mFolderUpLlyt.setVisibility(GONE);
        }else{
            mFolderUpLlyt.setVisibility(VISIBLE);
        }
        try{
            File specItemDir = new File(url);
            if(!specItemDir.exists()){
                specItemDir.mkdir();
            }
            if(!specItemDir.exists()){

            }else {
                //取出文件列表：
                final File[] files = specItemDir.listFiles();
                if(files == null){
                    mCurPath = FileUtils.getFileDirPath(mCurPath);
                    return;
                }
                mFileList.clear();
                for(int i=0; i<files.length; i++){
                    if(files[i].getName().endsWith(".srt") || files[i].isDirectory()){
                        mFileList.add(files[i]);
                    }
                }
                mAdapter.notifyDataSetChanged();
                mPathTv.setText(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void setIconStatus(TextView iconTv, File file, boolean isSelect){
        if(file.isDirectory()){
            //文件夹
            iconTv.setText("");
            iconTv.setBackgroundResource(R.drawable.ic_folder_small);
        }else{
            iconTv.setText("srt");
            iconTv.setTextColor(Color.parseColor("#08080e"));
            if(isSelect){
                iconTv.setBackgroundResource(R.drawable.bg_small_file_selected);
            }else{
                iconTv.setBackgroundResource(R.drawable.bg_small_file);
            }
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

        public void setContent(int index, final File file) {
            mTitle.setText(file.getName());
            if(mSubtitleBase != null && !TextUtils.isEmpty(mSubtitleBase.getName())){
                if(mSubtitleBase.getPath().equals(file.getPath())){
                    //之前已选的字幕文件
                    setIconStatus(mIcon, file, true);
                    mTitle.setTextColor(Color.parseColor("#35eeff"));
                }else{
                    mTitle.setTextColor(Color.parseColor("#ffffff"));
                }
            }
            setIconStatus(mIcon, file, false);

        }

        @Override
        public void onClick(View v) {
            // 播放
            int position = getAdapterPosition();
            File file = mFileList.get(position);
            String fileName = file.getName();
            mCurPath = mCurPath + "/" + fileName;
            if(fileName.endsWith(".srt")){
                //选中srt字幕文件
                SRTUtils.parseSrt(mCurPath, false);
                return;
            }
            if(!file.isDirectory()){
                //非字幕文件，并且不是文件夹
                return;
            }
            mFolderUpLlyt.setVisibility(VISIBLE);
            updateFolderLv(mCurPath);
        }

    }

    public void setPageClickListener(PageClickListener mListener) {
        this.mListener = mListener;
    }

    public interface PageClickListener{
        void cancelAdd();
    }
}
