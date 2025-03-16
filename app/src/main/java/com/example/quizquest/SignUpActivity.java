package com.example.quizquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static com.example.quizquest.DbQuery.createUserData;

public class SignUpActivity extends AppCompatActivity {

    private EditText uname, email, pass, confirm_pass;
    private String nameStr, emailStr, passStr, confirmPassStr;
    private Button signupB;
    private ImageView backB;
    private FirebaseAuth mAuth;
    private String TAG = "SignUpActivity";
    private long mLastClickTime = 0;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        loadingDialog = new Dialog(SignUpActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void initViews()
    {
        uname = findViewById(R.id.su_name);
        email = findViewById(R.id.su_email);
        pass = findViewById(R.id.su_password);
        confirm_pass = findViewById(R.id.su_confirm_pass);
        signupB = findViewById(R.id.su_register);
        backB = findViewById(R.id.su_backB);
    }

    private void init()
    {
        initViews();

        mAuth = FirebaseAuth.getInstance();

        signupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isMultiClick(2000))
                {
                    return;
                }

                if(validateData())
                {
                    registerUser();
                }
            }
        });

        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isMultiClick(1000))
                {
                    return;
                }

                SignUpActivity.this.finish();
            }
        });

    }

    private boolean validateData()
    {
        nameStr = uname.getText().toString();
        emailStr = email.getText().toString();
        passStr = pass.getText().toString();
        confirmPassStr = confirm_pass.getText().toString();


        if(nameStr.isEmpty())
        {
            uname.setError("Enter Full Name");
            return false;
        }

        if(emailStr.isEmpty())
        {
            email.setError("Enter Email ID");
            return false;
        }

        if(passStr.isEmpty())
        {
            pass.setError("Enter Password");
            return false;
        }

        if(confirmPassStr.isEmpty())
        {
            confirm_pass.setError("Enter Password");
            return false;
        }

        if(passStr.compareTo(confirmPassStr) != 0)
        {
            Toast.makeText(this,"Password & Confirm Password should be same", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registerUser()
    {
        loadingDialog.show();

        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUpActivity.this, "Success",
                                    Toast.LENGTH_SHORT).show();
                            updateProfileName();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    private void updateProfileName()
    {
        FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nameStr)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            // Go To Main Activity

                            createUserData(nameStr,emailStr,null,null,new OnQCompleteListener() {
                                @Override
                                public void onSuccess() {

                                    Toast.makeText(SignUpActivity.this, "Account created successfully. Please Login to Continue", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();

                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                                @Override
                                public void onFailure()
                                {
                                    Toast.makeText(SignUpActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                                    //Log.d()
                                    loadingDialog.dismiss();
                                }
                            });

                        }
                    }
                });
    }


    private boolean isMultiClick(int interval)
    {
        if(SystemClock.elapsedRealtime() - mLastClickTime < interval)
        {
            mLastClickTime = SystemClock.elapsedRealtime();
            return true;
        }

        mLastClickTime = SystemClock.elapsedRealtime();
        return false;
    }

}
