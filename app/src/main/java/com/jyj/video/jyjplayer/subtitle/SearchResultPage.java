package com.jyj.video.jyjplayer.subtitle;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.subtitle.bean.SubTitleFileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengjiayang on 2017/12/7.
 */

public class SearchResultPage extends RelativeLayout implements RequestManager.DownLoadListener{

    private RecyclerView mFileLv;
    private List<SubTitleFileInfo> mSelectFileList;
    private List<SubTitleFileInfo> mFileList;

    private RecyclerView.Adapter<ViewHolder> mAdapter;

    private LinearLayout mOperateLlyt;
    private TextView mDownLoadTv;
    private TextView mCancelTv;

    private RelativeLayout mNoFoundTip;
    private TextView mBackTv;

    private PageClickListener mListener;

    public SearchResultPage(Context context) {
        this(context, null);
    }

    public SearchResultPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchResultPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        RequestManager.getInstance().setDownLoadListener(this);
        initView(context);
    }

    public void initView(Context context){
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_online_search_result, null);
        mFileLv = rootView.findViewById(R.id.subtitle_lv);
        mOperateLlyt = rootView.findViewById(R.id.operate_llyt);
        mDownLoadTv = rootView.findViewById(R.id.down_load_tv);
        mCancelTv = rootView.findViewById(R.id.cancel_tv);
        mNoFoundTip = rootView.findViewById(R.id.no_found_tip);
        mBackTv = rootView.findViewById(R.id.back_tv);

        mDownLoadTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSelectFileList == null || mSelectFileList.size() == 0){
                    return;
                }
                String[] mSelectArr = new String[mSelectFileList.size()];
                for(int i=0; i<mSelectFileList.size(); i++){
                    mSelectArr[i] = mSelectFileList.get(i).getSubTitleFileId();
                }
                RequestManager.getInstance().downloadSubtitle(mSelectFileList);
                if(mListener != null){
                    mListener.clickDownload();
                }
            }
        });

        mCancelTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.clickCancel();
                }
            }
        });

        mBackTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.clickCancel();
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mFileLv.setLayoutManager(linearLayoutManager);
        mFileList = new ArrayList<>();
        mSelectFileList = new ArrayList<>();
        mAdapter =  new RecyclerView.Adapter<ViewHolder>(){

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtitle_search, parent, false);
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

    public void setResultData(List<SubTitleFileInfo> resultData){
        if(resultData == null || resultData.size() == 0){
            mNoFoundTip.setVisibility(VISIBLE);
            mOperateLlyt.setVisibility(GONE);
            mFileLv.setVisibility(GONE);
        }else{
            mNoFoundTip.setVisibility(GONE);
            mOperateLlyt.setVisibility(VISIBLE);
            mFileLv.setVisibility(VISIBLE);
        }
        mFileList.clear();
        mFileList.addAll(resultData);
        mAdapter.notifyDataSetChanged();
    }

    public void scrollToTop(){
        if(mFileList.size() > 0){
            mFileLv.smoothScrollToPosition(0);
        }
    }

    @Override
    public void downSuccess(String filePath) {
        if(mListener != null){
            mListener.downFinish(true, filePath);
        }
    }

    @Override
    public void downFail() {
        if(mListener != null){
            mListener.downFinish(false, "");
        }
    }

    /**
     * File viewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        private RelativeLayout mRootRlyt;
        private TextView mSubTitleNameTv;
        private CheckBox mCheckBox;


        public ViewHolder(View itemView) {
            super(itemView);

            mRootRlyt = (RelativeLayout) itemView.findViewById(R.id.down_load_item_rlyt);
            mSubTitleNameTv = (TextView) itemView.findViewById(R.id.subtitle_name_tv);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.is_download_checkbox);

            mRootRlyt.setOnClickListener(this);
        }

        public void setContent(int index, final SubTitleFileInfo file) {
            mSubTitleNameTv.setText(file.getSubTitleFileName());
            mCheckBox.setChecked(file.isSelect());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            SubTitleFileInfo fileInfo = mFileList.get(position);
            boolean isSelected = fileInfo.isSelect();
            if(!isSelected){
                //假如未被选中
                mSelectFileList.add(fileInfo);
                if(mSelectFileList.size() >= 3){
                    mSelectFileList.get(0).setSelect(false);
                    mSelectFileList.remove(0);
                }
            }else{
                mSelectFileList.remove(fileInfo);
            }
            fileInfo.setSelect(!isSelected);
            mCheckBox.setChecked(!isSelected);
            mAdapter.notifyDataSetChanged();
        }

    }

    public PageClickListener getPageClickListener() {
        return mListener;
    }

    public void setPageClickListener(PageClickListener mListener) {
        this.mListener = mListener;
    }

    public interface PageClickListener{
        void clickCancel();
        void clickDownload();
        void downFinish(boolean isSuccess, String filePath);
    }
}
