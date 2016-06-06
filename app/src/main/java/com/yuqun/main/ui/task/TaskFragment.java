package com.yuqun.main.ui.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqun.main.R;
import com.yuqun.main.base.BaseFragment;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.component.CustomAutoChangeLineIndex;
import com.yuqun.main.component.refresh.PtrClassicFrameLayout;
import com.yuqun.main.component.refresh.PtrDefaultHandler;
import com.yuqun.main.component.refresh.PtrFrameLayout;
import com.yuqun.main.component.refresh.PtrHandler;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务
 * Created by zzp on 2016/3/9.
 */
public class TaskFragment extends BaseFragment implements View.OnClickListener {

    public static final String GET_RECEIVE_Task = "com.task.receive";
    /**
     * 判断在刷新时是否转动进度条
     */
    private static boolean refreshProgressBar = true;
    /**
     * 任务list
     */
    private List<Map<String, String>> mListTaskMap = new ArrayList<>();
    /**
     * 任务map
     */
    private Map<String, String> mTaskMap;
    private View mInflater;
    /**
     * 点击图片跳转 ”我的“界面
     */
    private ImageView mImgHeader;
    private RadioButton mRadioButtonTaskAll;
    private RadioButton mRadioButtonTaskOn;
    private RadioButton mRadioButtonTaskAudit;
    private RadioButton mRadioButtonTaskComplete;
    private RadioButton mRadioButtonTaskFail;
    private ListView mListviewItem;
    private PtrClassicFrameLayout ptrFrame;
    private AdapterMoreTask taskAdapter;
    /**
     * 页码
     */
    private int pageNum = 1;
    /**
     * 用户ID
     */
    private String userID;
    /**
     * 可接受
     */
    private boolean allFlag = true;
    /**
     * 审核
     */
    private boolean auditFlag = false;
    /**
     * 进行中
     */
    private boolean beingFlag = false;
    /**
     * 已完成
     */
    private boolean completeFlag = false;
    /**
     * 失败
     */
    private boolean failFlag = false;
    /**
     * 在任务进行总显示的头文字
     */
    private TextView mTxtBeing;
    private String mLoadMore;
    private String mWaiteCheckLoadMore;
    private String mCompleteLoadMore;
    private String mFailLoadMore;
    private String mUnAcceptLoadMore;
    private GetRefreshBroadcast mGetRefreshBroadcast;
    private View mListFooterView;
    private TextView mListLoadMore;
    private ProgressBar mListLoadMorePb;
    /**
     * 进行中2
     */
    Handler handlerMoreAcceptTask = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (mListTaskMap.isEmpty()) {
                        setOnItemClickFalse();
                        mTxtBeing.setVisibility(View.INVISIBLE);
                        taskAdapter.setListData(mListTaskMap);
                    }

                    mLoadMore = msg.obj.toString();
                    mListLoadMore.setText(mLoadMore);
                    mListLoadMore.setVisibility(View.VISIBLE);
                    mListLoadMorePb.setVisibility(View.GONE);


                    break;

                case CommonData.HTTP_HANDLE_SUCCESS:
                    setOnItemClickTrue();
                    if (msg.obj != null) {
                        receiveAndAnalysisData(msg);

                        //能导致 数据为空的背景出现
                        if (mListTaskMap.isEmpty()) {
                            mTxtBeing.setVisibility(View.INVISIBLE);
                        } else {
                            mTxtBeing.setVisibility(View.VISIBLE);
                        }

                    }
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 待审核3
     */
    Handler handlerMoreWaiteCheckTask = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (mListTaskMap.isEmpty()) {
                        setOnItemClickFalse();
                        taskAdapter.setListData(mListTaskMap);
                    }

                    mWaiteCheckLoadMore = msg.obj.toString();
                    mListLoadMore.setText(mWaiteCheckLoadMore);
                    mListLoadMore.setVisibility(View.VISIBLE);
                    mListLoadMorePb.setVisibility(View.GONE);


                    break;

                case CommonData.HTTP_HANDLE_SUCCESS:
                    setOnItemClickTrue();
                    if (msg.obj != null) {
                        receiveAndAnalysisData(msg);
                    }

