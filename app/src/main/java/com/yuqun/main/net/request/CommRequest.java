package com.yuqun.main.net.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;
import android.os.Message;

import com.yuqun.main.comm.CommonData;

/**
 * 通用请求类
 * 
 */
public class CommRequest extends JSONRequest {
	private Handler handler;
	private Map<String, String> mapData;
	private String httpUrl;
	private Class<?> rspClass;

	public CommRequest(String httpUrl, Map<String, String> reqMap, Class<?> rspClass, Handler handler) {
		super(handler);
		this.handler = handler;
		this.mapData = reqMap;
		this.httpUrl = httpUrl;
		this.rspClass = rspClass;
	}

	public CommRequest(String httpUrl, Map<String, String> reqMap, Handler handler) {
		super(handler);
		this.handler = handler;
		this.mapData = reqMap;
		this.httpUrl = httpUrl;
	}

	@Override
	public String getAction() {
		return httpUrl;
	}

	@Override
	protected void onHttpSuccess(Object str) {
		Message msg = new Message();
		msg.what = CommonData.HTTP_HANDLE_SUCCESS;
		msg.obj = str;
		handler.sendMessage(msg);

	}

	@Override
	protected void onHttpFailure(int errorCode, String why) {
		Message msg = new Message();
		msg.what = CommonData.HTTP_HANDLE_FAILE;
		msg.obj = why;
		handler.sendMessage(msg);
	}

	@Override
	protected List<BasicNameValuePair> getParam() {
		if (null == mapData) {
			return null;
		}
		/* Post运作传送变数必须用NameValuePair[]阵列储存 */
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		for (Map.Entry<String, String> entry : mapData.entrySet()) {
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return params;
	}

	@Override
	protected Class<?> getResponseBean() {

		return rspClass;
	}

}
