package com.yuqun.main.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuqun.main.R;
import com.yuqun.main.ui.model.CityModel;

import java.util.List;

/**
 * Created by Administrator on 2016/3/11.
 */
public class Reg2GenderAdapter extends BaseAdapter {
    private List<String> mFoundsList;
    private Context context;

    public Reg2GenderAdapter(List<String> mFoundsList, Context context) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_reg2_citys, null);
        TextView tv_city_name = (TextView) convertView.findViewById(R.id.tv_city_name);
        String gender = position == 1 ? gender = "男" : "女";
        tv_city_name.setText(gender);
       /* convertView.findViewById(R.id.ll_city).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();  //Itent就是我们要发送的内容
                intent.putExtra("city",cityModel);
                System.out.print("--------------"+cityModel.getCityName());
                intent.setAction(CommonData.REG_CITY);   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
                context.sendBroadcast(intent);   //发送广播
            }
        });*/
        return convertView;
    }
}
