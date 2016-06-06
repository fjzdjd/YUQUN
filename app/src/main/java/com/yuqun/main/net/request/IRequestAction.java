package com.yuqun.main.net.request;

/***
 * 请求action
 */
public interface IRequestAction {

    /**
     * 任务列表
     */
    String GetCanAcceptTaskList = "TaskOperation.asmx/GetCanAcceptTaskList";
    /**
     * 登录
     */
    String request_login = "UserOperation.asmx/UserLogin";

    /**
     * 获取可接受任务详细
     */
    String GetDetail = "TaskOperation.asmx/GetDetail";

    /**
     * 取消任务
     */
    String TaskCancel = "TaskOperation.asmx/TaskCancel";

    /**
     * 得到除可接受之外的其余详细
     */
    String GetOrderDetailInfo = "TaskOperation.asmx/GetOrderDetailInfo";

    /**
     * 上传图片
     */
    String TaskOrderFinlishForApp = "PointOperation.asmx/TaskOrderFinlishForApp";



    String userRenZheng="UserOperation.asmx/userRenZheng";

    /**
     * 获取网页内用
     */
    String getWebTitle = "TaskOperation.asmx/getWebTitle";

    /**
     * 立即抢单
     */
    String AcceptTask = "TaskOperation.asmx/AcceptTask";

    /**
     * 我的奖金数
     */
    String GetMoneyDetail = "FundsOperation.asmx/GetMoneyDetail";

    /**
     * 保证金流程
     */
    String getDeposit = "CommenOperation.asmx/getDeposit";

    /**
     * 奖金流水
     */
    String GetFoundsList = "FundsOperation.asmx/GetFundsList";
    /**
     * 获取验证码
     */
    String SendSMSCode = "SmsOperation.asmx/SendSMSCode";
    /**
     * 注册
     */
    String UserReg = "UserOperation.asmx/UserReg";
    /**
     * 注册2
     */
    String UserRegDeliver = "UserOperation.asmx/UserRegDeliver";
    /**
     * 修改密码
     */
    String ChangePWD = "UserOperation.asmx/ChangePWD";

    /**
     * 获取工作
     */
    String GetJobList = "BaseOperation.asmx/GetJobList";
    /**
     * 获取学历
     */
    String GetEducationList = "BaseOperation.asmx/GetEducationList";
    /**
     * 获取薪资
     */
    String GetRevenueList = "BaseOperation.asmx/GetRevenueList";
    /**
     * 奖金问答
     */
    String getConstAnswer = "CommenOperation.asmx/getConstAnswer";
    /**
     * 获取用户标签
     */
    String GetTypeOfUserTags = "TagOperation.asmx/GetTypeOfUserTags";
    String GetCitys = "BaseOperation.asmx/GetCitys";
    /**
     * 获取我的直接代理列表
     */
    String GetUserDirectAgentsSummaryList = "YaoqingOperation.asmx/GetUserDirectAgentsSummaryList";
    /**
     * 修改用户信息 没有头像
     */
    String UpdateUserInfoWithNoImg = "UserOperation.asmx/UpdateUserInfoWithNoImg";
    /**
     * UpdateUserInfoWithOutUserNameNoImg
     */
    String UpdateUserInfoWithOutUserNameNoImg = "UserOperation" +
            ".asmx/UpdateUserInfoWithOutUserNameNoImg";
    /**
     * ForgetPWD
     */
    String ForgetPWD = "UserOperation.asmx/ForgetPWD";
    /**
     * ForgetPWD
     */
    String getConstAnswerYaoqing = "CommenOperation.asmx/getConstAnswerYaoqing";

    /**
     * 获取信誉最大值
     */
    String GetOptions = "UserOperation.asmx/GetOptions";
    /**
     * 任务接单流程
     */
    String getNoti = "CommenOperation.asmx/getNoti";
    /**
     * 获取客服信息
     */
    String getKefu = "CommenOperation.asmx/getKefu";
    /**
     * 共多少条小鱼
     */

    String getAllCount = "UserOperation.asmx/getAllCount";
    /**
     * 获取我的代理数据
     */
    String getMyAgentsInfo = "UserOperation.asmx/GetMyAgentsInfo";
    /**
     * 获取公告
     */
    String getNotiByTel = "BaseOperation.asmx/getNotiByTel";
    /**
     * 获取版本信息
     */
    String getVersionForAndroid = "VersionOperation.asmx/getVersionForAndroid";
    /**
     * 绑定微信ID
     */
    String updateWeiXinID = "UserOperation.asmx/UpdateWeiXinID";
    /**
     * 提现
     */
    String reflect = "MoneyOperation.asmx/Reflect";

    /**
     * 微信支付
     */
    String WXpay = "MoneyOperation.asmx/WxPay";
    /**
     * getAccessToken
     */
    String getAccessToken = "MoneyOperation.asmx/getAccessToken";
    /**
     * 获取实名认证信息
     */
    String getRenZhengInfo = "UserOperation.asmx/getRenZhengInfo";
}