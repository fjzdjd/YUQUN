package com.yuqun.main.ui.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diy.widget.CircularImage;
import com.lidroid.xutils.BitmapUtils;
import com.yuqun.main.R;
import com.yuqun.main.app.YuQunApplication;
import com.yuqun.main.base.BaseFragment;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.ui.login.LoginActivity;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.view.RoundCornerProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private View mInflater;
    private LinearLayout ll_money, ll_qiandao, ll_my, ll_psw, ll_contact, ll_mess, ll_version,ll_about;
    public static final String GET_RECEIVE_MONEY = "com.mine.money";
    /*姓名，绑定的手机号*/
    private TextView tv_mine_name, tv_mine_phone, tv_mine_level, tv_regDate, tv_regIp, tv_myMoney,tv_auth;
    private ImageView iv_level;
    /*进度条*/
    private RoundCornerProgressBar progressBar;
    /*信誉值*/
    private TextView tv_xinyu;
    private TextView tv_loginout;
    private CircularImage mHeadImg;
    private BitmapUtils bitmapUtils;
    private String mHeadPic;
    private List<String> mStrList = new ArrayList<>();
    /*消息提示的红点；通过接收到不同的广播进行隐藏与显示*/
    private TextView tv_mess_num;
    private Handler handler_level = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray = jsonObject.getJSONArray("Data");
                        JSONObject jsonObject2 = jsonArray.getJSONObject(0);
                        JSONObject jsonOb = jsonObject2.getJSONObject("LvDefine");
                        for (int i = 0; i < jsonOb.length(); i++) {
                            mStrList.add(jsonOb.getString(i + 1 + ""));
                        }
                        initView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    private String str_money;
    private String auth;//是否通过认证，为1 的时候显示身份证标识
    private  InvisibleReceiver invisibleReceiver;
    private VisibleReceiver visibleReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mInflater = inflater.inflate(R.layout.fragment_mine_layout, container, false);
        bitmapUtils = new BitmapUtils(getActivity());
        initLevel();
        initView();
        registerInvisibleReceiver();
        registerVisibleReceiver();
        return mInflater;
    }

    /*获取等级信誉值*/
    private void initLevel() {
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetOptions, null, handler_level);
    }

    private void initView() {
        mHeadImg = (CircularImage) mInflater.findViewById(R.id.login_img_enter);
        mHeadPic = SharePreferenceManager.getInstance().getString(CommonData.WX_HEADER, "");
        if (StringUtils.isEmpty(mHeadPic)) {
            Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.face_big);
            mHeadImg.setImageBitmap(thumb);
        } else {
            bitmapUtils.display(mHeadImg, mHeadPic);
        }

        ll_mess = (LinearLayout) mInflater.findViewById(R.id.ll_xiaoxi);
        ll_money = (LinearLayout) mInflater.findViewById(R.id.ll_money);
        ll_qiandao = (LinearLayout) mInflater.findViewById(R.id.ll_qiandao);
        ll_my = (LinearLayout) mInflater.findViewById(R.id.ll_my);
        ll_psw = (LinearLayout) mInflater.findViewById(R.id.ll_psw);
        ll_contact = (LinearLayout) mInflater.findViewById(R.id.ll_contact);
        ll_version = (LinearLayout) mInflater.findViewById(R.id.ll_version);
        ll_about = (LinearLayout) mInflater.findViewById(R.id.ll_about);
        tv_mine_phone = (TextView) mInflater.findViewById(R.id.tv_mine_phone);
        tv_mine_name = (TextView) mInflater.findViewById(R.id.tv_mine_name);
        iv_level = (ImageView) mInflater.findViewById(R.id.iv_level);
        tv_myMoney = (TextView) mInflater.findViewById(R.id.tv_my_money);
        tv_mine_level = (TextView) mInflater.findViewById(R.id.tv_mine_level);
        tv_regDate = (TextView) mInflater.findViewById(R.id.tv_regDate);
        tv_regIp = (TextView) mInflater.findViewById(R.id.tv_regIp);
        tv_regDate.setText(SharePreferenceManager.getInstance().getString(CommonData.USER_REGDATE, ""));
        tv_regIp.setText(SharePreferenceManager.getInstance().getString(CommonData.USER_IP, ""));
        String str_phone = SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE, "");
        str_phone = str_phone.substring(0, 3) + "*****" + str_phone.substring(8, str_phone.length());
        tv_mine_phone.setText("绑定手机：" + str_phone);
        String level = SharePreferenceManager.getInstance().getString(CommonData.USER_LEVEL, "");
