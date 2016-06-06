package com.yuqun.main.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.yuqun.main.R;
import com.yuqun.main.ui.model.AnswerModel;
import com.yuqun.main.ui.model.RevenueModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/11.
 */
public class InviteAnswerAdapter extends BaseAdapter {
    private List<AnswerModel> mFoundsList= new ArrayList<AnswerModel>();
    private Context context;
    private BitmapUtils bitmapUtils;
    public InviteAnswerAdapter(List<AnswerModel> mFoundsList, Context context) {
        this.mFoundsList = mFoundsList;
        this.context = context;
        bitmapUtils=new BitmapUtils(context);
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
        AnswerModel answerModel = mFoundsList.get(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.item_invite_question, null);
        TextView tv_invite_answer = (TextView) convertView.findViewById(R.id.tv_invite_answer);
        TextView tv_invite_question = (TextView) convertView.findViewById(R.id.tv_invite_question);
        ImageView iv_invite_question = (ImageView) convertView.findViewById(R.id.iv_invite_question);

        tv_invite_answer.setText(answerModel.getQueation());
        tv_invite_question.setText(answerModel.getAnswer());
        bitmapUtils.display(iv_invite_question,answerModel.getImg());
        return convertView;
    }
}
