/*
 * 文件名：BaseManager.java
 * 版权：Copyright 2014-2014 AllCam Tech. Co. Ltd. All Rights Reserved. 
 * 描述： BaseManager.java
 * 修改人：hu
 * 修改时间：2014-9-1
 * 修改内容：新增
 */
package com.yuqun.main.manager;

import android.content.Context;

/**
 * 管理类基类
 * 
 * @author hu
 * @version V1.0 2014-9-1
 */
public class BaseManager {
	protected Context appContext;

	protected boolean isInit = false;

	public void init(Context context) {
		isInit = true;
		this.appContext = context;
	}

}
