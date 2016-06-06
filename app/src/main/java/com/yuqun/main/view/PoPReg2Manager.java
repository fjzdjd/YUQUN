package com.yuqun.main.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.yuqun.main.R;

import java.util.ArrayList;
import java.util.List;


public class PoPReg2Manager {
	private PopupWindow pop;
	private View popView;
	private static volatile PoPReg2Manager instance;
	List<String> data = new ArrayList<String>();
	public Context context;

	public PoPReg2Manager() {
	}

	/**
	 * 创建单例类，提供静态方法调用
	 * 
	 * @return ActivityManager
	 */
	public static PoPReg2Manager getInstance() {
		if (instance == null) {
			instance = new PoPReg2Manager();
		}
		return instance;
	}

	/***
	 * popWindow初始化方法
	 * 
	 * @param context
	 * @param
	 */
	public void init(Context context, final int width, int height, int id) {
		this.context = context;
		// 创建PopupWindow对象
		pop = new PopupWindow(setPopView(context, id),
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		setPopWidth(context, width, height);
		// 需要设置一下此参数，点击外边可消失
		pop.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		pop.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		pop.setFocusable(true);

	}

	public PopupWindow getPopView() {
		return pop;
	}

	public View getView() {
		return popView;
	}

	public View setPopView(Context context, int id) {
		if (popView == null) {
			LayoutInflater inflater = LayoutInflater.from(this.context);
			// 引入窗口配置文件
			popView = inflater.inflate(id, null);
			// popView.setAlpha(110);
		}
		return popView;
	}

	public void showPopAsDropDown(View view, int x, int y) {
		if (pop != null) {
			pop.showAsDropDown(view, x, y);
		} else {
		}
	}

	public void showPopAllLocation(View parent, int gravity, int x, int y) {
		if (pop != null) {
			pop.showAtLocation(parent, gravity, x, y);
		} else {

		}
	}

	public void dismissPop() {
		pop.dismiss();
	}

	public void setPopWidth(Context context, final int width, final int height) {
		if (popView == null) {

			return;
		}
		popView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// 动态设置pop的宽度
						FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) popView
								.getLayoutParams();
						linearParams.width = width;
						linearParams.height = height;
					}
				});
	}


	public ListView getListView(){
		com.yuqun.main.view.ListView4ScrollView lvsv = (ListView4ScrollView) popView.findViewById(R.id.lvsv);
		return lvsv;
	};
}
