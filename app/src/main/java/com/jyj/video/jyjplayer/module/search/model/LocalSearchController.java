package com.jyj.video.jyjplayer.module.search.model;


import com.jyj.video.jyjplayer.filescan.SortComparators;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.zjyang.base.utils.HandlerUtils;
import com.zjyang.base.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <br>
 * 类描述: 本地搜索的控制类 <br>
 * 功能详细描述: 现在用于控制联系人和日历搜索
 * 
 * @author yangxijun
 * @date [2015年5月21日]
 */
public class LocalSearchController {

	private static final String TAG = "LocalSearch";

	/**
	 * 数据加载的状态
	 */
	public static final int DATA_STATE_NO_LOAD = 0;
	public static final int DATA_STATE_LOADED = 1;
	private int mDataState = DATA_STATE_NO_LOAD;

	private static LocalSearchController sInstance = null;
	private SearchUtils mSearchUtil = null;


	// 所有视频列表-搜索范围
	private List<VideoInfo> mAllVideoList = new CopyOnWriteArrayList<VideoInfo>();
	private List<VideoInfo> mResultVideoList = new CopyOnWriteArrayList<VideoInfo>();

	private FileVideoModel mFileVideoModel = null;

	private final static Object LOCK = new Object();

    private static SearchObserver sSearchObserver;

    public static synchronized void register(SearchObserver observer) {
        sSearchObserver = observer;
    }

    public static synchronized void unregister(SearchObserver observer) {
        sSearchObserver = null;
    }

	public LocalSearchController() {
		init();
	}

	private void init() {
		mSearchUtil = SearchUtils.getInstance();
		if (mFileVideoModel == null) {
			mFileVideoModel = new FileVideoModel();
		}
		startLoadVideos();
	}

	public static LocalSearchController getInstance() {
		if (sInstance == null) {
			synchronized (LOCK) {
				if (sInstance == null) {
					sInstance = new LocalSearchController();
				}
			}
		}
		return sInstance;
	}

	/**
	 * 释放不必要的东西
	 */
	public void release() {
		sInstance = null;
	}

	/**
	 * @return 返回 dataState
	 */
	public int getDataState() {
		return mDataState;
	}


	/**
	 * 清除所有搜索内容（包括缓存）
	 */
	public void removeAllResult() {
		if (mResultVideoList != null) {
			mResultVideoList.clear();
		}
	}

    /**
     * @return 结果可能为空
     *
     */
	public List<VideoInfo> getSearchResult() {
		return mResultVideoList;
	}

	/**
	 * <br>
	 * 功能简述: 开始查找 <br>
	 * 注意: 需要等到联系人初始化完成才有数据 可以通过 @method getDataState获取到状态
	 * 
	 * @param key
	 */
	public void searchVideos(String key) {
		searchContactsTask(key);
	}

	/**
	 * 获取视频 为搜索作准备
	 */
	private void startLoadVideos() {

		mDataState = DATA_STATE_NO_LOAD;
		mAllVideoList.clear();
		mAllVideoList = FileVideoModel.getAllVideoListClone();

		if (mAllVideoList == null || mAllVideoList.size() < 1) {
			loadVideoTask();
		} else {
			mDataState = DATA_STATE_LOADED;
		}
	}

	/**
	 * 异步添加视频列表
	 */
	private void loadVideoTask() {
		mFileVideoModel.loadFolderInfosAsync();
		// TODO 扫描数据库返回处理添加
	}


	/** 
	 * 功能简述:异步查找方法
	 * 功能详细描述:
	 * 注意:
	 * TODO 后续优化 1.之前key搜索过，并且搜不到内容时，在输入直接return空 2.若输入的不是数字，则直接不查找电话
	 * 
	 * @param key
	 */
	private void searchContactsTask(final String key) {
		HandlerUtils.postThread(new Runnable() {

			@Override
			public void run() {
				mAllVideoList = FileVideoModel.getAllVideoListClone();
//				mAllVideoList.addAll(FileVideoModel.getDownloadVideoInfoCachedClone());
				if (mAllVideoList != null && mAllVideoList.size() > 0) {
					mResultVideoList.clear();
					mResultVideoList = searchContactsWithKey(key);
					// TODO 匹配度排序


                    if (sSearchObserver != null) {
                    	HandlerUtils.post(new Runnable() {
							@Override
							public void run() {
								if (sSearchObserver != null) {
									if (mResultVideoList.size() < 1) {
										sSearchObserver.onSearchFailed(key, SearchErrorCode.NO_MATCH);
									} else {
										LogUtil.d(TAG, "search result: " + mResultVideoList.size());
										sSearchObserver.onSearchFinish(key, mResultVideoList, mResultVideoList.size());
									}
								}
							}
						});
                    }
                } else {
                    if (sSearchObserver != null) {
						HandlerUtils.post(new Runnable() {
							@Override
							public void run() {
								if (sSearchObserver != null) {
									sSearchObserver.onSearchFailed(key, SearchErrorCode.NO_VIDEO);
								}
							}
						});
                    }
                }
			}
		});
	}

	/**
	 * 查询方法
	 *
	 * @param key 搜索关键字
	 * @return  result 符合搜索条件并将视频文件名高亮处理后的list
	 */
	private synchronized List<VideoInfo> searchContactsWithKey(String key) {
		LogUtil.e(TAG, "searchContactsWithKey=======" + key);
		List<VideoInfo> result = new ArrayList<VideoInfo>();
		Collections.sort(mAllVideoList, SortComparators.getInstance().mCreateTimeDesc);

		for (VideoInfo videoInfo : mAllVideoList) {
			String searchKeyName = videoInfo.getDisplayName(); // 被查询对象

			if (searchKeyName == null || searchKeyName.length() < 1) {
				continue;
			}
			SearchResultItem item = mSearchUtil.match(key, searchKeyName);
			if (item != null && item.mMatchValue > 0) {
				// 根据需求，将匹配的关键字部分高亮显示
				String highLightMatchString = SearchUtils.getHighLightString(videoInfo.getDisplayName(), item);
//				LogUtil.e(TAG, "mMatchPos=" + item.mMatchPos + "   mMatchWords " + item.mMatchWords + "  mMatchValue=" + item.mMatchValue + "   matchString " + highLightMatchString);

				VideoInfo v = new VideoInfo();
				v.setDisplayName(highLightMatchString);
				v.setDuration(videoInfo.getDuration());
				v.setName(videoInfo.getName());
				v.setIsPlaying(videoInfo.getIsPlaying());
				v.setLastModify(videoInfo.getLastModify());
				v.setPath(videoInfo.getPath());
				v.setSize(videoInfo.getSize());
				v.setParentFileName(videoInfo.getParentFileName());
				v.setSubtitlePath(videoInfo.getSubtitlePath());
				v.setSubtitleName(videoInfo.getSubtitleName());
				v.setIsNew(videoInfo.getIsNew());
				v.setMatchPos(item.mMatchPos);
				result.add(v);
			}
		}

		Collections.sort(result, SortComparators.getInstance().mMatchPos); // 按匹配位置前后排序
		return result;
	}

}
