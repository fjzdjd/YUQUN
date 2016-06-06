package com.yuqun.main.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqun.main.MainActivity;
import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.ui.adapter.InviteAnswerAdapter;
import com.yuqun.main.ui.model.UserModle;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.utils.Utils;
import com.yuqun.main.view.PoPAnswerManager;
import com.yuqun.main.view.PoPXieYiManager;

import java.util.HashMap;
import java.util.List;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_send_code;
    private EditText et_phone;
    private EditText  et_code;
    private EditText  et_name;
    private EditText  et_pwd1;
    private EditText  et_pwd2;
    private TextView  tv_next;
    private TextView  tv_to_login;
    private TimeCount timeCount;
    private android.os.Handler send_code_handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if(null !=msg.obj){
                        Toast.makeText(RegisterActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
//                    List<UserModle> list = JsonUtil.parseFromJsonToList(json, UserModle.class);
//                    UserModle userModle = list.get(0);
                    break;
            }
        }
    };
    private android.os.Handler reg_handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if(null !=msg.obj){
                        Toast.makeText(RegisterActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    activityManager.startNextActivity(RegisterActivity2.class);
//                    List<UserModle> list = JsonUtil.parseFromJsonToList(json, UserModle.class);
//                    UserModle userModle = list.get(0);
                    break;
            }
        }
    };
    private CheckBox cb_xieyi;
    private CheckBox cb_ishceck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        bindListener();
        initDatas();
    }

    @Override
    public void initViews() {
        tv_send_code = (TextView) findViewById(R.id.tv_send_code);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        et_name = (EditText) findViewById(R.id.et_name);
        et_pwd1 = (EditText) findViewById(R.id.et_pwd1);
        et_pwd2 = (EditText) findViewById(R.id.et_pwd2);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_to_login = (TextView) findViewById(R.id.tv_to_login);
        cb_xieyi = (CheckBox) findViewById(R.id.cb_xieyi);
        cb_ishceck = (CheckBox) findViewById(R.id.cb_ishceck);

        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void bindListener() {
        tv_send_code.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        tv_to_login.setOnClickListener(this);
        cb_xieyi.setOnClickListener(this);
    }

    @Override
    public void initDatas() {
        timeCount = new TimeCount(60000, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发送验证码
            case R.id.tv_send_code:

                if(null == et_phone.getText().toString() || "" == et_phone.getText().toString()){
                    Toast.makeText(RegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Utils.isMobileNO(et_phone.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "手机号格式有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 开始倒计时
                timeCount.start();
                showWaitDialog(R.string.common_requesting);
                String phone = et_phone.getText().toString();
                HashMap<String, String> map = new HashMap<>();
                map.put("tel", phone);
                HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.SendSMSCode, map, send_code_handler);
                break;

            case R.id.tv_to_login:

             activityManager.startNextActivity(LoginActivity.class);
                break;
            //用户协议
            case R.id.cb_xieyi:
                setPopModifyWidth();
                break;
                  //下一步
            case R.id.tv_next:
//                activityManager.startNextActivity(RegisterActivity2.class);
                String phone2 = et_phone.getText().toString();
                String code = et_code.getText().toString();
                if(StringUtils.isEmpty(code)){
                    Toast.makeText(RegisterActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = et_name.getText().toString();
               if(StringUtils.isEmpty(name)){
                   Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                   return;
               }
                String pwd1 = et_pwd1.getText().toString();
               if(StringUtils.isEmpty(pwd1)){
                   Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                   return;
               }
                String pwd2 = et_pwd2.getText().toString();
               if(StringUtils.isEmpty(pwd2)){
                   Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                   return;
               }
                if(!cb_ishceck.isChecked()){
                    Toast.makeText(RegisterActivity.this, "请同意用户协议", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> map2 = new HashMap<>();
                map2.put("Name",name);
                map2.put("Tel",phone2);
                map2.put("PWD",pwd1);
                map2.put("RecommendedUserID","0");
                map2.put("SMSCode", code);

                Intent intent = new Intent();
                intent.putExtra("Name",name);
                intent.putExtra("Tel",phone2);
                intent.putExtra("PWD",pwd1);
                intent.putExtra("RecommendedUserID","0");
                intent.putExtra("SMSCode",code);
                activityManager.startNextActivity(intent,RegisterActivity2.class);
//                HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.UserReg, map2, reg_handler);
                break;


        }


    }

    /***
     * 设置pop
     */
    private void setPopModifyWidth() {
        PoPXieYiManager modifyPriceManager = new PoPXieYiManager();
        @SuppressWarnings("deprecation")
        int width = getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = getWindowManager().getDefaultDisplay().getHeight();
        modifyPriceManager.init(this, width, height,
                R.layout.pop_reg_xieyi);
        modifyPriceManager.showPopAllLocation(cb_xieyi,Gravity.CENTER, 0, 0);

    }


    /**
     * 倒计时
     */
    private class TimeCount extends CountDownTimer {

        /***
         * @param millisInFuture    总时长
         * @param countDownInterval 计时的时间间隔
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onFinish() {// 计时完毕时触发
            tv_send_code.setText("重新验证");
            tv_send_code.setClickable(true);
            tv_send_code.setBackground(getResources().getDrawable(R.drawable.corners_bg_blue));
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            tv_send_code.setClickable(false);
            tv_send_code.setText("重发 " + millisUntilFinished / 1000 + " 秒");
            tv_send_code.setBackground(getResources().getDrawable(R.drawable.corners_bg_gray));
        }
    }
}
