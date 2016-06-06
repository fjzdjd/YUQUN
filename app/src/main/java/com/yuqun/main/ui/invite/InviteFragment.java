package com.yuqun.main.ui.invite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuqun.main.R;
import com.yuqun.main.base.BaseFragment;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.component.refresh.PtrClassicFrameLayout;
import com.yuqun.main.component.refresh.PtrDefaultHandler;
import com.yuqun.main.component.refresh.PtrFrameLayout;
import com.yuqun.main.component.refresh.PtrHandler;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.ui.adapter.InviteAnswerAdapter;
import com.yuqun.main.ui.adapter.MyInviteAdapter;
import com.yuqun.main.ui.model.AnswerModel;
import com.yuqun.main.ui.model.MyInviteModel;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.Utils;
import com.yuqun.main.view.ListView4ScrollView;
import com.yuqun.main.view.PoPAnswerManager;
import com.yuqun.main.view.PoPShareWindowManager;
import com.yuqun.main.wxapi.WeChatConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 邀请好友
 * @author gqy
 */
public class InviteFragment extends BaseFragment implements View.OnClickListener {

    private View mInflater;
    private View headView;
    private TextView tv_share_instruction;
    private TextView tv_invite_count;
    private TextView tv_invite_m;
    private ImageView iv_share_bg;
    private List list_invite;
    private List<AnswerModel> list_answeModel;
    private ListView lv_invite;
    /*点击邀请朋友按钮*/
    private TextView tv_invite;
    private List<MyInviteModel> mFoundsList = new ArrayList<>();
    private MyInviteAdapter myInviteAdapter;
    private View view;
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    if (myInviteAdapter == null) {
                        myInviteAdapter = new MyInviteAdapter(list_invite, getActivity());
                        lv_invite.setAdapter(myInviteAdapter);
                    } else {
                        myInviteAdapter.notifyDataSetChanged();

                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray = jsonObject.getJSONArray("Data");
                        JSONObject jsonObj = (JSONObject) jsonArray.get(0);
                        String myyaoqingnum = jsonObj.getString("myyaoqingnum");
                        String totalmoney = jsonObj.getString("totalmoney");
                        JSONArray jsonArr = jsonObj.getJSONArray("flowlist");
                        /*解析数组*/
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<MyInviteModel>>() {
                        }.getType();// TypeToken内的泛型就是Json数据中的类型
                        list_invite = gson.fromJson(jsonArr.toString(), listType);
                        mFoundsList.addAll(list_invite);
                        if (list_invite.size() == 0) {
//                            Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
                        }

                        tv_invite_count.setText(myyaoqingnum);
                        tv_invite_m.setText(totalmoney);
                        if (myInviteAdapter == null) {
                            myInviteAdapter = new MyInviteAdapter(mFoundsList, getActivity());
                            lv_invite.setAdapter(myInviteAdapter);
                        } else {
                            myInviteAdapter.notifyDataSetChanged();
                        }
                        // 监听listview滚到最底部
                        lv_invite.setOnScrollListener(new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView view, int scrollState) {
                                switch (scrollState) {
                                    // 当不滚动时
                                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                                        // 判断滚动到底部
                                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                                            ++currentPage;
                                            initAsgentList(currentPage);
                                        }
                                        break;
                                }
                            }

                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem,
                                                 int visibleItemCount, int totalItemCount) {
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
            }
        }
    };
    private android.os.Handler answer_handler = new android.os.Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    list_answeModel = JsonUtil.parseFromJsonToList(json, AnswerModel.class);


                    break;
            }
        }
    };
    private ListView4ScrollView listView;
    private int currentPage = 1;
    private PicMessageReceiver picMessageReceiver;
    private PtrClassicFrameLayout ptrFrame;
    private IWXAPI api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        mInflater = inflater.inflate(R.layout.frament_invite_list_layout, container, false);

        return mInflater;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initDatas();

     //实例化
