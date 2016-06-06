package com.yuqun.main.component;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.yuqun.main.R;

import java.util.ArrayList;
import java.util.List;


public class PoPShareWindowManager {
    private static volatile PoPShareWindowManager instance;
    public Context context;
    List<String> data = new ArrayList<String>();
    private PopupWindow pop;
    private View popView;
    private View p;

    private PoPShareWindowManager() {
    }

    /**
     * 创建单例类，提供静态方法调用
     *
     * @return ActivityManager
     */
    public static PoPShareWindowManager getInstance() {
        if (instance == null) {
            instance = new PoPShareWindowManager();
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
//		setPopWidth(context, width, height);
        pop.setWidth(width);
        pop.setHeight(height);
        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
//		 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(false);
//		 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);

    }

    public PopupWindow getPopView() {
        return pop;
    }

    public View setPopView(Context context, int id) {
        if (popView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            // 引入窗口配置文件
            popView = inflater.inflate(id, null);
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
        p = parent;
        if (pop != null) {
            if (!pop.isShowing()) {
                pop.showAtLocation(parent, gravity, x, y);
            }
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

    public void OnClickWechat(OnClickListener onClickListener) {
        popView.findViewById(R.id.layout_wechat).setOnClickListener(onClickListener);
    }

    public void OnClickWechatCircle(OnClickListener selectListener) {
        popView.findViewById(R.id.layout_wechat_circle).setOnClickListener(selectListener);
    }

    public void setPickViewData(List<String> data) {
        this.data = data;
    }
    public void OnClick(OnClickListener selectListener) {
        popView.findViewById(R.id.viewId).setOnClickListener(selectListener);
    }

}
