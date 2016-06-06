package com.yuqun.main.net.request;

import android.os.Handler;

import com.google.gson.Gson;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.utils.LogN;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public abstract class JSONRequest extends Request {
    Gson gson = new Gson();
    private List<BasicNameValuePair> param;
    @SuppressWarnings("unused")
    private Class<?> rspClass;

    public JSONRequest(Handler handler) {
        super(handler);
    }

    public abstract String getAction();

    @Override
    protected void httpConnect() {
        param = getParam();
        String uriAPI = CommonData.SERVER_ADDRESS + getAction();
        LogN.d(this, uriAPI);
        methodPost(uriAPI, param);
    }


    /**
     * 通过POST请求的方式请求服务器、然后返回请求的结果
     *
     * @param URL
     * @param parameters
     * @return
     */
    public void methodPost(String URL, List<BasicNameValuePair> parameters) {

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(URL);
        UrlEncodedFormEntity entity = null;
        try {
            if (null != param) {
                // 把参数作为一个表单提交
                entity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                request.setEntity(entity);
            }
            // 执行请求
            HttpResponse response = client.execute(request);
            // 判断是否完全返回 200
            if (response.getStatusLine().getStatusCode() == 200) {
                // 取出回应字串
                String json = EntityUtils.toString(response.getEntity());
                jsonToObjectOrArray(json);
            } else {
                onHttpFailure(0, "网络请求有误出问题了");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void jsonToObjectOrArray(String json) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            int rc = jsonObject.getInt("RC");
            String ed = jsonObject.getString("ED");
            if (rc == 1) {
                onHttpSuccess(json);
            } else {
                onHttpFailure(CommonData.HTTP_HANDLE_FAILE, ed);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected abstract void onHttpSuccess(Object str);

    protected abstract void onHttpFailure(int errorCode, String why);

    protected abstract List<BasicNameValuePair> getParam();

    protected abstract Class<?> getResponseBean();
}
