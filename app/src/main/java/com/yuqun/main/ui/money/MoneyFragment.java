package com.yuqun.main.ui.money;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.yuqun.main.R;
import com.yuqun.main.app.YuQunApplication;
import com.yuqun.main.base.BaseFragment;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.component.refresh.PtrClassicFrameLayout;
import com.yuqun.main.component.refresh.PtrDefaultHandler;
import com.yuqun.main.component.refresh.PtrFrameLayout;
import com.yuqun.main.component.refresh.PtrHandler;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;

import com.yuqun.main.ui.adapter.FoundQuestionAdapter;
import com.yuqun.main.ui.adapter.FoundsAdapter;
import com.yuqun.main.ui.model.FoundsModel;
import com.yuqun.main.ui.model.QuestionModel;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.view.PoPModifyPriceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 奖金
 * Created by admin on 2016/3/9.
 */
public class MoneyFragment extends BaseFragment implements View.OnClickListener {
    /*提现问答*/
    private TextView tv_ask_tixian;

    private View mInflater;
    /*奖金流水*/
    private ListView lv_money;
    private TextView tv_founds_yes, tv_founds_leiji, tv_founds_my;
    private int currentPage = 1;

    private List<FoundsModel> mFoundsList = new ArrayList<>();

    private FoundsAdapter mFoundsAdapter;
    private List<QuestionModel> mQuestionList;

