package com.yuqun.main.ui.mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.yuqun.main.ui.model.CityModel;
import com.yuqun.main.ui.model.EducationModel;
import com.yuqun.main.ui.model.JobModel;
import com.yuqun.main.ui.model.RevenueModel;
import com.yuqun.main.ui.model.TagModel;
import com.yuqun.main.utils.DateTimePickDialogUtil;
import com.yuqun.main.utils.JsonUtil;
import com.yuqun.main.view.PoPReg2Manager;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 修改资料
 * Created by Administrator on 2016/3/10.
 */
public class ModifyInfo extends BaseActivity implements View.OnClickListener {
    /*用户属性*/
    private TextView tv_phone, tv_name, tv_sex, tv_birth, tv_city, tv_salary, tv_job, tv_edu;
    private LinearLayout lv_birth, lv_salary, lv_job, lv_edu, lv_sex, lv_city;
    /*生日*/
    private String dateStr;
    private PoPReg2Manager modifyPriceManager;
    private List<CityModel> list;
    private List<JobModel> list_job;
    private List<EducationModel> list_education;
    private List<RevenueModel> list_revenue;
    /*保存修改按钮*/
    private TextView tv_modify_info;
    /*用于存放原本的、选中的标签*/
    private List<TagModel> mOriginalTagList = new ArrayList<>();
    /*上传时的参数*/
    private String str_tags = "";
    private String str_gender = "";
    private String str_birthday = "";
    private String str_Education = "";
    private String str_Tel = "";
    private String str_Revenue = "";
    private String str_cityID = "";
    private String str_Job = "";
    private String name = "";


