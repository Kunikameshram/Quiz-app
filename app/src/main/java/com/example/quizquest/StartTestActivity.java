package com.example.quizquest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.quizquest.DbQuery.g_catList;
import static com.example.quizquest.DbQuery.g_quesList;
import static com.example.quizquest.DbQuery.g_testList;
import static com.example.quizquest.DbQuery.loadQuestions;
import static com.example.quizquest.DbQuery.selected_cat_index;
import static com.example.quizquest.DbQuery.selected_test_index;

public class StartTestActivity extends AppCompatActivity {

    private TextView catName, testNo, totalQ, bestScore, time;
    private Button startTestB;
    private ImageView backB;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        init();

        loadingDialog = new Dialog(StartTestActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        loadingDialog.show();

        loadQuestions ( new OnQCompleteListener() {
            @Override
            public void onSuccess() {
                setData();

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure()
            {
                Toast.makeText(StartTestActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                //Log.d()
                loadingDialog.dismiss();
            }
        });

        //setData();

    }


    private void initViews()
    {
        catName = findViewById(R.id.st_cat_name);
        testNo = findViewById(R.id.st_test_no);
        totalQ = findViewById(R.id.st_total_ques);
        bestScore = findViewById(R.id.st_best_score);
        time = findViewById(R.id.st_time);
        startTestB = findViewById(R.id.start_test);
        backB = findViewById(R.id.st_backB);
    }

    private void init()
    {
        initViews();


        startTestB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartTestActivity.this, QuestionsActivity.class );
                startActivity(intent);
                finish();
            }
        });

        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTestActivity.this.finish();
            }
        });

    }

    private void setData()
    {
        catName.setText(g_catList.get(selected_cat_index).getName());
        testNo.setText("Test No. " + String.valueOf(selected_test_index+1));
        totalQ.setText(String.valueOf(g_quesList.size()));
        bestScore.setText(String.valueOf(g_testList.get(selected_test_index).getTopScore()));
        time.setText(String.valueOf(g_testList.get(selected_test_index).getTime()) + " m");
    }
}
