package com.yuqun.main.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuqun.main.R;
import com.yuqun.main.app.YuQunApplication;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.ui.model.CityModel;
import com.yuqun.main.ui.model.FoundsModel;

import java.util.List;

/**
 * Created by Administrator on 2016/3/11.
 */
public class Reg2CitysAdapter extends BaseAdapter {
    private List<CityModel> mFoundsList;
    private Context context;

    public Reg2CitysAdapter(List<CityModel> mFoundsList, Context context) {
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
        ViewHolder holder = null;
        final CityModel cityModel = mFoundsList.get(position);
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_reg2_citys, null);
            holder = new ViewHolder();

            holder.tv_letter = (TextView)convertView.findViewById(R.id.tv_letter);
            holder.tv_city_name = (TextView) convertView.findViewById(R.id.tv_city_name);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.tv_letter .setText(cityModel.getFirstLetter());
        holder.tv_city_name.setText(cityModel.getCityName());

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
    public class ViewHolder {

        public TextView tv_letter;
        public TextView tv_city_name;
    }
}
