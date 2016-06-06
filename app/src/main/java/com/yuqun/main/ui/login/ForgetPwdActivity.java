package com.yuqun.main.ui.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.utils.Utils;

import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * 忘记密码
 */
public class ForgetPwdActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_to_login;
    private TextView tv_send_code;
    private TextView tv_next;
    private TimeCount timeCount;
    private EditText et_phone;
    private EditText et_code;
    private EditText  et_pwd1;
    private EditText  et_pwd2;
    private android.os.Handler send_code_handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if(null !=msg.obj){
                        Toast.makeText(ForgetPwdActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
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
                        Toast.makeText(ForgetPwdActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    activityManager.startNextActivity(LoginActivity.class);
                    activityManager.popActivity(ForgetPwdActivity.this);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        initViews();
        bindListener();
        initDatas();
    }

    @Override
    public void initViews() {
        tv_to_login = (TextView) findViewById(R.id.tv_to_login);
        tv_send_code = (TextView) findViewById(R.id.tv_send_code);
        tv_next = (TextView) findViewById(R.id.tv_next);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        et_pwd1 = (EditText) findViewById(R.id.et_pwd1);
        et_pwd2 = (EditText) findViewById(R.id.et_pwd2);
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void bindListener() {
        tv_to_login.setOnClickListener(this);
        tv_send_code.setOnClickListener(this);
        et_code.setOnClickListener(this);
        tv_next.setOnClickListener(this);

    }

    @Override
    public void initDatas() {
        timeCount = new TimeCount(60000, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_to_login:
                activityManager.startNextActivity(LoginActivity.class);
                activityManager.popActivity(ForgetPwdActivity.this);
                break;

            case R.id.tv_next:
                String phone2 = et_phone.getText().toString();
                String code = et_code.getText().toString();
                if(StringUtils.isEmpty(code)){
                    Toast.makeText(ForgetPwdActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String pwd1 = et_pwd1.getText().toString();
                if(StringUtils.isEmpty(pwd1)){
                    Toast.makeText(ForgetPwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String pwd2 = et_pwd2.getText().toString();
                if(StringUtils.isEmpty(pwd2)){
                    Toast.makeText(ForgetPwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pwd1.equals(pwd2)){
                    Toast.makeText(ForgetPwdActivity.this, "两次输入的密码不一样", Toast.LENGTH_SHORT).show();
                    return;
                }
                showWaitDialog(R.string.common_requesting);
                HashMap<String, String> map2 = new HashMap<>();
                map2.put("tel",phone2);
                map2.put("SMSCode",code);
                map2.put("pwd",pwd1);
                HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.ForgetPWD, map2, reg_handler);
                break;

            //发送验证码
            case R.id.tv_send_code:

                if(null == et_phone.getText().toString() || "" == et_phone.getText().toString()){
                    Toast.makeText(ForgetPwdActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Utils.isMobileNO(et_phone.getText().toString())){
                    Toast.makeText(ForgetPwdActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
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
        }
    }


    /**
     * 倒计时
     *
     * @author zzp
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
