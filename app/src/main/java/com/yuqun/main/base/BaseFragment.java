package com.yuqun.main.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuqun.main.utils.LogN;
import com.yuqun.main.utils.WaitingAlertDialog;



/**
 * @author zzp
 */
public class BaseFragment extends Fragment {


    @Override
    public void setInitialSavedState(SavedState state) {
        super.setInitialSavedState(state);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        LogN.d(this," BaseFragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        LogN.d(this,"BaseFragment onPase");
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissWaitDialog();
    }

    private WaitingAlertDialog waitDialog;

    public void showWaitDialog(int textRes) {
        if (null == waitDialog) {
            waitDialog = new WaitingAlertDialog(getActivity(), textRes);
        } else {
            waitDialog.setShowText(textRes);
            if (!waitDialog.isShown()) {
                waitDialog.show();
            }
        }
    }

    public void dismissWaitDialog() {
        if (null != waitDialog) {
            waitDialog.dismiss();
        }
    }

}
