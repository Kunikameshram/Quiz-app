package com.example.quizquest;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.quizquest.DbQuery.g_userList;
import static com.example.quizquest.DbQuery.getTopUsers;
import static com.example.quizquest.DbQuery.isMeOnTopList;
import static com.example.quizquest.DbQuery.myPerformance;
import static com.example.quizquest.DbQuery.usersCount;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {


    public MyProfileFragment() {
        // Required empty public constructor
    }

    private LinearLayout logoutB;
    private ImageView profileImg;
    private TextView profileTxt;
    private TextView profileName;
    private TextView myRank;
    private TextView myScore;
    private LinearLayout goToLeaderB;
    private LinearLayout bookmarkB;
    private BottomNavigationView bottomNavigationView;
    private Dialog loadingDialog;
    private LinearLayout myAccounntB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        TextView title = getActivity().findViewById(R.id.title);
        title.setText("My Account");

        logoutB = view.findViewById(R.id.logout);
        profileImg = view.findViewById(R.id.profile_img);
        profileTxt = view.findViewById(R.id.profile_text_img);
        profileName = view.findViewById(R.id.profile_uname);
        myRank = view.findViewById(R.id.ma_rank);
        myScore = view.findViewById(R.id.ma_score);
        goToLeaderB = view.findViewById(R.id.ma_leaderboardB);
        bookmarkB = view.findViewById(R.id.ma_bookmarkB);
        bottomNavigationView = getActivity().findViewById(R.id.navigation);
        myAccounntB = view.findViewById(R.id.myAccountB);


        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();


        logoutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                // Build a GoogleSignInClient with the options specified by gso.
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);


                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getContext(),LoginActivity.class);
                        getContext().startActivity(intent);
                        getActivity().finish();
                    }
                });

            }
        });

        goToLeaderB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_leaderboard);
            }
        });


        bookmarkB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), BookMarksActivity.class);
                startActivity(intent);

            }
        });

        myAccounntB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyAccountActivity.class);
                startActivity(intent);
            }
        });



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            String name = user.getDisplayName();

            Uri photoUrl = user.getPhotoUrl();
            if(photoUrl != null)
            {
                //set Profile img
                profileImg.setVisibility(View.VISIBLE);
                profileTxt.setVisibility(View.GONE);

                Glide.with(this).load(photoUrl).into(profileImg);
                //drawerProfileImg.setImageURI(photoUrl);
            }
            else
            {
                //set Alphabet img
                profileImg.setVisibility(View.GONE);
                profileTxt.setVisibility(View.VISIBLE);
                profileTxt.setText(name.toUpperCase().substring(0,1));
            }

            profileName.setText(name);
        }



        if(g_userList.size() == 0)
        {
            getTopUsers(new OnQCompleteListener() {
                @Override
                public void onSuccess() {

                    if(myPerformance.getScore() != 0) {

                        if (!isMeOnTopList) {
                            calculateMyRank();
                        }

                        myScore.setText(String.valueOf(myPerformance.getScore()));
                        myRank.setText(String.valueOf(myPerformance.getRank()));
                    }

                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure()
                {
                    Toast.makeText(getContext(),"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                    //Log.d()/
                    loadingDialog.dismiss();
                }
            });
        }
        else
        {
            myScore.setText(String.valueOf(myPerformance.getScore()));
            if(myPerformance.getScore() == 0)
            {
                myRank.setText("NA");
            }
            else {
                myRank.setText(String.valueOf(myPerformance.getRank()));
            }

            loadingDialog.dismiss();
        }



        return view;
    }


    private void calculateMyRank()
    {

        int lowTopScore = g_userList.get(g_userList.size() - 1).getScore();

        int remaining_slots = usersCount - 20;
        int my_slots = (remaining_slots*myPerformance.getScore())/ lowTopScore;

        int rank;
        if(lowTopScore != myPerformance.getScore())
            rank = usersCount - my_slots;
        else
            rank = 21;



        /*
        int lowTopScore = g_userList.get(g_userList.size() - 1).getScore();
        Log.d("lowest score = ", String.valueOf(lowTopScore));

        Log.d("my score = ", String.valueOf(myPerformance.getScore()));

        for(int i=0; i < g_userList.size(); i++)
            Log.d("LOGGG",String.valueOf(g_userList.get(i).getScore()));

        int step = usersCount/lowTopScore;

        int rank=0;
        if(lowTopScore != myPerformance.getScore())
            rank  = ((lowTopScore - myPerformance.getScore())* step) - step/2;
        else
            rank = 21;

            */

        myPerformance.setRank(rank);
    }

}
