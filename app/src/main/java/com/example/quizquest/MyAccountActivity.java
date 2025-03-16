package com.example.quizquest;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.example.quizquest.DbQuery.myprofile;
import static com.example.quizquest.DbQuery.saveProfileData;


public class MyAccountActivity extends AppCompatActivity {

    private EditText name, email, phone, state;
    private LinearLayout editB;
    private LinearLayout buttonsLayout;
    private Button cancelB, saveB;
    private Dialog loadingDialog;
    private String nameStr, phoneStr, stateStr;
    private ImageView profileImg;
    private TextView profileTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        name = findViewById(R.id.mp_name);
        email = findViewById(R.id.mp_email);
        phone = findViewById(R.id.mp_phone);
        state = findViewById(R.id.mp_state);
        editB = findViewById(R.id.editB);
        buttonsLayout = findViewById(R.id.buttonLayout);
        cancelB = findViewById(R.id.mp_cancel);
        saveB = findViewById(R.id.mp_save);
        profileImg = findViewById(R.id.profile_img);
        profileTxt = findViewById(R.id.profile_text_img);


        loadingDialog = new Dialog(MyAccountActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        disableEditing();

        editB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditing();
            }
        });


        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEditing();
            }
        });


        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateData())
                {
                    saveData();
                }

            }
        });

    }

    private boolean validateData()
    {
        nameStr = name.getText().toString();
        phoneStr = phone.getText().toString();
        stateStr = state.getText().toString();

        if(nameStr.isEmpty())
        {
            name.setError("Name can not be Empty !");
            return false;
        }

        if(! phoneStr.isEmpty()) {

            if ((phoneStr.length() != 10) && (TextUtils.isDigitsOnly(phoneStr))) {
                phone.setError("Enter Valid Phone Number");
                return false;
            }
        }

        return true;
    }


    private void disableEditing()
    {
        name.setEnabled(false);
        email.setEnabled(false);
        phone.setEnabled(false);
        state.setEnabled(false);

        name.setText(myprofile.getName());
        email.setText(myprofile.getEmailID());

        if(myprofile.getPhoneNo() != null)
            phone.setText(myprofile.getPhoneNo());

        if(myprofile.getState() != null)
            state.setText(myprofile.getState());

        buttonsLayout.setVisibility(View.GONE);


        String name = myprofile.getName();
        String photoUrl = myprofile.getPhotoURL();
        if(photoUrl != null)
        {
            //set Profile img
            profileImg.setVisibility(View.VISIBLE);
            profileTxt.setVisibility(View.GONE);

            Glide.with(this).load(photoUrl).into(profileImg);
        }
        else
        {
            //set Alphabet img
            profileImg.setVisibility(View.GONE);
            profileTxt.setVisibility(View.VISIBLE);
            profileTxt.setText(name.toUpperCase().substring(0,1));
        }



    }

    private void enableEditing()
    {
        name.setEnabled(true);
        //email.setEnabled(true);
        phone.setEnabled(true);
        state.setEnabled(true);

        buttonsLayout.setVisibility(View.VISIBLE);
    }


    private void saveData()
    {
        loadingDialog.show();

        if(stateStr.isEmpty())
            stateStr = null;

        if(phoneStr.isEmpty())
            phoneStr = null;

        saveProfileData(nameStr,phoneStr, stateStr, new OnQCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MyAccountActivity.this,"Profile updated successfully.",Toast.LENGTH_SHORT).show();

                disableEditing();

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(MyAccountActivity.this,"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                //Log.d()
                loadingDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            MyAccountActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
