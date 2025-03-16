package com.example.quizquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button resetPassB;
    private FirebaseAuth mAuth;
    private TextView resetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        init();

        Toolbar toolbar = findViewById(R.id.fp_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resetText.setVisibility(View.GONE);

        resetPassB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateData())
                {
                    resetPassword();
                }
            }
        });

    }

    private void initViews()
    {
        email = findViewById(R.id.fp_email);
        resetPassB = findViewById(R.id.reset_passB);
        resetText = findViewById(R.id.reset_text);
    }

    private void init()
    {
        initViews();

        mAuth = FirebaseAuth.getInstance();
    }

    private boolean validateData()
    {
        if(email.getText().toString().isEmpty())
        {
            email.setError("Enter Email ID");
            return false;
        }

        return true;
    }


    private void resetPassword()
    {
        resetPassB.setEnabled(false);

        mAuth.sendPasswordResetEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            resetText.setVisibility(View.VISIBLE);

                        }
                        else
                        {
                            Toast.makeText(ResetPasswordActivity.this,"Invalid E-Mail ID",Toast.LENGTH_SHORT).show();
                        }

                        resetPassB.setEnabled(true);
                    }
                });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            ResetPasswordActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
