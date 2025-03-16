package com.example.quizquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quizquest.Adapters.QuestionGridAdapter;
import com.example.quizquest.Adapters.QuestionsAdapter;

import java.util.concurrent.TimeUnit;

import static com.example.quizquest.DbQuery.g_catList;
import static com.example.quizquest.DbQuery.g_quesList;
import static com.example.quizquest.DbQuery.g_testList;
import static com.example.quizquest.DbQuery.selected_cat_index;
import static com.example.quizquest.DbQuery.selected_test_index;

public class QuestionsActivity extends AppCompatActivity {

    private RecyclerView questionsView;
    private int quesID;
    private ImageButton prevQuesB, nextQuesB;
    private Button markB, clearSelectionB;
    private TextView tvQuesID;

    private ImageView markImage, bookmarkB;
    private ImageView questListB;
    private DrawerLayout drawer;
    private GridView quesListGV;
    private ImageButton closeDrawerB;
    private QuestionGridAdapter gridAdapter;
    private QuestionsAdapter quesAdapter;
    private Button submitB;
    private CountDownTimer timer;
    private TextView timerTV, catName;
    private long timeLeft;

    public static final int NOT_VISITED = 0;
    public static final int UNANSWERED = 1;
    public static final int ANSWERED = 2;
    public static final int REVIEW = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_list_layout);

        init();

       // loadQuestionsList();

        quesAdapter = new QuestionsAdapter(g_quesList);
        questionsView.setAdapter(quesAdapter);

        gridAdapter = new QuestionGridAdapter(g_quesList.size(), this);
        quesListGV.setAdapter(gridAdapter);

        startTimer();

    }

    private void init()
    {
        initViews();

        setClickListeners();

        setData();

        setSnapHelper();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        questionsView.setLayoutManager(layoutManager);

    }

    private void initViews()
    {
        questionsView = findViewById(R.id.questions_view);
        prevQuesB = findViewById(R.id.prev_quesB);
        nextQuesB = findViewById(R.id.next_quesB);
        markB = findViewById(R.id.markB);
        tvQuesID = findViewById(R.id.tv_quesID);
        markImage = findViewById(R.id.mark_image);
        questListB = findViewById(R.id.ques_listB);
        quesListGV = findViewById(R.id.ques_list_gv);
        closeDrawerB = findViewById(R.id.close_drawer_btn);
        clearSelectionB = findViewById(R.id.clear_selB);
        submitB = findViewById(R.id.submitB);
        drawer = findViewById(R.id.drawer_layout);
        timerTV = findViewById(R.id.tv_timer);
        bookmarkB = findViewById(R.id.bookmarkB);
        catName = findViewById(R.id.qa_catName);

    }

    private void setData()
    {
        tvQuesID.setText( "1/" + String.valueOf(g_quesList.size()));
        catName.setText(g_catList.get(selected_cat_index).getName());

        if(g_quesList.get(0).isBookmarked())
        {
            bookmarkB.setImageResource(R.drawable.ic_bookmark);
        }
        else
        {
            bookmarkB.setImageResource(R.drawable.ic_bookmark_outline);
        }

    }



    private void setClickListeners()
    {
        prevQuesB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quesID > 0) {
                    questionsView.smoothScrollToPosition(quesID - 1);
                }
            }
        });

        nextQuesB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quesID < g_quesList.size() - 1)
                {
                    questionsView.smoothScrollToPosition(quesID + 1);
                }
            }
        });

        markB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(markImage.getVisibility() != View.VISIBLE)
                {
                    markImage.setVisibility(View.VISIBLE);

                    g_quesList.get(quesID).setStatus(REVIEW);

                }
                else
                {
                    markImage.setVisibility(View.GONE);

                    if(g_quesList.get(quesID).getSelectedAns() != -1 ) {
                        g_quesList.get(quesID).setStatus(ANSWERED);
                    }
                    else {
                        g_quesList.get(quesID).setStatus(UNANSWERED);
                    }

                }
            }
        });

        questListB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (! drawer.isDrawerOpen(GravityCompat.END)) {
                    gridAdapter.notifyDataSetChanged();
                    drawer.openDrawer(GravityCompat.END);
                }

            }
        });


        closeDrawerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }
            }
        });


        clearSelectionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                g_quesList.get(quesID).setSelectedAns(-1);
                g_quesList.get(quesID).setStatus(UNANSWERED);
                markImage.setVisibility(View.GONE);
                quesAdapter.notifyDataSetChanged();

            }
        });

        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTest();
            }
        });

        bookmarkB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToBookmark();
            }
        });

    }

    private void setSnapHelper()
    {
        final SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(questionsView);


        questionsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View v = snapHelper.findSnapView(recyclerView.getLayoutManager());
                quesID = recyclerView.getLayoutManager().getPosition(v);

                if(g_quesList.get(quesID).getStatus() == NOT_VISITED) {
                    g_quesList.get(quesID).setStatus(UNANSWERED);
                    //gridAdapter.notifyDataSetChanged();
                }
                tvQuesID.setText(String.valueOf(quesID + 1) + "/" + String.valueOf(g_quesList.size()));

                if(g_quesList.get(quesID).getStatus() == REVIEW)
                {
                    markImage.setVisibility(View.VISIBLE);
                }
                else
                {
                    markImage.setVisibility(View.GONE);
                }


                if(g_quesList.get(quesID).isBookmarked())
                {
                    bookmarkB.setImageResource(R.drawable.ic_bookmark);
                }
                else
                {
                    bookmarkB.setImageResource(R.drawable.ic_bookmark_outline);
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });



    }


    private void addToBookmark()
    {

        if(g_quesList.get(quesID).isBookmarked()) {
            g_quesList.get(quesID).setBookmarked(false);
            bookmarkB.setImageResource(R.drawable.ic_bookmark_outline);
        }
        else
        {
            g_quesList.get(quesID).setBookmarked(true);
            bookmarkB.setImageResource(R.drawable.ic_bookmark);
        }
    }


    public void goToQuestion(int position)
    {
        questionsView.smoothScrollToPosition(position);

        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }
    }

    private void submitTest()
    {

        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(QuestionsActivity.this);
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.stop_test_dialog,null);

        Button cancel = view.findViewById(R.id.et_cancelB);
        Button confirm = view.findViewById(R.id.et_confirmB);
        TextView title = view.findViewById(R.id.et_title);
        TextView msg = view.findViewById(R.id.et_message);

        title.setText("Submit Test");
        msg.setText("Do You want to Submit ?");

        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timer.cancel();
                alertDialog.dismiss();
                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                long total = g_testList.get(selected_test_index).getTime()*60*1000;
                intent.putExtra("TIME_TAKEN",  total - timeLeft);
                startActivity(intent);
                QuestionsActivity.this.finish();
            }
        });

        alertDialog.show();

    }


    private void startTimer()
    {
        long time = g_testList.get(selected_test_index).getTime()*60*1000;

        timer = new CountDownTimer(time + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeLeft = millisUntilFinished;

              String remainingTime =  String.format("%02d:%02d min",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                    timerTV.setText(remainingTime);

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                long total = g_testList.get(selected_test_index).getTime()*60*1000;
                intent.putExtra("TIME_TAKEN",  total);
                startActivity(intent);
                QuestionsActivity.this.finish();
            }
        };

        timer.start();

    }


    @Override
    public void onBackPressed()
    {

        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(QuestionsActivity.this);
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.stop_test_dialog,null);

        Button cancel = view.findViewById(R.id.et_cancelB);
        Button confirm = view.findViewById(R.id.et_confirmB);

        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    alertDialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    alertDialog.dismiss();
                    timer.cancel();
                    QuestionsActivity.this.finish();
            }
        });

        alertDialog.show();

    }


}
