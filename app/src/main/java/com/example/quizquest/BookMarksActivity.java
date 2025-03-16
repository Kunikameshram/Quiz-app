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

import com.example.quizquest.Adapters.BookMarksAdapter;

import static com.example.quizquest.DbQuery.g_bookmarkList;

public class BookMarksActivity extends AppCompatActivity {

    private RecyclerView quesListView;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_marks);

        quesListView = findViewById(R.id.ba_recycler_view);

        Toolbar toolbar = findViewById(R.id.ba_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Saved Questions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(BookMarksActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        loadingDialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        quesListView.setLayoutManager(layoutManager);


        DbQuery.loadBookmarks(new OnQCompleteListener() {
            @Override
            public void onSuccess() {
                BookMarksAdapter adapter = new BookMarksAdapter(g_bookmarkList);
                quesListView.setAdapter(adapter);
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(BookMarksActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            BookMarksActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