    private ListView lv_question;
    private PicMessageReceiver picMessageReceiver;
    /*微信绑定成功发出的广播*/
    private WXReceiver wxReceiver;
    private EditText et_money;
    private TextView tv_cash;
    private String wxId, wxPic;
    private PtrClassicFrameLayout ptrFrame;
    private android.os.Handler handler_yes = new android.os.Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    /*当下一页数据为空时，后台返回错误（即RC:0）*/
//                    if (null != msg.obj) {
//                        Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
//                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray = jsonObject.getJSONArray("Data");
                        JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);
                        String money_yes = jsonObject1.getString("yesterdayMoney");
                        tv_founds_my.setText("￥" + jsonObject1.getString("myMoney"));
                        tv_founds_leiji.setText(jsonObject1.getString("leiJiMoney"));
                        tv_founds_yes.setText(money_yes);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    /*奖金流水handler*/
    private android.os.Handler handler_founds = new android.os.Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    if (mFoundsAdapter == null) {
                        mFoundsAdapter = new FoundsAdapter(mFoundsList, getActivity());
                        lv_money.setAdapter(mFoundsAdapter);
                    } else {
                        mFoundsAdapter.notifyDataSetChanged();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    List<FoundsModel> list = JsonUtil.parseFromJsonToList(json, FoundsModel.class);
                    if(currentPage==1){
                        mFoundsList.clear();
                    }
                    mFoundsList.addAll(list);
                    if (mFoundsAdapter == null) {
                        mFoundsAdapter = new FoundsAdapter(mFoundsList, getActivity());
                        lv_money.setAdapter(mFoundsAdapter);
                    } else {
                        mFoundsAdapter.notifyDataSetChanged();
                    }
                    // 监听listview滚到最底部
                    lv_money.setOnScrollListener(new OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            switch (scrollState) {
                                // 当不滚动时
                                case OnScrollListener.SCROLL_STATE_IDLE:
                                    // 判断滚动到底部
                                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                                        ++currentPage;
                                        getFundsList(currentPage);
                                    }
                                    break;
                            }
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem,
                                             int visibleItemCount, int totalItemCount) {
                        }
                    });
                    break;
            }
        }
    };

    private android.os.Handler handler_question = new android.os.Handler() {


        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    mQuestionList = JsonUtil.parseFromJsonToList(json, QuestionModel.class);
                    lv_question.setAdapter(new FoundQuestionAdapter(mQuestionList, getActivity()));
                    break;
            }
        }
    };

    private android.os.Handler handler_reflect = new android.os.Handler() {


        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        dismissWaitDialog();
//                        if (msg.obj.equals("openid和appid不匹配")) {
//                            CommonData.isShare = false;
//                            sendLogInReq();
//                        } else {
                            Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
//                        }
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    dismissWaitDialog();
                    String json = msg.obj.toString();
                    Toast.makeText(getActivity(), "提现成功", Toast.LENGTH_SHORT).show();
                    //提现成功之后刷新当前页面的金额和流水
                    currentPage = 1;
                    initData();
                    break;
            }
        }
    };
    private PoPModifyPriceManager modifyPriceManager;
    private String userId, userTel;
    private String money;
    private String unionId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mInflater = inflater.inflate(R.layout.fragment_money_layout, container, false);
        initView();
        initData();
        return mInflater;
    }


    @Override
    public void onResume() {
        super.onResume();
        lv_money.setSelection(0);
    }

    private void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", userId);
        /*我的奖金数目*/
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetMoneyDetail, map, handler_yes);
        getFundsList(currentPage);
    }

    /*获取奖金流水*/
    private void getFundsList(int curPage) {
        HashMap<String, String> map = new HashMap<>();
        map.put("pageSize", CommonData.PageSize);
        map.put("curPage", curPage + "");
        String userId = SharePreferenceManager.getInstance().getString(CommonData.USER_ID, "");
        map.put("userID", userId);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetFoundsList, map, handler_founds);
    }

    private void initView() {
        wxId = SharePreferenceManager.getInstance().getString(CommonData.WX_ID, "");
        unionId = SharePreferenceManager.getInstance().getString(CommonData.UNION_ID, "");
        wxPic = SharePreferenceManager.getInstance().getString(CommonData.WX_HEADER, "");
        userId = SharePreferenceManager.getInstance().getString(CommonData.USER_ID, "");
        userTel = SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE, "");
        lv_money = (ListView) mInflater.findViewById(R.id.lv_money);
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_headview, null);
        lv_money.addHeaderView(headView);
        tv_ask_tixian = (TextView) headView.findViewById(R.id.tv_ask_tixian);
        tv_ask_tixian.setOnClickListener(this);
        tv_founds_yes = (TextView) headView.findViewById(R.id.tv_founds_yes);
        tv_founds_leiji = (TextView) headView.findViewById(R.id.tv_founds_leiji);
        tv_founds_my = (TextView) headView.findViewById(R.id.tv_founds_my);
        et_money = (EditText) headView.findViewById(R.id.et_money);
        tv_cash = (TextView) headView.findViewById(R.id.tv_cash);
        tv_cash.setOnClickListener(this);
        registerHeaderMessageReceiver();
        registerWXReceiver();
        refreshHandler();

    }

    /***
     * 设置pop
     */
    private void setPopModifyWidth() {
        modifyPriceManager = new PoPModifyPriceManager();
        @SuppressWarnings("deprecation")
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        modifyPriceManager.init(getActivity(), width, height,
                R.layout.pop_tixian);
        modifyPriceManager.showPopAllLocation(tv_ask_tixian, Gravity.CENTER, 0, 0);
        initPopWidgets();
    }

    /*操作pop中的组件*/
    private void initPopWidgets() {
        View updataForm = modifyPriceManager.getView();
        lv_question = (ListView) updataForm.findViewById(R.id.lv_question);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.getConstAnswer, null, handler_question);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ask_tixian:
                setPopModifyWidth();
                break;
            case R.id.tv_cash:
                money = et_money.getText().toString().trim();
                if (StringUtils.isEmpty(money)) {
                    Toast.makeText(getActivity(), "请输入提现金额", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                   /* //TODO 判断是否已经绑定微信
                    if (StringUtils.isEmpty(unionId)) {
                        *//*区分登录和分享的接收后的处理*//*
                        CommonData.isShare = false;
                        sendLogInReq();
                    } else if (StringUtils.isEmpty(wxId)) {
                        Toast.makeText(getActivity(), "请前去微信关注鱼群之家公众号", Toast.LENGTH_SHORT).show();
                        return;

                    } else {
                        //直接提现
                        reflect();
                    }*/
                    if(StringUtils.isEmpty(wxId)){
                        Toast.makeText(getActivity(), "请前去微信关注鱼群之家公众号,并在公众号提现", Toast.LENGTH_SHORT).show();
                    }else{
                        reflect();

                    }
                }
                break;
        }
    }

    /**
     * 提现
     */
    private void reflect() {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", userId);
        map.put("utel", userTel);
        map.put("weixinid", wxId);
        map.put("money", money);
        showWaitDialog(R.string.common_requesting);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.reflect, map, handler_reflect);

    }

    /**
     * 注册广播
     */
    public void registerHeaderMessageReceiver() {
        picMessageReceiver = new PicMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(CommonData.Invite_listview);
        getActivity().registerReceiver(picMessageReceiver, filter);
    }

    /**
     * 接受广播
     */
    public class PicMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != lv_money)
                lv_money.setSelection(0);
        }
    }

    /**
     * 注册广播
     */
    public void registerWXReceiver() {
        wxReceiver = new WXReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(CommonData.WXMessage);
        getActivity().registerReceiver(wxReceiver, filter);
    }

    /**
     * 接受广播
     */
    public class WXReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //执行提现操作
            reflect();
        }
    }

    /**
     * 刷新
     */
    private void refreshHandler() {
        ptrFrame = (PtrClassicFrameLayout) mInflater.findViewById(R
                .id.pfl_money);
        ptrFrame.setLastUpdateTimeRelateObject(this);
        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //刷新数据
                        currentPage = 1;
                        initData();
                        ptrFrame.refreshComplete();
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
     * 用此方法申请登录授权
     */
    protected void sendLogInReq() {
        // req.scope = "snsapi_userinfo";
        // 关于state,此处自己做修改
        // req.state = "test_wx_login";
        // req.scope = "snsapi_userinfo";
//		SendAuth.Req req = new SendAuth.Req();
//		req.scope = "snsapi_userinfo";
//		req.state = "wechat_sdk_demo";
//		MyApplication.api.sendReq(req);
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        boolean reqResult = false;
        reqResult = YuQunApplication.api.sendReq(req);
        Toast.makeText(getActivity(), "微信登录中...", Toast.LENGTH_SHORT).show();
    }
}
