package com.yuqun.main.ui.task;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.component.CustomAutoChangeLine;
import com.yuqun.main.domain.GetDetail;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.ui.mine.AuthenticationActivity;
import com.yuqun.main.ui.money.MoneyChargeActivity;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.utils.Utils;
import com.yuqun.main.view.PoPModifyPriceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <P>可接受任务详情页</P>
 *
 * @author zzp
 */
public class TaskAllDetailActivity extends BaseActivity implements Html.ImageGetter {


    /**
     * 获取任务详情页数据
     */
    private List<GetDetail> mListGetDetail = new ArrayList<>();
    /**
     * 任务详情对象(可接任务)
     */
    private GetDetail getDetail;
    /**
     * 标题
     */
    private TextView mTitle;
    /**
     * 价格
     */
    private TextView mPriceStr;
    /**
     * 预计完成时间
     */
    private TextView mFinishTime;
    /**
     * 接单截止时间
     */
    private TextView mEndDate;
    /**
     * 倒计时
     */
    private TextView mCountdown;
    /**
     * 定时器
     */
    Handler handler_time = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 001) {
                mCountdown.setText(Utils.getTimeDiff(getDetail.getFinlishDate() + " " +
                        "00:00:00"));
            }

        }

        ;
    };
    private TextView task_all_toast_explain;
    private TextView mContent;
    private TextView mFinlishStandard;
    private TextView mLink;
    private TextView mPerson1;
    private TextView mPerson2;
    private Button mAcceptTask;
    private TextView mExplain;
    private CustomAutoChangeLine mTags;
    private String taskID;
    /**
     * 当前IP
     */
    private String currentIp;
    private PoPModifyPriceManager modifyPriceManager;
    /**
     * 弹出框提醒
     */
    private TextView mPopNotice;
    private Button mPopConfirm;
    private Button mPopCancel;
    private ImageView mPopImgRight;
    private Map<String, String> mOderID = new HashMap<>();
    /**
     * 提交任务
     */
    Handler handlerAcceptTask = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (msg.obj != null) {
                        String json = msg.obj.toString();
                        Toast.makeText(TaskAllDetailActivity.this, json, Toast.LENGTH_SHORT).show();

                        if (json.contains("余额不够")) {

                            activityManager.startNextActivity(MoneyChargeActivity.class);
                        }
                    }

                    break;

                case CommonData.HTTP_HANDLE_SUCCESS:
                    if (msg.obj != null) {
                        String json = msg.obj.toString();
                        try {
                            JSONObject mJsonObject = new JSONObject(json);
                            String OrderId = mJsonObject.optString("ED");

                            mOderID.put("OrderId", OrderId);
                            mPopImgRight.setVisibility(View.VISIBLE);
                            mPopNotice.setText("恭喜您,接单成功");
                            mPopConfirm.setText("去做任务");
                            mPopCancel.setText("返回列表");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    break;

                default:
                    break;
            }

        }


    };
    private BitmapUtils bitmapUtils;
    private ImageView img_process;
    /**
     * 任务接单说明
     */
    private String mTaskProcess;
    /**
     * 接单流程说明
     */
    Handler handlerGetNoti = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:

                    break;

                case CommonData.HTTP_HANDLE_SUCCESS:
                    if (msg.obj != null) {
                        String json = msg.obj.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONObject jsonObject1 = (JSONObject) jsonObject.getJSONArray("Data")
                                    .get(0);
                            mTaskProcess = jsonObject1.getString("img");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    };
    /**
     * 保证金
     */
    private TextView mInsurance;
    /**
     * 保证金说明
     */
    private TextView mInsuranceGuide;
    /**
     * 保证金问答
     */
    private Map<String, String> mMapGetDepost;
    /**
     * 保证金问答
     */
    private List<Map<String, String>> mListMapGetDepost = new ArrayList<>();

    private ListView mListViewDeposit;
    private String uid;
    private WebView mWebContent;
    private LinearLayout mLayoutInsurance;
    private LinearLayout mLytLink;
    private TextView mAuthentication;
    private LinearLayout mLytAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        setContentView(R.layout.activity_task_all_detail);
        //获取上界面传递的值TID
        Intent intent = getIntent();
        taskID = intent.getStringExtra("TID");
        uid = intent.getStringExtra("uid");
        getCurrentIp();//获取外网当前IP
        bitmapUtils = new BitmapUtils(this);
        //初始化数据 根据TID取值
        if (!taskID.isEmpty()) {
            getDetail(taskID, uid);
        }

        initDatas();
        initViews();
        bindListener();

    }

    /**
     * 获取当前IP
     */
    private void getCurrentIp() {

        new AsyncTask<Void, Void, String>() {

            String req = "http://1212.ip138.com/ic.asp";

            @Override
            protected String doInBackground(Void... params) {
                String response = null;
                try {
                    response = GetNetIp(req);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (StringUtils.isEmpty(s)) {
                    //备用获取IP方法
                    GetNetCurrentIP("http://pv.sohu.com/cityjson");

                } else {
                    currentIp = s.toString();
                }

            }

        }.execute();

    }

    /**
     * 获取外网IP
     *
     * @param url
     * @return
     */
    private String GetNetCurrentIP(final String url) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String response = GetNetIp2(url);
                return response;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (!StringUtils.isEmpty(s)) {
                    try {
                        JSONObject jsonObject = new JSONObject(s.toString());
                        currentIp = jsonObject.optString("cip");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    currentIp = Utils.getLocalIpAddress(getApplicationContext());
                }
            }
        }.execute();

        return null;
    }

    /**
     * 获取外网IP
     *
     * @param params
     * @return
     */
    private String GetNetIp2(String params) {
        URL infoUrl = null;
        InputStream inStream = null;
        try {
            infoUrl = new URL(params);
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream,
                        "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                //从反馈的结果中提取出IP地址
                int start = strber.indexOf("{");
                int end = strber.indexOf("}", start);
                line = strber.substring(start, end + 1);
                return line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取外网IP
     *
     * @param params
     * @return
     */
    private String GetNetIp(String params) {
        URL infoUrl = null;
        InputStream inStream = null;
        try {
            infoUrl = new URL(params);
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream,
                        "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                //从反馈的结果中提取出IP地址
                int start = strber.indexOf("[");
                int end = strber.indexOf("]", start + 1);
                line = strber.substring(start + 1, end);
                return line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 给组件赋值
     */
    private void setWidgetsData() {
        mTitle.setText(getDetail.getTitle());
        mPriceStr.setText(getDetail.getPriceStr());
        mFinishTime.setText("预计" + getDetail.getExpectedToTakeTimeStr() + "钟完成任务");
        mEndDate.setText(getDetail.getFinlishYMD());

//        Spanned spanned = Html.fromHtml(getDetail.getContent(), TaskAllDetailActivity.this, null);
//        mContent.setText(spanned);


        String headerHtml = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><meta" +
                " " +
                "name=\"viewport\" content=\"initial-scale=1, maximum-scale=1, user-scalable=no, " +
                "width=device-width\"><style>img{max-width:320px !important;max-height:480px;" +
                "}</style></head><body>";

        String footerHtml = "</body></html>";

        getHtmlCode(headerHtml + getDetail.getContent() + footerHtml);

        mFinlishStandard.setText(getDetail.getFinlishStandard());

        //如果任务链接为空 就隐藏
        if (StringUtils.isEmpty(getDetail.getLink())) {
            mLytLink.setVisibility(View.GONE);
        } else {
            mLytLink.setVisibility(View.VISIBLE);
            mLink.setText(getDetail.getLink());
        }

        mPerson1.setText(" " + getDetail.getTaskAcceptNum() + "人" + " ");
        mPerson2.setText(" " + String.valueOf(
                Integer.parseInt(getDetail.getTaskPersonLimit()) - Integer.parseInt
                        (getDetail.getTaskAcceptNum())
        ) + " ");

        //保证金为0不显示
        if (getDetail.getDepositStr().equals("0")) {
            mLayoutInsurance.setVisibility(View.GONE);
        } else {
            mLayoutInsurance.setVisibility(View.VISIBLE);
            mInsurance.setText(Html.fromHtml("<font color=\"#ff9900\">" + "￥" + getDetail
                    .getDepositStr() + "</font>"));
        }

        //下划线
        mInsuranceGuide.setText(Html.fromHtml("<u>" + "任务保证金说明" + "</u>"));

        // 定时器 隔一分钟刷新时间
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // 需要做的事:发送消息
                Message message = new Message();
                message.what = 001;
                handler_time.sendMessage(message);
            }
        };
        timer.schedule(task, 0, 1000); // 执行task,经过1s再次执行

        if (getDetail.getAuthLimit().equals("0")) {
            //0不需要认证，1需要认证
            mLytAuthentication.setVisibility(View.GONE);
        } else {
            if (SharePreferenceManager.getInstance().getString(CommonData.AUTH, "").equals("0")) {
                mLytAuthentication.setVisibility(View.VISIBLE);
            } else {
                mLytAuthentication.setVisibility(View.GONE);
            }


        }

    }


    @Override
    public void initViews() {
        mTitle = (TextView) findViewById(R.id.task_all_txt_title);
        mPriceStr = (TextView) findViewById(R.id.task_all_txt_price);
        mFinishTime = (TextView) findViewById(R.id.task_all_txt_finish_time);
        mEndDate = (TextView) findViewById(R.id.task_all_txt_end_date);
        mCountdown = (TextView) findViewById(R.id.task_all_txt_countdown);
        mContent = (TextView) findViewById(R.id.task_all_txt_detail_html);
        mFinlishStandard = (TextView) findViewById(R.id.task_all_standard_complete);
        mLink = (TextView) findViewById(R.id.task_all_detail_link);
        mPerson1 = (TextView) findViewById(R.id.task_all_detail_person1);
        mPerson2 = (TextView) findViewById(R.id.task_all_detail_person2);
        mAcceptTask = (Button) findViewById(R.id.task_all_btn_getOrder);
        mExplain = (TextView) findViewById(R.id.task_all_toast_explain);
        mTags = (CustomAutoChangeLine) findViewById(R.id.task_all_lyt_tags);
        mWebContent = (WebView) findViewById(R.id.task_all_detail_webHtml);
        mLayoutInsurance = (LinearLayout) findViewById(R.id.task_all_lyt_insurance);
        mLytLink = (LinearLayout) findViewById(R.id.task_all_detail_lyt_link);
        mInsurance = (TextView) findViewById(R.id.task_all_detail_insurance);
        mInsuranceGuide = (TextView) findViewById(R.id.task_all_detail_insurance_guide);

        mLytAuthentication = (LinearLayout) findViewById(R.id.task_all_detail_lyt_auth);

        findViewById(R.id.task_all_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskAllDetailActivity.this.finish();
            }
        });

        task_all_toast_explain = (TextView) findViewById(R.id.task_all_toast_explain);
        mAuthentication = (TextView) findViewById(R.id.task_all_detail_txt_auth);
        mAuthentication.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);


    }


    /**
     * 通过webview展示内容
     *
     * @param data
     */
    @SuppressLint("JavascriptInterface")
    private void getHtmlCode(String data) {

        WebSettings ws = mWebContent.getSettings();
        ws.setJavaScriptEnabled(true); // 设置支持javascript脚本
        ws.setAllowFileAccess(true); // 允许访问文件
        ws.setBuiltInZoomControls(true); // 设置显示缩放按钮
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setSupportZoom(true); // 支持缩放 <span style="color:#337fe5;"> /**
        // * 用WebView显示图片，可使用这个参数
        // * 设置网页布局类型：
        // * 1、LayoutAlgorithm.NARROW_COLUMNS ： 适应内容大小
        // * 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
        // */
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        ws.setDefaultTextEncodingName("utf-8"); // 设置文本编码
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);// 设置缓存模式</span>
        mWebContent.addJavascriptInterface(this, "java2js");

        mWebContent.setWebViewClient(new WebViewClientHtml(data));

        mWebContent.loadData(data, "text/html; charset=UTF-8", null);


    }

    /***
     * 接单流程
     */
    private void setPopModify() {
        modifyPriceManager = new PoPModifyPriceManager();
        @SuppressWarnings("deprecation")
        int width = TaskAllDetailActivity.this.getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = TaskAllDetailActivity.this.getWindowManager().getDefaultDisplay().getHeight();
        modifyPriceManager.init(TaskAllDetailActivity.this, width, height,
                R.layout.pop_process);
        modifyPriceManager.showPopAllLocation(task_all_toast_explain, Gravity.CENTER, 0, 0);

        initNoticePop();
    }

    /**
     * 保证金提示
     */
    private void popDepositNotice() {

        modifyPriceManager = new PoPModifyPriceManager();
        @SuppressWarnings("deprecation")
        int width = TaskAllDetailActivity.this.getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = TaskAllDetailActivity.this.getWindowManager().getDefaultDisplay().getHeight();
        modifyPriceManager.init(TaskAllDetailActivity.this, width, height,
                R.layout.task_all_detail_pop_deposit_layout);
        modifyPriceManager.showPopAllLocation(mInsuranceGuide, Gravity.CENTER, 0, 0);

        View depositNotice = modifyPriceManager.getView();

        mListViewDeposit = (ListView) depositNotice.findViewById(R.id
                .task_all_detail_list);

        DepositNoitceAdapter depositNoitceAdapter = new DepositNoitceAdapter();

        mListViewDeposit.setAdapter(depositNoitceAdapter);


    }

    /**
     * 提示操作
     */
    private void initNoticePop() {
        View updataForm = modifyPriceManager.getView();
        img_process = (ImageView) updataForm.findViewById(R.id.img_process);
        bitmapUtils.display(img_process, mTaskProcess);

    }

    @Override
    public void bindListener() {

        //接受任务
        mAcceptTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopModifyWidth();

//                测试充值
//                activityManager.startNextActivity(MoneyChargeActivity.class);
            }
        });

        // 查看任务内容
        mLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getDetail.getLink().isEmpty()) {
                    Intent intent = new Intent();
                    intent.putExtra("weblink", getDetail.getLink());
                    intent.putExtra("title", getDetail.getTitle());
                    activityManager.startNextActivity(intent, TaskLinkPreviewActivity.class);
                }
            }
        });

        //保证金说明
        mInsuranceGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDepositNotice();
            }
        });


        //接单流程
        task_all_toast_explain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*任务接单流程*/
                setPopModify();
            }
        });

        mAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转认证界面
                activityManager.startNextActivity(AuthenticationActivity.class);
            }
        });

    }

    /**
     * 立即抢单
     *
     * @param userID
     * @param tid
     * @param curIP
     */
    private void getAcceptTask(String userID, String tid, String curIP) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("userID", userID);
        paramsMap.put("tid", tid);
        paramsMap.put("curIP", curIP);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.AcceptTask, paramsMap,
                handlerAcceptTask);

    }

    /**
     * 获取任务详情
     * <P></P>
     *
     * @param taskID
     * @param uid
     */
    private void getDetail(String taskID, String uid) {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }

        //传递参数
        RequestParams params = new RequestParams();
        params.addBodyParameter("taskID", taskID);
        params.addBodyParameter("uid", uid);

        //使用xUtils网络请求框架
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, CommonData.SERVER_ADDRESS + IRequestAction
                        .GetDetail, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        //添加progressbar
                        showWaitDialog(R.string.common_upload_requesting);
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
                                String ED = jsonObject.optString("ED");
                                JSONArray jsonArray = jsonObject.optJSONArray("Data");

                                // 当前等级不够，此单不能抢
                                if (RC.equals("1")) {
                                    analysis(responseInfo, jsonArray);
                                    mAcceptTask.setText(ED);
                                    mAcceptTask.setBackground(getResources().getDrawable(R.drawable
                                            .corners_bg_gray));
                                    mAcceptTask.setClickable(false);

                                    //前订单已抢完
                                } else if (RC.equals("2")) {
                                    analysis(responseInfo, jsonArray);
                                    mAcceptTask.setText(ED);
                                    mAcceptTask.setBackground(getResources().getDrawable(R.drawable
                                            .corners_bg_gray));
                                    mAcceptTask.setClickable(false);

                                    //信誉值为零，不能抢单
                                } else if (RC.equals("3")) {

                                    analysis(responseInfo, jsonArray);
                                    mAcceptTask.setText(ED);
                                    mAcceptTask.setBackground(getResources().getDrawable(R.drawable
                                            .corners_bg_gray));
                                    mAcceptTask.setClickable(false);

                                    //立即抢单
                                } else if (RC.equals("4")) {
                                    analysis(responseInfo, jsonArray);
                                    mAcceptTask.setText(ED);

                                } else if (RC.equals("0")) {

                                    analysis(responseInfo, jsonArray);
                                    mAcceptTask.setText(ED);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("请求数据为空", responseInfo.result.toString());
                        }

                        dismissWaitDialog();

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dismissWaitDialog();
                    }


                });

    }

    /**
     * 解析数据
     *
     * @param responseInfo
     * @param jsonArray
     */
    private void analysis(ResponseInfo<String> responseInfo, JSONArray jsonArray) {
        mListGetDetail = JsonUtil.parseFromJsonToList(responseInfo.result.toString(), GetDetail
                .class);
        getDetail = mListGetDetail.get(0);
        //控制null报错
        if (!getDetail.getTID().isEmpty()) {
            setWidgetsData();
        }
    }

    @Override
    public void initDatas() {
        //网络请求
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, CommonData.SERVER_ADDRESS + IRequestAction
                .getDeposit, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseInfo.result
                                .toString());
                        String RC = jsonObject.optString("RC");
                        String ED = jsonObject.optString("ED");
                        JSONArray jsonArray = jsonObject.optJSONArray("Data");
                        // 当前等级不够，此单不能抢
                        if (RC.equals("1")) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonItem = jsonArray.getJSONObject(i);
                                String Question = jsonItem.optString("Queation");
                                String Answer = jsonItem.optString("Answer");
                                String img = jsonItem.optString("img");

                                mMapGetDepost = new HashMap<>();
                                mMapGetDepost.put("Question", Question);
                                mMapGetDepost.put("Answer", Answer);
                                mMapGetDepost.put("img", img);

                                mListMapGetDepost.add(mMapGetDepost);
                            }

                        } else {
                            //立即抢单
                            Toast.makeText(TaskAllDetailActivity.this, ED, Toast
                                    .LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("请求数据为空", responseInfo.result.toString());
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });

        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.getNoti, null,
                handlerGetNoti);

    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = getResources().getDrawable(R.mipmap.welcome);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        new LoadImage().execute(source, d);
        return d;
    }

    /***
     * 接任务
     */
    private void setPopModifyWidth() {
        modifyPriceManager = new PoPModifyPriceManager();
        @SuppressWarnings("deprecation")
        int width = TaskAllDetailActivity.this.getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = TaskAllDetailActivity.this.getWindowManager().getDefaultDisplay().getHeight();
        modifyPriceManager.init(TaskAllDetailActivity.this, width, height,
                R.layout.pop_accept_layout);
        modifyPriceManager.showPopAllLocation(mAcceptTask, Gravity.CENTER, 0, 0);
        initPopWidgets();
    }

    /**
     * 弹出框
     */
    private void initPopWidgets() {
        View updataForm = modifyPriceManager.getView();
        mPopNotice = (TextView) updataForm.findViewById(R.id.task_pop_all_notice);
        mPopNotice.setText(Html.fromHtml("友情提示:如果任务未完成,将会扣除<font color=\"#ff9900\">" + getDetail
                .getReleasePoint() + "</font>点信誉值"));
        mPopConfirm = (Button) updataForm.findViewById(R.id.task_pop_all_confirm);
        mPopImgRight = (ImageView) updataForm.findViewById(R.id.task_pop_all_img);
        mPopCancel = (Button) updataForm.findViewById(R.id.task_pop_all_cancel);

        /**
         * 取消
         */
        mPopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据界面文字 进行操作
                if (mPopCancel.getText().toString().equals("返回列表")) {
                    modifyPriceManager.dismissPop();
                    finish();
                } else {
                    modifyPriceManager.dismissPop();
                }

            }
        });

        /**
         * 接单
         */
        mPopConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopConfirm.getText().toString().equals("去做任务") && !mOderID.get("OrderId")
                        .isEmpty()) {

                    //抢单之后 任务执行的操作
                    Intent intent = new Intent();
                    intent.putExtra("OID", mOderID.get("OrderId"));
                    activityManager.startNextActivity(intent, TaskBeingDetailActivity.class);
                    modifyPriceManager.dismissPop();
                    finish();
                } else {
                    //立即抢单
                    getAcceptTask(SharePreferenceManager.getInstance().getString(CommonData
                            .USER_ID, ""), taskID, currentIp);
                }

            }
        });

    }

    /**
     * 自定义webview
     */
    private class WebViewClientHtml extends WebViewClient {

        String data;

        public WebViewClientHtml(String data) {
            this.data = data;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadData(data, "text/html", "utf-8");
            return true;
        }
    }

    /**
     * 保证金适配器
     */
    private class DepositNoitceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListMapGetDepost.size();
        }

        @Override
        public Object getItem(int position) {
            return mListMapGetDepost.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = LayoutInflater.from(TaskAllDetailActivity.this).inflate(R.layout
                    .task_all_detail_pop_deposit_item, null);

            TextView mQuestion = (TextView) view.findViewById(R.id.task_deposit_txt_question);
            TextView mAnswer = (TextView) view.findViewById(R.id.task_deposit_txt_answer);
            ImageView mDepositImg = (ImageView) view.findViewById(R.id.task_deposit_img);


            mQuestion.setText(mListMapGetDepost.get(position).get("Question"));
            mAnswer.setText(mListMapGetDepost.get(position).get("Answer"));

            //暂时没有说明图片,xutils提示url解析错误
//            if (!StringUtils.isEmpty(mListMapGetDepost.get(position).get("img"))){
//                bitmapUtils.display(mDepositImg, mListMapGetDepost.get(position).get("img"));
//            }

            return view;
        }
    }

    /**
     * 加载html网页图片
     */
    private class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            Log.d("html", "doInBackground " + source);
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                @SuppressWarnings("deprecation")
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);

                mDrawable.setBounds(0, 0, (bitmap.getWidth() / 3) * 5, (bitmap.getHeight() / 2) *
                        3);
//                mDrawable.setBounds(0, 0, mContent.getWidth(), bitmap.getHeight());

                mDrawable.setLevel(1);
                CharSequence t = mContent.getText();
                mContent.setText(t);
            }
        }
    }


}