//        tv_level.setText("lv" + level);
        tv_mine_level.setText("LV" + level);
        str_money = SharePreferenceManager.getInstance().getString(CommonData.USER_MONEY, "");
        tv_myMoney.setText("￥" + str_money);
        int progress = Integer.parseInt(SharePreferenceManager.getInstance().getString(CommonData.USER_POINT, ""));
        tv_xinyu = (TextView) mInflater.findViewById(R.id.tv_xinyu);
        String max = "2000";
        if ((mStrList.size() != 0) && (Integer.parseInt(level) >= 1)) {
            max = mStrList.get(Integer.parseInt(level) - 1);
            max = max.substring(max.indexOf(",") + 1, max.indexOf("]"));
        }
        tv_xinyu.setText(progress + "/" + max);
        progressBar = (RoundCornerProgressBar) mInflater.findViewById(R.id.progressBar);
        progressBar.setMax(Integer.parseInt(max));
        progressBar.setProgress(progress);
        ll_money.setOnClickListener(this);
        ll_qiandao.setOnClickListener(this);
        ll_my.setOnClickListener(this);
        ll_psw.setOnClickListener(this);
        ll_mess.setOnClickListener(this);
        ll_contact.setOnClickListener(this);
        ll_about.setOnClickListener(this);
        tv_loginout = (TextView) mInflater.findViewById(R.id.tv_loginout);
        tv_loginout.setOnClickListener(this);
        ll_version.setOnClickListener(this);
        tv_mess_num = (TextView) mInflater.findViewById(R.id.tv_mess_num);
        tv_auth= (TextView) mInflater.findViewById(R.id.tv_auth);
        tv_auth.setOnClickListener(this);
        auth = SharePreferenceManager.getInstance().getString(CommonData.AUTH,"");
        if(auth.equals("1")){
            tv_auth.setVisibility(View.GONE);
        }else{
            tv_auth.setVisibility(View.VISIBLE);
        }

        iv_level.setOnClickListener(this);


    }

    /**
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //头像刷新
        mHeadPic = SharePreferenceManager.getInstance().getString(CommonData.WX_HEADER, "");
        if (StringUtils.isEmpty(mHeadPic)) {
            Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.face_big);
            mHeadImg.setImageBitmap(thumb);
        } else {
            bitmapUtils.display(mHeadImg, mHeadPic);
        }
        //奖金刷新
        str_money = SharePreferenceManager.getInstance().getString(CommonData.USER_MONEY, "");
        tv_myMoney.setText("￥" + str_money);

    }

    @Override
    public void onResume() {
        super.onResume();
        tv_mine_name.setText(SharePreferenceManager.getInstance().getString(CommonData.USER_NAME, ""));
        //0 未认证 ：1 已认证   ： 2 认证中
        String auto = SharePreferenceManager.getInstance().getString(CommonData.AUTH,"");
        if(auto.equals("0")){
            tv_auth.setText("进行实名认证");
        }else if(auto.equals("1")){
            tv_auth.setText("已认证");
        }else if(auto.equals("2")){
            tv_auth.setText("认证中");
        }
        if(auto.equals("1")){
            tv_auth.setVisibility(View.GONE);
        }else{
            tv_auth.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //我的奖金
            case R.id.ll_money:
                //发广播
                Intent intent = new Intent();
                intent.setAction(GET_RECEIVE_MONEY);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                break;
            //修改密码
            case R.id.ll_psw:
                startActivity(new Intent(getActivity(), ModifyPsw.class));
                break;
            //修改资料
            case R.id.ll_my:
                startActivity(new Intent(getActivity(), ModifyInfo.class));
                break;
            case R.id.ll_contact:
                startActivity(new Intent(getActivity(), ContactActivity.class));
                break;
            case R.id.tv_loginout:
                /*清空缓存的数据以及标签集合*/
                SharePreferenceManager.getInstance().clear();
                YuQunApplication.modelList.clear();
                /*跳到登录界面*/
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case R.id.ll_qiandao:
                //签到
                startActivity(new Intent(getActivity(), SignActivity.class));
                break;
            case R.id.ll_xiaoxi:
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.ll_version:
                try {
                    CommonData.isUpdate = false;
                    new UpdateManager(getActivity()).isupdate();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_auth:
            case R.id.iv_level:
                startActivity(new Intent(getActivity(),AuthenticationActivity.class));
                break;
            //关于我们
            case R.id.ll_about :
                startActivity(new Intent(getActivity(),AboutActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 注册广播
     */
    public void registerVisibleReceiver() {
        visibleReceiver = new VisibleReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(CommonData.CircleVisible);
        getActivity().registerReceiver(visibleReceiver, filter);
    }

    /**
     * 接受广播
     */
    public class VisibleReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            //红点显示
            tv_mess_num.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 注册广播
     */
    public void registerInvisibleReceiver() {
        invisibleReceiver = new InvisibleReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(CommonData.CircleInVisible);
        getActivity().registerReceiver(invisibleReceiver, filter);
    }

    /**
     * 接受广播
     */
    public class InvisibleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
           //红点隐藏
            tv_mess_num.setVisibility(View.GONE);
        }
    }
}
