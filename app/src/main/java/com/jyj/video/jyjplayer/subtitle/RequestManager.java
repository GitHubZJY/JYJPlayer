package com.jyj.video.jyjplayer.subtitle;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.constant.SpConstant;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.manager.VideoPlayDataManager;
import com.jyj.video.jyjplayer.subtitle.bean.SubLanguage;
import com.jyj.video.jyjplayer.subtitle.bean.SubTitleFileInfo;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.SpUtils;

import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhengjiayang on 2017/12/4.
 */

public class RequestManager {

    private static final String SERVER_URL = "https://api.opensubtitles.org/xml-rpc";

    private static final String TAG = "SubtitleRequest";
    private static RequestManager mInstance = null;
    private String mToken = "";
    private static final String USER_AGENT = "go video player";
    //初始化请求接口
    private static final String METHOD_NAME_INIT_TOKEN = "LogIn";
    //搜索字幕接口
    private static final String METHOD_NAME_SEARCH_SUBTITLE = "SearchSubtitles";
    //下载字幕接口
    private static final String METHOD_NAME_DOWNLOAD_SUBTITLE = "DownloadSubtitles";

    private RequestSubListener mRequestSubListener;
    private DownLoadListener mDownLoadListener;
    private GetAllLanguageListener mGetAllLanguageListener;

    private String mDownFilePath;
    private List<SubLanguage> mLanguageList;

    public static RequestManager getInstance(){
        if(mInstance == null){
            mInstance = new RequestManager();
        }
        return mInstance;
    }
    /**
     * 发送请求字幕网站服务器信息，可作为测试方法
     */
    public void getServerInfo(){
        //创建参数列表
        Object[] params = new Object[0];
        params[0] = "test";
        sendRpcRequest("ServerInfo", params, new RpcHttpRequestListener() {
            @Override
            public void success(HashMap result) {

            }

            @Override
            public void fail(String failInfoStr) {

            }
        });
    }

    /**
     * LogIn( $username, $password, $language, $useragent )
     * 登陆后台，并且返回token，以便后续调用其他接口使用
     */
    public void initToken(){
        String subToken = SpUtils.obtain(SpConstant.SETTING).getString(SpConstant.ONLINE_SUBTITLE_TOKEN);
        if(!TextUtils.isEmpty(subToken)){
            mToken = subToken;
            return;
        }
        //创建参数列表
        Object[] params = new Object[4];
        params[0] = "";  //用户名可为空
        params[1] = "";  //密码可为空
        params[2] = "UTF-8";  //语言
        params[3] = USER_AGENT;  //测试所用用户标识
        sendRpcRequest(METHOD_NAME_INIT_TOKEN, params, new RpcHttpRequestListener() {
            @Override
            public void success(HashMap result) {
                mToken = (String)result.get("token");
                SpUtils.obtain(SpConstant.SETTING).save(SpConstant.ONLINE_SUBTITLE_TOKEN, mToken);
                LogUtil.d(TAG, "token: " + mToken);
            }

            @Override
            public void fail(String failInfoStr) {

            }
        });
    }



    public void searchSubTitle(String movieName, String language){
        if(TextUtils.isEmpty(movieName)){
            return;
        }
        final List<SubTitleFileInfo> resultList = new ArrayList<>();
        //创建参数列表
        Object[] params = new Object[3];
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("query", movieName);
        hashMap.put("sublanguageid", language);
        //hashMap.put("tag", "太子妃升职记.mp4");
        Object[] movieParams = new Object[]{hashMap};
        Object[] limitParams = new Object[]{"500"};
        params[0] = mToken;
        params[1] = movieParams;
        params[2] = limitParams;

        sendRpcRequest(METHOD_NAME_SEARCH_SUBTITLE, params, new RpcHttpRequestListener() {
            @Override
            public void success(HashMap result) {
                String status = (String)result.get("status");
                if(status.startsWith("401")){
                    //token失效，重新发请求
                    SpUtils.obtain(SpConstant.SETTING).save(SpConstant.ONLINE_SUBTITLE_TOKEN, "");
                    initToken();
                    return;
                }
                Object data = result.get("data");
                if(null == data){
                    if(mRequestSubListener != null){
                        mRequestSubListener.success(resultList);
                    }
                    return;
                }
                if(data instanceof HashMap){
                    HashMap objects = (HashMap)data;

                    for (Object key : objects.keySet()) {
                        HashMap<?, ?> h = (HashMap<?, ?>)objects.get(key);
                        SubTitleFileInfo subTitleFileInfo = new SubTitleFileInfo();
                        boolean isSrt = false;
                        for (Object j : h.keySet()) {
                            if(j.equals("IDSubtitleFile")){
                                subTitleFileInfo.setSubTitleFileId((String)h.get(j));
                            }
                            if(j.equals("SubFileName")){
                                String name = (String)h.get(j);
                                subTitleFileInfo.setSubTitleFileName(name);
                                if(name.endsWith("srt")){
                                    isSrt = true;
                                }
                            }
                        }
                        if(isSrt){
                            resultList.add(subTitleFileInfo);
                        }
                    }
                }else{
                    Object[] objects = (Object[])data;
                    for (Object object : objects) {
                        HashMap<?, ?> h = (HashMap<?, ?>)object;
                        SubTitleFileInfo subTitleFileInfo = new SubTitleFileInfo();
                        boolean isSrt = false;
                        for (Object j : h.keySet()) {
                            if(j.equals("IDSubtitleFile")){
                                subTitleFileInfo.setSubTitleFileId((String)h.get(j));
                            }
                            if(j.equals("SubFileName")){
                                String name = (String)h.get(j);
                                subTitleFileInfo.setSubTitleFileName(name);
                                if(name.endsWith("srt")){
                                    isSrt = true;
                                }
                            }
                        }
                        if(isSrt){
                            resultList.add(subTitleFileInfo);
                        }
                    }
                }


                LogUtil.d(TAG, "请求结束, 共查到" + resultList.size() + "个字幕数据");
                if(mRequestSubListener != null){
                    mRequestSubListener.success(resultList);
                }
            }

            @Override
            public void fail(String failInfoStr) {
                if(mRequestSubListener != null){
                    mRequestSubListener.fail(failInfoStr);
                }
            }
        });

    }

