package com.yuqun.main.ui.task;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.component.PoPShareWindowManager;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.utils.PictureUtil;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.utils.Utils;
import com.yuqun.main.view.ProgressWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * 分享页面
 *
 * @author zzp
 */
public class TaskLinkPreviewActivity extends BaseActivity {

    private ProgressWebView mWebView;
    private String webLink;

    private TextView mShareWX;
    private LinearLayout mShareLayout;
    private TextView mTxtTitle;
    private String mTitle;
    private Map<String, String> shareData = new HashMap<>();
    private Bitmap wxBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        setContentView(R.layout.activity_task_link_preview);
        initDatas();
        initViews();


    }

    /**
     * 获取图片
     *
     * @param url 传入URL地址
     */
    private void getShareHtmlImg(final String url) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = getImageFromNet(url);

                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                if (bitmap == null) {

                    wxBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app);

                } else {
                    wxBitmap = PictureUtil.CompressImage(bitmap);
                }

                bindListener();

            }
        }.execute();
    }


    @Override
    public void initViews() {
        mWebView = (ProgressWebView) findViewById(R.id.task_link_web);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(webLink.trim());
        findViewById(R.id.task_share_link_img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTxtTitle = (TextView) findViewById(R.id.task_share_link_txt_title);
        mTxtTitle.setText(mTitle);
        mShareLayout = (LinearLayout) findViewById(R.id.task_share_layout);
        mShareWX = (TextView) findViewById(R.id.task_share_link_txt_wx);

    }

    @Override
    public void bindListener() {
        mShareWX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = shareData.get("title");
                String description = shareData.get("subsTitle");

                if (StringUtils.isEmpty(title)) {
                    title = mTitle;
                }
                if (StringUtils.isEmpty(description)) {
                    description = mTitle;

                }

                choiceShareWay(webLink, title, description, wxBitmap);

            }
        });
    }

    @Override
    public void initDatas() {

        Intent intent = getIntent();
        webLink = intent.getStringExtra("weblink");
        mTitle = intent.getStringExtra("title");

        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }

        //传递参数
        RequestParams params = new RequestParams();

        params.addBodyParameter("url", webLink);

        //使用xUtils网络请求框架
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, CommonData.SERVER_ADDRESS + IRequestAction
                        .getWebTitle, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onCancelled() {
                        super.onCancelled();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                        if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                            try {
                                JSONObject jsonObject = new JSONObject(responseInfo.result
                                        .toString());
                                String RC = jsonObject.optString("RC");
                                JSONArray jsonArray = jsonObject.optJSONArray("Data");
                                if (RC.equals("1")) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonItem = jsonArray.getJSONObject(i);
                                        String title = jsonItem.optString("title");
                                        String subTitle = jsonItem.optString("subTitle");
                                        String img = jsonItem.optString("img");

                                        shareData.put("title", title);
                                        shareData.put("subTitle", subTitle);
                                        shareData.put("img", img);
                                    }

                                } else {

                                    getHtmlContent(webLink);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("请求数据为空", responseInfo.result.toString());
                        }

                        if (StringUtils.isEmpty(shareData.get("img"))) {
                            wxBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app);
                        } else {
                            getShareHtmlImg(shareData.get("img"));
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }

                });

    }

    /**
     * 选择分享方式
     */
    private void choiceShareWay(final String linkUrl, final String mainTitle, final String
            subtitle, final Bitmap nailImg) {

        int width = getWindowManager().getDefaultDisplay().getWidth();
        PoPShareWindowManager.getInstance().init(getApplicationContext(), width, ViewGroup
                        .LayoutParams.WRAP_CONTENT,
                R.layout.pop_share_wechat);
        PoPShareWindowManager.getInstance().showPopAllLocation(mShareLayout, Gravity.CENTER |
                Gravity
                        .BOTTOM, 0, 0);


        //分享到微信
        PoPShareWindowManager.getInstance().OnClickWechat(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Utils.wechatShare(0, linkUrl, mainTitle, subtitle, nailImg);
                PoPShareWindowManager.getInstance().dismissPop();

            }
        });

        PoPShareWindowManager.getInstance().OnClick(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PoPShareWindowManager.getInstance().dismissPop();
            }
        });

        //分享到朋友圈
        PoPShareWindowManager.getInstance().OnClickWechatCircle(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Utils.wechatShare(1, linkUrl, mainTitle, subtitle, nailImg);
                PoPShareWindowManager.getInstance().dismissPop();

            }
        });
    }

    /**
     * 根据url连接取网络抓去图片返回
     *
     * @param url
     * @return url对应的图片
     */
    private Bitmap getImageFromNet(String url) {
        HttpURLConnection conn = null;
        try {
            URL mURL = new URL(url); // 创建一个url对象

            // 得到http的连接对象
            conn = (HttpURLConnection) mURL.openConnection();

            conn.setRequestMethod("GET");      // 设置请求方法为Get
            conn.setConnectTimeout(10000);     // 设置连接服务器的超时时间, 如果超过10秒钟, 没有连接成功, 会抛异常
            conn.setReadTimeout(5000);      // 设置读取数据时超时时间, 如果超过5秒, 抛异常

            conn.connect();      // 开始链接

            int responseCode = conn.getResponseCode(); // 得到服务器的响应码
            if (responseCode == 200) {
                // 访问成功
                InputStream is = conn.getInputStream();   // 获得服务器返回的流数据

                Bitmap bitmap = BitmapFactory.decodeStream(is); // 根据 流数据 创建一个bitmap位图对象

                return bitmap;
            } else {
                Log.i("******", "访问失败: responseCode = " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();       // 断开连接
            }
        }
        return null;
    }


    /**
     * 获取网页源码
     *
     * @param url 传入url地址
     */
    private void getHtmlContent(String url) {

        //网络请求
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (!StringUtils.isEmpty(responseInfo.result.toString())) {

                    String result = responseInfo.result.toString();
                    int url = result.indexOf("msg_cdn_url");
                    int url_cut1 = result.indexOf("=", url);
                    int url_cut2 = result.indexOf(";", url);
                    String img_url = result.substring(url_cut1 + 3, url_cut2 - 1);
                    shareData.put("img", img_url);
                } else {
                    Log.d("请求数据为空", responseInfo.result.toString());
                }

                if (StringUtils.isEmpty(shareData.get("img"))) {
                    wxBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app);
                } else {
                    getShareHtmlImg(shareData.get("img"));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });

    }


}





