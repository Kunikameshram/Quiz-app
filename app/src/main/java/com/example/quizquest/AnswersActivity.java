package com.example.quizquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.quizquest.Adapters.AnswersAdapter;

import static com.example.quizquest.DbQuery.g_quesList;

public class AnswersActivity extends AppCompatActivity {

    private RecyclerView quesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);


        quesListView = findViewById(R.id.aa_recycler_view);

        Toolbar toolbar = findViewById(R.id.aa_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Your Answers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        quesListView.setLayoutManager(layoutManager);

        AnswersAdapter adapter = new AnswersAdapter(g_quesList);

        quesListView.setAdapter(adapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            AnswersActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
