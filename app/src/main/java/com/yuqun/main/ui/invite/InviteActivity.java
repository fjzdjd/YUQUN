package com.yuqun.main.ui.invite;

import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.view.PoPShareWindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 邀请二级页面
 * Created by Administrator on 2016/3/17.
 */
public class InviteActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_invite_num, tv_invite_name, btn_invite_share;
    private LinearLayout ll_layout;
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(InviteActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonarray = jsonObject.getJSONArray("Data");
                        JSONObject jsonObj = (JSONObject) jsonarray.get(0);
                        String str_count = jsonObj.getString("allcount");
                        tv_invite_num.setText(str_count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_invite);
        initViews();
        initDatas();
        bindListener();
    }

    @Override
    public void initViews() {
        tv_invite_num = (TextView) findViewById(R.id.tv_people_num);
        tv_invite_name = (TextView) findViewById(R.id.tv_invite_name);
        ll_layout = (LinearLayout) findViewById(R.id.ll_layout);
        String name = SharePreferenceManager.getInstance().getString(CommonData.USER_NAME, "");
        String sex = SharePreferenceManager.getInstance().getString(CommonData.USER_GENDER, "");
        if (sex.equals("1")) {
            name = name.substring(0, 1) + "先生";
        } else {
            name = name.substring(0, 1) + "女士";
        }
        tv_invite_name.setText("您的朋友" + name + "，邀请您一起来赚外快:)");
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.getAllCount, null, handler);
        btn_invite_share = (TextView) findViewById(R.id.btn_invite_share);
        btn_invite_share.setOnClickListener(this);

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
            case R.id.btn_invite_share:
                //弹出分享
                showCustomUI();
                break;
        }
    }

    /**
     * 分享监听器
     */
   /*SnsPostListener mShareListener = new SnsPostListener() {

        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int stCode, SocializeEntity entity) {
            if (stCode == 200) {
                // Toast.makeText(SetupAboutActivity.this, "分享成功",
                // Toast.LENGTH_SHORT).show();
            } else {
//				Toast.makeText(SetupAboutActivity.this, "分享失败 : error code : " + stCode, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void initShare() {
        String appId = "wxfbd9c8d3159bc175";
        String appSecret = "59feb1141e06816e952ff5bc7098c42e";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(SetupAboutActivity.this, appId, appSecret);
        wxHandler.addToSocialSDK();
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(SetupAboutActivity.this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        // 设置分享文字
        weixinContent.setShareContent(content);
        // 设置title
        weixinContent.setTitle("车惠卖车通");
        // 设置分享内容跳转URL
        weixinContent.setTargetUrl(url);
        weixinContent.setShareImage(new UMImage(SetupAboutActivity.this, R.drawable.icon_square));

        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(content);
        // 设置朋友圈title
        circleMedia.setTitle("车惠卖车通");
        // 设置分享内容跳转URL
        circleMedia.setTargetUrl(url);
        circleMedia.setShareImage(new UMImage(SetupAboutActivity.this, R.drawable.icon_square));
        // 首先在您的Activity中添加如下成员变量
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.setShareMedia(weixinContent);
        mController.setShareMedia(circleMedia);
        mController.getConfig().removePlatform(SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);
    }*/
    private void showCustomUI() {
        @SuppressWarnings("deprecation")
        int width = getWindowManager().getDefaultDisplay().getWidth();
        PoPShareWindowManager.getInstance().init(getApplicationContext(), width, ViewGroup.LayoutParams.WRAP_CONTENT,
                R.layout.pop_share_wechat);
        PoPShareWindowManager.getInstance().showPopAllLocation(tv_invite_name, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        PoPShareWindowManager.getInstance().OnClickWechat(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 调用直接分享
//                mController.directShare(InviteActivity.this, SHARE_MEDIA.WEIXIN, mShareListener);
                Toast.makeText(InviteActivity.this, "分享给朋友，环境搭建中...", Toast.LENGTH_SHORT).show();
            }
        });
        PoPShareWindowManager.getInstance().OnClickWechatCircle(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 调用直接分享
//                mController.directShare(InviteActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, mShareListener);
                Toast.makeText(InviteActivity.this, "分享到朋友圈,环境搭建中...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
