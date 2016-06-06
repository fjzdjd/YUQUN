package com.yuqun.main.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.TextView;

import com.yuqun.main.R;


public class WaitingAlertDialog {
    private android.app.AlertDialog alertDialog;

    private TextView messageTextView;

    public WaitingAlertDialog(Context context) {
        alertDialog = new android.app.AlertDialog.Builder(context).create();
        alertDialog.show();

        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.custom_waitingdialog_layout);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;

        window.setLayout(w_screen, h_screen);

        //设置对话框背景色为透明
        window.setBackgroundDrawableResource(R.color.transparent);
//
        messageTextView = (TextView) window.findViewById(R.id.loading_text);
    }

    public WaitingAlertDialog(Context context, int msgText) {
        this(context);
        setShowText(msgText);
    }

    public void setShowText(int resId) {
        messageTextView.setText(resId);
    }

    public void setShowText(String message) {
        messageTextView.setText(message);
    }

    public boolean isShown() {
        return alertDialog.isShowing();
    }

    /**
     * 显示对话�?
     */
    public void show() {
        alertDialog.show();
    }

    /**
     * 关闭对话�?
     */
    public void dismiss() {
        alertDialog.dismiss();
    }

}