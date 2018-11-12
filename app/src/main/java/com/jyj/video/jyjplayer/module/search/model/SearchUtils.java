package com.jyj.video.jyjplayer.module.search.model;


import com.zjyang.base.utils.LogUtil;

import java.util.ArrayList;

/**
 * 
 * @author panguowei
 *
 */
public class SearchUtils {

	private static SearchUtils sInstance = null;

	public static SearchUtils getInstance() {
		if (sInstance == null) {
			sInstance = new SearchUtils();
		}
		return sInstance;
	}

	/**
	 * 根据查询条件匹配被查询对象是否包含查询条件关键字
	 * 
	 * @param key
	 *            查询条件
	 * @param target
	 *            被查询的对象
	 * @return
	 */
	public SearchResultItem match(String key, String target) {

		if (key == null || "".equals(key) || target == null || "".equals(target)) {
			return null;
		}
		SearchResultItem item = null;
		// 先进行原文匹配，若能匹配到则不进行拼音匹配了
		item = matchSrc(key, target);
		if (item != null && item.mMatchValue > 0) {
			return item;
		}

		// 混合写匹配
		item = matchMixCaseSrc(key, target);
		if (item != null && item.mMatchValue > 0) {
			return item;
		}

//		ArrayList<String[]> spells = changeStringToSpellList(target);
//		if (spells != null) {
//			item = new SearchResultItem();
//			NdkPySearch.GetPYMatchValueEx(key, spells, item, false, 0);
//		}
//		return item;
		return null;
	}

	/**
	 * 不做任何转换，直接原文匹配
	 * 
	 * @param key
	 * @param target
	 * @return
	 */
	private SearchResultItem matchSrc(String key, String target) {
		if (key == null || "".equals(key) || target == null || "".equals(target)) {
			return null;
		}

		SearchResultItem item = null;
		int index = target.indexOf(key);
		if (index > -1) {
			item = new SearchResultItem();
			item.mMatchPos = index;
			item.mMatchWords = key.length();
			item.mMatchValue = key.length();
		}
		return item;
	}

	/**
	 * 进行数据大小写匹配
	 * 
	 * @author huyong
	 * @param key
	 * @param target
	 * @return
	 */
	private SearchResultItem matchMixCaseSrc(String key, String target) {
		if (key == null || "".equals(key) || target == null || "".equals(target)) {
			return null;
		}
		// 小写匹配
		key = key.toLowerCase();
		target = target.toLowerCase();
		SearchResultItem item = matchSrc(key, target);
		if (item != null) {
			return item;
		}
		// 大写匹配
		key = key.toUpperCase();
		target = target.toUpperCase();
		item = matchSrc(key, target);
		return item;

	}

	/**
	 * 将一个字符串转换成一个拼音组合列表
	 * 
	 * @param str
	 * @return 转换后的列表，英文字母不做变换
	 */
	private ArrayList<String[]> changeStringToSpellList(String str) {

		if (str == null || "".equals(str)) {
			return null;
		}

		ArrayList<String[]> result = new ArrayList<String[]>();
		String[] spells = null;
		char[] chars = str.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			spells = changeHanziToSpell(chars[i]);
			if (spells != null) {
				result.add(spells);
			}
		}

		return result;
	}

	/**
	 * 汉字转换成拼音
	 * 
	 * @param key
	 * @return
	 */
	private String[] changeHanziToSpell(char key) {
		String[] results = null;
//		Hanzi2Pinyin pInstance = Hanzi2Pinyin.getInstance(mContex, R.raw.unicode2pinyin);
//		if (pInstance == null) {
//			return results;
//		}
		// 是汉字则转成拼音，不是则原样转成数组返回
		if (isHanzi(key)) {
//			int code = key;
//			results = pInstance.GetPinyin(code);
		} else {
			results = new String[] { String.valueOf(key) };
		}

		return results;
	}

	/**
	 * 是否汉字
	 * 
	 * @param key
	 * @return
	 */
	private boolean isHanzi(char key) {
		boolean isHanzi = false;
		if (key >= 0x4e00 && key <= 0x9fa5) {
			isHanzi = true;
		}
		return isHanzi;
	}


	/**
	 * 获得高亮的拼接内容
	 */
	public static String getHighLightString(String target, SearchResultItem resultItem) {
		int keyStart = resultItem.mMatchPos;
		int keyLength = resultItem.mMatchWords;
		int keyEnd = keyStart + keyLength;
//		LogUtil.e("DLY", "" + target + "  " + keyStart + " " + keyEnd);

		///////////////////////////////// 转义特殊字符 < >/////////////////////////////////////////
		if (target.length() > 0) {
			// "<"
			int i = target.indexOf("<");
			while (i != -1) {
				LogUtil.e("DLY", "" + target);

				StringBuffer sb = new StringBuffer();
				if (i - 1 >= 0) {
					sb.append(target.substring(0, i));
				}
				sb.append("&lt;");
				if (i + 1 < target.length()) {
					sb.append(target.substring(i + 1, target.length()));
				}

				if (keyStart > i) {
					keyStart += 3;
				}
				if (keyEnd >= i + 1) {
					keyEnd += 3;
				}

				LogUtil.e("DLY", "" + target + "  " + keyStart + " " + keyEnd);
				target = sb.toString();
				i = target.indexOf("<");
			}

			// ">"
			i = target.indexOf(">");
			while (i != -1) {
//				LogUtil.e("DLY", "" + target);

				StringBuffer sb = new StringBuffer();
				if (i - 1 >= 0) {
					sb.append(target.substring(0, i));
				}
				sb.append("&gt;");
				if (i + 1 < target.length()) {
					sb.append(target.substring(i + 1, target.length()));
				}

				if (keyStart > i) {
					keyStart += 3;
				}
				if (keyEnd >= i + 1) {
					keyEnd += 3;
				}

//				LogUtil.e("DLY", "" + target + "  " + keyStart + " " + keyEnd);
				target = sb.toString();
				i = target.indexOf("<");
			}
		}
		///////////////////////////////// 转义特殊字符 END/////////////////////////////////////////

		StringBuffer sb = new StringBuffer();
		if (keyStart - 1 >= 0) {
			sb.append(target.substring(0, keyStart));
		}
		sb.append("<font color=\"#04bbff\">" + target.substring(keyStart, keyEnd) + "</font>");
		if (keyEnd < target.length()) {
			sb.append(target.substring(keyEnd, target.length()));
		}
		return sb.toString();
	}

}