    private Handler city_handler = new Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(ModifyInfo.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    list = JsonUtil.parseFromJsonToList(json, CityModel.class);
                    System.out.print(list.size());
                    break;
            }
        }
    };
    private Handler job_handler = new Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(ModifyInfo.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    list_job = JsonUtil.parseFromJsonToList(json, JobModel.class);

                    break;
            }
        }
    };
    private Handler education_handler = new Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(ModifyInfo.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    list_education = JsonUtil.parseFromJsonToList(json, EducationModel.class);

                    break;
            }
        }
    };
    private Handler revenue_handler = new Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(ModifyInfo.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    list_revenue = JsonUtil.parseFromJsonToList(json, RevenueModel.class);
                    break;
            }
        }
    };
    int type = 0;
    private Handler handler_tags = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    Toast.makeText(ModifyInfo.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
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

    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    if (null != msg.obj) {
                        Toast.makeText(ModifyInfo.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    SharePreferenceManager.getInstance().setString(CommonData.USER_NAME, name);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_GENDER, str_gender);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_BIRTHDAY, str_birthday);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_CITYSTR, city.substring(2));
                    SharePreferenceManager.getInstance().setString(CommonData.USER_JOBSTR, job);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_EDUSTR, education);
                    SharePreferenceManager.getInstance().setString(CommonData.USER_REV, Revenue);
                    finish();
                    break;
            }
        }
    };
    private CustomAutoChangeLine2 autoChangeLine;
    private CustomAutoChangeLine originalChangeLine;
    private ListView listView;
    private String job, Revenue, city, education;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_modify_info);
        initViews();
        initDatas();
        bindListener();
    }

    @Override
    public void initViews() {
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetJobList, null, job_handler);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetEducationList, null, education_handler);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetCitys, null, city_handler);
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetRevenueList, null, revenue_handler);
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_phone = (TextView) findViewById(R.id.tv_info_phone);
        String str_phone = SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE, "");
        tv_phone.setText(str_phone);
        tv_name = (TextView) findViewById(R.id.tv_info_name);
        tv_job = (TextView) findViewById(R.id.tv_info_job);
        tv_salary = (TextView) findViewById(R.id.tv_info_salary);
        tv_city = (TextView) findViewById(R.id.tv_info_city);
        tv_edu = (TextView) findViewById(R.id.tv_info_edu);
        tv_sex = (TextView) findViewById(R.id.tv_info_sex);
        tv_birth = (TextView) findViewById(R.id.tv_info_birth);
        lv_birth = (LinearLayout) findViewById(R.id.lv_info_birth);
        lv_sex = (LinearLayout) findViewById(R.id.lv_info_sex);
        lv_job = (LinearLayout) findViewById(R.id.lv_info_job);
        lv_salary = (LinearLayout) findViewById(R.id.lv_info_salary);
        lv_city = (LinearLayout) findViewById(R.id.lv_info_city);
        lv_edu = (LinearLayout) findViewById(R.id.lv_info_edu);

        String str_name = SharePreferenceManager.getInstance().getString(CommonData.USER_NAME, "");
        tv_name.setText(str_name);
        String str_city = SharePreferenceManager.getInstance().getString(CommonData.USER_CITYSTR, "");

        tv_city.setText(getPinYinHeadChar(getPinYin(str_city).substring(0, 1)).toUpperCase() + " " + str_city);
        String str_job = SharePreferenceManager.getInstance().getString(CommonData.USER_JOBSTR, "");
        tv_job.setText(str_job);
        String str_salary = SharePreferenceManager.getInstance().getString(CommonData.USER_REV, "");

        tv_salary.setText(str_salary);
        String gender = SharePreferenceManager.getInstance().getString(CommonData.USER_GENDER, "");
        if (gender.equals("1")) {
            gender = "男";
        } else {
            gender = "女";
        }
        tv_sex.setText(gender);
        String str_edu = SharePreferenceManager.getInstance().getString(CommonData.USER_EDUSTR, "");
        tv_edu.setText(str_edu);
        Log.d("Jessine", "str_rev=" + str_salary + "\nstr_edu=" + str_edu);
        dateStr = SharePreferenceManager.getInstance().getString(CommonData.USER_BIRTH, "");
        tv_birth.setText(dateStr);
        lv_birth.setOnClickListener(this);
        lv_sex.setOnClickListener(this);
        lv_job.setOnClickListener(this);
        lv_salary.setOnClickListener(this);
        lv_edu.setOnClickListener(this);
        lv_city.setOnClickListener(this);
        tv_modify_info = (TextView) findViewById(R.id.tv_modify_info);
        tv_modify_info.setOnClickListener(this);


        /*初始化mOriginalTagList，即登录返回的标签集合*/
        mOriginalTagList.addAll(YuQunApplication.modelList);
        originalChangeLine = (CustomAutoChangeLine) findViewById(R.id.mine_lyt_original);
        initTags(mOriginalTagList);
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

    @Override
    public void bindListener() {

    }

    @Override
    public void initDatas() {

    }

    /**
     * 动态加载pop中的组件
     */
    private void dynamicInitWidegts(CustomAutoChangeLine2 lyt, final TagModel tagModel, int type) {

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(10, 10, 10, 10);
        final TextView textView = new TextView(ModifyInfo.this);
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

    }

    /**
     * 动态加载组件
     */
    private void dyInitOriginalWidegts(CustomAutoChangeLine lyt, String text, int type) {

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(10, 10, 10, 10);
        TextView textView = new TextView(ModifyInfo.this);
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

    }

    /***
     * 设置pop
     */
    private void setPopModifyWidth() {
        modifyPriceManager = new PoPReg2Manager();
        @SuppressWarnings("deprecation")
        int width = ModifyInfo.this.getWindowManager().getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
        int height = ModifyInfo.this.getWindowManager().getDefaultDisplay().getHeight();
        modifyPriceManager.init(ModifyInfo.this, width, height,
                R.layout.pop_tag);
        modifyPriceManager.showPopAllLocation(tv_name, Gravity.CENTER, 0, 0);
        initPopWidgets();
    }

    private void initPopWidgets() {
        View updataForm = modifyPriceManager.getView();
        autoChangeLine = (CustomAutoChangeLine2) updataForm.findViewById(R.id.mine_lyt_tags);
        ImageView img_close = (ImageView) updataForm.findViewById(R.id.pop_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPriceManager.dismissPop();
            }
        });
        TextView tv_save_tags = (TextView) updataForm.findViewById(R.id.tv_save_tags);
        tv_save_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPriceManager.dismissPop();
                initTags(mOriginalTagList);

            }
        });
        /*获取标签*/
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.GetTypeOfUserTags, null, handler_tags);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_info_birth:
                dateStr = tv_birth.getText().toString();
                dateStr = dateStr.substring(0, 4) + "年" + dateStr.substring(5, 7) + "月" + dateStr.substring(8, 10) + "日";
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        ModifyInfo.this, dateStr);
                dateTimePicKDialog.dateTimePicKDialog(tv_birth);
                break;
            /*性别*/
            case R.id.lv_info_sex:
                setPopCitys(R.id.tv_gender_pv);
                break;
            /*城市*/
            case R.id.lv_info_city:
                setPopCitys(R.id.tv_phone_pv);
                break;
            /*学历*/
            case R.id.lv_info_edu:
                setPopCitys(R.id.tv_education_pv);
                break;
            /*职业*/
            case R.id.lv_info_job:
                setPopCitys(R.id.tv_job_pv);
                break;
            case R.id.lv_info_salary:
                setPopCitys(R.id.tv_revenue_pv);
                break;
            case R.id.tv_modify_info:
                /*获取每一个参数的id*/
                education = tv_edu.getText().toString();
                job = tv_job.getText().toString();
                Revenue = tv_salary.getText().toString();
                city = tv_city.getText().toString();
                if (list_education != null) {
                    for (int i = 0; i < list_education.size(); i++) {
                        if (list_education.get(i).getEducationName().equals(education)) {
                            str_Education = list_education.get(i).getID();
                        }
                    }
                }
                if (list_job != null) {
                    for (int i = 0; i < list_job.size(); i++) {
                        if (list_job.get(i).getJobName().equals(job)) {
                            str_Job = list_job.get(i).getID();
                        }
                    }
                }
                if (list_revenue != null) {
                    for (int i = 0; i < list_revenue.size(); i++) {
                        if (list_revenue.get(i).getRangeName().equals(Revenue)) {
                            str_Revenue = list_revenue.get(i).getID();
                        }
                    }
                }
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getCityName().equals(city.substring(2).trim())) {
                            str_cityID = list.get(i).getID();
                        }
                    }
                }
                str_tags = "";
                for (int i = 0; i < mOriginalTagList.size(); i++) {
                    str_tags = str_tags + mOriginalTagList.get(i).getID() + ",";
                }
                str_tags = str_tags.substring(0, str_tags.length() - 1);
                str_Tel = SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE, "");
                str_birthday = tv_birth.getText().toString();
                str_gender = tv_sex.getText().toString();
                name = tv_name.getText().toString();
                String phoneNum = SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE, "");
                HashMap<String, String> map = new HashMap<>();
                map.put("tags", str_tags);
                map.put("gender", str_gender);
                map.put("birthday", str_birthday);
                map.put("Education", str_Education);
                map.put("Revenue", str_Revenue);
                map.put("Tel", phoneNum);
                map.put("CityID", str_cityID);
                map.put("Job", str_Job);
                map.put("name", name);
                System.out.print("params=" + str_tags + "\ngender==" + str_gender + "\nstr_birthday==" + "str_tel" + str_Tel + str_birthday + "\nstr_Education==" + str_Education + "\nstr_Revenue==" + str_Revenue + "\nstr_cityID=" + str_cityID + "\nstr_job=" + str_Job);
                HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.UpdateUserInfoWithNoImg, map, handler);
                break;
            default:
                break;
        }
    }


    /**
     * 将汉字转换为全拼
     *
     * @param src
     * @return String
     */
    public static String getPinYin(String src) {
        char[] t1 = null;
        t1 = src.toCharArray();
        // System.out.println(t1.length);
        String[] t2 = new String[t1.length];
        // System.out.println(t2.length);
        // 设置汉字拼音输出的格式
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                // System.out.println(t1[i]);
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
                    t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
                } else {
                    // 如果不是汉字字符，直接取出字符并连接到字符串t4后
                    t4 += Character.toString(t1[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return t4;
    }

    /**
     * 提取每个汉字的首字母
     *
     * @param str
     * @return String
     */
    public static String getPinYinHeadChar(String str) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
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
            //city城市
            case R.id.tv_phone_pv:
                modifyPriceManager.init(this, width, height,
                        R.layout.pop_reg2_city2);
                modifyPriceManager.showPopAllLocation(tv_phone, Gravity.CENTER, 0, 0);
                listView = modifyPriceManager.getListView();
                listView.setAdapter(new Reg2CitysAdapter(list, ModifyInfo.this));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyPriceManager.dismissPop();
                        CityModel cityModel = list.get(position);
                        tv_city.setText(cityModel.getFirstLetter() + " " + cityModel.getCityName());
                    }
                });
                break;
            //性别
            case R.id.tv_gender_pv:
                modifyPriceManager.init(this, width, height,
                        R.layout.pop_reg2_citys);
                modifyPriceManager.showPopAllLocation(tv_phone, Gravity.CENTER, 0, 0);
                listView = modifyPriceManager.getListView();
                List list_gender = new ArrayList<String>();
                list_gender.add("1");// 男
                list_gender.add("2");//女
                listView.setAdapter(new Reg2GenderAdapter(list_gender, ModifyInfo.this));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyPriceManager.dismissPop();
                        String gender = position == 1 ? gender = "男" : "女";
                        tv_sex.setText(gender);
                    }
                });
                break;
            //工作
            case R.id.tv_job_pv:
                modifyPriceManager.init(this, width, height,
                        R.layout.pop_reg2_citys);
                modifyPriceManager.showPopAllLocation(tv_phone, Gravity.CENTER, 0, 0);
                listView = modifyPriceManager.getListView();
                listView.setAdapter(new Reg2JobAdapter(list_job, ModifyInfo.this));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyPriceManager.dismissPop();
                        tv_job.setText(list_job.get(position).getJobName());
                    }
                });
                break;
            //学历
            case R.id.tv_education_pv:
                modifyPriceManager.init(this, width, height,
                        R.layout.pop_reg2_citys);
                modifyPriceManager.showPopAllLocation(tv_phone, Gravity.CENTER, 0, 0);
                listView = modifyPriceManager.getListView();
                listView.setAdapter(new Reg2EducationAdapter(list_education, ModifyInfo.this));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyPriceManager.dismissPop();
                        tv_edu.setText(list_education.get(position).getEducationName());
                    }
                });
                break;
            //收入
            case R.id.tv_revenue_pv:
                modifyPriceManager.init(this, width, height,
                        R.layout.pop_reg2_citys);
                modifyPriceManager.showPopAllLocation(tv_phone, Gravity.CENTER, 0, 0);
                listView = modifyPriceManager.getListView();
                listView.setAdapter(new Reg2ReveneAdapter(list_revenue, ModifyInfo.this));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyPriceManager.dismissPop();
                        tv_salary.setText(list_revenue.get(position).getRangeName());
                    }
                });
                break;
        }


    }
}
