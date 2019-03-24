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
import com.jyj.video.jyjplayer.module.download.event.StartDownLoadEvent;
import com.zjyang.base.utils.ShapeUtils;
import com.zjyang.base.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import static com.jyj.video.jyjplayer.download.Constant.Status.FINISH;


/**
 * Created by gulian on 2018/05/15.
 */

public class RenameDialog extends Dialog {


    private Context mContext;
    private ImageView mIcon;
    private TextView mTitle;
    private TextView mContent;
    private RelativeLayout mSaveName;
    private ImageView mClear;
    private EditText mEditName;
    private LinearLayout mBottom;
    private Button mCancelBtn;
    private Button mOkBtn;

    public RenameDialog(Context context) {
        super(context);
        initView(context);
        mContext = context;
    }

    public RenameDialog(Context context, int themeResId) {
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
        mCancelBtn = rootView.findViewById(R.id.cancel);
        mOkBtn = rootView.findViewById(R.id.ok);
        //mEditName.setBackground(ShapeUtils.getRoundRectDrawable(DrawUtils.dp2px(0), Color.parseColor("#fbfbfb")));
        mClear.setBackground(ShapeUtils.drawColor(getContext().getDrawable(R.drawable.web_close), Color.parseColor("#000000")));

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = mEditName.getText().toString();
                if (TextUtils.isEmpty(str)) {
                    mEditName.setHint(mContext.getResources().getString(R.string.no_empty_tip));
                    mEditName.setHintTextColor(Color.parseColor("#fc6060"));
                    return;
                }else{
                    if(mCallback != null){
                        mCallback.clickConfirm(str);
                    }
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

    public void showDownloadDialog(String name, ClickCallback clickCallback) {
        mCallback = clickCallback;
        mIcon.setVisibility(View.GONE);
        mTitle.setText(getContext().getResources().getText(R.string.rename));
        mContent.setVisibility(View.GONE);
        mSaveName.setVisibility(View.VISIBLE);
        mBottom.setVisibility(View.VISIBLE);
        mEditName.setText(name);
        mOkBtn.setText(getContext().getResources().getText(R.string.ok));
        show();
    }

    private ClickCallback mCallback;

    public interface ClickCallback {
        void clickConfirm(String str);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
