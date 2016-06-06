package com.yuqun.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.yuqun.main.comm.CommonData;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.ui.invite.InviteFragment;
import com.yuqun.main.ui.mine.MineFragment;
import com.yuqun.main.ui.mine.UpdateManager;
import com.yuqun.main.ui.money.MoneyFragment;
import com.yuqun.main.ui.task.TaskFragment;
import com.yuqun.main.utils.LogN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * @author zzp
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private static FragmentManager mFragmentManager;
    /**
     * 定义一个变量，来标识是否退出
     */
    private static boolean isExit = false;
    /**
     * 监听退出状态
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    private ArrayList<Fragment> fragments = null;
    private InviteFragment inviteFragment;
    private MineFragment mineFragment;
    private MoneyFragment moneyFragment;
    private TaskFragment taskFragment;
    private int currentFootIndex = 0;
    private RadioButton footer_rb_money;
    private RadioButton footer_rb_task;
    private RadioButton footer_rb_mine;
    private PicMessageReceiver mPicMessageReceiver;
    private HeaderMessageReceiver mHeaderMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        try {
            /*判断是否*/
            CommonData.isUpdate = true;
            new UpdateManager(MainActivity.this).isupdate();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        registerPicMessageReceiver();
        registerHeaderMessageReceiver();

        initJPushTag(getApplicationContext());
    }

    private void initJPushTag(Context context) {
        String phone = SharePreferenceManager.getInstance().getString(CommonData.USER_PHONE, "");
        Set<String> set = new HashSet<String>();
        set.add(phone);
        JPushInterface.setAliasAndTags(context, phone, set,
                new TagAliasCallback() {

                    @Override
                    public void gotResult(int result, String arg1,
                                          Set<String> arg2) {
                        LogN.d(MainActivity.this,
                                "JPushInterface.setTags---------------------result="
                                        + result);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mPicMessageReceiver);
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mHeaderMessageReceiver);
    }


    private void initWidgets() {
        footer_rb_money = (RadioButton) findViewById(R.id.footer_rb_money);
        findViewById(R.id.footer_rb_invite).setOnClickListener(this);
        footer_rb_mine = (RadioButton) findViewById(R.id.footer_rb_mine);
        footer_rb_mine.setOnClickListener(this);
        footer_rb_money.setOnClickListener(this);
        footer_rb_task = (RadioButton) findViewById(R.id.footer_rb_task);
        footer_rb_task.setOnClickListener(this);

        // 获取FragmentManager实例
        mFragmentManager = getSupportFragmentManager();
        // 初始化首个fragment
        initFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.footer_rb_task:
                setFootItemSelected(currentFootIndex, 0);

                break;

            case R.id.footer_rb_money:
                setFootItemSelected(currentFootIndex, 1);
                sendBroadcast(new Intent(CommonData.Invite_listview));

                break;

            case R.id.footer_rb_invite:
                setFootItemSelected(currentFootIndex, 2);
                Intent intent = new Intent(CommonData.Invite_listview);
                sendBroadcast(intent);
                break;

            case R.id.footer_rb_mine:
                setFootItemSelected(currentFootIndex, 3);

                break;

            default:
                break;
        }

    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
        fragments = new ArrayList<>();
        taskFragment = new TaskFragment();
        moneyFragment = new MoneyFragment();
        inviteFragment = new InviteFragment();
        mineFragment = new MineFragment();
        fragments.add(taskFragment);
        fragments.add(moneyFragment);
        fragments.add(inviteFragment);
        fragments.add(mineFragment);

        fragmentTransaction.add(R.id.content, fragments.get(0)).commit();

    }

    /**
     * 切换tab更换展示内容
     *
     * @param
     */
    public void setFootItemSelected(int currentFragment, int index) {
        if (currentFragment != index && fragments != null && fragments.get(index) != null) {
            mFragmentManager.popBackStack();
            mFragmentManager.beginTransaction().setCustomAnimations(R.anim.push_right_in, R.anim
                    .push_left_out);
            if (fragments.get(index).isAdded()) {
                mFragmentManager.beginTransaction().hide(fragments.get(currentFragment)).show
                        (fragments.get(index))
                        .commitAllowingStateLoss();
            } else {
                mFragmentManager.beginTransaction().hide(fragments.get(currentFragment))
                        .add(R.id.content, fragments.get(index)).commitAllowingStateLoss();
            }
            currentFootIndex = index;
        }
    }

    /**
     * 注册广播
     */
    public void registerPicMessageReceiver() {
        mPicMessageReceiver = new PicMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MineFragment.GET_RECEIVE_MONEY);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mPicMessageReceiver, filter);
    }

    /**
     * 注册广播
     */
    public void registerHeaderMessageReceiver() {
        mHeaderMessageReceiver = new HeaderMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(TaskFragment.GET_RECEIVE_Task);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mHeaderMessageReceiver, filter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    /**
     * 接受广播
     */
    public class PicMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MineFragment.GET_RECEIVE_MONEY.equals(intent.getAction())) {
                setFootItemSelected(currentFootIndex, 1);
                footer_rb_money.setChecked(true);
            }
        }
    }

    /**
     * 接受广播
     */
    public class HeaderMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TaskFragment.GET_RECEIVE_Task.equals(intent.getAction())) {
                setFootItemSelected(currentFootIndex, 3);
                footer_rb_mine.setChecked(true);

            }
        }
    }


}
