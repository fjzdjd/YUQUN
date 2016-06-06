package com.yuqun.main.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.yuqun.main.MainActivity;
import com.yuqun.main.R;
import com.yuqun.main.app.YuQunApplication;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.ui.model.CityModel;
import com.yuqun.main.ui.model.UserModle;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.utils.Utils;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;


/**
 * Created by zzp on 2016/3/9.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_to_register;
    private TextView tv_login;
    private EditText et_login_phone;
    private EditText et_login_pwd;
    private CheckBox ck_autologin;
    private String tel;
    private String pwd;
    private TextView tv_forget_pwd;

    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(LoginActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    Log.d("gay", json);
                    List<UserModle> list = JsonUtil.parseFromJsonToList(json, UserModle.class);
                    UserModle userModle = list.get(0);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_PHONE, tel);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_PWD, pwd);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_ID, userModle.getUID());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_PHONE, et_login_phone.getText().toString());
                    SharePreferenceManager.getInstance().setString(CommonData.USER_PWD, et_login_pwd.getText().toString());
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
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    LoginActivity.this.finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  /*      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }*/
        setContentView(R.layout.activity_login);

        initViews();
        bindListener();
        initDatas();
    }

    @Override
    public void initViews() {
        tv_to_register = (TextView) findViewById(R.id.tv_to_register);
        tv_login = (TextView) findViewById(R.id.tv_login);
        et_login_phone = (EditText) findViewById(R.id.et_login_phone);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        ck_autologin = (CheckBox) findViewById(R.id.ck_autologin);

        tel = SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE, "");
        pwd = SharePreferenceManager.getInstance().getString(CommonData.USER_PWD, "");
        String auto = SharePreferenceManager.getInstance().getString(CommonData.USER_AUTO, "");

//        Toast.makeText(LoginActivity.this, "tel="+tel+",auto"+pwd, Toast.LENGTH_SHORT).show();
        if ("1".equals(auto)) {
            et_login_phone.setText(tel);
            et_login_pwd.setText(pwd);
            HashMap<String, String> map = new HashMap<>();
            map.put("tel", tel);
            map.put("pwd", pwd);
            map.put("osType","Android");
            HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.request_login, map, handler);
        } else {
            et_login_phone.setText("");
            et_login_pwd.setText("");
        }
        ck_autologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharePreferenceManager.getInstance().setString(CommonData.USER_AUTO, "1");
                } else {
                    SharePreferenceManager.getInstance().setString(CommonData.USER_AUTO, "0");
                }
            }
        });

        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);

        findViewById(R.id.tv_wx_login).setOnClickListener(this);
    }

    @Override
    public void bindListener() {
        tv_to_register.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        tv_forget_pwd.setOnClickListener(this);
    }

    @Override
    public void initDatas() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_to_register:
                activityManager.startNextActivity(RegisterActivity.class);
                break;
            case R.id.tv_forget_pwd:
                activityManager.startNextActivity(ForgetPwdActivity.class);
                break;
            //微信Login
            case R.id.tv_wx_login:
                //方便接收微信返回的结果时和share区分
                CommonData.isShare = false;
                sendLogInReq();
                break;
            case R.id.tv_login:
                String phone = et_login_phone.getText().toString();
                String pwd = et_login_pwd.getText().toString();
                if (StringUtils.isEmpty(phone)) {
                    Toast.makeText(LoginActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (StringUtils.isEmpty(pwd)) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (!Utils.isMobileNO(phone)) {
                    Toast.makeText(LoginActivity.this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> map = new HashMap<>();
                map.put("tel", phone);
                map.put("pwd", pwd);
                map.put("osType","Android");
                HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.request_login, map, handler);
                showWaitDialog(R.string.common_requesting);

                break;


        }
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
        Toast.makeText(this, "微信登录中...", Toast.LENGTH_LONG).show();
    }
}
