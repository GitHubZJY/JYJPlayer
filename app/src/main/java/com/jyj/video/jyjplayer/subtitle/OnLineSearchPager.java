package com.jyj.video.jyjplayer.subtitle;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.event.SoftInputActionEvent;
import com.jyj.video.jyjplayer.subtitle.bean.SubLanguage;
import com.jyj.video.jyjplayer.subtitle.bean.SubTitleFileInfo;
import com.jyj.video.jyjplayer.ui.SoftInputObserveEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengjiayang on 2017/12/6.
 */

public class OnLineSearchPager extends RelativeLayout implements RequestManager.RequestSubListener{

    private Button mSearchBtn;
    private SoftInputObserveEditText mSearchEdit;
    private ImageView mClearIv;
    private TextView mLanguageTv;
    private TextView mLinkTv;
    private PopupWindow popupWindow = null;

    private List<SubLanguage> mSubLanguageList;
    private PageClickListener mListener;

    private SubLanguage mCurSubLanguage;
    private String mDefaultName = "";

    public OnLineSearchPager(Context context) {
        this(context, null);
    }

    public OnLineSearchPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OnLineSearchPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        RequestManager.getInstance().setRequestSubListener(this);
        mCurSubLanguage = new SubLanguage();
        mCurSubLanguage.setLanguageName("English");
        mSubLanguageList = new ArrayList<>();
        List<SubLanguage> languages = RequestManager.getInstance().getLanguageList();
        if(languages != null){
            mSubLanguageList.addAll(languages);
        }
        initView(context);
    }

    public void initView(final Context context){
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_online_search, null);

        mSearchBtn = (Button) rootView.findViewById(R.id.search_btn);
        mSearchEdit = (SoftInputObserveEditText) rootView.findViewById(R.id.search_key_edit);
        mLanguageTv = (TextView) rootView.findViewById(R.id.language_switch_tv);
        mLinkTv = (TextView) rootView.findViewById(R.id.link_tv);
        mClearIv = (ImageView) rootView.findViewById(R.id.clear_iv);

        mLanguageTv.setText(Html.fromHtml("<u>"+"English"+"</u>"));
        mLinkTv.setText("www.OpenSubtitles.org");
        mLinkTv.setAutoLinkMask(Linkify.ALL);
        mLinkTv.setMovementMethod(LinkMovementMethod.getInstance());
        stripUnderlines(mLinkTv);
        mLinkTv.setLinkTextColor(Color.parseColor("#80ffffff"));

        mSearchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(mSearchEdit.getText()) || mSearchEdit.getText().toString().startsWith(" ")){
                    return;
                }
                if(mCurSubLanguage == null || TextUtils.isEmpty(mCurSubLanguage.getLanguageId())){
                    RequestManager.getInstance().searchSubTitle(mSearchEdit.getText().toString(), "eng");
                }else{
                    RequestManager.getInstance().searchSubTitle(mSearchEdit.getText().toString(), mCurSubLanguage.getLanguageId());
                }
                if(mListener != null){
                    mListener.clickSearch();
                }
            }
        });

        mClearIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEdit.setText("");
            }
        });

        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(TextUtils.isEmpty(charSequence) || charSequence.toString().startsWith(" ")){
                    mClearIv.setVisibility(GONE);
                }else{
                    mClearIv.setVisibility(VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSearchEdit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        mSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);
                    EventBus.getDefault().post(new SoftInputActionEvent());
                    return true;
                }else if(i == EditorInfo.IME_ACTION_GO){
                    EventBus.getDefault().post(new SoftInputActionEvent());
                }
                return false;
            }
        });

        mLanguageTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopWindow(context);
            }
        });
        addView(rootView);
    }

    @Override
    public void success(List<SubTitleFileInfo> result) {
        if(mListener != null){
            mListener.searchFinish(true, result);
        }
    }

    @Override
    public void fail(String failInfoStr) {
        if(mListener != null){
            mListener.searchFinish(false, null);
        }
    }

    public void setDefaultName(String name){
        mDefaultName = name;
        if(!TextUtils.isEmpty(mDefaultName)){
            mSearchEdit.setText(mDefaultName);
        }
    }

    /**
     * 打开下拉列表弹窗
     */
    private void showPopWindow(Context context) {
        List<SubLanguage> languages = RequestManager.getInstance().getLanguageList();
        if(languages != null){
            mSubLanguageList.clear();
            mSubLanguageList.addAll(languages);
        }
        // 加载popupWindow的布局文件
        LayoutInflater layoutInflater =  (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView  = layoutInflater.inflate(R.layout.layout_sublanguage_popup, null,false);
        ListView listView = (ListView)contentView.findViewById(R.id.listView);

        listView.setAdapter(new SubLanguageAdapter(context, mSubLanguageList));
        popupWindow = new PopupWindow(contentView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sub_language_popup));
        popupWindow.setOutsideTouchable(true);
        //popupWindow.showAsDropDown(mLanguageTv);
        int[] location = new int[2];
        mLanguageTv.getLocationOnScreen(location);
        popupWindow.showAtLocation(mLanguageTv, Gravity.TOP | Gravity.START, location[0], location[1]);
    }
    /**
     * 关闭下拉列表弹窗
     */
    private void closePopWindow(){
        popupWindow.dismiss();
        popupWindow = null;
    }

    public PageClickListener getPageClickListener() {
        return mListener;
    }

    public void setPageClickListener(PageClickListener mListener) {
        this.mListener = mListener;
    }

    public interface PageClickListener{
        void clickSearch();
        void searchFinish(boolean isSuccess, List<SubTitleFileInfo> result);
    }

    class SubLanguageAdapter extends BaseAdapter{

        private List<SubLanguage> dataList;
        LayoutInflater inflater;

        public SubLanguageAdapter(Context context, List<SubLanguage> dataList) {
            this.dataList = dataList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rootView = inflater.inflate(R.layout.item_sublanguage, null);
            final SubLanguage subLanguage = dataList.get(i);
            final TextView languageTv = rootView.findViewById(R.id.language_tv);
            final RelativeLayout languageRlyt = rootView.findViewById(R.id.language_rlyt);
            languageTv.setText(subLanguage.getLanguageName());
            if(languageTv.getText().equals(mCurSubLanguage.getLanguageName())){
                languageTv.setTextColor(Color.parseColor("#43cfff"));
            }else{
                languageTv.setTextColor(Color.parseColor("#ffffff"));
            }

            languageRlyt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCurSubLanguage.setLanguageName(languageTv.getText().toString());
                    mCurSubLanguage.setLanguageId(subLanguage.getLanguageId());
                    mLanguageTv.setText(Html.fromHtml("<u>"+languageTv.getText().toString()+"</u>"));
                    closePopWindow();
                }
            });

            return rootView;
        }
    }

    //无下划线超链接，使用textColorLink、textColorHighlight分别修改超链接前景色和按下时的颜色
    private class URLSpanNoUnderline extends URLSpan {

        private int CREATOR;

        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    private void stripUnderlines(TextView textView) {
        if(null!=textView&&textView.getText() instanceof Spannable){
            Spannable s = (Spannable)textView.getText();
            URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
            for (URLSpan span: spans) {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);
                s.removeSpan(span);
                span = new URLSpanNoUnderline(span.getURL());
                s.setSpan(span, start, end, 0);
            }

            textView.setAutoLinkMask(0);
            textView.setText(s);
        }
    }

}
