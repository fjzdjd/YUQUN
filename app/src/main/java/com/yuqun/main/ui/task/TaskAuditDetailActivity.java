package com.yuqun.main.ui.task;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.domain.GetOrderDetailInfo;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.utils.Utils;
import com.yuqun.main.view.PoPModifyPriceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zzp on 2016/3/15.
 */
public class TaskAuditDetailActivity extends BaseActivity implements Html.ImageGetter, View
        .OnClickListener {

    private TextView mCountdown;
    private TextView mEndDate;
    private TextView mExpectTime;
    private TextView mTxtHtml;
    private TextView mPerson1;
    private TextView mPerson2;
    private TextView mStandard;
    private TextView mPrice;
    private TextView mTitle;
    private String taskOID;

    /**
     * 标签集合
     */
    private List<Map<String, String>> mListTags = new ArrayList<>();
    private Map<String, String> mMapTags;

    /**
     * 任务详情信息
     */
    private Map<String, String> mMapgetOrderDetailInfoTaskInfo;

    /**
     * 定时器
     */
    Handler handler_time = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 001) {
                mCountdown.setText(Utils.getTimeDiff(mMapgetOrderDetailInfoTaskInfo.get
                        ("FinlishDate")));
            }

        }

        ;
    };

    /**
     * 任务接单流程
     */
    private TextView tv_audit_process;
    private ImageView img_process;

    /**
     * 任务详情信息
     */
    private List<GetOrderDetailInfo> mListGetOrderDetailInfo = new ArrayList<>();
    private GetOrderDetailInfo getOrderDetailInfo;
    private BitmapUtils bitmapUtils;
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
                            String str_pic = jsonObject1.getString("img");
                            bitmapUtils.display(img_process, str_pic);
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
    private PoPModifyPriceManager modifyPriceManager;
    private TextView mLink;
    private TextView mInsurance;
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
    private WebView mWebContent;
    private LinearLayout mLayoutInsurance;
    private LinearLayout mLytLink;
    /**
     * 获取详情信息
     */
    Handler handlerGetOrderDetailInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:


                    break;

                case CommonData.HTTP_HANDLE_SUCCESS:
                    if (msg.obj != null) {
                        String json = msg.obj.toString();
                        mListGetOrderDetailInfo = JsonUtil.parseFromJsonToList(json,
                                GetOrderDetailInfo.class);
                        getOrderDetailInfo = mListGetOrderDetailInfo.get(0);

                        try {
                            JSONArray mJSONArray = new JSONArray(JsonUtil.getDataJson(json));
                            JSONObject item = mJSONArray.getJSONObject(0);
                            JSONObject taskInfo = item.getJSONObject("TaskInfo");
                            String Title = taskInfo.optString("Title");
                            String Content = taskInfo.optString("Content");
                            String Link = taskInfo.optString("Link");
                            String FinlishStandard = taskInfo.optString("FinlishStandard");
                            String PriceStr = taskInfo.optString("PriceStr");
                            String FinlishDate = taskInfo.optString("FinlishDate");
                            String FinlishYMD = taskInfo.optString("FinlishYMD");
                            String ExpectedToTakeTimeStr = taskInfo.optString
                                    ("ExpectedToTakeTimeStr");
                            String TaskPersonLimit = taskInfo.optString("TaskPersonLimit");
                            String TaskAcceptNum = taskInfo.optString("TaskAcceptNum");
                            String DepositStr = taskInfo.optString("DepositStr");

                            mMapgetOrderDetailInfoTaskInfo = new HashMap<>();
                            mMapgetOrderDetailInfoTaskInfo.put("Title", Title);
                            mMapgetOrderDetailInfoTaskInfo.put("Content", Content);
                            mMapgetOrderDetailInfoTaskInfo.put("Link", Link);
                            mMapgetOrderDetailInfoTaskInfo.put("FinlishStandard", FinlishStandard);
                            mMapgetOrderDetailInfoTaskInfo.put("PriceStr", PriceStr);
                            mMapgetOrderDetailInfoTaskInfo.put("FinlishDate", FinlishDate);
                            mMapgetOrderDetailInfoTaskInfo.put("FinlishYMD", FinlishYMD);
                            mMapgetOrderDetailInfoTaskInfo.put("ExpectedToTakeTimeStr",
                                    ExpectedToTakeTimeStr);
                            mMapgetOrderDetailInfoTaskInfo.put("DepositStr", DepositStr);
                            mMapgetOrderDetailInfoTaskInfo.put("TaskPersonLimit", TaskPersonLimit);
                            mMapgetOrderDetailInfoTaskInfo.put("TaskAcceptNum", TaskAcceptNum);

                            JSONArray tagsJsonArray = taskInfo.optJSONArray("Tags");
                            for (int j = 0; j < tagsJsonArray.length(); j++) {
                                JSONObject tagItem = tagsJsonArray.getJSONObject(j);
                                String Name = tagItem.optString("Name");

                                mMapTags = new HashMap<>();

                                mMapTags.put("Name", Name);

                                mListTags.add(mMapTags);
                            }

                            setWidgetsData();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        setContentView(R.layout.activity_task_audit_detail);
        Intent intent = getIntent();
        taskOID = intent.getStringExtra("OID");
        bitmapUtils = new BitmapUtils(this);
        initViews();
        initDatas();
        bindListener();

    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = getResources().getDrawable(R.mipmap.ic_launcher);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        new LoadImage().execute(source, d);
        return d;
    }

    /**
     * 通过webview展示内容
     *
     * @param data
     */
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
//        mWebContent.addJavascriptInterface(this, "java2js");

        mWebContent.setWebViewClient(new WebViewClientHtml(data));

        mWebContent.loadData(data, "text/html; charset=UTF-8", null);


    }

    /**
     * 给组件赋值
     */
    private void setWidgetsData() {
        mTitle.setText(mMapgetOrderDetailInfoTaskInfo.get("Title"));
        mPrice.setText(mMapgetOrderDetailInfoTaskInfo.get("PriceStr"));
        mEndDate.setText(mMapgetOrderDetailInfoTaskInfo.get("FinlishYMD"));
        mExpectTime.setText("预计" + mMapgetOrderDetailInfoTaskInfo.get("ExpectedToTakeTimeStr")
                + "钟完成任务");
        mPerson1.setText(" " + mMapgetOrderDetailInfoTaskInfo.get("TaskAcceptNum") + "人" + " ");
        mPerson2.setText(" " + String.valueOf(
                Integer.parseInt(mMapgetOrderDetailInfoTaskInfo.get("TaskPersonLimit")) - Integer
                        .parseInt
                                (mMapgetOrderDetailInfoTaskInfo.get("TaskAcceptNum"))
        ) + " ");

        //当任务链接为空就不显示
        if (StringUtils.isEmpty(mMapgetOrderDetailInfoTaskInfo.get("Link"))) {
            mLytLink.setVisibility(View.GONE);
        } else {
            mLytLink.setVisibility(View.VISIBLE);
            mLink.setText(mMapgetOrderDetailInfoTaskInfo.get("Link"));
        }

        mStandard.setText(mMapgetOrderDetailInfoTaskInfo.get("FinlishStandard"));

//        Spanned spanned = Html.fromHtml(mMapgetOrderDetailInfoTaskInfo.get
//                ("Content"), TaskAuditDetailActivity.this, null);
//        mTxtHtml.setText(spanned);


        String headerHtml = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><meta" +
                " " +
                "name=\"viewport\" content=\"initial-scale=1, maximum-scale=1, user-scalable=no, " +
                "width=device-width\"><style>img{max-width:320px !important;max-height:480px;" +
                "}</style></head><body>";

        String footerHtml = "</body></html>";

        getHtmlCode(headerHtml + mMapgetOrderDetailInfoTaskInfo.get
                ("Content") + footerHtml);


        //下划线
        mInsuranceGuide.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);


        //任务保证金为空就不显示
        if (mMapgetOrderDetailInfoTaskInfo.get("DepositStr").equals("0")) {
            mLayoutInsurance.setVisibility(View.GONE);
        } else {
            mLayoutInsurance.setVisibility(View.VISIBLE);
            mInsurance.setText(Html.fromHtml("<font color=\"#ff9900\">" + "￥" +
                    mMapgetOrderDetailInfoTaskInfo.get("DepositStr") + "</font>"));
        }


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

    }

    @Override
    public void initViews() {
        mCountdown = (TextView) findViewById(R.id.task_audit_txt_countdown);
        mEndDate = (TextView) findViewById(R.id.task_audit_txt_endDate);
        mExpectTime = (TextView) findViewById(R.id.task_audit_txt_expectTime);
        mTxtHtml = (TextView) findViewById(R.id.task_audit_txt_html);
        mPerson1 = (TextView) findViewById(R.id.task_audit_txt_person1);
        mPerson2 = (TextView) findViewById(R.id.task_audit_txt_person2);
        mStandard = (TextView) findViewById(R.id.task_audit_txt_standard);
        mPrice = (TextView) findViewById(R.id.task_audit_txt_price);
        mLink = (TextView) findViewById(R.id.task_audit_detail_link);
        mTitle = (TextView) findViewById(R.id.task_audit_txt_title);
        mWebContent = (WebView) findViewById(R.id.task_audit_detail_webHtml);

        mLayoutInsurance = (LinearLayout) findViewById(R.id.task_audit_lyt_insurance);

        mInsurance = (TextView) findViewById(R.id.task_audit_detail_insurance);
        mInsuranceGuide = (TextView) findViewById(R.id.task_audit_detail_insurance_guide);
        mLytLink = (LinearLayout) findViewById(R.id.task_audit_detail_lyt_link);

        tv_audit_process = (TextView) findViewById(R.id.tv_audit_process);
        tv_audit_process.setOnClickListener(this);
        findViewById(R.id.task_audit_img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void bindListener() {
        mInsuranceGuide.setOnClickListener(this);
        mLink.setOnClickListener(this);

    }

    @Override
    public void initDatas() {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("orderID", taskOID);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetOrderDetailInfo, paramsMap,
                handlerGetOrderDetailInfo);

        //添加progressbar
        showWaitDialog(R.string.common_requesting);

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
                            Toast.makeText(TaskAuditDetailActivity.this, ED, Toast
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


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_audit_process:
                /*弹出任务接单流程*/
                setPopModifyWidth();
                break;

            //保证金提示框
            case R.id.task_audit_detail_insurance_guide:

                popDepositNotice();

                break;

            case R.id.task_audit_detail_link:
                if (!mMapgetOrderDetailInfoTaskInfo.get("Link").isEmpty()) {
                    Intent intent = new Intent();
                    intent.putExtra("weblink", mMapgetOrderDetailInfoTaskInfo.get("Link"));
                    intent.putExtra("title", mMapgetOrderDetailInfoTaskInfo.get("Title"));
                    activityManager.startNextActivity(intent, TaskLinkPreviewActivity.class);
                }

                break;
            default:
                break;
        }
    }

    /**
     * 保证金提示
     */
    private void popDepositNotice() {

        modifyPriceManager = new PoPModifyPriceManager();
        @SuppressWarnings("deprecation")
        int width = TaskAuditDetailActivity.this.getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = TaskAuditDetailActivity.this.getWindowManager().getDefaultDisplay()
                .getHeight();
        modifyPriceManager.init(TaskAuditDetailActivity.this, width, height,
                R.layout.task_all_detail_pop_deposit_layout);
        modifyPriceManager.showPopAllLocation(mInsuranceGuide, Gravity.CENTER, 0, 0);

        View depositNotice = modifyPriceManager.getView();

        mListViewDeposit = (ListView) depositNotice.findViewById(R.id
                .task_all_detail_list);

        DepositNoitceAdapter depositNoitceAdapter = new DepositNoitceAdapter();

        mListViewDeposit.setAdapter(depositNoitceAdapter);


    }

    /***
     * 设置pop
     */
    private void setPopModifyWidth() {
        modifyPriceManager = new PoPModifyPriceManager();
        @SuppressWarnings("deprecation")
        int width = TaskAuditDetailActivity.this.getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = TaskAuditDetailActivity.this.getWindowManager().getDefaultDisplay()
                .getHeight();
        modifyPriceManager.init(TaskAuditDetailActivity.this, width, height,
                R.layout.pop_process);
        modifyPriceManager.showPopAllLocation(tv_audit_process, Gravity.CENTER, 0, 0);
        initPopWidgets();
    }

    /*操作pop中的组件*/
    private void initPopWidgets() {
        View updataForm = modifyPriceManager.getView();
        img_process = (ImageView) updataForm.findViewById(R.id.img_process);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.getNoti, null,
                handlerGetNoti);
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

            View view = LayoutInflater.from(TaskAuditDetailActivity.this).inflate(R.layout
                    .task_all_detail_pop_deposit_item, null);

            TextView mQuestion = (TextView) view.findViewById(R.id.task_deposit_txt_question);
            TextView mAnswer = (TextView) view.findViewById(R.id.task_deposit_txt_answer);
            ImageView mDepositImg = (ImageView) view.findViewById(R.id.task_deposit_img);


            mQuestion.setText(mListMapGetDepost.get(position).get("Question"));
            mAnswer.setText(mListMapGetDepost.get(position).get("Answer"));

//            if (!StringUtils.isEmpty(mListMapGetDepost.get(position).get("img"))) {
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
//                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setBounds(0, 0, mTxtHtml.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);
                CharSequence t = mTxtHtml.getText();
                mTxtHtml.setText(t);
            }
        }
    }
}
