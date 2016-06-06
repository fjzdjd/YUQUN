package com.yuqun.main.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuqun.main.R;
import com.yuqun.main.ui.model.EducationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/11.
 */
public class Reg2EducationAdapter extends BaseAdapter {
    private List<EducationModel> mFoundsList= new ArrayList<EducationModel>();
    private Context context;

    public Reg2EducationAdapter(List<EducationModel> mFoundsList, Context context) {
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
        tv_city_name.setText(mFoundsList.get(position).getEducationName());
        return convertView;
    }
}
