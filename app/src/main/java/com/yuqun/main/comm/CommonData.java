package com.yuqun.main.comm;

/**
 * @author zzp
 */
public class CommonData {

    /**
     * 包名
     */
    public static final String PACKNAME = "com.yuqun.main";

    public static final int HTTP_HANDLE_SUCCESS = 1;
    /**
     * 数据获取失败
     */
    public static final int HTTP_HANDLE_FAILE = 0;
    public static final String USER_ID = "user_id";
    public static final String USER_PHONE = "user_phone";
    public static final String USER_PWD = "user_pwd";
    public static final String USER_AUTO = "USER_PWD";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_LEVEL = "USER_LEVEL";
    public static final String USER_GENDER = "user_gender";
    public static final String USER_CITYID = "user_cityid";
    public static final String USER_JOB = "user_job";
    public static final String USER_BIRTHDAY = "user_birthday";
    public static final String USER_REGDATE = "USER_REGDATE";
    public static final String USER_IP = "USER_IP";
    public static final String USER_MONEY = "USER_MONEY";
    public static final String USER_POINT = "USER_POINT";
    public static final String USER_JOBSTR = "USER_JOBSTR";
    public static final String USER_EDUSTR = "USER_EDUSTR";
    public static final String USER_CITYSTR = "USER_CITYSTR";
    public static final String USER_BIRTH = "USER_BIRTH";
    public static final String USER_REV = "USER_REV";
    public static final String USER_TAGS = "USER_TAGS";
    public static final String CITY_JSON = "CITY_JSON";
    public static final String WX_HEADER = "WX_HEADER";
    public static final String WX_ID = "WX_ID";
    public static final String UNION_ID = "UNION_ID";
    public static final String AUTH = "AUTH";
    /**
     * 分页数目，暂定为30
     */
    public static final String PageSize = "30";
    public static final String REG_CITY = "reg_city";
    public static final String Invite_listview = "Invite_listview";
    public static final String WXMessage = "WXMessage";
    public static final String CircleVisible = "CircleVisible";
    public static final String CircleInVisible = "CircleInVisible";

    /**
     * 发布接口
     * <p/>
     * http://yuqun.chehui.com/
     * <p/>
     * http://yuqunwebservice.17yuqun.com/
     * <p/>
     * http://192.168.1.252:8029/
     * </P>
     */
    public static String SERVER_ADDRESS = "http://yuqunwebservice.17yuqun.com/";

    /**
     * 签到网页
     * http://192.168.1.252:8172/qiandaotest.aspx?uid=9
     */
    public static String SIGNURL = "http://partner.17yuqun.com/qiandaoForApp.aspx?uid=";

    /**
     * 判断版本更新，是首页还是检测版本页面
     */
    public static boolean isUpdate = false;

    /**
     * 判断是分享，还是wx登陆
     */
    public static boolean isShare = false;
}
