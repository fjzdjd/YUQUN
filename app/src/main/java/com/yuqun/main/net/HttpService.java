package com.yuqun.main.net;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yuqun.main.comm.CommErrorCode;
import com.yuqun.main.utils.Utils;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class HttpService {
    public static int HTTP_RETRY_TIMES = 3;

    public static int HTTP_REQUEST_TIMEOUT = 90 * 1000;
    private volatile static HttpService instance = null;
    private String name = null;
    private String pwd = null;
    private String serverIP = null;
    private int serverPort = 8080;
    private String serverUrl = null;
    private Context ctx = null;
    private AsyncHttpClient client = null;
    private String nonce = "4a568c428a037e2f849526a7dc65ef25";

    private HttpService() {
        client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(HTTP_RETRY_TIMES, HTTP_REQUEST_TIMEOUT);
        client.setTimeout(HTTP_REQUEST_TIMEOUT);
    }

    public static HttpService getInstance() {
        if (instance == null) {
            instance = new HttpService();
        }

        return instance;
    }

    public void init(Context ctx, String ip, int port) {
        this.ctx = ctx;
        serverIP = ip;
        serverPort = port;
        serverUrl = "http://" + serverIP + ":" + serverPort + "/BPC/HttpServer";
    }

    public void setAuthInfo(String userName, String userPwd) {
        this.name = userName;
        this.pwd = userPwd;
    }

    /**
     * public int postOnAuth(Header[] headers, MyJsonHttpResponseHandler jhrHandler)
     * {
     * if(null == headers || null == jhrHandler)
     * {
     * return CommErrorCode.FAILED;
     * }
     * <p>
     * for (int i = 0; i < headers.length; i++)
     * {
     * if (headers[i].getName().equals("WWW-Authenticate"))
     * {
     * // 解析nonce
     * int pos1 = headers[i].getValue().indexOf("nonce=") + 6;
     * int pos2 = headers[i].getValue().indexOf(",opaque=");
     * nonce = headers[i].getValue().substring(pos1, pos2);
     * LogN.i(this, "new nonce:" + nonce);
     * <p>
     * return post(jhrHandler.getPostJsonObj(), jhrHandler);
     * }
     * <p>
     * }
     * <p>
     * return CommErrorCode.FAILED;
     * }
     */
    public int post(JSONObject body, JsonHttpResponseHandler jsonResponse) {
        if (null == body) {

            return CommErrorCode.FAILED;
        }

        try {
            StringEntity entity = new StringEntity(body.toString(), "UTF-8");


            client.addHeader("Authorization", buildDigest());
            client.post(ctx, serverUrl, entity, "UTF-8", jsonResponse);
            return CommErrorCode.SUCCESS;
        } catch (UnsupportedEncodingException e) {

        }
        return CommErrorCode.FAILED;
    }

    private String buildDigest() {
        StringBuffer sb = new StringBuffer();
        sb.append("Digest username=").append(name)
                .append(",realm=realm@host.com,nonce=").append(nonce)
                .append(",uri=").append(serverUrl)
                .append(",response=").append(Utils.md5Encode(nonce + name + pwd + "POST" +
                serverUrl))
                .append(",cnonce=00000001,opaque=5ccc069c403ebaf9f0171e9517f40e41,qop=auth," +
						"nc=00000001");

        //LogN.i("buildDigest", "request: " + sb.toString());
        return sb.toString();
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", buildDigest());
        client.post(url, params, responseHandler);
    }

}
