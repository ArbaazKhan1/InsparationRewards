package com.example.hw1_insparationrewards;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
     ImageView userImage;
     TextView fullName;
     TextView posDep;
     TextView points;
    public LeaderboardViewHolder(@NonNull View itemView) {
        super(itemView);
        userImage= itemView.findViewById(R.id.userImage_ImageView);
        fullName = itemView.findViewById(R.id.fullName_TextView);
        posDep = itemView.findViewById(R.id.posDep_TextView);
        points = itemView.findViewById(R.id.points_TextView);
    }
}
