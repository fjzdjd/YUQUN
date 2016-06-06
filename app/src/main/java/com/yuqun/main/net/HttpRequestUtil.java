package com.yuqun.main.net;

import android.os.Handler;


import com.yuqun.main.net.request.CommRequest;

import java.util.Map;

/**
 * http 请求通用工具类
 *
 * @author Administrator
 */
public class HttpRequestUtil {

    /**
     * post 请求
     *
     * @param httpUrl 路径
     * @param reqMap  参数，无惨传null
     * @param handler 接收handler
     */
    public static void sendHttpPostCommonRequest(String httpUrl, Map<String, String> reqMap,
                                                 Handler handler) {
        new CommRequest(httpUrl, reqMap, handler).sendRequest();
    }
}