    /**
     * 带监听的请求
     * @param movieName
     * @param language
     * @param listener
     */
    public void searchSubTitle(String movieName, String language, final RequestSubListener listener){
        if(TextUtils.isEmpty(movieName)){
            return;
        }
        final List<SubTitleFileInfo> resultList = new ArrayList<>();
        //创建参数列表
        Object[] params = new Object[3];
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("query", movieName);
        hashMap.put("sublanguageid", language);
        //hashMap.put("tag", "太子妃升职记.mp4");
        Object[] movieParams = new Object[]{hashMap};
        Object[] limitParams = new Object[]{"500"};
        params[0] = mToken;
        params[1] = movieParams;
        params[2] = limitParams;

        sendRpcRequest(METHOD_NAME_SEARCH_SUBTITLE, params, new RpcHttpRequestListener() {
            @Override
            public void success(HashMap result) {
                String status = (String)result.get("status");
                if(status.startsWith("401")){
                    //token失效，重新发请求
                    SpUtils.obtain(SpConstant.SETTING).save(SpConstant.ONLINE_SUBTITLE_TOKEN, "");
                    initToken();
                    return;
                }
                Object data = result.get("data");
                if(null == data){
                    if(listener != null){
                        listener.success(resultList);
                    }
                    return;
                }
                if(data instanceof HashMap){
                    HashMap objects = (HashMap)data;

                    for (Object key : objects.keySet()) {
                        HashMap<?, ?> h = (HashMap<?, ?>)objects.get(key);
                        SubTitleFileInfo subTitleFileInfo = new SubTitleFileInfo();
                        boolean isSrt = false;
                        for (Object j : h.keySet()) {
                            if(j.equals("IDSubtitleFile")){
                                subTitleFileInfo.setSubTitleFileId((String)h.get(j));
                            }
                            if(j.equals("SubFileName")){
                                String name = (String)h.get(j);
                                subTitleFileInfo.setSubTitleFileName(name);
                                if(name.endsWith("srt")){
                                    isSrt = true;
                                }
                            }
                        }
                        if(isSrt){
                            resultList.add(subTitleFileInfo);
                        }
                    }
                }else{
                    Object[] objects = (Object[])data;
                    for (Object object : objects) {
                        HashMap<?, ?> h = (HashMap<?, ?>)object;
                        SubTitleFileInfo subTitleFileInfo = new SubTitleFileInfo();
                        boolean isSrt = false;
                        for (Object j : h.keySet()) {
                            if(j.equals("IDSubtitleFile")){
                                subTitleFileInfo.setSubTitleFileId((String)h.get(j));
                            }
                            if(j.equals("SubFileName")){
                                String name = (String)h.get(j);
                                subTitleFileInfo.setSubTitleFileName(name);
                                if(name.endsWith("srt")){
                                    isSrt = true;
                                }
                            }
                        }
                        if(isSrt){
                            resultList.add(subTitleFileInfo);
                        }
                    }
                }


                LogUtil.d(TAG, "请求结束, 共查到" + resultList.size() + "个字幕数据");
                if(listener != null){
                    listener.success(resultList);
                }
            }

            @Override
            public void fail(String failInfoStr) {
                if(listener != null){
                    listener.fail(failInfoStr);
                }
            }
        });

    }

