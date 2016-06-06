package com.yuqun.main.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuqun.main.R;
import com.yuqun.main.ui.model.MyInviteModel;
import com.yuqun.main.ui.model.RevenueModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 我邀请的好友列表
 */
public class MyInviteAdapter extends BaseAdapter {
    private List<MyInviteModel> mFoundsList= new ArrayList<MyInviteModel>();
    private Context context;

    public MyInviteAdapter(List<MyInviteModel> mFoundsList, Context context) {
        this.mFoundsList = mFoundsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mFoundsList.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_invite, null);
        MyInviteModel myInviteModel = mFoundsList.get(position);
        Log.d("Jessine",myInviteModel.toString());
        TextView tv_invite_name = (TextView) convertView.findViewById(R.id.tv_invite_name);
        String name = myInviteModel.getName();
        name= name.substring(0, 1);

        String tel = myInviteModel.getTel();
        String tel1 = tel.substring(0, 3);
        String tel2 = tel.substring(7, tel.length());

        tv_invite_name.setText(name + "**  (" +tel1+"****"+tel2+")");

        TextView tv_invite_time = (TextView) convertView.findViewById(R.id.tv_invite_time);
        tv_invite_time.setText(myInviteModel.getRegDate());

        double   f   =  Double.parseDouble(myInviteModel.getBounsMoney());
        BigDecimal b   =   new   BigDecimal(f);
        double   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        TextView tv_invite_money = (TextView) convertView.findViewById(R.id.tv_invite_money);
        tv_invite_money.setText("￥"+f1);

        return convertView;
    }
}
