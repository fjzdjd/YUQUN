package com.yuqun.main.ui.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqun.main.R;
import com.yuqun.main.app.YuQunApplication;
import com.yuqun.main.base.BaseActivity;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.component.CustomAutoChangeLine;
import com.yuqun.main.component.CustomAutoChangeLine2;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.ui.adapter.Reg2CitysAdapter;
import com.yuqun.main.ui.adapter.Reg2EducationAdapter;
import com.yuqun.main.ui.adapter.Reg2GenderAdapter;
import com.yuqun.main.ui.adapter.Reg2JobAdapter;
import com.yuqun.main.ui.adapter.Reg2ReveneAdapter;
import com.yuqun.main.ui.mine.ModifyInfo;
import com.yuqun.main.ui.model.CityModel;
import com.yuqun.main.ui.model.EducationModel;
import com.yuqun.main.ui.model.JobModel;
import com.yuqun.main.ui.model.RevenueModel;
import com.yuqun.main.ui.model.TagModel;
import com.yuqun.main.utils.DateTimePickDialogUtil;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.view.PoPReg2Manager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/3/11 0011.
 */
public class RegisterActivity2 extends BaseActivity implements View.OnClickListener {
    private TextView tv_phone;
    private TextView tv_gender_pv;
    private TextView tv_job_pv;
    private TextView tv_education_pv;
    private TextView tv_revenue_pv;
    private TextView tv_birth_pv;
    private TextView tv_next;
    private TextView tv_reg2_to_login;
    private List<CityModel> list;
    private List<JobModel> list_job;
    private List<EducationModel> list_education;
    private List<RevenueModel> list_revenue;
    private List<String> data = new ArrayList<String>();
    private PoPReg2Manager modifyPriceManager;
    private PoPReg2Manager modifyPriceManager2;
    private ListView listView;
    private CustomAutoChangeLine2 autoChangeLine;
    private CustomAutoChangeLine originalChangeLine;
    private CityModel currentCityModel;
    private int type = 0;
    private String str_tags = "";
    private TextView tv_reg2_tag_add;
    /*用于存放原本的、选中的标签*/
    private List<TagModel> mOriginalTagList = new ArrayList<>();
    private android.os.Handler city_handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(RegisterActivity2.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    list = JsonUtil.parseFromJsonToList(json, CityModel.class);
                    System.out.print(list.size());
                    for (int i = 0; i < list.size(); i++) {
                        data.add(list.get(i).getCityName());
                    }
                    break;
            }
        }
    };
    private android.os.Handler job_handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(RegisterActivity2.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    list_job = JsonUtil.parseFromJsonToList(json, JobModel.class);

                    break;
            }
        }
    };
    private android.os.Handler education_handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(RegisterActivity2.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    list_education = JsonUtil.parseFromJsonToList(json, EducationModel.class);

                    break;
            }
        }
    };
    private android.os.Handler revenue_handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(RegisterActivity2.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    list_revenue = JsonUtil.parseFromJsonToList(json, RevenueModel.class);

                    break;
            }
        }
    };
    private android.os.Handler modify_handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(RegisterActivity2.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    activityManager.startNextActivity(LoginActivity.class);
                    activityManager.popActivity(RegisterActivity2.this);
                    break;
            }
        }
    };

    private Handler handler_tags = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    Toast.makeText(RegisterActivity2.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    final List<TagModel> mTagList = JsonUtil.parseFromJsonToList(json, TagModel.class);
                    if (mTagList.size() != 0) {
                        List<String> mOriStrList = new ArrayList<>();
                        for (int i = 0; i < mTagList.size(); i++) {
                            final TagModel tagModel = mTagList.get(i);

                            for (int j = 0; j < mOriginalTagList.size(); j++) {
                                mOriStrList.add(mOriginalTagList.get(j).getName());
                            }
                            if (mOriStrList.contains(tagModel.getName()))
                                type = 0;
                            else
                                type = 1;
                            dynamicInitWidegts(autoChangeLine, tagModel, type);
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    };
    private JobModel currentJobModel;
    private EducationModel currentEducationModel;
    private RevenueModel currentRevenueModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        initViews();
        bindListener();
        initDatas();
    }

    @Override
    public void initViews() {
        Bundle extras = getIntent().getExtras();
        final String name = (String) extras.get("Name");
        final String Tel = (String) extras.get("Tel");
        final String PWD = (String) extras.get("PWD");
        final String RecommendedUserID = (String) extras.get("RecommendedUserID");
        final String SMSCode = (String) extras.get("SMSCode");

        tv_phone = (TextView) findViewById(R.id.tv_phone_pv);
        tv_gender_pv = (TextView) findViewById(R.id.tv_gender_pv);
        tv_job_pv = (TextView) findViewById(R.id.tv_job_pv);
        tv_education_pv = (TextView) findViewById(R.id.tv_education_pv);
        tv_revenue_pv = (TextView) findViewById(R.id.tv_revenue_pv);
        tv_birth_pv = (TextView) findViewById(R.id.tv_birth_pv);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_reg2_to_login = (TextView) findViewById(R.id.tv_reg2_to_login);
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_phone.getText().toString() == null || tv_phone.getText().toString() == "") {
                    Toast.makeText(RegisterActivity2.this, "请选择城市", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tv_gender_pv.getText().toString() == null || tv_gender_pv.getText().toString() == "") {
                    Toast.makeText(RegisterActivity2.this, "请选择性别", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tv_birth_pv.getText().toString() == null || tv_birth_pv.getText().toString() == "") {
                    Toast.makeText(RegisterActivity2.this, "请选择出生日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tv_job_pv.getText().toString() == null || tv_job_pv.getText().toString() == "") {
                    Toast.makeText(RegisterActivity2.this, "请选择职业", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tv_education_pv.getText().toString() == null || tv_education_pv.getText().toString() == "") {
                    Toast.makeText(RegisterActivity2.this, "请选择学历", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tv_revenue_pv.getText().toString() == null || tv_revenue_pv.getText().toString() == "") {
                    Toast.makeText(RegisterActivity2.this, "请选择收入范围", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mOriginalTagList == null || mOriginalTagList.size() <= 0) {
                    Toast.makeText(RegisterActivity2.this, "请选择标签", Toast.LENGTH_SHORT).show();
                    return;
                }

                String tel = SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE, "");
                for (int i = 0; i < mOriginalTagList.size(); i++) {
                    str_tags = str_tags + mOriginalTagList.get(i).getID() + ",";
                }
                str_tags = str_tags.substring(0, str_tags.length() - 1);
                HashMap<String, String> map = new HashMap<>();

                map.put("Name", name);
                map.put("Tel", Tel);
                map.put("PWD", PWD);
                map.put("RecommendedUserID", RecommendedUserID);
                map.put("SMSCode", SMSCode);

                map.put("tags", str_tags);
                map.put("gender", tv_gender_pv.getText().toString().trim());
                map.put("birthday", tv_birth_pv.getText().toString().trim());
                map.put("Education", currentEducationModel.getID());
                map.put("Revenue", currentRevenueModel.getID());
                map.put("CityID", currentCityModel.getID());
                map.put("Job", currentJobModel.getID());
                map.put("OsType", "Android");
                for (String key : map.keySet()) {
                    System.out.println("key= " + key + " and value= " + map.get(key));
                }
                HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.UserRegDeliver, map, modify_handler);
            }
        });

        tv_reg2_tag_add = (TextView) findViewById(R.id.tv_reg2_tag_add);
        /*初始化mOriginalTagList，即登录返回的标签集合*/
        mOriginalTagList.addAll(YuQunApplication.modelList);
        originalChangeLine = (CustomAutoChangeLine) findViewById(R.id.mine_lyt_original);
        initTags(mOriginalTagList);

        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void bindListener() {
        tv_phone.setOnClickListener(this);
        tv_gender_pv.setOnClickListener(this);
        tv_job_pv.setOnClickListener(this);
        tv_education_pv.setOnClickListener(this);
        tv_revenue_pv.setOnClickListener(this);
        tv_birth_pv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(RegisterActivity2.this, "1950年01月01日");
                dateTimePicKDialog.dateTimePicKDialog(tv_birth_pv);
            }
        });
        tv_reg2_to_login.setOnClickListener(this);
        tv_reg2_tag_add.setOnClickListener(this);

    }

    @Override
    public void initDatas() {
        getCityList();
    }


    private void getCityList() {
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetCitys, null, city_handler);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetJobList, null, job_handler);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetEducationList, null, education_handler);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetRevenueList, null, revenue_handler);
    }

    /***
     * 设置pop
     */
    private void setPopModifyWidth() {
        modifyPriceManager2 = new PoPReg2Manager();
        @SuppressWarnings("deprecation")
        int width = getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = getWindowManager().getDefaultDisplay().getHeight();
        modifyPriceManager2.init(this, width, height,
                R.layout.pop_tag);
        modifyPriceManager2.showPopAllLocation(tv_phone, Gravity.CENTER, 0, 0);
        initPopWidgets();
    }

    private void initPopWidgets() {
        View updataForm = modifyPriceManager2.getView();
        autoChangeLine = (CustomAutoChangeLine2) updataForm.findViewById(R.id.mine_lyt_tags);
        ImageView img_close = (ImageView) updataForm.findViewById(R.id.pop_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPriceManager2.dismissPop();
            }
        });
        TextView tv_save_tags = (TextView) updataForm.findViewById(R.id.tv_save_tags);
        tv_save_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPriceManager2.dismissPop();
                initTags(mOriginalTagList);
            }
        });
        /*获取标签*/
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetTypeOfUserTags, null, handler_tags);
    }

    /**
     * 初始化标签部分的显示情况
     *
     * @param list
     */
    private void initTags(List<TagModel> list) {
        originalChangeLine.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            dyInitOriginalWidegts(originalChangeLine, list.get(i).getName(), 0);
        }
        dyInitOriginalWidegts(originalChangeLine, "+添加标签", 1);
    }

    /**
     * 动态加载pop中的组件
     */
    private Void dynamicInitWidegts(CustomAutoChangeLine2 lyt, final TagModel tagModel, int type) {

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(10, 10, 10, 10);
        final TextView textView = new TextView(RegisterActivity2.this);
        /*设为选中状态*/
        if (type == 0) {
            textView.setBackgroundResource(R.drawable.task_corners_shape_blue);
            textView.setTextColor(getResources().getColor(R.color.white));
        } else {
            /*设为非选中状态*/
            textView.setBackgroundResource(R.drawable.task_corners_shape);
            textView.setTextColor(getResources().getColor(R.color.maincolor));
        }

        textView.setPadding(0, 0, 0, 0);
        textView.setLayoutParams(llp);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(12);
        textView.setText(tagModel.getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> mStrList = new ArrayList<String>();
                for (int i = 0; i < mOriginalTagList.size(); i++) {
                    mStrList.add(mOriginalTagList.get(i).getName());
                    if (mOriginalTagList.get(i).getName().equals(tagModel.getName())) {
                        mOriginalTagList.remove(i);
                          /*设为非选中状态*/
                        textView.setBackgroundResource(R.drawable.task_corners_shape);
                        textView.setTextColor(getResources().getColor(R.color.maincolor));
                    }
                }
                if (!mStrList.contains(tagModel.getName())) {
                    mOriginalTagList.add(tagModel);
                    /*设为选中状态*/
                    textView.setBackgroundResource(R.drawable.task_corners_shape_blue);
                    textView.setTextColor(getResources().getColor(R.color.white));

                }

            }
        });

        lyt.addView(textView);
        return null;
    }

    /**
     * 动态加载组件
     */
    private Void dyInitOriginalWidegts(CustomAutoChangeLine lyt, String text, int type) {

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(10, 10, 10, 10);
        TextView textView = new TextView(RegisterActivity2.this);
        textView.setPadding(0, 0, 0, 0);
        textView.setLayoutParams(llp);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(12);
        textView.setText(text);
        if (type == 0) {
            /*标签内容*/
            textView.setBackgroundResource(R.drawable.task_corners_shape_blue);
            textView.setTextColor(getResources().getColor(R.color.white));
        } else {
            /*选择标签按钮*/
            textView.setBackgroundResource(R.drawable.corners_bg_icon);
            textView.setTextColor(getResources().getColor(R.color.colorGray));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPopModifyWidth();
                }
            });
        }
        lyt.addView(textView);
        return null;
    }

    /***
     * 设置pop
     */
    private void setPopCitys(int type) {
        modifyPriceManager = new PoPReg2Manager();
        @SuppressWarnings("deprecation")
        int width = getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = getWindowManager().getDefaultDisplay().getHeight();

        switch (type) {
            //city
            case R.id.tv_phone_pv:
                modifyPriceManager.init(this, width, height,
                        R.layout.pop_reg2_city2);
                listView = modifyPriceManager.getListView();
                listView.setAdapter(new Reg2CitysAdapter(list, RegisterActivity2.this));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyPriceManager.dismissPop();
                        currentCityModel = list.get(position);
                        tv_phone.setText(currentCityModel.getFirstLetter() + " " + currentCityModel.getCityName());
                    }
                });
                break;
            //性别
            case R.id.tv_gender_pv:
                modifyPriceManager.init(this, width, height,
                        R.layout.pop_reg2_citys);
                listView = modifyPriceManager.getListView();
                List list_gender = new ArrayList<String>();
                list_gender.add("1");// 男
                list_gender.add("2");//女
                listView.setAdapter(new Reg2GenderAdapter(list_gender, RegisterActivity2.this));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyPriceManager.dismissPop();
                        String gender = position == 1 ? gender = "男" : "女";
                        tv_gender_pv.setText(gender);
                    }
                });
                break;
            //工作
            case R.id.tv_job_pv:
                modifyPriceManager.init(this, width, height,
                        R.layout.pop_reg2_citys);
                listView = modifyPriceManager.getListView();
                listView.setAdapter(new Reg2JobAdapter(list_job, RegisterActivity2.this));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyPriceManager.dismissPop();
                        currentJobModel = list_job.get(position);
                        tv_job_pv.setText(currentJobModel.getJobName());
                    }
                });
                break;
            //学历
            case R.id.tv_education_pv:
                modifyPriceManager.init(this, width, height,
                        R.layout.pop_reg2_citys);
                listView = modifyPriceManager.getListView();
                listView.setAdapter(new Reg2EducationAdapter(list_education, RegisterActivity2.this));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyPriceManager.dismissPop();
                        currentEducationModel = list_education.get(position);
                        tv_education_pv.setText(currentEducationModel.getEducationName());
                    }
                });
                break;
            //收入
            case R.id.tv_revenue_pv:
                modifyPriceManager.init(this, width, height,
                        R.layout.pop_reg2_citys);
                listView = modifyPriceManager.getListView();
                listView.setAdapter(new Reg2ReveneAdapter(list_revenue, RegisterActivity2.this));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyPriceManager.dismissPop();
                        currentRevenueModel = list_revenue.get(position);
                        tv_revenue_pv.setText(currentRevenueModel.getRangeName());
                    }
                });
                break;


        }
        modifyPriceManager.showPopAllLocation(tv_phone, Gravity.CENTER, 0, 0);

    }
   /* public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            modifyPriceManager.dismissPop();
            if (CommonData.REG_CITY.equals(intent.getAction())) {
                //选择城市
                CityModel city = (CityModel) intent.getExtras().getSerializable("city");
                tv_phone.setText(city.getFirstLetter()+" " +city.getCityName());
            }
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_phone_pv:
                setPopCitys(R.id.tv_phone_pv);
                break;
            case R.id.tv_gender_pv:
                setPopCitys(R.id.tv_gender_pv);
                break;
            case R.id.tv_job_pv:
                setPopCitys(R.id.tv_job_pv);
                break;
            case R.id.tv_education_pv:
                setPopCitys(R.id.tv_education_pv);
                break;
            case R.id.tv_revenue_pv:
                setPopCitys(R.id.tv_revenue_pv);
                break;
            case R.id.tv_reg2_to_login:
                activityManager.startNextActivity(LoginActivity.class);
                RegisterActivity2.this.finish();
                break;
            //添加标签
            case R.id.tv_reg2_tag_add:
                setPopModifyWidth();
                break;
        }
    }
}
