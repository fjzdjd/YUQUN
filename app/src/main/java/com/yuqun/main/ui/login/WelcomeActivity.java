package com.yuqun.main.ui.login;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yuqun.main.MainActivity;
import com.yuqun.main.R;
import com.yuqun.main.app.YuQunApplication;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.ui.model.UserModle;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class WelcomeActivity extends BaseActivity implements Runnable {

    /**
     * 是否第一次使用
     */
    private boolean isFirstUse;

    /**
     * 版本号
     */
    private TextView txt_version;
    /**
     * 项目包名
     */
    private String packName;

    private String versionName;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private Vibrator mVibrator01 = null;

    private LocationClient mLocClient;
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(WelcomeActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    activityManager.startNextActivity(getIntent(), LoginActivity.class);
                    finish();
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    List<UserModle> list = JsonUtil.parseFromJsonToList(json, UserModle.class);
                    UserModle userModle = list.get(0);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_PHONE, tel);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_PWD, pwd);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_ID, userModle.getUID());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_PHONE, tel);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_PWD, pwd);

                    SharePreferenceManager.getInstance().setString(CommonData.USER_NAME, userModle.getName());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_LEVEL, userModle.getUserLevel());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_REGDATE, userModle.getRegDate());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_IP, userModle.getRegIP());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_MONEY, userModle.getMoneyCanWithdrawals());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_POINT, userModle.getPoints());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_GENDER, userModle.getGender());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_JOBSTR, userModle.getJobStr());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_EDUSTR, userModle.getRevStr());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_CITYSTR, userModle.getCityStr());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_REV, userModle.getEduStr());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_BIRTH, userModle.getBirthday());
                    SharePreferenceManager.getInstance().setString(CommonData.AUTH, userModle.getPassAuthentication());
                    /*微信id 和头像*/
                    SharePreferenceManager.getInstance().setString(CommonData.WX_ID, userModle.getWeiXinID());
                    SharePreferenceManager.getInstance().setString(CommonData.WX_HEADER, userModle.getUserHeadPic());
                    YuQunApplication.modelList.clear();
                    YuQunApplication.modelList.addAll(userModle.getTags());
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    WelcomeActivity.this.finish();

                    break;
            }
        }
    };
    private String tel;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_welcome_layout);
        if (!Utils.isNetworkAvailable(getApplication())) {
            Toast.makeText(WelcomeActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
            activityManager.startNextActivity(LoginActivity.class);
            this.finish();
            return;
        }
        initViews();
        initDatas();
        bindListener();
        new Thread(WelcomeActivity.this).start();
    }

    @Override
    public void initViews() {
        if (!Utils.isNetworkAvailable(getApplication())) {
            Toast.makeText(WelcomeActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mLocClient = ((YuQunApplication) getApplication()).mLocationClient;
//        ((MyApplication)getApplication()).mTv = mTv;
            mVibrator01 = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
//        ((YuQunApplication)getApplication()).mVibrator01 = mVibrator01;
//        getLocation();
            setLocationOption(mLocClient);
            mLocClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void bindListener() {

    }

    @Override
    public void initDatas() {
        initVersion();

    }

    /**
     * 获取版本号
     */
    private void initVersion() {
      /*  txt_version = (TextView) findViewById(R.id.txt_version);
        packName = CommonData.PACKNAME;
        try {
            versionName = this.getPackageManager().getPackageInfo(packName, 0).versionName;
            txt_version.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void run() {
        /**
         * 延迟两秒时间
         */
        try {
            Thread.sleep(500);
            // 读取SharedPreferences中需要的数据

            SharedPreferences preferences = getSharedPreferences("isFirstUse", MODE_WORLD_READABLE);

            isFirstUse = preferences.getBoolean("isFirstUse", true);

            /**
             * 如果用户不是第一次使用则直接调转到显示界面,否则调转到引导界面
             */
            if (isFirstUse) {
                activityManager.startNextActivity(getIntent(), GuideManagerActivity.class);
                this.finish();
            } else {

                String auto = SharePreferenceManager.getInstance().getString(CommonData.USER_AUTO, "");
                if ("1".equals(auto)) {
                    if (Utils.isNetworkAvailable(getApplication())) {
                        tel = SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE, "");
                        pwd = SharePreferenceManager.getInstance().getString(CommonData.USER_PWD, "");
                        HashMap<String, String> map = new HashMap<>();
                        map.put("tel", tel);
                        map.put("pwd", pwd);
                        map.put("osType","Android");
                        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.request_login, map, handler);
                    } else {
                        Toast.makeText(WelcomeActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    activityManager.startNextActivity(getIntent(), LoginActivity.class);
                    finish();
                }


            }


            // 实例化Editor对象
            Editor editor = preferences.edit();
            // 存入数据
            editor.putBoolean("isFirstUse", false);
            // 提交修改
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Utils.isNetworkAvailable(getApplication())) {
            return;
        }
        Utils.GetNetCurrentIP("http://pv.sohu.com/cityjson");
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }


    //设置相关参数
    private static void setLocationOption(LocationClient mLocClient) {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);                //打开gps
        option.setCoorType("bd09ll");        //设置坐标类型
        option.setServiceName("com.baidu.location.service_v2.9");
//        option.setPoiExtraInfo(true);
//        if(mIsAddrInfoCheck.isChecked())
//        {
        option.setAddrType("all");
//        }
//        if(null!=mSpanEdit.getText().toString())
//        {
//            boolean b = isNumeric(mSpanEdit.getText().toString());
//            if(b)
//            {
        option.setScanSpan(3000);    //设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
//            }
//        }
//		option.setScanSpan(3000);

//        if(mPriorityCheck.isChecked())
//        {
        option.setPriority(LocationClientOption.NetWorkFirst);      //设置网络优先
//        }
//        else
//        {
//            option.setPriority(LocationClientOption.GpsFirst);        //不设置，默认是gps优先
//        }

        option.setPoiNumber(10);
        option.disableCache(true);
        mLocClient.setLocOption(option);
    }

}
