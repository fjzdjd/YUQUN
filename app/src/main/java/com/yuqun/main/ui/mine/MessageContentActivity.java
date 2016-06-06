package com.yuqun.main.ui.mine;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.view.ProgressWebView;

/**
 * 消息公告内容也页
 * Created by Administrator on 2016/3/28.
 */
public class MessageContentActivity extends BaseActivity {
    private ProgressWebView mView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notify_content);
        initViews();
        initDatas();
        bindListener();
    }

    @Override
    public void initViews() {
        url = getIntent().getStringExtra("URL");
        mView = (ProgressWebView) findViewById(R.id.webViewId);
        mView.getSettings().setJavaScriptEnabled(true);
        mView.loadUrl(url);
        mView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
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
}
