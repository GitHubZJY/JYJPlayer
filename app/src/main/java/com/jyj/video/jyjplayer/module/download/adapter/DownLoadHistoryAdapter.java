package com.jyj.video.jyjplayer.module.download.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.module.download.widget.DownloadDialog;
import com.jyj.video.jyjplayer.utils.SuffixUtils;
import com.zjyang.base.utils.ToastUtils;

import java.util.List;

/**
 * Created by 74215 on 2019/1/20.
 */

public class DownLoadHistoryAdapter extends RecyclerView.Adapter<DownLoadHistoryAdapter.DownHistoryListViewHolder>{

    private Context mContext;
    private List<String> mDownHistoryList;

    public DownLoadHistoryAdapter(Context mContext, List<String> mDownHistoryList) {
        this.mContext = mContext;
        this.mDownHistoryList = mDownHistoryList;
    }

    @Override
    public DownHistoryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_down_history, parent, false);
        return new DownHistoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DownHistoryListViewHolder holder, int position) {
        holder.setContent(position, mDownHistoryList.get(position));
    }

    @Override
    public int getItemCount() {
        if(mDownHistoryList == null ){
            return 0;
        }
        return mDownHistoryList.size();
    }


    public class DownHistoryListViewHolder extends RecyclerView.ViewHolder {


        TextView mUrlTv;
        TextView mDownLoadTv;

        public DownHistoryListViewHolder(View itemView) {
            super(itemView);

            mUrlTv = (TextView) itemView.findViewById(R.id.item_url_tv);
            mDownLoadTv = (TextView) itemView.findViewById(R.id.download_tv);

        }

        public void setContent(final int index, final String url) {
            if (url == null) {
                return;
            }
            mUrlTv.setText(url);
            mDownLoadTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickDownLoad(mUrlTv.getText().toString());
                }
            });
        }
    }

    private void clickDownLoad(String url){
        if (TextUtils.isEmpty(url)){
            ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.down_empty_tips));
            return;
        }
        final DownloadDialog dialog = new DownloadDialog(mContext, R.style.CustomDialog);
        final String newUrl = url.replaceAll("\\\\", "/");
        final String suffix = SuffixUtils.isMatchVideoFile(newUrl);
        if (!Patterns.WEB_URL.matcher(newUrl).matches() || TextUtils.isEmpty(suffix)) {
            ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.unformat_url));
            return;
        }
//        if(!MachineUtils.isNetworkAvailable()){
//            ToastUtil.showShort(getContext(), getResources().getString(R.string.network_error));
//            return;
//        }
//        dialog.showDownloadDialog(DownloadDialog.WAIT_DOWNLOADER, "", "");
        if (newUrl.contains("youtube")) {
            dialog.showDownloadDialog(DownloadDialog.FORBIDDEN_YOUTUBE, "", "");
        }else{
            dialog.showDownloadDialog(DownloadDialog.SAVE_AS, newUrl, "."+suffix);
        }
    }
}
