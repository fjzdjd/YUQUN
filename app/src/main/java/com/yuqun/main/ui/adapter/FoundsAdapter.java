package com.yuqun.main.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuqun.main.R;
import com.yuqun.main.ui.model.FoundsModel;

import java.util.List;

/**
 * Created by Administrator on 2016/3/11.
 */
public class FoundsAdapter extends BaseAdapter {
    private List<FoundsModel> mFoundsList;
    private Context context;

    public FoundsAdapter(List<FoundsModel> mFoundsList, Context context) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_money, null);
        TextView tv_source = (TextView) convertView.findViewById(R.id.tv_source);
        TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        TextView tv_founds = (TextView) convertView.findViewById(R.id.tv_founds);
        FoundsModel foundsModel = mFoundsList.get(position);
        tv_source.setText(foundsModel.getTaskTitle());
        tv_time.setText(foundsModel.getOccurredDate());
        if (foundsModel.getFundsType().equals("3") || foundsModel.getFundsType().equals("4")) {
            tv_founds.setText("-￥" + foundsModel.getAmountMoneyStr());
            tv_founds.setTextColor(context.getResources().getColor(R.color.lightGreen));
        } else {
            tv_founds.setText("+￥" + foundsModel.getAmountMoneyStr());
        }
        return convertView;
    }
}
