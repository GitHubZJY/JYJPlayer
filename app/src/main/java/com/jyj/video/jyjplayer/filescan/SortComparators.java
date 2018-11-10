package com.jyj.video.jyjplayer.filescan;

import com.jyj.video.jyjplayer.db.bean.Download;
import com.jyj.video.jyjplayer.download.bean.DownloadInfo;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.AbstractBean;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;

import java.util.Comparator;

/**
 * @author zjyang
 * @date 17-11-8
 */

public class SortComparators {

    private static SortComparators sSortComparators = null;

    public static SortComparators getInstance() {
        if (sSortComparators == null) {
            synchronized (new Object()) {
                if (sSortComparators == null) {
                    sSortComparators = new SortComparators();
                }
            }
        }
        return sSortComparators;
    }


    /**
     * 时间比较
     */
    public Comparator<AbstractBean> mInfoTime = new Comparator<AbstractBean>() {
        @Override
        public int compare(AbstractBean lhs, AbstractBean rhs) {
            if (lhs == null || rhs == null) {
                return 0;
            }
            long l = 0L;
            long r = 0L;

            if (lhs instanceof FolderInfo && rhs instanceof FolderInfo) {
                l = ((FolderInfo) lhs).getLastModify();
                r = ((FolderInfo) rhs).getLastModify();
            } else if (lhs instanceof VideoInfo && rhs instanceof VideoInfo) {
                l = ((VideoInfo) lhs).getLastModify();
                r = ((VideoInfo) rhs).getLastModify();
            }

            if (r - l > 0) {
                return - 1;
            } else if (r - l < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    };


    /**
     * 时间比较
     */
    public Comparator<AbstractBean> mInfoTimeDesc = new Comparator<AbstractBean>() {
        @Override
        public int compare(AbstractBean lhs, AbstractBean rhs) {
            if (lhs == null || rhs == null) {
                return 0;
            }
            long l = 0L;
            long r = 0L;

            if (lhs instanceof FolderInfo && rhs instanceof FolderInfo) {
                l = ((FolderInfo) lhs).getLastModify();
                r = ((FolderInfo) rhs).getLastModify();
            } else if (lhs instanceof VideoInfo && rhs instanceof VideoInfo) {
                l = ((VideoInfo) lhs).getLastModify();
                r = ((VideoInfo) rhs).getLastModify();
            }

            if (r - l > 0) {
                return 1;
            } else if (r - l < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    };


    /**
     * 时间比较
     */
    public Comparator<AbstractBean> mCreateTimeDesc = new Comparator<AbstractBean>() {
        @Override
        public int compare(AbstractBean lhs, AbstractBean rhs) {
            if (lhs == null || rhs == null) {
                return 0;
            }
            long l = 0L;
            long r = 0L;

            if (lhs instanceof FolderInfo && rhs instanceof FolderInfo) {
                l = ((FolderInfo) lhs).getCreateTime();
                r = ((FolderInfo) rhs).getCreateTime();
            } else if (lhs instanceof VideoInfo && rhs instanceof VideoInfo) {
                l = ((VideoInfo) lhs).getCreateTime();
                r = ((VideoInfo) rhs).getCreateTime();
            } else if (lhs instanceof Download && rhs instanceof Download) {
                l = ((Download) lhs).getCreateTime();
                r = ((Download) rhs).getCreateTime();
            } else if (lhs instanceof DownloadInfo && rhs instanceof DownloadInfo) {
                l = ((DownloadInfo) lhs).getCreateTime();
                r = ((DownloadInfo) rhs).getCreateTime();
            } else if (lhs instanceof DownLoadFilmInfo && rhs instanceof DownLoadFilmInfo) {
                l = ((DownLoadFilmInfo) lhs).getCreateTime();
                r = ((DownLoadFilmInfo) rhs).getCreateTime();
            }
            if (r - l > 0) {
                return 1;
            } else if (r - l < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    };


    /**
     * 时间比较
     */
    public Comparator<AbstractBean> mMatchPos = new Comparator<AbstractBean>() {
        @Override
        public int compare(AbstractBean lhs, AbstractBean rhs) {
            if (lhs == null || rhs == null) {
                return 0;
            }
            long l = 0L;
            long r = 0L;

            if (lhs instanceof VideoInfo && rhs instanceof VideoInfo) {
                l = ((VideoInfo) lhs).getMatchPos();
                r = ((VideoInfo) rhs).getMatchPos();
            }
            if (r - l > 0) {
                return - 1;
            } else if (r - l < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    };
}
