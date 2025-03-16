package com.example.quizquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.quizquest.Adapters.TestListAdapter;

import static com.example.quizquest.DbQuery.g_catList;
import static com.example.quizquest.DbQuery.g_testList;
import static com.example.quizquest.DbQuery.loadMyScores;
import static com.example.quizquest.DbQuery.loadTestData;
import static com.example.quizquest.DbQuery.selected_cat_index;

public class TestsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView testView;
    private TestListAdapter adapter;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);

        toolbar = findViewById(R.id.at_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(g_catList.get(selected_cat_index).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        testView = findViewById(R.id.test_recyler_view);

        loadingDialog = new Dialog(TestsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        testView.setLayoutManager(layoutManager);

        //loadTestData();

        loadingDialog.show();
        loadTestData(new OnQCompleteListener() {
            @Override
            public void onSuccess() {
                loadMyScores(new OnQCompleteListener() {
                    @Override
                    public void onSuccess() {

                        adapter = new TestListAdapter(g_testList);
                        testView.setAdapter(adapter);
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFailure()
                    {
                        Toast.makeText(TestsActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                        //Log.d()
                        loadingDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure()
            {
                Toast.makeText(TestsActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                //Log.d()
                loadingDialog.dismiss();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(adapter != null)
            adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            TestsActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
