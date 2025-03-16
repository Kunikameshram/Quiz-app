package com.example.quizquest.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quizquest.Models.RankModel;
import com.example.quizquest.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private List<RankModel> userList;

    public RankAdapter(List<RankModel> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public RankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankAdapter.ViewHolder holder, int position) {

        String name = userList.get(position).getuName();
        int score = userList.get(position).getScore();
        int rank = userList.get(position).getRank();

        String photoUrl = userList.get(position).getPhotoUrl();

        holder.setData(name, score, rank, photoUrl);
    }

    @Override
    public int getItemCount() {

        if(userList.size() > 10)
            return 10;
        else
            return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTV, rankTV, scoreTV;
        private ImageView pImg;
        private TextView pText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.ra_uname);
            rankTV = itemView.findViewById(R.id.ra_rank);
            scoreTV = itemView.findViewById(R.id.ra_score);
            pImg = itemView.findViewById(R.id.lb_img);
            pText = itemView.findViewById(R.id.lb_text_img);
        }

        private void setData(String name, int score, int rank, String photoUrl)
        {
            if(photoUrl != null)
            {
                //set Profile img
                pImg.setVisibility(View.VISIBLE);
                pText.setVisibility(View.GONE);

                Glide.with(itemView.getContext()).load(photoUrl).into(pImg);

            }
            else
            {
                //set Alphabet img
                pImg.setVisibility(View.GONE);
                pText.setVisibility(View.VISIBLE);
                pText.setText(name.toUpperCase().substring(0,1));
            }


            nameTV.setText(name);
            rankTV.setText("Rank - " + String.valueOf(rank));
            scoreTV.setText("Score : " + String.valueOf(score));
        }

    }
}
