package com.zjyang.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zjyang.base.BaseApp;
import com.zjyang.base.constant.SpConstant;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhengjiayang on 2018/7/19.
 */
public class SpUtils {

    private SharedPreferences mSharedPreferences;

    private static Context sContext = BaseApp.sContext;

    private static Map<String, SoftReference<SharedPreferences>> sMap = new HashMap<String, SoftReference<SharedPreferences>>();

    private SpUtils(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public static SpUtils obtain() {
        return SpUtils.obtain(SpConstant.DEFAULT_SP_FILE);
    }

    /**
     * 获取SpUtils对象
     *
     * @param spName
     * @return
     */
    public static SpUtils obtain(String spName) {
        SharedPreferences sharedPreferences = null;
        if (sMap.containsKey(spName)) {
            SoftReference<SharedPreferences> softReference = sMap.get(spName);
            if (softReference != null) {
                sharedPreferences = softReference.get();
            }
        }

        if (null == sharedPreferences) {
            sharedPreferences = sContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
            sMap.put(spName, new SoftReference<SharedPreferences>(sharedPreferences));
        }
        return new SpUtils(sharedPreferences);
    }

    public static SharedPreferences getSharedPreferences(String spName) {
        return sContext.getSharedPreferences(spName, Context.MODE_MULTI_PROCESS);
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public SharedPreferences.Editor getEditor() {
        return mSharedPreferences.edit();
    }

    public void save(String key, boolean value) {
        getEditor().putBoolean(key, value).apply();
    }

    public void saveASyn(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }

    public void save(String key, int value) {
        getEditor().putInt(key, value).apply();
    }

    public void saveASyn(String key, int value) {
        getEditor().putInt(key, value).commit();
    }

    public void save(String key, long value) {
        getEditor().putLong(key, value).apply();
    }

    public void saveASyn(String key, long value) {
        getEditor().putLong(key, value).commit();
    }

    public void save(String key, String value) {
        getEditor().putString(key, value).apply();
    }

    public void saveASyn(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    public void save(String key, float value) {
        getEditor().putFloat(key, value).apply();
    }

    public void saveASyn(String key, float value) {
        getEditor().putFloat(key, value).commit();
    }

    public void save(String key, Set<String> value) {
        getEditor().putStringSet(key, value).apply();
    }

    public void remove(String... keys) {
        if (null != keys && keys.length > 0) {
            for (String key : keys) {
                getEditor().remove(key);
            }
            getEditor().apply();
        }
    }

    public void removeASyn(String... keys) {
        if (null != keys && keys.length > 0) {
            for (String key : keys) {
                getEditor().remove(key);
            }
            getEditor().commit();
        }
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    public int getInt(String key) {
        return mSharedPreferences.getInt(key, -1);
    }

    public int getInt(String key, int def) {
        return mSharedPreferences.getInt(key, def);
    }

    public long getLong(String key) {
        return mSharedPreferences.getLong(key, -1);
    }

    public long getLong(String key, long def) {
        return mSharedPreferences.getLong(key, def);
    }

    public float getFloat(String key) {
        return mSharedPreferences.getFloat(key, -1);
    }

    public float getFloat(String key, float def) {
        return mSharedPreferences.getFloat(key, def);
    }

    public Set<String> getStringSet(String key) {
        return mSharedPreferences.getStringSet(key, null);
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return mSharedPreferences.getStringSet(key, defaultValue);
    }

}
