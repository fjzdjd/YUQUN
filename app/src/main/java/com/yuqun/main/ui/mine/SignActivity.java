package com.yuqun.main.ui.mine;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.view.ProgressWebView;

/**
 * Created by Administrator on 2016/3/17.
 */
public class SignActivity extends BaseActivity {
    private ProgressWebView mWebView;
    private String uid;

    @Override
    public void initViews() {
        mWebView = (ProgressWebView) findViewById(R.id.webViewId);
        mWebView.getSettings().setJavaScriptEnabled(true);
        uid = SharePreferenceManager.getInstance().getString(CommonData.USER_ID,"");
        mWebView.loadUrl(CommonData.SIGNURL+uid);
    }

    @Override
    public void bindListener() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initDatas() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sign);
        initViews();
        initDatas();
        bindListener();
    }
}
