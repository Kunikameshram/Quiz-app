package com.example.quizquest.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quizquest.QuestionsActivity;
import com.example.quizquest.R;

import androidx.core.content.ContextCompat;

import static com.example.quizquest.DbQuery.g_quesList;
import static com.example.quizquest.QuestionsActivity.ANSWERED;
import static com.example.quizquest.QuestionsActivity.NOT_VISITED;
import static com.example.quizquest.QuestionsActivity.REVIEW;
import static com.example.quizquest.QuestionsActivity.UNANSWERED;

public class QuestionGridAdapter extends BaseAdapter {

    private int numOfQues;
    private Context context;

    public QuestionGridAdapter(int numOfQues, Context context) {
        this.numOfQues = numOfQues;
        this.context = context;
    }

    @Override
    public int getCount() {
        return numOfQues;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {


        View view;

        if(convertView == null)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ques_grid_item,parent,false);
        }
        else
        {
            view = convertView;
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(context instanceof QuestionsActivity)
                        ((QuestionsActivity)context).goToQuestion(position);
            }
        });

        TextView quesTV = view.findViewById(R.id.quest_number);

        quesTV.setText(String.valueOf(position+1));

        //quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(view.getContext(),R.color.red)));
        Log.d("Status",String.valueOf(position) + " " + String.valueOf(g_quesList.get(position).getStatus()));

        switch (g_quesList.get(position).getStatus())
        {
            case NOT_VISITED:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(view.getContext(),R.color.gray)));
                break;

            case ANSWERED:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(view.getContext(),R.color.green)));
                break;

            case UNANSWERED:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(view.getContext(),R.color.red)));
                break;

            case REVIEW:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(view.getContext().getColor(R.color.pink)));
                break;
        }




        return view;
    }
}
