package com.yuqun.main.ui.task;

import android.content.Context;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.yuqun.main.domain.GetOrderDetailInfo;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.PictureUtil;
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

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * <P>任务进行中</P>
 * Created by zzp on 2016/3/15.
 */
public class TaskBeingDetailActivity extends BaseActivity implements Html.ImageGetter, View
        .OnClickListener {


    /**
     * 进行中
     */
    public static String mBroadcastRegistData = "beingDetailActivity";


    /**
     * 取消任务
     */
    Handler handlerTaskCancel = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (msg.obj != null) {
                        String json = msg.obj.toString();

                        Toast.makeText(TaskBeingDetailActivity.this, json, Toast.LENGTH_SHORT)
                                .show();

                    }
                    break;

                case CommonData.HTTP_HANDLE_SUCCESS:
                    if (msg.obj != null) {

                        //发送应用内广播
                        Intent intent = new Intent();
                        intent.setAction(mBroadcastRegistData);
                        intent.putExtra("usingRoad", "beingDetail");
                        LocalBroadcastManager.getInstance(TaskBeingDetailActivity.this)
                                .sendBroadcast(intent);

                        finish();
                    }
                    break;

                default:
                    break;
            }

        }


    };
    /**
     * 图片返回正确值
     */
    private int REQUEST_IMAGE = 2;
    private LinearLayout mUpLoadImg;
    private TextView mTxtStandard;
    private TextView mTxtCountdown;
    private TextView mTxtEndTime;
    private TextView mTxtFinishTime;
    private TextView mTxtHtml;
    private TextView mTxtGiveUp;
    /**
     * 接单人数
     */
    private TextView mTxtPerson1;
    /**
     * 完成人数
     */
    private TextView mTxtPerson2;
    private TextView mTxtPrice;
    private TextView mTxttitle;
    /**
     * orderID
     */
    private String taskOID;
    /**
     * 标签集合
     */
    private List<Map<String, String>> mListTags = new ArrayList<>();
    private Map<String, String> mMapTags;
    private Map<String, String> mMapgetOrderDetailInfoTaskInfo;
    /**
     * 定时器
     */
    Handler handler_time = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 001) {
                mTxtCountdown.setText(Utils.getTimeDiff(mMapgetOrderDetailInfoTaskInfo.get
                        ("FinlishDate")));
            }

        }
    };
    /**
     * 进行中任务数据集合
     */
    private List<GetOrderDetailInfo> mListGetOrderDetailInfo = new ArrayList<>();
    private GetOrderDetailInfo getOrderDetailInfo;
    /**
     * 任务取消弹出 框位置
     */
    private RelativeLayout mCancelTask;
    private TextView mPopNotice;
    /**
     * 确定
     */
    private Button mPopConfirm;
    private ImageView mPopImgRight;
    /**
     * 取消
     */
    private Button mPopCancel;
    private PoPModifyPriceManager modifyPriceManager;
    /**
     * 默认上传图片点击选择图片
     */
    private ImageView mImgDefault;
    /**
     * 上传图片的gridview
     */
    private GridView mGridViewImg;
    /**
     * 开始上传txt按钮
     */
    private TextView mTxtStartUpload;
    private ArrayList<String> mSelectPath;
    private BitmapUtils bitmapUtils;
    private ImageView mImgUpload1;
    private ImageView mImgUpload2;
    private ImageView mImgUpload3;
    private ImageView mImgUpload4;
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
    private LinearLayout mLinearFooter;
    private LinearLayout mLinearPreview;
    private LinearLayout mLinearPicture;
    private String[] mPreviewImgArray;
    /**
     * 得到返回图片
     */
    private boolean flagReturnPic = false;
    private WebView mWebContent;
    private LinearLayout mLayoutInsurance;
    private LinearLayout mLytLink;
    /**
     * 解析任务进行中数据
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

                        if (flagReturnPic) {

                            //以逗号为分隔截取图片
                            mPreviewImgArray = getOrderDetailInfo.getOrderBackPic().split(",");

                            for (int i = 0; i < mPreviewImgArray.length; i++) {
                                ImageView imageView = new ImageView(TaskBeingDetailActivity.this);
                                imageView.setPadding(10, 2, 10, 2);
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(150,
                                        200));

                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                                //查看图片，要求缩放
                                imageView.setOnClickListener(new ImageOnClickListener(i));

                                bitmapUtils.display(imageView, mPreviewImgArray[i]);
                                mLinearPicture.addView(imageView);

                            }
                        }

                        flagReturnPic = false;

                        try {
                            //获取data
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

                            //空值报错
                            if (!getOrderDetailInfo.getOID().isEmpty()) {
                                setWidgetsData();
                            }
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
     * @param v        视图
     * @param position 图片位置
     */
    public void setViewPagerAndZoom(View v, int position) {
        ViewPager expandedView = (ViewPager) findViewById(R.id.task_being_viewpager_banner_pic);

        //实现放大缩小类，传入当前的容器和要放大展示的对象
        ZoomBeingTutorial mZoomTutorial = new ZoomBeingTutorial(mCancelTask, expandedView);

        ViewPagerAdapter adapter = new ViewPagerAdapter(TaskBeingDetailActivity.this,
                mPreviewImgArray, mZoomTutorial);

        expandedView.setAdapter(adapter);
        expandedView.setCurrentItem(position);

        // 通过传入Id来从小图片扩展到大图，开始执行动画
        mZoomTutorial.zoomImageFromThumb(v);
        mZoomTutorial.setOnZoomListener(new ZoomBeingTutorial.OnZoomListener() {

            @Override
            public void onThumbed() {
                // TODO 自动生成的方法存根
                Log.d("现在是------>", " 小图状态");
            }

            @Override
            public void onExpanded() {
                // TODO 自动生成的方法存根
                Log.d("现在是------>", " 大图状态");
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        setContentView(R.layout.activity_task_being_detail);

        bitmapUtils = new BitmapUtils(this);
        Intent intent = getIntent();
        taskOID = intent.getStringExtra("OID");
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
     * 给组件赋值
     */
    private void setWidgetsData() {
        mTxttitle.setText(mMapgetOrderDetailInfoTaskInfo.get("Title"));
        mTxtStandard.setText(mMapgetOrderDetailInfoTaskInfo.get("FinlishStandard"));
        mTxtEndTime.setText(mMapgetOrderDetailInfoTaskInfo.get("FinlishYMD"));
        mTxtFinishTime.setText("预计" + mMapgetOrderDetailInfoTaskInfo.get("ExpectedToTakeTimeStr")
                + "钟完成任务");


        //当链接为空就不显示
        if (StringUtils.isEmpty(mMapgetOrderDetailInfoTaskInfo.get("Link"))) {
            mLytLink.setVisibility(View.GONE);
        } else {
            mLytLink.setVisibility(View.VISIBLE);
            mLink.setText(mMapgetOrderDetailInfoTaskInfo.get("Link"));
        }

        mTxtPrice.setText(mMapgetOrderDetailInfoTaskInfo.get("PriceStr"));

//        Spanned spanned = Html.fromHtml(mMapgetOrderDetailInfoTaskInfo.get("Content"),
//                TaskBeingDetailActivity.this, null);
//        mTxtHtml.setText(spanned);


        String headerHtml = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><meta" +
                " " +
                "name=\"viewport\" content=\"initial-scale=1, maximum-scale=1, user-scalable=no, " +
                "width=device-width\"><style>img{max-width:320px !important;max-height:480px;" +
                "}</style></head><body>";

        String footerHtml = "</body></html>";

        getHtmlCode(headerHtml + mMapgetOrderDetailInfoTaskInfo.get
                ("Content") + footerHtml);


        mTxtPerson1.setText(" " + mMapgetOrderDetailInfoTaskInfo.get("TaskAcceptNum") + "人" + " ");
        mTxtPerson2.setText(" " + String.valueOf(
                Integer.parseInt(mMapgetOrderDetailInfoTaskInfo.get("TaskPersonLimit")) - Integer
                        .parseInt
                                (mMapgetOrderDetailInfoTaskInfo.get("TaskAcceptNum"))
        ) + " ");

        //下划线
        mInsuranceGuide.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);


        //保证金为空就不显示
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
        mUpLoadImg = (LinearLayout) findViewById(R.id.task_being_lyt_uploadImg);
        mTxtStandard = (TextView) findViewById(R.id.task_being_txt_standard);
        mTxtCountdown = (TextView) findViewById(R.id.task_being_txt_countdown);
        mTxtEndTime = (TextView) findViewById(R.id.task_being_txt_endTime);
        mTxtFinishTime = (TextView) findViewById(R.id.task_being_txt_finishTime);
        mTxtHtml = (TextView) findViewById(R.id.task_being_txt_html);
        mTxtGiveUp = (TextView) findViewById(R.id.task_being_txt_giveUp);
        mTxtPerson1 = (TextView) findViewById(R.id.task_being_txt_person1);
        mTxtPerson2 = (TextView) findViewById(R.id.task_being_txt_person2);
        mTxtPrice = (TextView) findViewById(R.id.task_being_txt_price);
        mTxttitle = (TextView) findViewById(R.id.task_being_txt_title);
        mWebContent = (WebView) findViewById(R.id.task_being_detail_webHtml);
        mInsuranceGuide = (TextView) findViewById(R.id.task_being_detail_insurance_guide);
        mLayoutInsurance = (LinearLayout) findViewById(R.id.layout_task_being_detail_insurance);
        mLinearFooter = (LinearLayout) findViewById(R.id.task_being_lyt_footer);
        mLinearPreview = (LinearLayout) findViewById(R.id.task_being_lyt_footer_preview);
        mLinearPreview.setVisibility(View.GONE);
        mLinearPicture = (LinearLayout) findViewById(R.id.task_being_lyt_preview_picture);
        mInsurance = (TextView) findViewById(R.id.task_being_detail_insurance);
        mLink = (TextView) findViewById(R.id.task_being_detail_link);
        mCancelTask = (RelativeLayout) findViewById(R.id.task_being_rlt_cancel_task);
        mLytLink = (LinearLayout) findViewById(R.id.task_being_detail_lyt_link);

        findViewById(R.id.task_being_img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    @Override
    public void bindListener() {
        mTxtGiveUp.setOnClickListener(this);
        mUpLoadImg.setOnClickListener(this);
        mInsuranceGuide.setOnClickListener(this);
        mLink.setOnClickListener(this);
    }

    @Override
    public void initDatas() {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }
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
                            Toast.makeText(TaskBeingDetailActivity.this, ED, Toast
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

    /**
     * 取消任务根据 orderId
     *
     * @param orderID 上一个界面传递的订单号
     */
    private void TaskCancel(String orderID) {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("orderID", taskOID);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.TaskCancel, params,
                handlerTaskCancel);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //上传图片
            case R.id.task_being_lyt_uploadImg:

                //上传图片
                popSelectUploadImg();

                break;

            //取消任务
            case R.id.task_being_txt_giveUp:

                //取消任务
                setPopModifyWidth();

                break;

            //弹出保证金提示框
            case R.id.task_being_detail_insurance_guide:

                popDepositNotice();

                break;


            //跳转链接界面
            case R.id.task_being_detail_link:

                if (!mMapgetOrderDetailInfoTaskInfo.get("Link").isEmpty()) {
                    Intent intent = new Intent();
                    intent.putExtra("weblink", mMapgetOrderDetailInfoTaskInfo.get("Link"));
                    intent.putExtra("title", mMapgetOrderDetailInfoTaskInfo.get("Title"));
                    activityManager.startNextActivity(intent, TaskLinkPreviewActivity.class);
                }

                break;

            //点击图片到选择上传
            case R.id.task_being_upload_img1:

                choicePicFromGallary();

                break;

            //点击图片到选择上传
            case R.id.task_being_upload_img2:
                choicePicFromGallary();


                break;

            //点击图片到选择上传
            case R.id.task_being_upload_img3:

                choicePicFromGallary();

                break;

            //点击图片到选择上传
            case R.id.task_being_upload_img4:

                choicePicFromGallary();

                break;

            //点击图片到选择上传
            case R.id.task_being_upload_img0:

                choicePicFromGallary();

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
        int width = TaskBeingDetailActivity.this.getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = TaskBeingDetailActivity.this.getWindowManager().getDefaultDisplay()
                .getHeight();
        modifyPriceManager.init(TaskBeingDetailActivity.this, width, height,
                R.layout.task_all_detail_pop_deposit_layout);
        modifyPriceManager.showPopAllLocation(mInsuranceGuide, Gravity.CENTER, 0, 0);

        View depositNotice = modifyPriceManager.getView();

        mListViewDeposit = (ListView) depositNotice.findViewById(R.id
                .task_all_detail_list);

        DepositNoitceAdapter depositNoitceAdapter = new DepositNoitceAdapter();

        mListViewDeposit.setAdapter(depositNoitceAdapter);


    }

    /***
     * 上传任务截图
     */
    private void popSelectUploadImg() {

        modifyPriceManager = new PoPModifyPriceManager();

        @SuppressWarnings("deprecation")
        int width = TaskBeingDetailActivity.this.getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = TaskBeingDetailActivity.this.getWindowManager().getDefaultDisplay()
                .getHeight();
        modifyPriceManager.init(TaskBeingDetailActivity.this, width, height,
                R.layout.task_being_pop_select_pic_layout);
        modifyPriceManager.showPopAllLocation(mCancelTask, Gravity.CENTER, 0, 0);
        initPopSelectWidgets();
    }

    /**
     * 初始化上传图片窗口
     */
    private void initPopSelectWidgets() {

        View PopUploadImg = modifyPriceManager.getView();

        mImgDefault = (ImageView) PopUploadImg.findViewById(R.id
                .task_being_upload_img0);
        mGridViewImg = (GridView) PopUploadImg.findViewById(R.id
                .task_being_select_gridView);
        mTxtStartUpload = (TextView) PopUploadImg.findViewById(R.id
                .task_being_select_txt_start_upload);

        mImgUpload1 = (ImageView) PopUploadImg.findViewById(R.id.task_being_upload_img1);
        mImgUpload2 = (ImageView) PopUploadImg.findViewById(R.id.task_being_upload_img2);
        mImgUpload3 = (ImageView) PopUploadImg.findViewById(R.id.task_being_upload_img3);
        mImgUpload4 = (ImageView) PopUploadImg.findViewById(R.id.task_being_upload_img4);

        mImgDefault.setOnClickListener(this);

        mImgUpload1.setOnClickListener(this);
        mImgUpload2.setOnClickListener(this);
        mImgUpload3.setOnClickListener(this);
        mImgUpload4.setOnClickListener(this);


        mTxtStartUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectPath != null) {

                    if (mSelectPath.size() > 0) {
                        //根据选择的图片确定上传
                        if (mSelectPath.size() == 1) {

                            uploadImageView(getOrderDetailInfo.getOID(), getOrderDetailInfo
                                            .getTID(),
                                    getOrderDetailInfo.getUserID(), getOrderDetailInfo
                                            .getOrderState(),
                                    PictureUtil.bitmapToString(mSelectPath.get(0)));

                        } else if (mSelectPath.size() == 2) {

                            uploadImageView(getOrderDetailInfo.getOID(), getOrderDetailInfo
                                            .getTID(),
                                    getOrderDetailInfo.getUserID(), getOrderDetailInfo
                                            .getOrderState(),
                                    PictureUtil.bitmapToString(mSelectPath.get(0)) + "|" +
                                            PictureUtil.bitmapToString(mSelectPath.get(1)));

                        } else if (mSelectPath.size() == 3) {

                            uploadImageView(getOrderDetailInfo.getOID(), getOrderDetailInfo
                                            .getTID(),
                                    getOrderDetailInfo.getUserID(), getOrderDetailInfo
                                            .getOrderState(),
                                    PictureUtil.bitmapToString(mSelectPath.get(0)) + "|" +
                                            PictureUtil.bitmapToString(mSelectPath.get(1)) + "|" +
                                            PictureUtil.bitmapToString(mSelectPath.get(2)));

                        } else if (mSelectPath.size() == 4) {

                            uploadImageView(getOrderDetailInfo.getOID(), getOrderDetailInfo
                                            .getTID(),
                                    getOrderDetailInfo.getUserID(), getOrderDetailInfo
                                            .getOrderState(),
                                    PictureUtil.bitmapToString(mSelectPath.get(0)) + "|" +
                                            PictureUtil.bitmapToString(mSelectPath.get(1)) + "|" +
                                            PictureUtil.bitmapToString(mSelectPath.get(2)) + "|" +
                                            PictureUtil.bitmapToString(mSelectPath.get(3)));

                        }
                    } else {
                        Toast.makeText(TaskBeingDetailActivity.this, "请选择图片", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(TaskBeingDetailActivity.this, "请选择图片", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    /**
     * 从图库选择图片上传
     */
    private void choicePicFromGallary() {
        int selectedMode = MultiImageSelectorActivity.MODE_MULTI;

        int maxNum = 4;

        boolean showCamera = true;

        Intent intent = new Intent(TaskBeingDetailActivity.this,
                MultiImageSelectorActivity.class);

        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // 默认选择
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST,
                    mSelectPath);
        }

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    /**
     * 上传图片
     *
     * @param oid        订单id
     * @param tid        任务id
     * @param uid        用户id
     * @param orderstate 用户状态
     * @param base64     图片集合
     */
    private void uploadImageView(final String oid, final String tid, final String uid, final
    String orderstate, final String base64) {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }
        //传递参数
        RequestParams params = new RequestParams();
        params.addBodyParameter("oid", oid);
        params.addBodyParameter("tid", tid);
        params.addBodyParameter("uid", uid);
        params.addBodyParameter("orderstate", orderstate);
        params.addBodyParameter("base64", base64);

        //使用xUtils网络请求框架
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, CommonData.SERVER_ADDRESS + IRequestAction
                        .TaskOrderFinlishForApp, params,
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
                        dismissWaitDialog();

                        //与微信站一致，上传完成不跳转审核界面，在原界面进行预览
                        //Intent intent = new Intent();
                        //intent.putExtra("OID", getOrderDetailInfo.getOID());
                        //activityManager.startNextActivity(intent, TaskAuditDetailActivity.class);
                        //TaskBeingDetailActivity.this.finish();


                        if (StringUtils.isEmpty(responseInfo.result.toString())) {

                            //返回为空不处理

                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(responseInfo.result
                                        .toString());
                                String RC = jsonObject.optString("RC");
                                String ED = jsonObject.optString("ED");

                                if (RC.equals("1")) {

                                    mLinearPreview.setVisibility(View.VISIBLE);

                                    flagReturnPic = true;

                                    //上传成功重新请求接口返回图片
                                    initDatas();

                                    //关闭当前弹出框
                                    modifyPriceManager.dismissPop();

                                    //隐藏底部栏布局
                                    mLinearFooter.setVisibility(View.GONE);
                                    mUpLoadImg.setVisibility(View.GONE);

                                    //发送应用内广播通知主页面刷新
                                    Intent intent = new Intent();
                                    intent.setAction(mBroadcastRegistData);
                                    intent.putExtra("usingRoad", "beingDetail");
                                    LocalBroadcastManager.getInstance(TaskBeingDetailActivity.this)
                                            .sendBroadcast(intent);


                                } else {
                                    Toast.makeText(TaskBeingDetailActivity.this, ED, Toast
                                            .LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dismissWaitDialog();
                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);


                if (mSelectPath.size() == 1) {
                    mImgUpload1.setVisibility(View.VISIBLE);
                    bitmapUtils.display(mImgUpload1, mSelectPath.get(0));
                    bitmapUtils.display(mImgUpload2, null);
                    bitmapUtils.display(mImgUpload3, null);
                    bitmapUtils.display(mImgUpload4, null);

                } else if (mSelectPath.size() == 2) {
                    mImgUpload1.setVisibility(View.VISIBLE);
                    mImgUpload2.setVisibility(View.VISIBLE);

                    bitmapUtils.display(mImgUpload1, mSelectPath.get(0));
                    bitmapUtils.display(mImgUpload2, mSelectPath.get(1));
                    bitmapUtils.display(mImgUpload3, null);
                    bitmapUtils.display(mImgUpload4, null);

                } else if (mSelectPath.size() == 3) {
                    mImgUpload1.setVisibility(View.VISIBLE);
                    mImgUpload2.setVisibility(View.VISIBLE);
                    mImgUpload3.setVisibility(View.VISIBLE);

                    bitmapUtils.display(mImgUpload1, mSelectPath.get(0));
                    bitmapUtils.display(mImgUpload2, mSelectPath.get(1));
                    bitmapUtils.display(mImgUpload3, mSelectPath.get(2));
                    bitmapUtils.display(mImgUpload4, null);

                } else if (mSelectPath.size() == 4) {
                    mImgUpload1.setVisibility(View.VISIBLE);
                    mImgUpload2.setVisibility(View.VISIBLE);
                    mImgUpload3.setVisibility(View.VISIBLE);
                    mImgUpload4.setVisibility(View.VISIBLE);

                    bitmapUtils.display(mImgUpload1, mSelectPath.get(0));
                    bitmapUtils.display(mImgUpload2, mSelectPath.get(1));
                    bitmapUtils.display(mImgUpload3, mSelectPath.get(2));
                    bitmapUtils.display(mImgUpload4, mSelectPath.get(3));
                }

//                Toast.makeText(this, mSelectPath.toString(), Toast.LENGTH_SHORT).show();


                //第一版上传4张照片暂时不用
//                mGridViewImg.setAdapter(new GridViewAdapter(TaskBeingDetailActivity.this,
//                        mSelectPath));
            }
        }


    }

    /***
     * 取消任务
     */
    private void setPopModifyWidth() {
        modifyPriceManager = new PoPModifyPriceManager();
        @SuppressWarnings("deprecation")
        int width = TaskBeingDetailActivity.this.getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = TaskBeingDetailActivity.this.getWindowManager().getDefaultDisplay()
                .getHeight();
        modifyPriceManager.init(TaskBeingDetailActivity.this, width, height,
                R.layout.pop_accept_layout);
        modifyPriceManager.showPopAllLocation(mCancelTask, Gravity.CENTER, 0, 0);
        initPopWidgets();
    }

    /**
     * 取消任务弹出框
     */
    private void initPopWidgets() {
        View cancelTaskPop = modifyPriceManager.getView();
        mPopNotice = (TextView) cancelTaskPop.findViewById(R.id.task_pop_all_notice);
        mPopNotice.setText(Html.fromHtml("友情提示:放弃任务将不扣除信誉值!"));
        mPopConfirm = (Button) cancelTaskPop.findViewById(R.id.task_pop_all_confirm);
        mPopImgRight = (ImageView) cancelTaskPop.findViewById(R.id.task_pop_all_img);
        mPopCancel = (Button) cancelTaskPop.findViewById(R.id.task_pop_all_cancel);

        mPopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                modifyPriceManager.dismissPop();

            }
        });

        mPopConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消任务
                TaskCancel(taskOID);
                modifyPriceManager.dismissPop();

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
     * 给图片循环设置点击事件
     */
    private class ImageOnClickListener implements View.OnClickListener {

        private int i;

        public ImageOnClickListener(int i) {
            this.i = i;

        }

        @Override
        public void onClick(View v) {
            setViewPagerAndZoom(v, i);
        }
    }

    /**
     * @author zzp
     *         create by 2016/3/30
     *         图片点击放大适配器
     */
    private class ViewPagerAdapter extends PagerAdapter {

        private String[] mpicarray;
        private Context mContext;
        private ZoomBeingTutorial mZoomTutorial;


        public ViewPagerAdapter(Context context, String[] imgIds, ZoomBeingTutorial zoomTutorial) {
            this.mpicarray = imgIds;
            this.mContext = context;
            this.mZoomTutorial = zoomTutorial;
        }

        @Override
        public int getCount() {
            return mpicarray.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {

            final ImageView imageView = new ImageView(mContext);

            bitmapUtils.display(imageView, mpicarray[position]);

            container.addView(imageView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager
                    .LayoutParams.MATCH_PARENT);

            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mZoomTutorial.closeZoomAnim(position);
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
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

            View view = LayoutInflater.from(TaskBeingDetailActivity.this).inflate(R.layout
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
                // mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());//初始值
                mDrawable.setBounds(0, 0, mTxtHtml.getWidth(), bitmap.getHeight());//修改后的值
                mDrawable.setLevel(1);
                CharSequence t = mTxtHtml.getText();
                mTxtHtml.setText(t);
            }
        }
    }


}