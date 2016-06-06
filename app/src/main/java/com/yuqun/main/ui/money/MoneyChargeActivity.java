package com.yuqun.main.ui.money;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuqun.main.R;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.utils.Utils;
import com.yuqun.main.wxapi.WeChatConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zzp on 2016/4/8.
 * <p>
 * <P>充值金额</P>
 */
public class MoneyChargeActivity extends BaseActivity implements View.OnClickListener {

    private IWXAPI api;

    private TextView mAssets;
    private EditText mChargeNum;
    private Button mConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recharge);
        api = WXAPIFactory.createWXAPI(this, WeChatConstants.APP_ID);
        initViews();
        bindListener();
        initDatas();
    }

    @Override
    public void initViews() {
        mAssets = (TextView) findViewById(R.id.money_charge_txt_mine_asset);
        mChargeNum = (EditText) findViewById(R.id.money_charge_edt_mine_asset);
        mConfirm = (Button) findViewById(R.id.money_charge_btn_mine_asset);
    }

    @Override
    public void bindListener() {
        mConfirm.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.money_charge_btn_mine_asset:

                if (!Utils.isNetworkAvailable(this)) {
                    Toast.makeText(this, R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(mChargeNum.getText().toString().trim())) {
                    Toast.makeText(this, "请填写正确金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                //传递参数
                RequestParams params = new RequestParams();
                params.addBodyParameter("uid", SharePreferenceManager.getInstance().getString
                        (CommonData
                                .USER_ID, ""));
                params.addBodyParameter("paymoney", mChargeNum.getText().toString().trim());

                //使用xUtils网络请求框架
                HttpUtils http = new HttpUtils();
                http.send(HttpRequest.HttpMethod.POST, CommonData.SERVER_ADDRESS + IRequestAction
                                .WXpay, params,
                        new RequestCallBack<String>() {

                            @Override
                            public void onStart() {
                                super.onStart();
                                //添加progressbar
                                showWaitDialog(R.string.common_upload_requesting);
                            }

                            @Override
                            public void onCancelled() {
                                super.onCancelled();
                            }

                            @Override
                            public void onLoading(long total, long current, boolean isUploading) {
                                super.onLoading(total, current, isUploading);
                            }

                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                dismissWaitDialog();

                                if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseInfo.result
                                                .toString());
                                        String RC = jsonObject.optString("RC");
                                        String ED = jsonObject.optString("ED");
                                        JSONArray jsonArray = jsonObject.optJSONArray("Data");
                                        if (RC.equals("1")) {
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject json = jsonArray.getJSONObject(i);
                                                String appid = json.optString("appid");
                                                String partnerid = json.optString("partnerid");
                                                String prepayid = json.optString("prepayid");
                                                String noncestr = json.optString("nonce_str");
                                                String timestamp = json.optString("timestamp");
                                                String packageValue = json.optString("package");
                                                String sign = json.optString("sign");

                                                payMoney(json, appid, partnerid, prepayid,
                                                        noncestr, timestamp, packageValue, sign);
                                            }
                                        } else {
                                            Toast.makeText(MoneyChargeActivity.this, ED, Toast
                                                    .LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.d("请求数据为空", responseInfo.result.toString());
                                }


                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                dismissWaitDialog();
                            }
                        });


                break;

            default:
                break;
        }
    }

    /**
     * @param jsonContent  返回数据
     * @param appid        参数
     * @param partnerid    参数
     * @param prepayid     参数
     * @param noncestr     参数
     * @param timestamp    参数
     * @param packagevalue 参数
     * @param sign         参数
     */
    private void payMoney(JSONObject jsonContent, String appid, String partnerid, String
            prepayid, String noncestr,
                          String timestamp, String packagevalue, String sign) {

        mConfirm.setClickable(false);

        Toast.makeText(MoneyChargeActivity.this, "获取订单中...",
                Toast.LENGTH_SHORT).show();

        try {
            if (jsonContent != null) {
                if (null != jsonContent) {
                    PayReq req = new PayReq();
                    req.appId = appid;
                    req.partnerId = partnerid;
                    req.prepayId = prepayid;
                    req.nonceStr = noncestr;
                    req.timeStamp = timestamp;
                    req.packageValue = packagevalue;
                    req.sign = sign;
                    Toast.makeText(MoneyChargeActivity.this,
                            "正常调起支付", Toast
                                    .LENGTH_SHORT).show();
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg
                    // .registerApp将应用注册到微信
                    api.sendReq(req);
                } else {
                    Log.d("PAY_GET", "返回错误" + jsonContent.optString
                            ("retmsg"));
                    Toast.makeText(MoneyChargeActivity.this,
                            "返回错误" + jsonContent.optString
                                    ("retmsg"), Toast.LENGTH_SHORT)
                            .show();
                }

            } else {
                Log.d("PAY_GET", "服务器请求错误");
                Toast.makeText(MoneyChargeActivity.this,
                        "服务器请求错误", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            Log.e("PAY_GET", "异常：" + e.getMessage());
            Toast.makeText(MoneyChargeActivity.this, "异常：" +
                    e.getMessage(), Toast
                    .LENGTH_SHORT).show();
        }

        mConfirm.setClickable(true);
    }

    @Override
    public void initDatas() {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }
        //传递参数
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", SharePreferenceManager.getInstance().getString(CommonData
                .USER_ID, ""));

        //使用xUtils网络请求框架
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, CommonData.SERVER_ADDRESS + IRequestAction
                        .GetMoneyDetail, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        //添加progressbar
                        showWaitDialog(R.string.common_upload_requesting);
                    }

                    @Override
                    public void onCancelled() {
                        super.onCancelled();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        dismissWaitDialog();

                        if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                            try {
                                JSONObject jsonObject = new JSONObject(responseInfo.result
                                        .toString());
                                String RC = jsonObject.optString("RC");
                                String ED = jsonObject.optString("ED");
                                JSONArray jsonArray = jsonObject.optJSONArray("Data");
                                if (RC.equals("1")) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonItem = jsonArray.getJSONObject(i);

                                        String myMoney = jsonItem.optString("myMoney");

                                        mAssets.setText("￥ " + myMoney);
                                    }
                                } else {
                                    Toast.makeText(MoneyChargeActivity.this, ED, Toast
                                            .LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("请求数据为空", responseInfo.result.toString());
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dismissWaitDialog();
                    }
                });
    }
}
