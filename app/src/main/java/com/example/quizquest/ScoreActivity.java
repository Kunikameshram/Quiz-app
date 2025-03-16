package com.example.quizquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import static com.example.quizquest.DbQuery.g_bmIdList;
import static com.example.quizquest.DbQuery.g_quesList;
import static com.example.quizquest.DbQuery.myPerformance;
import static com.example.quizquest.QuestionsActivity.NOT_VISITED;

public class ScoreActivity extends AppCompatActivity {

    private TextView scoreTV, totalQTV, timeTV, correctQTV, wrongQTV, unattempQTV;
    private Button goToLeaderB, reattemptB, viewAnsB;
    private long timeTaken;
    private int finalScore;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Toolbar toolbar = findViewById(R.id.sa_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("  Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scoreTV = findViewById(R.id.sa_score);
        totalQTV = findViewById(R.id.sa_total_ques);
        timeTV = findViewById(R.id.sa_time);
        correctQTV = findViewById(R.id.sa_correct_ques);
        wrongQTV = findViewById(R.id.sa_wrong_ques);
        unattempQTV = findViewById(R.id.sa_unattempted);
        goToLeaderB = findViewById(R.id.sa_leaderB);
        reattemptB = findViewById(R.id.sa_reattempt);
        viewAnsB = findViewById(R.id.view_answers);

        loadingDialog = new Dialog(ScoreActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        timeTaken = getIntent().getLongExtra("TIME_TAKEN",0);

        goToLeaderB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
                intent.putExtra("LAYOUT","LEADERBOARD");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        viewAnsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreActivity.this, AnswersActivity.class);
                startActivity(intent);
            }
        });

        reattemptB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reAttempt();
            }
        });


        loadData();

        setBookMarks();

        saveResult();

    }

    private void loadData()
    {
        int correctQ=0, wrongQ=0, unattemptQ=0;

        for(int i=0; i < g_quesList.size(); i++)
        {
            if(g_quesList.get(i).getSelectedAns() == -1)
            {
                unattemptQ++;
            }
            else
            {
                if(g_quesList.get(i).getSelectedAns() == g_quesList.get(i).getCorrectAns())
                {
                    correctQ++;
                }
                else
                {
                    wrongQ++;
                }
            }

        }


        correctQTV.setText(String.valueOf(correctQ));
        wrongQTV.setText(String.valueOf(wrongQ));
        unattempQTV.setText(String.valueOf(unattemptQ));
        totalQTV.setText(String.valueOf(g_quesList.size()));

        String time =  String.format("%02d:%02d m",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTaken)));

        timeTV.setText(time);

        finalScore = (correctQ*100)/g_quesList.size();
        scoreTV.setText(String.valueOf(finalScore));

    }

    private void setBookMarks()
    {
        for(int i=0; i < g_quesList.size(); i++)
        {
            if(g_quesList.get(i).isBookmarked())
            {
                if( ! g_bmIdList.contains(g_quesList.get(i).getqID()))
                {
                    //Newly Added
                    g_bmIdList.add(g_quesList.get(i).getqID());
                    myPerformance.setBookmarksCount(g_bmIdList.size());
                }

            }
            else
            {
                if(g_bmIdList.contains(g_quesList.get(i).getqID()))
                {
                    //Removed
                    g_bmIdList.remove(g_quesList.get(i).getqID());
                    myPerformance.setBookmarksCount(g_bmIdList.size());
                }

            }

        }
    }

    private void reAttempt()
    {

        for(int i=0; i < g_quesList.size(); i++)
        {
            g_quesList.get(i).setSelectedAns(-1);
            g_quesList.get(i).setStatus(NOT_VISITED);
        }

        Intent intent = new Intent(ScoreActivity.this, QuestionsActivity.class);
        startActivity(intent);
        finish();

    }

    private void saveResult()
    {


        DbQuery.saveResult(finalScore, new OnQCompleteListener() {
            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure()
            {
                Toast.makeText(ScoreActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                //Log.d()
                loadingDialog.dismiss();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            ScoreActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
