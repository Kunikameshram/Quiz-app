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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.example.quizquest.DbQuery.createUserData;
import static com.example.quizquest.DbQuery.loadData;

public class LoginActivity extends AppCompatActivity {

    private TextView signUpBtn, forgotPassB;
    private EditText email, pass;
    private Button loginB;
    private FirebaseAuth mAuth;
    private RelativeLayout googleSignInB;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private Dialog loadingDialog;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        /*
        if(mAuth.getCurrentUser() != null)
        {
            goToMainActivity();
        } */

    }

    private void initViews()
    {
        email = findViewById(R.id.tv_email);
        pass = findViewById(R.id.tv_password);
        loginB = findViewById(R.id.login_btn);
        signUpBtn = findViewById(R.id.signup_btn);
        forgotPassB = findViewById(R.id.forgot_passB);
        googleSignInB = findViewById(R.id.google_signin);
    }

    private void setClickListeners()
    {
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isMultiClick(1000))
                {
                    return;
                }

                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isMultiClick(2000))
                {
                    return;
                }

                if(validateData())
                {
                    loginUser();
                }
            }
        });

        forgotPassB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isMultiClick(1000))
                {
                    return;
                }

                Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent);

            }
        });

        googleSignInB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isMultiClick(3000))
                {
                    return;
                }

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    private void init()
    {
        initViews();

        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setClickListeners();

        loadingDialog = new Dialog(LoginActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private boolean validateData()
    {
        if(email.getText().toString().isEmpty())
        {
            email.setError("Enter Email ID");
            return false;
        }

        if(pass.getText().toString().isEmpty())
        {
            pass.setError("Enter Password");
            return false;
        }


        return true;
    }

    private void loginUser()
    {
        loadingDialog.show();
        mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success

                            loadData(new OnQCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(LoginActivity.this, "Login Successfull",
                                            Toast.LENGTH_SHORT).show();

                                    loadingDialog.dismiss();
                                    goToMainActivity();

                                }

                                @Override
                                public void onFailure()
                                {
                                    Toast.makeText(LoginActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                                    //Log.d()
                                    loadingDialog.dismiss();
                                }
                            });



                        } else {

                            loadingDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    private void goToMainActivity()
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Log.d("TAG","google success");

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

            }

        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        loadingDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                                //Log.d("LOGGGG", "New User");
                                FirebaseUser user = mAuth.getCurrentUser();
                                createUserData(user.getDisplayName().toUpperCase(), user.getEmail(), null, user.getPhotoUrl().toString(), new OnQCompleteListener() {
                                    @Override
                                    public void onSuccess() {

                                        loadData(new OnQCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                loadingDialog.dismiss();
                                                goToMainActivity();
                                            }

                                            @Override
                                            public void onFailure()
                                            {
                                                Toast.makeText(LoginActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                                                 loadingDialog.dismiss();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure()
                                    {
                                        Toast.makeText(LoginActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                                        //Log.d()
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                            else {
                              //  Log.d("LOGGG", "Existing");

                                loadData(new OnQCompleteListener() {
                                    @Override
                                    public void onSuccess() {

                                        loadingDialog.dismiss();
                                        goToMainActivity();
                                    }

                                    @Override
                                    public void onFailure()
                                    {
                                        Toast.makeText(LoginActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                                        //Log.d()
                                        loadingDialog.dismiss();
                                    }
                                });

                            }


                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
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
