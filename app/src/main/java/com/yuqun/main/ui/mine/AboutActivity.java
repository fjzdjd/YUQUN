package com.yuqun.main.ui.mine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hp.hpl.sparta.Text;
import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;

/**
 * 关于我们
 * Created by Administrator on 2016/4/25.
 */
public class AboutActivity extends BaseActivity {
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about_us);
        initViews();
        initDatas();
        bindListener();
    }

    @Override
    public void initViews() {
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("鱼群V" + getVersionName(this));
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void initDatas() {

    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
