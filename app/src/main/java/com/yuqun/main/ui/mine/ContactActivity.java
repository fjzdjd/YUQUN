package com.yuqun.main.ui.mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.domain.GetOrderDetailInfo;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 联系客服
 * Created by Administrator on 2016/3/11.
 */
public class ContactActivity extends BaseActivity {
    private TextView tv_kefuTel,tv_time,tv_weixin;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    break;

                case CommonData.HTTP_HANDLE_SUCCESS:
                    if (msg.obj != null) {
                        String json = msg.obj.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONObject jsonObject2 = (JSONObject) jsonObject.getJSONArray("Data").get(0);
                            String str_kefuTel = jsonObject2.getString("kefuTel");
                            String str_time = jsonObject2.getString("time");
                            String str_weixin =  jsonObject2.getString("weixin");
                            tv_kefuTel.setText(str_kefuTel);
                            tv_time.setText(str_time);
                            tv_weixin.setText(str_weixin);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contact);
        initViews();
        initDatas();
        bindListener();
    }

    @Override
    public void initViews() {
        findViewById(R.id.img_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_kefuTel = (TextView) findViewById(R.id.tv_kefuTel);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_weixin = (TextView) findViewById(R.id.tv_weixin);
    }

    @Override
    public void bindListener() {
        findViewById(R.id.img_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void initDatas() {
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.getKefu, null,
                handler);
    }
}
