package com.yuqun.main.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yuqun.main.MainActivity;
import com.yuqun.main.R;
import com.yuqun.main.ui.model.NotifyModel;
import com.yuqun.main.utils.StringUtils;

import java.util.List;

/**
 * 消息公告适配器
 * Created by Administrator on 2016/3/25.
 */
public class NotificationAdapter extends BaseAdapter {
    private List<NotifyModel> modelList;

    private Context context;

    public NotificationAdapter(List<NotifyModel> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        NotifyModel messageModel = modelList.get(position);
        view = LayoutInflater.from(context).inflate(R.layout.listview_item_notification,
                null);
        TextView tv_notification = (TextView) view
                .findViewById(R.id.txt_notify);
        TextView tv_time = (TextView) view.findViewById(R.id.txt_time);
        tv_notification.setText(messageModel.getNotification());
        tv_time.setText(messageModel.getCreateTime());

      /*  lv_nofication.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(!StringUtils.isEmpty(modelList.get(position).getContent())){
                    Intent intent  = new Intent(context,MainActivity.class);
                    intent.putExtra("INDEX", 1);
                    intent.putExtra("URL", modelList.get(position).getContent());
                      context.startActivity(intent);
                }
            }
        });*/
        return view;
    }
}