    public void getAllLanguage(){
        //创建参数列表
        Object[] params = new Object[1];
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("language", "en");
        params[0] = hashMap;
        //params[1] = key;  //密码可为空
        //params[2] = "all";  //语言
        sendRpcRequest("GetSubLanguages", params, new RpcHttpRequestListener() {
            @Override
            public void success(HashMap result) {
                List<SubLanguage> subLanguageList = new ArrayList<SubLanguage>();
                Object[] objects = (Object[])result.get("data");
                if (objects != null) {
                    for (Object object : objects) {
                        HashMap<?, ?> h = (HashMap<?, ?>) object;
                        SubLanguage subLanguage = new SubLanguage();
                        for (Object j : h.keySet()) {
                            if (j.equals("SubLanguageID")) {
                                subLanguage.setLanguageId((String) h.get(j));
                                //Log.d("zjy", "SubLanguageID: "+h.get(j));
                            }
                            if (j.equals("LanguageName")) {
                                subLanguage.setLanguageName((String) h.get(j));
                                //Log.d("zjy", "LanguageName: "+h.get(j));
                            }
                            if (j.equals("ISO639")) {
                                subLanguage.setIsoCode((String) h.get(j));
                                //Log.d("zjy", "ISO639: "+h.get(j));
                            }
                        }
                        subLanguageList.add(subLanguage);
                    }
                    SpUtils.obtain().save(SpConstant.SUBTITLE_LANGUAGE_LIST, AppApplication.getGson().toJson(subLanguageList));
                }

                mLanguageList = new ArrayList<SubLanguage>();
                mLanguageList.addAll(subLanguageList);

                if(mGetAllLanguageListener != null){
                    mGetAllLanguageListener.getSuccess(subLanguageList);
                }

            }

            @Override
            public void fail(String failInfoStr) {
                if(mGetAllLanguageListener != null){
                    mGetAllLanguageListener.getFail(failInfoStr);
                }
            }
        });
    }


    public void quickSuggest(String key){
        //创建参数列表
        Object[] params = new Object[3];
        params[0] = mToken;  //用户名可为空
        params[1] = key;  //密码可为空
        params[2] = "all";  //语言
        sendRpcRequest("QuickSuggest", params, new RpcHttpRequestListener() {
            @Override
            public void success(HashMap result) {
            }

            @Override
            public void fail(String failInfoStr) {

            }
        });
    }

    public void downloadSubtitle(final List<SubTitleFileInfo> subTitleFileInfos){
        //创建参数列表
        Object[] params = new Object[2];
        params[0] = mToken;
        Object[] subTitleIds = new Object[subTitleFileInfos.size()];
        for(int i=0; i<subTitleFileInfos.size(); i++){
            subTitleIds[i] = subTitleFileInfos.get(i).getSubTitleFileId();
        }
        params[1] = subTitleIds;
        sendRpcRequest(METHOD_NAME_DOWNLOAD_SUBTITLE, params, new RpcHttpRequestListener() {
            @Override
            public void success(HashMap result) {
                if(DownLoadFileUtil.mIsCancel){
                    DownLoadFileUtil.mIsCancel = false;
                    return;
                }
                if(result.get("data") instanceof Boolean){
                    if(mDownLoadListener != null){
                        mDownLoadListener.downFail();
                    }
                    return;
                }
                Object[] objects = (Object[])result.get("data");
                for (int i=0; i<objects.length; i++) {
                    HashMap<?, ?> h = (HashMap<?, ?>)objects[i];
                    for (Object j : h.keySet()) {
                        if(j.equals("data")){
                            if(i == 0){
                                mDownFilePath = DownLoadFileUtil.decodeBase64((String)h.get(j), subTitleFileInfos.get(i).getSubTitleFileName());
                            }else{
                                String secondFilePath = DownLoadFileUtil.decodeBase64((String)h.get(j), subTitleFileInfos.get(i).getSubTitleFileName());
                                VideoInfo videoInfo = VideoPlayDataManager.getInstance().getCurPlayVideoInfo();
                                if(videoInfo != null){
                                    FileVideoModel.createSubtitle(videoInfo.getPath(), secondFilePath);
                                }

                            }
                        }
                        LogUtil.d(TAG, "下载完成" + j + ", " + h.get(j));
                    }
                }
                if(mDownLoadListener != null && !TextUtils.isEmpty(mDownFilePath)){
                    mDownLoadListener.downSuccess(mDownFilePath);
                    mDownFilePath = "";
                }
            }

            @Override
            public void fail(String failInfoStr) {
                if(mDownLoadListener != null){
                    mDownLoadListener.downFail();
                }
            }
        });
    }

