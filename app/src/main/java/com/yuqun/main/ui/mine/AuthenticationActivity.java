package com.yuqun.main.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yuqun.main.MainActivity;
import com.yuqun.main.R;
import com.yuqun.main.app.YuQunApplication;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.ui.model.AuthModel;
import com.yuqun.main.ui.model.UserModle;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.utils.LogN;
import com.yuqun.main.utils.PictureUtil;
import com.yuqun.main.utils.StringUtils;
import com.yuqun.main.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 实名认证
 * Created by Administrator on 2016/4/20.
 */
public class AuthenticationActivity extends BaseActivity implements View.OnClickListener {


    /**
     * 图片返回正确值
     */
    private static final int REQUEST_IMAGE = 2;
    private ImageView img_select_front, img_select_back;
    private ArrayList<String> mSelectPath;

    /**
     * 上传图片集合
     */
    private ArrayList<String> mUploadImages = new ArrayList<>();
    private BitmapUtils bitmapUtils;
    private boolean choiceImage = true;
    private TextView mUploadImage;
    private String uid;
    private String name, sfz;
    private EditText et_name, et_sfz;
    private String authenticationState;//认证的状态值：0，正在审核；1，通过审核；2，审核失败
    private LinearLayout ll_no_renzheng;
    private LinearLayout ll_yes_renzheng;
    private TextView tv_renzheng_result;
    private android.os.Handler handler2 = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {

                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    List<UserModle> list = JsonUtil.parseFromJsonToList(json, UserModle.class);
                    UserModle userModle = list.get(0);
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
                    finish();
                    break;
            }
        }
    };
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(AuthenticationActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    et_name.setEnabled(true);
                    et_sfz.setEnabled(true);
                    img_select_front.setClickable(true);
                    img_select_back.setClickable(true);

                    ll_yes_renzheng.setVisibility(View.GONE);
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    List<AuthModel> list = JsonUtil.parseFromJsonToList(json, AuthModel.class);
                    AuthModel authModel = list.get(0);
                    authenticationState = authModel.getAuthenticationState();
                    if (authenticationState.equals("0")) {
                        setData(authModel);
                        tv_renzheng_result.setText("认证中");
                    } else if (authenticationState.equals("1")) {
                        setData(authModel);
                        tv_renzheng_result.setText("审核通过");
                    }else if(authenticationState.equals("2")){
                        setData(authModel);
                        tv_renzheng_result.setText(authModel.getFailedReson());


                        ll_no_renzheng.setVisibility(View.VISIBLE);
                        ll_yes_renzheng.setVisibility(View.VISIBLE);
                        mUploadImage.setVisibility(View.VISIBLE);
                        mUploadImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setContentView(R.layout.layout_auth);
                            }
                        });
                        mUploadImage.setText("重新认证");
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
        setContentView(R.layout.layout_auth);
        initDatas();
        bitmapUtils = new BitmapUtils(this);
        initViews();
        bindListener();
    }

    @Override
    public void initViews() {
        img_select_front = (ImageView) findViewById(R.id.img_select_front);
        img_select_back = (ImageView) findViewById(R.id.img_select_back);
        mUploadImage = (TextView) findViewById(R.id.auth_txt_upload);
        et_name = (EditText) findViewById(R.id.et_auth_name);
        et_sfz = (EditText) findViewById(R.id.et_auth_sfz);

        ll_no_renzheng = (LinearLayout) findViewById(R.id.ll_no_renzheng);
        ll_yes_renzheng = (LinearLayout) findViewById(R.id.ll_yes_renzheng);

        tv_renzheng_result = (TextView) findViewById(R.id.tv_renzheng_result);
    }

    @Override
    public void bindListener() {
        img_select_back.setOnClickListener(this);
        img_select_front.setOnClickListener(this);
        mUploadImage.setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
    }

    @Override
    public void initDatas() {
        uid = SharePreferenceManager.getInstance().getString(CommonData.USER_ID, "");
        //获取实名认证信息，从而决定样式怎么显示
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.getRenZhengInfo, map, handler);
    }

    private void setData(AuthModel authModel) {
        et_name.setText(authModel.getRealName());
        et_name.setEnabled(false);

        et_sfz.setText(authModel.getCertifyCode());
        et_sfz.setEnabled(false);

        String photo = authModel.getPhoto();
        String[] split = photo.split(",");
        LogN.d("image", split[0]);
        LogN.d("image", split[1]);
        bitmapUtils.display(img_select_front, split[0]);
        bitmapUtils.display(img_select_back, split[1]);

        img_select_front.setClickable(false);
        img_select_back.setClickable(false);
        mUploadImage.setVisibility(View.GONE);
        ll_yes_renzheng.setVisibility(View.VISIBLE);
        mUploadImage.setText("提交认证");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //选择身份证正面
            case R.id.img_select_front:
                choicePicFromGallary();
                choiceImage = true;
                break;

            //选择身份证反面
            case R.id.img_select_back:
                choicePicFromGallary();
                choiceImage = false;
                break;

            //上传
            case R.id.auth_txt_upload:
                name = et_name.getText().toString().trim();
                sfz = et_sfz.getText().toString().trim();
                if (StringUtils.isEmpty(name)) {
                    Toast.makeText(AuthenticationActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (StringUtils.isEmpty(sfz)) {
                    Toast.makeText(AuthenticationActivity.this, "请输入身份证号", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else if (sfz.length() != 18) {
                    Toast.makeText(AuthenticationActivity.this, "请输入正确的身份证号", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else if (mUploadImages.isEmpty() && mUploadImages.size() != 2) {
                    Toast.makeText(AuthenticationActivity.this, "请选择图片", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                uploadImageView(uid, name, sfz, "," +
                        PictureUtil.bitmapToString(mUploadImages.get(0))
                        + "|" + "," +
                        PictureUtil.bitmapToString(mUploadImages.get(1))
                );
                break;
            case R.id.img_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 从图库选择图片上传
     */
    private void choicePicFromGallary() {
        int selectedMode = MultiImageSelectorActivity.MODE_MULTI;

        int maxNum = 1;

        boolean showCamera = true;

        Intent intent = new Intent(AuthenticationActivity.this,
                MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // 默认选择
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST,
                    mSelectPath);
        }

        startActivityForResult(intent, REQUEST_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

                if (choiceImage) {
                    bitmapUtils.display(img_select_front, mSelectPath.get(0));
                    mUploadImages.add(mSelectPath.get(0));
                } else {
                    bitmapUtils.display(img_select_back, mSelectPath.get(0));
                    mUploadImages.add(mSelectPath.get(0));
                }

            }
        }
    }


    /**
     * 上传认证图片
     *
     * @param uid    用户ID
     * @param name   用户名
     * @param sfz    身份证号
     * @param base64 图片base64的String
     */
    private void uploadImageView(final String uid, final String name, final String sfz, final
    String base64) {

        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.common_net_notwork, Toast.LENGTH_SHORT).show();
            return;
        }

        //传递参数
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", uid);
        params.addBodyParameter("name", name);
        params.addBodyParameter("sfz", sfz);
        params.addBodyParameter("img", base64);

        //使用xUtils网络请求框架
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, CommonData.SERVER_ADDRESS + IRequestAction
                        .userRenZheng, params,
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

                        if (StringUtils.isEmpty(responseInfo.result.toString())) {

                            //返回为空不处理

                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(responseInfo.result
                                        .toString());
                                String RC = jsonObject.optString("RC");
                                String ED = jsonObject.optString("ED");

                                if (RC.equals("1")) {

                                    login();
                                } else {
                                    Toast.makeText(AuthenticationActivity.this, ED, Toast
                                            .LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dismissWaitDialog();
                    }


                });

    }

    private void login() {

        HashMap<String, String> map = new HashMap<>();
        map.put("tel", SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE,""));
        map.put("pwd",  SharePreferenceManager.getInstance().getString(CommonData.USER_PWD,""));
        map.put("osType","Android");
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.request_login, map, handler2);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUploadImages.clear();
    }
}
