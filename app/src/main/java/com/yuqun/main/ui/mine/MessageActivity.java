package com.yuqun.main.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqun.main.MainActivity;
import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.ui.adapter.NotificationAdapter;
import com.yuqun.main.ui.login.LoginActivity;
import com.yuqun.main.ui.model.NotifyModel;
import com.yuqun.main.ui.model.UserModle;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息公告
 * Created by Administrator on 2016/3/10.
 */
public class MessageActivity extends BaseActivity {
    private ListView lv_mess;
    private  List<NotifyModel>notifyModelList = new ArrayList<>();

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    Toast.makeText(MessageActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                   notifyModelList = JsonUtil.parseFromJsonToList(json, NotifyModel.class);
                    lv_mess.setAdapter(new NotificationAdapter(notifyModelList,MessageActivity.this));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_message);
        initViews();
        initDatas();
        bindListener();
        //发广播给我的界面，让红点消失
        Intent intent = new Intent();
        intent.setAction(CommonData.CircleInVisible);
        sendBroadcast(intent);
    }

    @Override
    public void initViews() {
        lv_mess= (ListView) findViewById(R.id.lv_mess);
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void bindListener() {
        lv_mess.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(!StringUtils.isEmpty(notifyModelList.get(position).getContent())){
                    Intent intent  = new Intent(MessageActivity.this,MessageContentActivity.class);
                    intent.putExtra("URL", notifyModelList.get(position).getContent());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void initDatas() {
        String  tel = SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE,"");
        HashMap<String, String> map = new HashMap<>();
        map.put("utel", tel);
        map.put("type", "");
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.getNotiByTel, map, handler);


    }


}