    /**
     * 带监听的请求
     * @param subTitleFileInfos
     * @param listener
     */
    public void downloadSubtitle(final List<SubTitleFileInfo> subTitleFileInfos, final DownLoadListener listener){
        //创建参数列表
        Object[] params = new Object[2];
        params[0] = mToken;
        Object[] subTitleIds = new Object[subTitleFileInfos.size()];
        for(int i=0; i<subTitleFileInfos.size(); i++){
            subTitleIds[i] = subTitleFileInfos.get(i).getSubTitleFileId();
        }
        params[1] = subTitleIds;
        sendRpcRequest(METHOD_NAME_DOWNLOAD_SUBTITLE, params, new RpcHttpRequestListener() {
            @Override
            public void success(HashMap result) {
                if(DownLoadFileUtil.mIsCancel){
                    DownLoadFileUtil.mIsCancel = false;
                    return;
                }
                if(result.get("data") instanceof Boolean){
                    if(listener != null){
                        listener.downFail();
                    }
                    return;
                }
                Object[] objects = (Object[])result.get("data");
                for (int i=0; i<objects.length; i++) {
                    HashMap<?, ?> h = (HashMap<?, ?>)objects[i];
                    for (Object j : h.keySet()) {
                        if(j.equals("data")){
                            if(i == 0){
                                mDownFilePath = DownLoadFileUtil.decodeBase64((String)h.get(j), subTitleFileInfos.get(i).getSubTitleFileName());
                            }else{
                                String secondFilePath = DownLoadFileUtil.decodeBase64((String)h.get(j), subTitleFileInfos.get(i).getSubTitleFileName());
                                VideoInfo videoInfo = VideoPlayDataManager.getInstance().getCurPlayVideoInfo();
                                if(videoInfo != null){
                                    FileVideoModel.createSubtitle(videoInfo.getPath(), secondFilePath);
                                }

                            }
                        }
                        LogUtil.d(TAG, "下载完成" + j + ", " + h.get(j));
                    }
                }
                if(listener != null && !TextUtils.isEmpty(mDownFilePath)){
                    listener.downSuccess(mDownFilePath);
                    mDownFilePath = "";
                }
            }

            @Override
            public void fail(String failInfoStr) {
                if(listener != null){
                    listener.downFail();
                }
            }
        });
    }

    /**
     * 发送RPC请求
     * @param methodName  接口方法名
     * @param params  参数数组
     */
    public void sendRpcRequest(String methodName, Object[] params, final RpcHttpRequestListener listener){
        try{
            //配置客户端
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            //设置服务器端地址
            config.setServerURL(new URL(SERVER_URL));
            //创建XmlRpc客户端
            XmlRpcClient client = new XmlRpcClient();
            //绑定以上设置
            client.setConfig(config);
            LogUtil.d(TAG, "开始请求" + "接口：" + methodName);
            //执行XML-RPC 请求
            client.executeAsync(config, methodName, params, new AsyncCallback() {
                @Override
                public void handleResult(XmlRpcRequest xmlRpcRequest, Object o) {
                    LogUtil.d(TAG, "请求结束" + o.toString());
                    HashMap result = (HashMap)o;
                    if(listener != null){
                        listener.success(result);
                    }
                }

                @Override
                public void handleError(XmlRpcRequest xmlRpcRequest, Throwable throwable) {
                    LogUtil.d(TAG, "请求失败" + throwable.toString());
                    if(listener != null){
                        listener.fail(throwable.toString());
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface RpcHttpRequestListener{
        void success(HashMap<?, ?> result);
        void fail(String failInfoStr);
    }

    public List<SubLanguage> getLanguageList(){
        String spString = SpUtils.obtain().getString(SpConstant.SUBTITLE_LANGUAGE_LIST, null);
        if (TextUtils.isEmpty(spString)) {
            RequestManager.getInstance().getAllLanguage();
            return mLanguageList;
        }
        mLanguageList = getFilmListFromJson(spString);
        return mLanguageList;
    }

    public static List<SubLanguage> getFilmListFromJson(String filmJson) {
        return AppApplication.getGson().fromJson(filmJson, new TypeToken<List<SubLanguage>>() {
        }.getType());
    }


    public void setRequestSubListener(RequestSubListener mRequestSubListener) {
        this.mRequestSubListener = mRequestSubListener;
    }

    public void setDownLoadListener(DownLoadListener mDownLoadListener) {
        this.mDownLoadListener = mDownLoadListener;
    }

    public void setGetAllLanguageListener(GetAllLanguageListener mGetAllLanguageListener) {
        this.mGetAllLanguageListener = mGetAllLanguageListener;
    }

    public interface RequestSubListener{
        void success(List<SubTitleFileInfo> result);
        void fail(String failInfoStr);
    }

    public interface DownLoadListener{
        void downSuccess(String filePath);
        void downFail();
    }

    public interface GetAllLanguageListener{
        void getSuccess(List<SubLanguage> result);
        void getFail(String failInfoStr);
    }
}

