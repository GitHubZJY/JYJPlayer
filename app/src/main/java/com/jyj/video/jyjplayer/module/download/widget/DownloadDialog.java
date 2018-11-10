package com.jyj.video.jyjplayer.module.download.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.download.film.FilmDownLoadManager;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.ShapeUtils;
import com.zjyang.base.utils.ToastUtils;

import static com.jyj.video.jyjplayer.download.Constant.Status.FINISH;


/**
 * Created by gulian on 2018/05/15.
 */

public class DownloadDialog extends Dialog {

    public static final int WRONG_URL = 0;
    public static final int FORBIDDEN_YOUTUBE = 1;
    public static final int SAVE_AS = 2;
    public static final int WAIT_DOWNLOADER = 3;

    private Context mContext;
    private ImageView mIcon;
    private TextView mTitle;
    private TextView mContent;
    private RelativeLayout mSaveName;
    private ImageView mClear;
    private EditText mEditName;
    private LinearLayout mBottom;
    private LinearLayout mBtnContanier;
    private Button mCancelBtn;
    private Button mOkBtn;
    private int mCurrent;
    private String mDownloadUrl;
    private String mSuffix;

    public DownloadDialog(Context context) {
        super(context);
        initView(context);
        mContext = context;
    }

    public DownloadDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
        mContext = context;
    }

    public void setContext(Context context) {
        initView(context);
        mContext = context;
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.download_dialog, null);
        initBtn(root);
        setContentView(root);
        setCanceledOnTouchOutside(true);
    }

    private void initBtn(View rootView) {
        mIcon = rootView.findViewById(R.id.iv_icon);
        mTitle = rootView.findViewById(R.id.tv_title);
        mContent = rootView.findViewById(R.id.tv_content);
        mSaveName = rootView.findViewById(R.id.rl_save_name);
        mClear = rootView.findViewById(R.id.iv_clear);
        mEditName = rootView.findViewById(R.id.download_url_edit);
        mBottom = rootView.findViewById(R.id.ll_bottom);
        mBtnContanier = rootView.findViewById(R.id.dialog_button_container);
        mCancelBtn = rootView.findViewById(R.id.cancel);
        mOkBtn = rootView.findViewById(R.id.ok);
        //mEditName.setBackground(ShapeUtils.getRoundRectDrawable(DrawUtils.dp2px(0), Color.parseColor("#fbfbfb")));


        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrent == SAVE_AS) {
                    String str = mEditName.getText().toString();
                    if (TextUtils.isEmpty(str)) {
                        mEditName.setHint(mContext.getResources().getString(R.string.no_empty_tip));
                        mEditName.setHintTextColor(Color.parseColor("#fc6060"));
                        return;
                    }else{
                        dismiss();
                    }
                    if (!TextUtils.isEmpty(mDownloadUrl)) {
                        DownLoadFilmInfo downLoadFilmInfo = FileVideoModel.getDownloadFilmInfo(mDownloadUrl, true);
                        if(downLoadFilmInfo != null){
                            //已经存在该链接
                            if(downLoadFilmInfo.getStatus() != FINISH){
                                //且不为下载完成状态，继续下载
                                FilmDownLoadManager.getInstance(AppApplication.getContext()).start(mDownloadUrl, str, mSuffix, 0,"");
                            }
                            ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.already_download));
                        }else{
                            FilmDownLoadManager.getInstance(AppApplication.getContext()).start(mDownloadUrl, str, mSuffix, 0,"");
                        }
                    }
                } else{
                    dismiss();
                }
            }
        });

        mEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mClear.setVisibility(View.GONE);
                    mEditName.setHint("");
                    mEditName.setHintTextColor(Color.parseColor("#33ffffff"));
                } else {
                    mClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditName.setText("");
            }
        });
    }

    public void showDownloadDialog(int type, String url, String suffix) {
        mCurrent = type;
        switch (type) {
            case WRONG_URL:
//                mIcon.setVisibility(View.VISIBLE);
//                mIcon.setImageResource(R.drawable.download_url_error_icon);
//                mTitle.setText(getContext().getResources().getText(R.string.download_error_title));
//                mContent.setVisibility(View.VISIBLE);
//                mSaveName.setVisibility(View.GONE);
//                mContent.setText(getContext().getResources().getText(R.string.download_error_content));
//                mBottom.setVisibility(View.VISIBLE);
//                mDownloadUrl = url;
//                mOkBtn.setText(getContext().getResources().getText(R.string.go));
                break;
            case FORBIDDEN_YOUTUBE:
                mIcon.setVisibility(View.VISIBLE);
                //mIcon.setImageResource(R.drawable.download_forbidden_icon);
                mTitle.setText(getContext().getResources().getText(R.string.download_forbidden_title));
                mContent.setVisibility(View.VISIBLE);
                mSaveName.setVisibility(View.GONE);
                String head = (String) getContext().getResources().getText(R.string.download_forbidden_content);
                String end = (String) getContext().getResources().getText(R.string.download_forbidden_content2);
                mContent.setText(head + "," + end);
                mBottom.setVisibility(View.VISIBLE);
                mCancelBtn.setVisibility(View.GONE);
                mOkBtn.setText(getContext().getResources().getText(R.string.ok));
                break;
            case SAVE_AS:
                mIcon.setVisibility(View.GONE);
                mTitle.setText(getContext().getResources().getText(R.string.download_save_title));
                mContent.setVisibility(View.GONE);
                mSaveName.setVisibility(View.VISIBLE);
                mBottom.setVisibility(View.VISIBLE);
                mEditName.setText(getFileNameFromUrl(url, suffix));
                mDownloadUrl = url;
                mSuffix = suffix;
                mOkBtn.setText(getContext().getResources().getText(R.string.ok));
                break;
            case WAIT_DOWNLOADER:
//                mIcon.setVisibility(View.GONE);
//                mTitle.setText(getContext().getResources().getText(R.string.download_wait_title));
//                mContent.setVisibility(View.VISIBLE);
//                mSaveName.setVisibility(View.GONE);
//                mContent.setText(getContext().getResources().getText(R.string.download_wait_content));
//                mBottom.setVisibility(View.GONE);
//                mOkBtn.setText(getContext().getResources().getText(R.string.ok));
                break;
        }
        show();
    }

    public String getFileNameFromUrl(String url, String suffix){
        if(TextUtils.isEmpty(url)){
            return "";
        }
        try{
            int nameEnd = url.lastIndexOf(suffix);
            String subString = url.substring(0, nameEnd);
            int nameStart = subString.lastIndexOf("/");
            String reallyFileName = url.substring(nameStart + 1, nameEnd);
            return reallyFileName;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
