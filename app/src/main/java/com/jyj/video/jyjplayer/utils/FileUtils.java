package com.jyj.video.jyjplayer.utils;

import android.content.Context;
import android.text.TextUtils;

import com.jyj.video.jyjplayer.download.DownloadUtils;
import com.zjyang.base.utils.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhengjiayang on 2017/11/13.
 */

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final int BUFFER_SIZE = 4096;

    /**
     * 将文件名 去掉类型后缀
     * @param fileName
     * @return
     */
    public static String getReallyFileName(String fileName){
        int i = fileName.lastIndexOf('.');
        if ( i != -1) {
            fileName = fileName.substring(0, i);
        }
        return fileName;
    }

    public static String getFileNameInPath(String path){
        int i = path.lastIndexOf("/");
        if(i != -1){
            path = path.substring(i+1, path.length());
        }
        return path;
    }

    /**
     * 根据文件路径，获取其所在的目录路径
     * @param fileUrl
     * @return
     */
    public static String getFileDirPath(String fileUrl){
        String newPath = "";
        String[] folderArr = fileUrl.split("/");
        if(folderArr.length == 2){
            newPath = fileUrl;
            return newPath;
        }
        for(int i=0; i < folderArr.length - 1; i++){
            if(i == folderArr.length - 2){
                newPath = newPath + folderArr[i];
            }else{
                newPath = newPath + folderArr[i] + "/";
            }
        }
        return newPath;
    }

    /**
     * 删除单个文件
     *
     * @param oldPath 被重命名文件路径
     * @param newName 重命名后的新路径
     * @return 重命名成功后的路径
     */
    public static String getFileNameOfRenameSingleFileWithNewName(String oldPath, String newName) {
        if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newName)) {
            return null;
        }
        String newPath = VideoUtil.getRenamedNewPath(oldPath, newName);
        if (newPath == null || TextUtils.isEmpty(newPath)) {
            return null;
        }
        newPath = checkDuplication(newPath);
        if (TextUtils.isEmpty(newPath)) {
            return null;
        }

        return newPath;
    }

    /**
     * 删除单个文件
     *
     * @param oldPath 被重命名文件路径
     * @param newName 重命名后的新路径
     * @return 重命名成功后的路径
     */
    public static String renameSingleFileWithNewName(String oldPath, String newName) {
        if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newName)) {
            return null;
        }
        String newPath = VideoUtil.getRenamedNewPath(oldPath, newName);
        if (newPath == null || TextUtils.isEmpty(newPath)) {
            return null;
        }
        newPath = checkDuplication(newPath);
        if (TextUtils.isEmpty(newPath)) {
            return null;
        }
        newName = VideoUtil.getVideoName(newPath);

        File file = new File(oldPath);
        if (file.isFile() && file.exists() && file.renameTo(new File(newPath))) {
            return newName;
        }
        return null;
    }

    /**
     * 删除单个文件
     *
     * @param oldPath 被重命名文件路径
     * @param newPath 重命名后的新路径
     * @return 是否重命名成功
     */
    public static String renameSingleFile(String oldPath, String newPath) {
        if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath)) {
            return null;
        }

        File file = new File(oldPath);
        newPath = checkDuplication(newPath);
        if (TextUtils.isEmpty(newPath)) {
            return null;
        }
        if (file.isFile() && file.exists() && file.renameTo(new File(newPath))) {
            return newPath;
        }
        return null;
    }

    public static String checkDuplication(final String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File newFile = new File(path);
        String parentPath = VideoUtil.getParentPath(path);
        String name = FileUtils.getReallyFileName(VideoUtil.getVideoName(path));
        String suffix = VideoUtil.getFileSuffix(path);
        String tempName = name;
        int i = 1;

        while (newFile.exists()) {
            String nameEnd = "-1";
            int index = name.lastIndexOf(".");
            if (index != -1) {
                nameEnd = name.substring(index + 1);
            }
            int endNum = -1;
            try {
                endNum = Integer.valueOf(nameEnd);
                LogUtil.e(TAG, "checkDuplication " + endNum);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "checkDuplication ERROR " + e);
            }

            if (endNum > 0) {
                name = name.substring(0, index);
            }


            tempName = name + "." + i;
            newFile = new File(parentPath + "/" + tempName + suffix);
            i ++;
        }
        return parentPath + "/" + tempName + suffix;
    }

    /**
     * 电影名是否重名，重名则改为 原名（n）.xxx
     * @param path
     * @return
     */
    public static String checkFilmDuplication(final String path, String fileName) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(fileName)) {
            return null;
        }
        File newFile = new File(path);
        String parentPath = VideoUtil.getParentPath(path);
        String tempPath = parentPath + "/" + fileName + ".temp";
        File tempFile = new File(tempPath);
        String name = FileUtils.getReallyFileName(VideoUtil.getVideoName(path));
        String suffix = VideoUtil.getFileSuffix(path);
        String tempName = name;
        int i = 1;

        while (newFile.exists() || tempFile.exists()) {
            LogUtil.e(TAG, "checkDuplication " + "本地目录存在同名文件！");
            String nameEnd = "-1";
            int index = name.lastIndexOf(".");
            if (index != -1) {
                nameEnd = name.substring(index + 1);
            }
            int endNum = -1;
            try {
                endNum = Integer.valueOf(nameEnd);
                LogUtil.e(TAG, "checkDuplication " + endNum);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "checkDuplication ERROR " + e);
            }

            if (endNum > 0) {
                name = name.substring(0, index);
            }


            tempName = name + "(" + i + ")";
            newFile = new File(parentPath + "/" + tempName + suffix);
            tempFile = new File(parentPath + "/" + tempName + ".temp");
            i ++;
        }
        return parentPath + "/" + tempName + suffix;
    }

    /**
     * MAP内排重 -文件名(不包含类型后缀)
     * @param pathCache
     * @return
     */
    public static HashMap<String, String> checkDuplicationWithinMap(final HashMap<String, String> pathCache) {

        // 集合内处理名字相同
        Set<String> nameSet = new HashSet<String>();
        for (String path : pathCache.keySet()) {
            String lockedPath = pathCache.get(path);
            String lockedName = FileUtils.getReallyFileName(VideoUtil.getVideoName(lockedPath));
            if (TextUtils.isEmpty(lockedName) || TextUtils.isEmpty(lockedPath)) {
                LogUtil.e(TAG, "checkDuplicationWithinMap ERROR lockedName or lockedPath为空");
                continue;
            }

            String parentPath = VideoUtil.getParentPath(lockedPath);
            String suffix = VideoUtil.getFileSuffix(lockedPath);
            String tempName = lockedName;
            int i = 1;

            while (nameSet.contains(lockedName)) {
                String nameEnd = "-1";
                int index = lockedName.lastIndexOf(".");
                if (index != -1) {
                    nameEnd = lockedName.substring(index + 1);
                }
                int endNum = -1;
                try {
                    endNum = Integer.valueOf(nameEnd);
                    LogUtil.e(TAG, "checkDuplicationWithinMap " + endNum);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "checkDuplicationWithinMap ERROR " + e);
                }

                if (endNum > 0) {
                    lockedName = lockedName.substring(0, index);
                }


                tempName = lockedName + "." + i;
                lockedName = tempName;
                i ++;
            }

            nameSet.add(lockedName);
            lockedPath = parentPath + "/" + lockedName + suffix;

            if (!TextUtils.isEmpty(lockedPath)) {
                pathCache.put(path, lockedPath);
            }
        }
        return pathCache;
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删文件路径
     * @return 是否删除成功
     */
    public static boolean deleteSingleFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        LogUtil.d("FileVideoModel", "deleteSingleFile: " + filePath + ", " + file.isFile() + ", " + file.exists());
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删文件夹路径
     * @return 是否删除成功
     */
    public static boolean deleteDirectory(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteSingleFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        // 子项未删除干净 当前目录不是空目录
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 根据路径删除指定的目录或文件
     *
     * @param filePath 被删文件 or 文件夹路径
     * @return 是否删除成功
     */
    public static boolean DeleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteSingleFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath 原文件绝对路径
     * @param newPath 复制后绝对路径
     * @return boolean 是否复制成功
     */
    public static boolean copyFile(String oldPath, String newPath) {
        if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath)) {
            return false;
        }
        int byteSum = 0;
        int byteRead = 0;
        File oldFile = new File(oldPath);
        File newFile = DownloadUtils.createFile(newPath);
        if (!oldFile.exists() || newFile == null || !newFile.exists()) {
            return false;
        }

        try {
            InputStream inputStream = new FileInputStream(oldPath); //读入原文件
            FileOutputStream outputStream = new FileOutputStream(newPath);
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((byteRead = inputStream.read(buffer)) != -1) {
                byteSum += byteRead; //字节数 文件大小
                System.out.println(byteSum);
                outputStream.write(buffer, 0, byteRead);
            }
            inputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "copyFile Exception " + e);

        }

        return false;
    }

    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(
                    context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
