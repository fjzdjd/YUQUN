package com.yuqun.main.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.yuqun.main.utils.LogN;

public class SharePreferenceManager extends BaseManager {

    private static String CONFIG_FILE_NAME = "chehui_yuqun";
    private volatile static SharePreferenceManager instance = null;
    private SharedPreferences sharepreference = null;

    public SharePreferenceManager() {

    }

    public static SharePreferenceManager getInstance() {
        if (instance == null) {
            instance = new SharePreferenceManager();
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        sharepreference = context.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        String ret = defaultValue;

        if (isInit && sharepreference != null) {
            ret = sharepreference.getString(key, defaultValue);
        } else {
            Log.e("", "init() method should call first or sp is null");
        }

        return ret;
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInt(String key, int defaultValue) {
        int ret = defaultValue;

        if (isInit && sharepreference != null) {
            ret = sharepreference.getInt(key, defaultValue);
        } else {
            Log.e("", "init() method should call first or sp is null");
        }

        return ret;
    }

    /**
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        if (isInit && sharepreference != null) {
            Editor ed = sharepreference.edit();
            ed.putString(key, value);
            ed.commit();
        } else {
            Log.e("", "init() method should call first or sp is null");
        }
    }
    /**
     * 清空数据
     */
    public void clear() {
        if (isInit && sharepreference != null) {
            Editor ed = sharepreference.edit();
            ed.clear();
            ed.commit();
            LogN.d(this, "clear data");
        }else {
            LogN.e(this, "清理数据失败!");
        }
    }

}
