package com.jyj.video.jyjplayer.module.setting.language;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.manager.SpManager;
import com.jyj.video.jyjplayer.module.setting.language.model.LanguageBean;
import com.jyj.video.jyjplayer.utils.LanguageUtils;

import java.util.List;

/**
 * Created by zhengjiayang on 2018/12/3.
 */

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageListViewHolder>{

    private List<LanguageBean> mLanguageList;
    private Context mContext;

    public LanguageAdapter(Context mContext, List<LanguageBean> mLanguageList) {
        this.mLanguageList = mLanguageList;
        this.mContext = mContext;
    }

    @Override
    public LanguageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new LanguageListViewHolder(view);
    }


    @Override
    public void onBindViewHolder(LanguageListViewHolder holder, int position) {
        final LanguageBean languageBean = mLanguageList.get(position);
        holder.mLanguageTv.setText(languageBean.getLocale().getDisplayName());
        String curLanguage = SpManager.getInstance().getCurLanguage();
        if(!TextUtils.isEmpty(curLanguage) && curLanguage.equals(languageBean.getLocale().getDisplayName())){
            holder.mLanguageIv.setVisibility(View.VISIBLE);
        }else {
            holder.mLanguageIv.setVisibility(View.GONE);
        }
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpManager.getInstance().setCurLanguage(languageBean.getLocale().getDisplayName());
                LanguageUtils.updateLanguage(languageBean.getLocale());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLanguageList == null ? 0 : mLanguageList.size();
    }

    public class LanguageListViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout mRootView;
        private ImageView mLanguageIv;
        private TextView mLanguageTv;

        public LanguageListViewHolder(View itemView) {
            super(itemView);
            mRootView = (RelativeLayout) itemView.findViewById(R.id.root_view);
            mLanguageIv = (ImageView) itemView.findViewById(R.id.language_select_iv);
            mLanguageTv = (TextView) itemView.findViewById(R.id.language_tv);
        }
    }
}