                    break;
                default:
                    break;
            }

        }
    };
    /**
     * 已完成4
     */
    Handler handlerMoreFinlishTask = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:

                    if (mListTaskMap.isEmpty()) {
                        setOnItemClickFalse();
                        taskAdapter.setListData(mListTaskMap);
                    }

                    mCompleteLoadMore = msg.obj.toString();
                    mListLoadMore.setText(mCompleteLoadMore);
                    mListLoadMore.setVisibility(View.VISIBLE);
                    mListLoadMorePb.setVisibility(View.GONE);


                    break;

                case CommonData.HTTP_HANDLE_SUCCESS:
                    setOnItemClickTrue();
                    if (msg.obj != null) {
                        receiveAndAnalysisData(msg);

                    }
                    break;
                default:
                    break;
            }

        }
    };
    /**
     * 任务失败5
     */
    Handler handlerMoreFailedTask = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (mListTaskMap.isEmpty()) {
                        setOnItemClickFalse();
                        taskAdapter.setListData(mListTaskMap);
                    }

                    mFailLoadMore = msg.obj.toString();
                    mListLoadMore.setText(mFailLoadMore);
                    mListLoadMore.setVisibility(View.VISIBLE);
                    mListLoadMorePb.setVisibility(View.GONE);


                    break;

                case CommonData.HTTP_HANDLE_SUCCESS:
                    setOnItemClickTrue();
                    if (msg.obj != null) {
                        receiveAndAnalysisData(msg);

                    }

                    break;
                default:
                    break;
            }

        }
    };
    /**
     * 所有任务1
     */
    Handler handlerMoreUnAcceptTask = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (mListTaskMap.isEmpty()) {
                        setOnItemClickFalse();
                        taskAdapter.setListData(mListTaskMap);
                    }


                    mUnAcceptLoadMore = msg.obj.toString();

                    mListLoadMore.setText(mUnAcceptLoadMore);
                    mListLoadMore.setVisibility(View.VISIBLE);
                    mListLoadMorePb.setVisibility(View.GONE);


                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    setOnItemClickTrue();
                    if (msg.obj != null) {
                        analysisAllTaskDatas(msg);
                    }
                    break;
                default:
                    break;
            }

        }
    };
    /**
     * 判断加载更多，显示进度条
     */
    private boolean loadMoreDatas = true;

    /**
     * 解析所有任务
     *
     * @param msg 收到返回数据
     */
    private void analysisAllTaskDatas(Message msg) {
        String json = msg.obj.toString();
        try {
            JSONArray mJSONArray = new JSONArray(JsonUtil.getDataJson(json));

            for (int i = 0; i < mJSONArray.length(); i++) {
                JSONObject item = mJSONArray.getJSONObject(i);

                String TID = item.optString("TID");
                String Title = item.optString("Title");
                String PriceStr = item.optString("PriceStr");
                String TaskPersonLimit = item.optString("TaskPersonLimit");
                String TaskAcceptNum = item.optString("TaskAcceptNum");
                String TagsString = item.optString("TagsString");

                String ExpectedToTakeTimeStr = item.optString
                        ("ExpectedToTakeTimeStr");
                String taskIsnew = item.optString("taskIsnew");

                String remainTime = String.valueOf(Integer.parseInt
                        (TaskPersonLimit) - Integer.parseInt(TaskAcceptNum));

                mTaskMap = new HashMap<>();
                mTaskMap.put("TID", TID);
                mTaskMap.put("Title", Title);
                mTaskMap.put("PriceStr", PriceStr);
                mTaskMap.put("TaskPersonLimit", TaskPersonLimit);
                mTaskMap.put("TaskAcceptNum", TaskAcceptNum);
                mTaskMap.put("remainTime", remainTime);
                mTaskMap.put("TagsString", TagsString);
                mTaskMap.put("ExpectedToTakeTimeStr", ExpectedToTakeTimeStr);
                mTaskMap.put("taskIsnew", taskIsnew);
                mListTaskMap.add(mTaskMap);

            }

            mUnAcceptLoadMore = null;

            taskAdapter.setListData(mListTaskMap);

            //更改底部栏加载更多
            mListLoadMore.setVisibility(View.VISIBLE);
            mListLoadMorePb.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 接受除(所有任务)成功消息
     *
     * @param msg
     */
    private void receiveAndAnalysisData(Message msg) {
        String json = msg.obj.toString();
        try {
            JSONArray mJSONArray = new JSONArray(JsonUtil.getDataJson(json));

            for (int i = 0; i < mJSONArray.length(); i++) {
                JSONObject item = mJSONArray.getJSONObject(i);
                String OrderState = item.optString("OrderState");
                String OID = item.optString("OID");
                String OrderFinlishDate = item.optString("OrderFinlishDate");

                JSONObject TaskInfo = item.optJSONObject("TaskInfo");
                String TID = TaskInfo.optString("TID");
                String Title = TaskInfo.optString("Title");
                String PriceStr = TaskInfo.optString("PriceStr");
                String TaskPersonLimit = TaskInfo.optString("TaskPersonLimit");
                String TaskAcceptNum = TaskInfo.optString("TaskAcceptNum");
                String TagsString = TaskInfo.optString("TagsString");
                String ExpectedToTakeTimeStr = TaskInfo.optString
                        ("ExpectedToTakeTimeStr");
                String taskIsnew = TaskInfo.optString("taskIsnew");

                String remainTime = String.valueOf(Integer.parseInt
                        (TaskPersonLimit) - Integer.parseInt(TaskAcceptNum));

                mTaskMap = new HashMap<>();
                mTaskMap.put("OrderState", OrderState);
                mTaskMap.put("OID", OID);
                mTaskMap.put("TID", TID);
                mTaskMap.put("Title", Title);
                mTaskMap.put("PriceStr", PriceStr);
                mTaskMap.put("TaskPersonLimit", TaskPersonLimit);
                mTaskMap.put("TaskAcceptNum", TaskAcceptNum);
                mTaskMap.put("remainTime", remainTime);
                mTaskMap.put("TagsString", TagsString);
                mTaskMap.put("ExpectedToTakeTimeStr", ExpectedToTakeTimeStr);
                mTaskMap.put("taskIsnew", taskIsnew);
                mTaskMap.put("OrderFinlishDate", OrderFinlishDate);
                mListTaskMap.add(mTaskMap);

            }

            //控制下拉加载更多的提示弹出
            mLoadMore = null;
            mFailLoadMore = null;
            mCompleteLoadMore = null;
            mWaiteCheckLoadMore = null;

            taskAdapter.setListData(mListTaskMap);

            //更改底部栏加载更多
            mListLoadMore.setVisibility(View.VISIBLE);
            mListLoadMorePb.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将字符串 以逗号为拆分为array数组
     *
     * @param str 分拆数据
     * @return String[]
     */
    private String[] convertStrToArray(String str) {
        String[] strArray = null;
        strArray = str.split(",");
        return strArray;
    }

    /**
     * 清空点击事件
     */
    private void setOnItemClickFalse() {
        mListviewItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mInflater = inflater.inflate(R.layout.fragment_task_layout, container, false);

        userID = SharePreferenceManager.getInstance().getString(CommonData.USER_ID, "");

        registerRefreshBroadcast();

        initWidgets(mInflater);
        mTxtBeing.setVisibility(View.INVISIBLE);

        //初始化界面值 “可接受任务界面”
        MoreUnAcceptTask(String.valueOf(pageNum), userID, "MoreUnAcceptTask");

        //给adapter赋值
        taskAdapter = new AdapterMoreTask(getActivity(), mListTaskMap);
        mListviewItem.setAdapter(taskAdapter);

        refreshHandler();//初始化刷新


        return mInflater;
    }

    /**
     * 刷新
     */
    private void refreshHandler() {
        ptrFrame = (PtrClassicFrameLayout) mInflater.findViewById(R
                .id.task_refresh_all);
        ptrFrame.setLastUpdateTimeRelateObject(this);
        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //刷新时将页码重新设置为1
                        pageNum = 1;
                        refreshProgressBar = false;

                        if (allFlag == true) {
                            mListTaskMap.clear();
                            MoreUnAcceptTask(String.valueOf(pageNum), userID, "MoreUnAcceptTask");
                            mListLoadMore.setText("加载更多");
                        } else if (beingFlag == true) {
                            mListTaskMap.clear();
                            MoreAcceptTask(String.valueOf(pageNum), userID, "MoreAcceptTask");
                            mListLoadMore.setText("加载更多");
                        } else if (auditFlag == true) {
                            mListTaskMap.clear();
                            MoreWaiteCheckTask(String.valueOf(pageNum), userID,
                                    "MoreWaitCheckTask");
                            mListLoadMore.setText("加载更多");
                        } else if (completeFlag == true) {
                            mListTaskMap.clear();
                            MoreFinlishTask(String.valueOf(pageNum), userID, "MoreFinlishTask");
                            mListLoadMore.setText("加载更多");
                        } else if (failFlag == true) {
                            mListTaskMap.clear();
                            MoreFailedTask(String.valueOf(pageNum), userID, "MoreFailedTask");
                            mListLoadMore.setText("加载更多");
                        }

                        ptrFrame.refreshComplete();

                        refreshProgressBar = true;
                    }
                }, 0);
            }
        });

        // the following are default settings
        ptrFrame.setResistance(3.5f);
        ptrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        ptrFrame.setDurationToClose(200);
        ptrFrame.setDurationToCloseHeader(1000);
        // default is false
        ptrFrame.setPullToRefresh(false);
        // default is true
        ptrFrame.setKeepHeaderWhenRefresh(true);
    }

    /**
     * 所有任务
     *
     * @param page 页数
     * @param uid  用户id
     * @param type MoreUnAcceptTask
     */
    public void MoreUnAcceptTask(String page, String uid, String type) {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("page", page);
        paramsMap.put("uid", uid);
        paramsMap.put("type", type);

        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetCanAcceptTaskList, paramsMap,
                handlerMoreUnAcceptTask);

        if (refreshProgressBar && loadMoreDatas) {
            showWaitDialog(R.string.common_requesting);
        }


    }

    /**
     * 进行中
     *
     * @param page 页数
     * @param uid  用户id
     * @param type MoreAcceptTask
     */
    public void MoreAcceptTask(String page, String uid, String type) {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }
        // 获取购车需求所有数据
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("page", page);
        paramsMap.put("uid", uid);
        paramsMap.put("type", type);

        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetCanAcceptTaskList, paramsMap,
                handlerMoreAcceptTask);

        if (refreshProgressBar && loadMoreDatas) {
            showWaitDialog(R.string.common_requesting);
        }

    }

    /**
     * 待审核
     *
     * @param page 页码
     * @param uid  用户id
     * @param type MoreWaiteCheckTask
     */
    public void MoreWaiteCheckTask(String page, String uid, String type) {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }
        // 获取购车需求所有数据
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("page", page);
        paramsMap.put("uid", uid);
        paramsMap.put("type", type);

        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetCanAcceptTaskList, paramsMap,
                handlerMoreWaiteCheckTask);

        if (refreshProgressBar && loadMoreDatas) {
            showWaitDialog(R.string.common_requesting);
        }
    }

    /**
     * 已完成
     *
     * @param page 页码
     * @param uid  用户id
     * @param type MoreFinlishTask
     */
    public void MoreFinlishTask(String page, String uid, String type) {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }
        // 获取购车需求所有数据
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("page", page);
        paramsMap.put("uid", uid);
        paramsMap.put("type", type);

        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetCanAcceptTaskList, paramsMap,
                handlerMoreFinlishTask);

        if (refreshProgressBar && loadMoreDatas) {
            showWaitDialog(R.string.common_requesting);
        }

    }

    /**
     * 任务失败
     *
     * @param page 页码
     * @param uid  用户id
     * @param type MoreFinlishTask
     */
    public void MoreFailedTask(String page, String uid, String type) {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }
        // 获取购车需求所有数据
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("page", page);
        paramsMap.put("uid", uid);
        paramsMap.put("type", type);

        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetCanAcceptTaskList, paramsMap,
                handlerMoreFailedTask);

        if (refreshProgressBar && loadMoreDatas) {
            showWaitDialog(R.string.common_requesting);
        }


    }

    /**
     * 初始化ui组件
     *
     * @param v 布局
     */
    private void initWidgets(View v) {

        mImgHeader = (ImageView) v.findViewById(R.id.task_img_header);
        mRadioButtonTaskAll = (RadioButton) v.findViewById(R.id.task_rb_all);
        mRadioButtonTaskOn = (RadioButton) v.findViewById(R.id.task_rb_on);
        mRadioButtonTaskAudit = (RadioButton) v.findViewById(R.id.task_rb_audit);
        mRadioButtonTaskComplete = (RadioButton) v.findViewById(R.id.task_rb_complete);
        mRadioButtonTaskFail = (RadioButton) v.findViewById(R.id.task_rb_fail);
        mListviewItem = (ListView) v.findViewById(R.id.task_list_item);
        mTxtBeing = (TextView) v.findViewById(R.id.task_txt_being);

        //添加listview底部加载更多
        mListFooterView = LayoutInflater.from(getActivity()).inflate(R.layout
                .listview_footer_loadmore, null);

        mListLoadMore = (TextView) mListFooterView.findViewById(R.id.list_text_loadMore);

        mListLoadMorePb = (ProgressBar) mListFooterView.findViewById(R.id
                .list_progressBar_loadMore);

        mListviewItem.addFooterView(mListFooterView);

        mListFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listViewLoadMoreDatas();

            }
        });

        mImgHeader.setOnClickListener(this);
        mRadioButtonTaskAll.setOnClickListener(this);
        mRadioButtonTaskOn.setOnClickListener(this);
        mRadioButtonTaskAudit.setOnClickListener(this);
        mRadioButtonTaskComplete.setOnClickListener(this);
        mRadioButtonTaskFail.setOnClickListener(this);

        setOnItemClickTrue();


        mListviewItem.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //The view is not scrolling.
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            listViewLoadMoreDatas();

                        }
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }
        });

    }


    /**
     * listview加载更多
     */
    private void listViewLoadMoreDatas() {
        //添加加载更多底部栏
        loadMoreDatas = false;
        mListLoadMore.setVisibility(View.INVISIBLE);
        mListLoadMorePb.setVisibility(View.VISIBLE);

        ++pageNum;
        if (allFlag == true) {

            MoreUnAcceptTask(String.valueOf(pageNum), userID,
                    "MoreUnAcceptTask");

            if (!StringUtils.isEmpty(mUnAcceptLoadMore)) {
                Toast.makeText(getActivity(), mUnAcceptLoadMore, Toast
                        .LENGTH_SHORT).show();
            }
            mUnAcceptLoadMore = null;

        } else if (beingFlag == true) {

            MoreAcceptTask(String.valueOf(pageNum), userID, "MoreAcceptTask");

            if (!StringUtils.isEmpty(mLoadMore)) {
                Toast.makeText(getActivity(), mLoadMore, Toast.LENGTH_SHORT)
                        .show();
            }
            mLoadMore = null;
        } else if (auditFlag == true) {

            MoreWaiteCheckTask(String.valueOf(pageNum), userID,
                    "MoreWaitCheckTask");

            if (!StringUtils.isEmpty(mWaiteCheckLoadMore)) {
                Toast.makeText(getActivity(), mWaiteCheckLoadMore, Toast
                        .LENGTH_SHORT).show();

            }
            mWaiteCheckLoadMore = null;
        } else if (completeFlag == true) {

            MoreFinlishTask(String.valueOf(pageNum), userID, "MoreFinlishTask");

            if (!StringUtils.isEmpty(mCompleteLoadMore)) {
                Toast.makeText(getActivity(), mCompleteLoadMore, Toast
                        .LENGTH_SHORT).show();

            }
            mCompleteLoadMore = null;

        } else if (failFlag == true) {

            MoreFailedTask(String.valueOf(pageNum), userID, "MoreFailedTask");

            if (!StringUtils.isEmpty(mFailLoadMore)) {
                Toast.makeText(getActivity(), mFailLoadMore, Toast.LENGTH_SHORT)
                        .show();
            }
            mFailLoadMore = null;
        }

        loadMoreDatas = true;
    }

    /**
     * 重新赋值点击事件
     */
    private void setOnItemClickTrue() {
        mListviewItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                //根据不同的选择进入详情页
                if (allFlag == true && !mListTaskMap.get(position).get("TID").isEmpty()) {

                    intent = new Intent(getActivity(), TaskAllDetailActivity.class);
                    intent.putExtra("TID", mListTaskMap.get(position).get("TID"));
                    intent.putExtra("uid", userID);

                } else if (beingFlag == true && !mListTaskMap.get(position).get("OID").isEmpty()) {

                    intent = new Intent(getActivity(), TaskBeingDetailActivity.class);
                    intent.putExtra("OID", mListTaskMap.get(position).get("OID"));

                } else if (auditFlag == true && !mListTaskMap.get(position).get("OID").isEmpty()) {

                    intent = new Intent(getActivity(), TaskAuditDetailActivity.class);
                    intent.putExtra("OID", mListTaskMap.get(position).get("OID"));

                } else if (completeFlag == true && !mListTaskMap.get(position).get("OID").isEmpty
                        ()) {

                    intent = new Intent(getActivity(), TaskCompleteDetailActivity.class);
                    intent.putExtra("OID", mListTaskMap.get(position).get("OID"));

                } else if (failFlag == true && !mListTaskMap.get(position).get("OID").isEmpty()) {

                    intent = new Intent(getActivity(), TaskFailDetailActivity.class);
                    intent.putExtra("OID", mListTaskMap.get(position).get("OID"));

                } else {
                    Toast.makeText(getActivity(), "网络异常请稍后", Toast.LENGTH_SHORT).show();
                }

                startActivity(intent);
            }
        });
    }


    //左右两个选择要重新设置
    private void selecterLeftFocus() {
        mRadioButtonTaskAll.setTextColor(getResources().getColor(R.color.white));
        mRadioButtonTaskAll.setBackground(getResources().getDrawable(R.drawable
                .task_header_left_shape));
    }

    private void selecterRightFocus() {
        mRadioButtonTaskFail.setTextColor(getResources().getColor(R.color.white));
        mRadioButtonTaskFail.setBackground(getResources().getDrawable(R.drawable
                .task_header_right_shape));

    }

    private void selecterLeftLost() {
        mRadioButtonTaskAll.setTextColor(getResources().getColor(R.color.maincolor));
        mRadioButtonTaskAll.setBackground(getResources().getDrawable(R.drawable
                .task_header_left_shapef));
    }

    private void selecterRightLost() {
        mRadioButtonTaskFail.setTextColor(getResources().getColor(R.color.maincolor));
        mRadioButtonTaskFail.setBackground(getResources().getDrawable(R.drawable
                .task_header_right_shapef));
    }

    private void selecterComplete() {
        mRadioButtonTaskOn.setTextColor(getResources().getColor(R.color.maincolor));
        mRadioButtonTaskAudit.setTextColor(getResources().getColor(R.color.maincolor));
        mRadioButtonTaskComplete.setTextColor(getResources().getColor(R.color.white));

        //true
        completeFlag = true;

        allFlag = false;
        beingFlag = false;
        auditFlag = false;
        failFlag = false;

    }

    private void selecterAudit() {
        mRadioButtonTaskOn.setTextColor(getResources().getColor(R.color.maincolor));
        mRadioButtonTaskAudit.setTextColor(getResources().getColor(R.color.white));
        mRadioButtonTaskComplete.setTextColor(getResources().getColor(R.color.maincolor));

        //true
        auditFlag = true;

        allFlag = false;
        beingFlag = false;
        completeFlag = false;
        failFlag = false;

    }

    private void selecterOn() {
        mRadioButtonTaskOn.setTextColor(getResources().getColor(R.color.white));
        mRadioButtonTaskAudit.setTextColor(getResources().getColor(R.color.maincolor));
        mRadioButtonTaskComplete.setTextColor(getResources().getColor(R.color.maincolor));

        //进行为true
        beingFlag = true;

        allFlag = false;
        auditFlag = false;
        completeFlag = false;
        failFlag = false;

    }

    private void selecterAll() {
        mRadioButtonTaskOn.setTextColor(getResources().getColor(R.color.maincolor));
        mRadioButtonTaskAudit.setTextColor(getResources().getColor(R.color.maincolor));
        mRadioButtonTaskComplete.setTextColor(getResources().getColor(R.color.maincolor));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //所有任务
            case R.id.task_rb_all:
                selecterLeftFocus();
                selecterRightLost();
                selecterAll();
                allFlag = true;
                beingFlag = false;
                auditFlag = false;
                completeFlag = false;
                failFlag = false;

                mTxtBeing.setVisibility(View.INVISIBLE);
                mListTaskMap.clear();
                pageNum = 1;
                MoreUnAcceptTask(String.valueOf(pageNum), userID, "MoreUnAcceptTask");
                mListLoadMore.setText("加载更多");
                mUnAcceptLoadMore = null;
                break;

            //任务进行中
            case R.id.task_rb_on:
                selecterLeftLost();
                selecterRightLost();
                selecterOn();

                mListTaskMap.clear();

                pageNum = 1;
                MoreAcceptTask(String.valueOf(pageNum), userID, "MoreAcceptTask");
                mListLoadMore.setText("加载更多");
                mLoadMore = null;
                break;

            //审核任务
            case R.id.task_rb_audit:
                selecterLeftLost();
                selecterRightLost();
                selecterAudit();

                mTxtBeing.setVisibility(View.INVISIBLE);
                mListTaskMap.clear();
                pageNum = 1;
                MoreWaiteCheckTask(String.valueOf(pageNum), userID, "MoreWaitCheckTask");
                mListLoadMore.setText("加载更多");
                mWaiteCheckLoadMore = null;
                break;

            //任务完成
            case R.id.task_rb_complete:
                selecterLeftLost();
                selecterRightLost();
                selecterComplete();

                mTxtBeing.setVisibility(View.INVISIBLE);
                mListTaskMap.clear();
                pageNum = 1;
                MoreFinlishTask(String.valueOf(pageNum), userID, "MoreFinlishTask");
                mListLoadMore.setText("加载更多");
                mCompleteLoadMore = null;
                break;

            //任务失败
            case R.id.task_rb_fail:
                selecterLeftLost();
                selecterRightFocus();
                selecterAll();
                allFlag = false;
                beingFlag = false;
                auditFlag = false;
                completeFlag = false;
                failFlag = true;

                mTxtBeing.setVisibility(View.INVISIBLE);
                mListTaskMap.clear();
                pageNum = 1;
                MoreFailedTask(String.valueOf(pageNum), userID, "MoreFailedTask");
                mListLoadMore.setText("加载更多");
                mFailLoadMore = null;
                break;

            //点击头像跳转我的界面
            case R.id.task_img_header:

                //发广播
                Intent intent = new Intent();
                intent.setAction(GET_RECEIVE_Task);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                break;

            default:
                break;
        }

    }

    /**
     * 动态加载组件
     */
    private void dynamicInitWidegts(CustomAutoChangeLineIndex lyt, String[] text) {
        for (int i = 0; i < text.length; i++) {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(getActivity());
            textView.setBackgroundResource(R.drawable.task_corners_shape);
            textView.setLayoutParams(llp);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(11);
            textView.setText(" " + text[i].toString() + " ");
            textView.setTextColor(getResources().getColor(R.color.maincolor));
            lyt.addView(textView);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }


    /**
     * 注册广播
     */
    private void registerRefreshBroadcast() {
        mGetRefreshBroadcast = new GetRefreshBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(TaskBeingDetailActivity.mBroadcastRegistData);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mGetRefreshBroadcast,
                filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mGetRefreshBroadcast);

    }

    /**
     * 接受广播
     */
    private class GetRefreshBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("usingRoad");

            if (message.equals("beingDetail")) {

                mListTaskMap.clear();
                MoreAcceptTask("1", userID, "MoreAcceptTask");

            } else {

                mListTaskMap.clear();
                MoreFailedTask("1", userID, "MoreFailedTask");

            }

        }
    }


    /**
     * 任务适配器
     */
    private class AdapterMoreTask extends BaseAdapter {

        private List<Map<String, String>> listMap = new ArrayList<>();

        private LayoutInflater inflater;

        public AdapterMoreTask(Context context, List<Map<String, String>> listMap) {
            super();

            this.listMap = listMap;
            this.inflater = LayoutInflater.from(context);
        }

        private void setListData(List<Map<String, String>> listMap) {
            this.listMap = listMap;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (listMap.size() <= 0) {
                return 1;
            }
            return listMap.size();

        }

        @Override
        public Object getItem(int position) {
            return listMap.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewAllHolder holder = null;

            //如果数据为空
            if (listMap.size() <= 0) {
                mListFooterView.setVisibility(View.GONE);
                convertView = inflater.inflate(R.layout.task_adapter_list_empty, null);


            } else {
                mListFooterView.setVisibility(View.VISIBLE);
                //根据不同的选择匹配 adapter布局
                if (allFlag == true) {
                    convertView = inflater.inflate(R.layout.task_adapter_layout, null);
                } else if (beingFlag == true) {

                    convertView = inflater.inflate(R.layout.task_adapter_layout_being, null);
                } else if (auditFlag == true) {

                    convertView = inflater.inflate(R.layout.task_adapter_layout_audit, null);
                } else if (completeFlag == true) {

                    convertView = inflater.inflate(R.layout.task_adapter_layout_complete, null);
                } else if (failFlag == true) {

                    convertView = inflater.inflate(R.layout.task_adapter_layout_fail, null);
                }


                holder = new ViewAllHolder(convertView);

                holder.adapterTxtTitle.setText(listMap.get(position).get("Title").trim());
                holder.adapterTxtPrice.setText(listMap.get(position).get("PriceStr"));
                holder.adapterTxtTaskNum.setText(listMap.get(position).get("remainTime"));
                holder.adapterTxtTime.setText("预计" + listMap.get(position).get
                        ("ExpectedToTakeTimeStr") + "可完成任务");

                if (auditFlag == true) {
                    holder.adapterFinishDate.setText("上传时间:" + listMap.get(position).get
                            ("OrderFinlishDate") + "预计24H审核完");
                }

                //绑定数据
                if (!listMap.get(position).get
                        ("TagsString").isEmpty()) {
                    //分割字符串成数组
                    String[] arrayTags = convertStrToArray(listMap.get(position).get("TagsString")
                            .trim());
                    dynamicInitWidegts(holder.adapterTxtTags, arrayTags);
                }

                if (listMap.get(position).get("taskIsnew").toString().trim().equals("1")) {
                    holder.adapterImgIsNew.setVisibility(View.VISIBLE);
                }


            }
            return convertView;
        }
    }

    class ViewAllHolder {
        public TextView adapterTxtPrice;
        public TextView adapterTxtTaskNum;
        public CustomAutoChangeLineIndex adapterTxtTags;
        public TextView adapterTxtTitle;
        public TextView adapterTxtTime;
        public ImageView adapterImgIsNew;
        public TextView adapterFinishDate;

        public ViewAllHolder(View convertView) {


            adapterTxtPrice = (TextView) convertView.findViewById(R.id
                    .task_adapter_txt_price);

            adapterTxtTime = (TextView) convertView.findViewById(R.id
                    .task_adapter_txt_time);

            adapterTxtTaskNum = (TextView) convertView.findViewById(R.id
                    .task_adapter_txt_num);

            adapterTxtTags = (CustomAutoChangeLineIndex) convertView.findViewById(R.id
                    .task_adapter_lyt_tags);

            adapterTxtTitle = (TextView) convertView.findViewById(R.id
                    .task_adapter_txt_title);


            adapterImgIsNew = (ImageView) convertView.findViewById(R.id
                    .task_adapter_img_new);

            adapterFinishDate = (TextView) convertView.findViewById(R.id
                    .task_adapter_txt_finish_date);

        }

    }
}
