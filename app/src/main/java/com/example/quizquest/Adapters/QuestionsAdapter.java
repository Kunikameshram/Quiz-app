package com.example.quizquest.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.quizquest.Models.QuestionModel;
import com.example.quizquest.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.quizquest.DbQuery.g_quesList;
import static com.example.quizquest.QuestionsActivity.ANSWERED;
import static com.example.quizquest.QuestionsActivity.REVIEW;
import static com.example.quizquest.QuestionsActivity.UNANSWERED;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private List<QuestionModel> questionsList;

    public QuestionsAdapter(List<QuestionModel> questionsList) {
        this.questionsList = questionsList;
    }

    @NonNull
    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_item_layout,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsAdapter.ViewHolder viewHolder, int i) {

        viewHolder.setData(i);

    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView ques;
        private Button optionA,optionB, optionC, optionD;
        private Button prevselectedB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ques = itemView.findViewById(R.id.tv_question);
            optionA = itemView.findViewById(R.id.optionA);
            optionB = itemView.findViewById(R.id.optionB);
            optionC = itemView.findViewById(R.id.optionC);
            optionD = itemView.findViewById(R.id.optionD);
            prevselectedB = null;
        }

        private void setData(final int pos)
        {
            ques.setText(questionsList.get(pos).getQuestion());
            optionA.setText(questionsList.get(pos).getOptionA());
            optionB.setText(questionsList.get(pos).getOptionB());
            optionC.setText(questionsList.get(pos).getOptionC());
            optionD.setText(questionsList.get(pos).getOptionD());

            setOptionSelection(optionA,1,pos);
            setOptionSelection(optionB,2,pos);
            setOptionSelection(optionC,3,pos);
            setOptionSelection(optionD,4,pos);

            optionA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOption(optionA, 1, pos);
                }
            });

            optionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOption(optionB, 2, pos);
                }
            });

            optionC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOption(optionC, 3, pos);
                }
            });

            optionD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOption(optionD, 4, pos);
                }
            });

        }


        private void selectOption(Button btn, int btn_pos, int quesID)
        {

            if(prevselectedB == null)
            {
                btn.setBackgroundResource(R.drawable.selected_option_btn);
                prevselectedB = btn;

                g_quesList.get(quesID).setSelectedAns(btn_pos);
                changeStatus(quesID, ANSWERED);
                return;
            }

            if(prevselectedB.getId() == btn.getId())
            {
                btn.setBackgroundResource(R.drawable.unselected_btn);
                prevselectedB = null;

                g_quesList.get(quesID).setSelectedAns(-1);
                changeStatus(quesID, UNANSWERED);
                return;
            }

            prevselectedB.setBackgroundResource(R.drawable.unselected_btn);
            btn.setBackgroundResource(R.drawable.selected_option_btn);
            prevselectedB = btn;

            g_quesList.get(quesID).setSelectedAns(btn_pos);
            changeStatus(quesID, ANSWERED);


        }


        private void changeStatus(int id, int status)
        {
            if(g_quesList.get(id).getStatus() == REVIEW)
            {
                g_quesList.get(id).setStatus(REVIEW);
            }
            else
            {
                g_quesList.get(id).setStatus(status);
            }

        }

        private void setOptionSelection(Button btn, int btn_pos, int id)
        {
            if(g_quesList.get(id).getSelectedAns() == btn_pos)
            {
                btn.setBackgroundResource(R.drawable.selected_option_btn);
            }
            else
            {
                btn.setBackgroundResource(R.drawable.unselected_btn);
            }

        }


    }
}
