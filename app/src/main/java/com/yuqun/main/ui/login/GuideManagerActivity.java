package com.yuqun.main.ui.login;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;

import java.util.ArrayList;

/**
 * 软件介绍导界面
 */
public class GuideManagerActivity extends BaseActivity {

    private View view01;
    private View view02;
    private View view03;
    private View view04;

    /**
     * 实例化viewpager
     */
    private ViewPager mViewPager;

    private ArrayList<View> mVLists;

    /**
     * 实例化ViewPager适配器
     */
    private ViewPagerAdapter mPagerAdapter;

    /**
     * 点击开始
     */
    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        setContentView(R.layout.login_guidemanager_layout);

        initViews();
        initDatas();
    }


    @Override
    public void initViews() {
        /**
         * 实例化布局对象
         */
        LayoutInflater mInflater = getLayoutInflater().from(GuideManagerActivity.this);
        view01 = mInflater.inflate(R.layout.loading_viewpager1, null);
        view02 = mInflater.inflate(R.layout.loading_viewpager2, null);
        view03 = mInflater.inflate(R.layout.loading_viewpager3, null);
        view04 = mInflater.inflate(R.layout.loading_viewpager4, null);

        mViewPager = (ViewPager) findViewById(R.id.viewpage);

        mVLists = new ArrayList<View>();

        mPagerAdapter = new ViewPagerAdapter(mVLists);

        mImg = (ImageView) view04.findViewById(R.id.image_tiyan);

    }

    @Override
    public void initDatas() {
        mVLists.add(view01);
        mVLists.add(view02);
//        mVLists.add(view03);
        mVLists.add(view04);

        mViewPager.setAdapter(mPagerAdapter);

        mImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                activityManager.startNextActivity(LoginActivity.class);
                GuideManagerActivity.this.finish();
            }
        });
    }


    @Override
    public void bindListener() {

    }


    /**
     * @author zzp
     *         <p>
     *         viewpager适配器
     *         <p>
     */
    private class ViewPagerAdapter extends PagerAdapter {

        /**
         * viewpager集合
         */
        private ArrayList<View> viewList;

        public ViewPagerAdapter(ArrayList<View> viewList) {
            this.viewList = viewList;

        }

        /**
         * 初始化位置
         */
        @Override
        public Object instantiateItem(View view, int position) {
            ((ViewPager) view).addView(viewList.get(position), 0);
            return viewList.get(position);
        }

        /**
         * 获得页面数目
         */
        @Override
        public int getCount() {
            if (viewList != null) {
                return viewList.size();
            }
            return 0;
        }

        /**
         * 判断是否为对象生成
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        /**
         * 销毁
         */
        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(viewList.get(position));
        }

    }

}
