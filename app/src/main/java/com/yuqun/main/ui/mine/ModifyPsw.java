package com.yuqun.main.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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
import com.yuqun.main.ui.login.LoginActivity;
import com.yuqun.main.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改密码
 * Created by Administrator on 2016/3/10.
 */
public class ModifyPsw extends BaseActivity implements View.OnClickListener {
    private EditText et_psw, et_psw_old, et_psw_sure;
    private TextView btn_modify;
    private String str_oldPsw, str_psw, str_surePsw;
    private TextView tv_phone;
    private Handler handler_login = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    Toast.makeText(ModifyPsw.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    try {
                        JSONObject jsonObject = new JSONObject(json.toString());
                        String RC = jsonObject.getString("RC");
                        if (RC.equals("1")) {
                            Toast.makeText(ModifyPsw.this, "修改成功", Toast.LENGTH_SHORT).show();
                        }
                        SharePreferenceManager.getInstance().setString(CommonData.USER_PHONE, "");
                        SharePreferenceManager.getInstance().setString(CommonData.USER_PWD, "");
                        startActivity(new Intent(ModifyPsw.this, LoginActivity.class));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
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
        setContentView(R.layout.layout_modify_psw);
        initViews();
        initDatas();
        bindListener();
    }

    @Override
    public void initViews() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_psw_old = (EditText) findViewById(R.id.et_psw_old);
        et_psw = (EditText) findViewById(R.id.et_psw_new);
        et_psw_sure = (EditText) findViewById(R.id.et_psw_sure);
        btn_modify = (TextView) findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(this);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_phone.setText(SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE,""));

    }

    @Override
    public void bindListener() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modify:
                str_oldPsw = et_psw_old.getText().toString();
                str_psw = et_psw.getText().toString();
                str_surePsw = et_psw_sure.getText().toString();
                if (StringUtils.isEmpty(str_oldPsw) || StringUtils.isEmpty(str_psw) || StringUtils.isEmpty(str_surePsw)) {
                    Toast.makeText(ModifyPsw.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!str_psw.equals(str_surePsw)) {
                    Toast.makeText(ModifyPsw.this, "两次密码输入不相同", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!str_oldPsw.equals(SharePreferenceManager.getInstance().getString(CommonData.USER_PWD, ""))) {
                    Toast.makeText(ModifyPsw.this, "原始密码不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("pwd", str_psw);
                hashMap.put("userID", SharePreferenceManager.getInstance().getString(CommonData.USER_ID, ""));
                HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.ChangePWD, hashMap,
                        handler_login);
                break;
        }
    }
}
