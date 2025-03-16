package com.example.quizquest;


import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quizquest.Adapters.RankAdapter;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.quizquest.DbQuery.g_userList;
import static com.example.quizquest.DbQuery.getTopUsers;
import static com.example.quizquest.DbQuery.isMeOnTopList;
import static com.example.quizquest.DbQuery.myPerformance;
import static com.example.quizquest.DbQuery.usersCount;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderBoardFragment extends Fragment {


    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private RankAdapter adapter;
    private TextView myScoreTV, myRankTV;
    private TextView totalUsersTV;
    private TextView myProfileTV;
    private ImageView myProfileImg;
    private Dialog loadingDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leader_board, container, false);

        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("LeaderBoard");

        recyclerView = view.findViewById(R.id.lb_recycler_view);
        myRankTV = view.findViewById(R.id.my_rank);
        myScoreTV = view.findViewById(R.id.my_score);
        totalUsersTV = view.findViewById(R.id.total_users);
        myProfileImg = view.findViewById(R.id.myp_img);
        myProfileTV = view.findViewById(R.id.myp_text_img);

        TextView title = getActivity().findViewById(R.id.title);
        title.setText("LeaderBoard");

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        totalUsersTV.setText("Total Users : " + usersCount);

        adapter = new RankAdapter(g_userList);
        recyclerView.setAdapter(adapter);

            getTopUsers(new OnQCompleteListener() {
                @Override
                public void onSuccess() {

                    adapter.notifyDataSetChanged();

                    if(myPerformance.getScore() != 0) {

                        if (!isMeOnTopList) {
                            calculateMyRank();
                        }

                        myScoreTV.setText(String.valueOf("Score : " + myPerformance.getScore()));
                        myRankTV.setText(String.valueOf("Rank - " + myPerformance.getRank()));

                    }

                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure()
                {
                    Toast.makeText(getContext(),"Something went wrong ! Please Try Again Later ",Toast.LENGTH_SHORT).show();
                    //Log.d()
                    loadingDialog.dismiss();
                }
            });


        Uri photoUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        if(photoUrl != null)
        {
            //set Profile img
            myProfileImg.setVisibility(View.VISIBLE);
            myProfileTV.setVisibility(View.GONE);

            Glide.with(getContext()).load(photoUrl).into(myProfileImg);

        }
        else
        {
            //set Alphabet img
            myProfileImg.setVisibility(View.GONE);
            myProfileTV.setVisibility(View.VISIBLE);
            myProfileTV.setText(myPerformance.getuName().toUpperCase().substring(0,1));
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

        myPerformance.setRank(rank);
    }

}
