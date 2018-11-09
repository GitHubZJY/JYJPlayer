package com.jyj.video.jyjplayer.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;


import com.jyj.video.jyjplayer.AppApplication;
import com.zjyang.base.utils.LogUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 类描述: 获取存储路径的方法
 * 功能详细描述:
 * 
 * @author  yangxijun
 * @date  [2015年6月30日]
 */
public class StorageAddressUtil {

	private static final Pattern DIR_SEPORATOR = Pattern.compile("/");

	private static final String ENV_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
	private static final String ENV_EMULATED_STORAGE_TARGET = "EMULATED_STORAGE_TARGET";
	private static final String ENV_SECONDARY_STORAGE = "SECONDARY_STORAGE";

	public static final String SDCARD_NAME_1 = "/storage/emulated/0";
	public static final String SDCARD_NAME_2 = "/storage/emulated/legacy";
	public static final String SDCARD_NAME_3 = "/storage/sdcard0";

	/**
	 * Return all available SD-Cards in the system (include emulated)
	 *
	 * @return paths to all available SD-Cards in the system (include emulated)
	 */
	public static String[] getStorageDirectories() {
		// 最终的输出路径集合，用set保证不重复
		final Set<String> dirs = new HashSet<String>();
		// 所有SD卡路径
		final String rawExternalStorage = System.getenv(ENV_EXTERNAL_STORAGE);
		// 所有二级SD卡路径
		final String rawSecondaryStoragesStr = System
				.getenv(ENV_SECONDARY_STORAGE);
		// 模拟SD卡路径
		final String rawEmulatedStorageTarget = System
				.getenv(ENV_EMULATED_STORAGE_TARGET);
		if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
			if (TextUtils.isEmpty(rawExternalStorage)) {
				// 如果都没有路径，直接写死用下面默认路径（源码）
				dirs.add("/storage/sdcard0");
			} else {
				dirs.add(rawExternalStorage);
			}
		} else {
			final String rawUserId;
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
				rawUserId = "";
			} else {
				final String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath();
				final String[] folders = DIR_SEPORATOR.split(path);
				final String lastFolder = folders[folders.length - 1];
				boolean isDigit = false;
				try {
					Integer.valueOf(lastFolder);
					isDigit = true;
				} catch (NumberFormatException ignored) {
				}
				rawUserId = isDigit ? lastFolder : "";
			}
			// 各种模拟路径 /storage/emulated/0[1,2,...]
			if (TextUtils.isEmpty(rawUserId)) {
				dirs.add(rawEmulatedStorageTarget);
			} else {
				dirs.add(rawEmulatedStorageTarget + File.separator + rawUserId);
			}
		}
		// 二级SD卡路径
		if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
			final String[] rawSecondaryStorages = rawSecondaryStoragesStr
					.split(File.pathSeparator);
			Collections.addAll(dirs, rawSecondaryStorages);
		}

		// 所有在默认SD卡下的默认路径父目录下的文件夹
		File innerDir = Environment.getExternalStorageDirectory();
		File rootDir = innerDir.getParentFile();
		File[] files = rootDir.listFiles();
		if (files != null) {
			for (File file : files) {
				dirs.add(file.getPath());
			}
		}

		return dirs.toArray(new String[dirs.size()]);
	}


	/**
	 * 通过反射的方式使用在sdk中被 隐藏 的类 StroageVolume 中的方法getVolumeList()，获取所有的存储空间（Stroage Volume），
	 * 然后通过参数is_removable控制，来获取内部存储和外部存储（内外sd卡）的路径，
	 * 参数 is_removable为false时得到的是内置sd卡路径，为true则为外置sd卡路径
	 * @param mContext
	 * @param is_removale true 为外置sd卡
	 * @return
	 */
	private static String getStoragePath(Context mContext, boolean is_removale) {

		StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
		Class<?> storageVolumeClazz = null;
		try {
			storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
			Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
			Method getPath = storageVolumeClazz.getMethod("getPath");
			Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
			Object result = getVolumeList.invoke(mStorageManager);
			final int length = Array.getLength(result);
			for (int i = 0; i < length; i++) {
				Object storageVolumeElement = Array.get(result, i);
				String path = (String) getPath.invoke(storageVolumeElement);
				boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
				LogUtil.e("FileScanner", is_removale + "!!!!!!!!!! " + path);
				if (is_removale == removable) {
					return path;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** <br>功能简述: 判断该目录能否写入
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param path
	 * @return
	 */
	public static boolean canWrite(String path) {
		File file = new File(path);

		// 判断file能否读取写入，并且不是一个 USB drive
		// 分开写用于debug
		if (file != null) {
			if (file.isDirectory() && file.canRead() && file.canWrite()) {
				if (file.listFiles() != null) {
					if (file.listFiles().length > 0) {
						return true;
					}
				}
			}
		}

		return false;
	}


	/** <br>功能简述: 判断该目录能否写入
	 * @param path
	 */
	public static boolean canWrite2(String path) {
		File file = new File(path);

		// 判断file能否读取写入，并且不是一个 USB drive
		// 分开写用于debug
		if (file != null) {
			if (file.isDirectory()) {
				if (file.canRead()) {
//					if (file.canWrite()) {
						return true;
//					} else {
//						LogUtil.e("FileScanner", "canWrite2  path 不可写 " + path);
//					}
				} else {
					LogUtil.e("FileScanner", "canWrite2  path 不可读 " + path);
				}
			} else {
				LogUtil.e("FileScanner", "canWrite2  path 不是文件夹 " + path);
			}
		} else {
			LogUtil.e("FileScanner", "canWrite2 path file 为NULL " + path);
		}

		return false;
	}

	/** <br>功能简述:获取合适的存储目录
	 * <br>功能详细描述:
	 * <br>注意: 暂时取第一个可以写的目录
	 *  TODO 可以优化增加条件
	 * @return
	 */
	public static String getSuitableStorage() {
		String[] dirs = getStorageDirectories();
		for (String dir : dirs) {
			if (canWrite(dir)) {
				return dir;
			}
		}
		return Environment.getExternalStorageDirectory().getPath();
	}

	/** <br>功能简述:获取合适的存储目录
	 * <br>功能详细描述:
	 * <br>注意: 暂时取第一个可以写的目录
	 *  TODO 可以优化增加条件
	 * @return
	 */
	public static String[] getAllSuitableStorage() {
		// 新增获取sd卡路径之方法 并用
//		String localSd = getStoragePath(AppApplication.getContext(), false);
		String outerSd = getStoragePath(AppApplication.getContext(), true);
		String[] dirs = getStorageDirectories();
		final Set<String> dirSet = new HashSet<String>();
		final Set<String> deleteSet = new HashSet<String>();

		for (String dir : dirs) {
			if (canWrite(dir)) {
				dirSet.add(dir);
			}

			// 避免重复的sdcard路径
			if (isLocalSdCardFile(dir)) {
				LogUtil.d("FileScanner", "避免重复的sdcard路径 delete path " + dir);
				deleteSet.add(dir);
			}
		}
		if (canWrite("/sdcard")) {
			dirSet.add("/sdcard");
		}

		if (!TextUtils.isEmpty(outerSd) && canWrite2(outerSd)) {
			dirSet.add(outerSd);
		} else {
			LogUtil.d("FileScanner", "外置sd卡为空 or 不可写  " + outerSd);
		}
		dirSet.removeAll(deleteSet);

		for (String s : dirSet) {
			LogUtil.e("FileScanner", "最终路径   " + s);
		}

		return dirSet.toArray(new String[dirSet.size()]);
	}


	/** 避免重复的内置sdcard路径
	 * 是否是内置sd卡上的文件
	 **/
	public static boolean isLocalSdCardFile(String sdCardPath) {
		if (sdCardPath.contains(SDCARD_NAME_1) || sdCardPath.contains(SDCARD_NAME_2) || sdCardPath.contains(SDCARD_NAME_3)) {
			return true;
		}
		return false;
	}
}
