package com.jyj.video.jyjplayer.module.search.model;

import java.util.List;


/**
 *
 *
 */
public interface SearchObserver {

	void onSearchFinish(String searchKey, List<?> list, int resultCount);
	void onSearchFailed(String searchKey, int errorCode);

}
