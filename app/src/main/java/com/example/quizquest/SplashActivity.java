package com.example.quizquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.quizquest.DbQuery.clearData;
import static com.example.quizquest.DbQuery.g_firestore;
import static com.example.quizquest.DbQuery.loadData;
import static java.lang.System.exit;


public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appName = findViewById(R.id.app_name);

        Typeface typeface = ResourcesCompat.getFont(this,R.font.blacklist);
        appName.setTypeface(typeface);

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.myanim);
        appName.setAnimation(anim);

        g_firestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        clearData();

            new Thread() {
                public void run() {


                    if(mAuth.getCurrentUser() != null)
                    {
                        loadData(new OnQCompleteListener() {
                            @Override
                            public void onSuccess() {

                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }

                            public void onFailure()
                            {
                                Toast.makeText(SplashActivity.this,"Something went wrong ! Please Try Later ",Toast.LENGTH_SHORT).show();
                                exit(0);
                            }
                        });
                    }
                    else
                    {

                        try {
                            sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }


                }
            }.start();

    }

}
