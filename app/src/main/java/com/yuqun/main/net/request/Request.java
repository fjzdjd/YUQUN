package com.yuqun.main.net.request;

import android.os.Handler;

public abstract class Request {

	public Request(Handler handler) {

	}

	public void sendRequest() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				httpConnect();
			}
		}).start();
	}
	
	public void sendRequestSync(){
		httpConnect();
	}

	protected abstract void httpConnect();
}
