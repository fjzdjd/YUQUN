package com.yuqun.main.manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzp on 2016/3/9.
 */
public class FragmentsManager {

    public static final String ORDER_CHECK = "order_check";
    public static final String ORDER_COUNT = "order_count";
    ;
    public static final String MESSAGE = "message";
    public static final String SET = "set";
    // 当前显示页面
    public static String TAG = "order_check";
    public static Map<String, Fragment> fragments = new HashMap<>();
    private static FragmentManager fMgr;
    private volatile static FragmentsManager instance;
    public View mTotalView;
    public boolean falg;

    /**
     * 创建单例类，提供静态方法调用
     *
     * @return ActivityManager
     */
    public static FragmentsManager getInstance() {
        if (instance == null) {
            instance = new FragmentsManager();
        }

        return instance;

    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fMgr = fragmentManager;
    }

    /**
     * 更改Fragment
     *
     * @param key TAG:当前页tag
     */
    public void changeFragment(String key) {

        fMgr.beginTransaction().hide(fragments.get(TAG)).show(fragments.get(key)).commit();
        TAG = key;

    }

    public void addFragment(Fragment fragment, String key) {
        fragments.put(key, fragment);

    }

    public void finishFragment() {
        fMgr.beginTransaction().remove(fragments.get(ORDER_CHECK)).remove(fragments.get
                (ORDER_COUNT))
                .remove(fragments.get(MESSAGE)).remove(fragments.get(SET)).commit();

        falg = false;

    }

}
