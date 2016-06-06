package com.yuqun.main.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuqun.main.R;
import com.yuqun.main.ui.model.FoundsModel;
import com.yuqun.main.ui.model.QuestionModel;

import java.util.List;

/**
 * Created by Administrator on 2016/3/11.
 */
public class FoundQuestionAdapter extends BaseAdapter {
    private List<QuestionModel> mQuestionList;
    private Context context;

    public FoundQuestionAdapter(List<QuestionModel> mQuestionList, Context context) {
        this.mQuestionList = mQuestionList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mQuestionList.size();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_pop_question, null);
        TextView tv_question = (TextView) convertView.findViewById(R.id.tv_question);
        TextView tv_answer = (TextView) convertView.findViewById(R.id.tv_answer);
        QuestionModel questionModel = mQuestionList.get(position);
        tv_answer.setText(questionModel.getAnswer());
        tv_question.setText(questionModel.getQueation());
        return convertView;
    }
}