/*        api = WXAPIFactory.createWXAPI(getActivity(), WeChatConstants.APP_ID, true);
        api.registerApp(WeChatConstants.APP_ID);*/
    }

    /**
     * 刷新
     */
    private void refreshHandler() {
        ptrFrame = (PtrClassicFrameLayout) mInflater.findViewById(R
                .id.pfl_invite);
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
                        initDatas();
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

    private void initViews() {
        lv_invite = (ListView) getActivity().findViewById(R.id.lv_invite);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_invite_header_layout, null);
        tv_share_instruction = (TextView) headView.findViewById(R.id.tv_share_instruction);
        tv_share_instruction.setOnClickListener(this);

        tv_invite_count = (TextView) headView.findViewById(R.id.tv_invite_count);
        tv_invite_m = (TextView) headView.findViewById(R.id.tv_invite_m);
        tv_invite = (TextView) headView.findViewById(R.id.btn_invite);
        tv_invite.setOnClickListener(this);
        iv_share_bg = (ImageView) headView.findViewById(R.id.iv_share_bg);
//        int h = ScreenUtils.getScreenHeight(getActivity()) / 5 * 3;
//        iv_share_bg.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, h));
        lv_invite.addHeaderView(headView);
        registerHeaderMessageReceiver();
        refreshHandler();


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

    private void initDatas() {

        initAsgentList(currentPage);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.getConstAnswerYaoqing, null, answer_handler);

    }

    private void initAsgentList(int curPage) {
        HashMap<String, String> map = new HashMap<>();
        String id = SharePreferenceManager.getInstance().getString(CommonData.USER_ID, "");
        map.put("userID", id);
        map.put("curPage", curPage + "");
        map.put("pageSize", CommonData.PageSize);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetUserDirectAgentsSummaryList, map, handler);

    }

    /***
     * 设置pop
     */
    private void setPopModifyWidth() {

        PoPAnswerManager modifyPriceManager = new PoPAnswerManager();
        @SuppressWarnings("deprecation")
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        modifyPriceManager.init(getActivity(), width, height,
                R.layout.pop_share_instructions);

        listView = modifyPriceManager.getListView();

        listView.setAdapter(new InviteAnswerAdapter(list_answeModel, getActivity()));
        modifyPriceManager.showPopAllLocation(getView(), Gravity.CENTER, 0, 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share_instruction:
                //显示邀请好友详情说明
                setPopModifyWidth();
                break;
            case R.id.btn_invite:
//                startActivity(new Intent(getActivity(), InviteActivity.class));
                showCustomUI();
                break;
        }
    }

    /**
     * 接受广播
     */
    public class PicMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != lv_invite)
                lv_invite.setSelection(0);
        }
    }

    private void showCustomUI() {
        @SuppressWarnings("deprecation")
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        PoPShareWindowManager.getInstance().init(getActivity(), width, ViewGroup.LayoutParams.WRAP_CONTENT,
                R.layout.pop_share_wechat);
        PoPShareWindowManager.getInstance().showPopAllLocation(tv_share_instruction, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        PoPShareWindowManager.getInstance().OnClickWechat(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String uid = SharePreferenceManager.getInstance().getString(CommonData.USER_ID, "");
                String url = "http://partner.17yuqun.com/myinvite-" + uid + ".html";
                String title = "鱼群诚邀您来注册，轻松赚外快!";
                String description = "快来加入鱼群吧";
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.app);
                Utils.wechatShare(0, url, title, description, thumb);
                PoPShareWindowManager.getInstance().dismissPop();
            }
        });

        PoPShareWindowManager.getInstance().OnClick(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PoPShareWindowManager.getInstance().dismissPop();
            }
        });
        PoPShareWindowManager.getInstance().OnClickWechatCircle(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 调用直接分享
                String uid = SharePreferenceManager.getInstance().getString(CommonData.USER_ID, "");
                String url = "http://partner.17yuqun.com/myinvite-" + uid + ".html";
                String title = "鱼群诚邀您来注册，轻松赚外快!";
                String description = "快来加入鱼群吧";
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.app);
                Utils.wechatShare(1, url, title, description, thumb);
                PoPShareWindowManager.getInstance().dismissPop();
            }
        });
    }
}
