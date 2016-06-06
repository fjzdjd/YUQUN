package com.yuqun.main.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuqun.main.R;
import com.yuqun.main.manager.ActivityManager;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.utils.LogN;
import com.yuqun.main.utils.WaitingAlertDialog;


public abstract class BaseActivity extends Activity {
    private static final int ADD_BTN_NATURE = -1;
    protected TextView topTitle;
    protected TextView left;
    protected TextView right;
    protected ActivityManager activityManager = ActivityManager.getInstance();

    protected TextView titleTextView;

    protected ImageButton backBtn;
    protected AlertDialog alertDialog;
    protected boolean isActive;
    private LinearLayout operBtnLayout;
    private WaitingAlertDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharePreferenceManager.getInstance().init(getApplicationContext());
        isActive = true;
        activityManager.pushActivity(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        LogN.d(this, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogN.d(this, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogN.d(this, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogN.d(this, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogN.d(this, "onRestart");
    }

    @Override
    protected void onDestroy() {
        isActive = false;
        dismissWaitDialog();
        dismissLastAlertDialog();
        waitDialog = null;

        activityManager.popActivity(this);

        super.onDestroy();
        LogN.d(this, "onDestroy");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    public void finishWithoutAnim() {
        super.finish();
    }

    public void dismissLastAlertDialog() {
        if (null != alertDialog) {
            if (alertDialog.isShown()) {
                alertDialog.dismiss();
            }

            alertDialog = null;
        }
    }

    public void showWaitDialog(int textRes) {
        if (null == waitDialog) {
            waitDialog = new WaitingAlertDialog(this, textRes);
        } else {
            waitDialog.setShowText(textRes);
            if (!waitDialog.isShown()) {
                waitDialog.show();
            }
        }
    }

    public void changeWaitDialogText(String text) {
        if (null != waitDialog) {
            waitDialog.setShowText(text);
        }
    }

    public void dismissWaitDialog() {
        if (null != waitDialog) {
            waitDialog.dismiss();
        }
    }

    public void hideBackBtn(boolean isHide) {
        if (null != backBtn) {
            backBtn.setVisibility(isHide ? View.INVISIBLE : View.VISIBLE);
        }
    }

    public void changeTitle(int titleTextRes) {
        if (null != this.titleTextView) {
            this.titleTextView.setText(titleTextRes);
        }
    }

    public void changeTitle(String titleText) {
        if (null != this.titleTextView) {
            this.titleTextView.setText(titleText);
        }
    }

    public void clearOperBtn() {
        if (null != operBtnLayout) {
            operBtnLayout.removeAllViews();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 显示软键盘
     *
     * @param view
     */
    public void showSoftInput(View view) {
        if (null == view) {
            LogN.e(this, "showSoftInput | view is null");
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput() {
        if (null == getCurrentFocus()) {
            LogN.w(this, "hideSoftInput currFocus is null");
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager
                .HIDE_NOT_ALWAYS);
    }


    public abstract void initViews();

    public abstract void bindListener();

    public abstract void initDatas();
}
