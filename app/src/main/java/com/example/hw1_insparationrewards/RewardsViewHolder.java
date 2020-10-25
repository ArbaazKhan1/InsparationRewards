package com.example.hw1_insparationrewards;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RewardsViewHolder extends RecyclerView.ViewHolder {
    TextView date;
    TextView name;
    TextView points;
    TextView comment;
    public RewardsViewHolder(@NonNull View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.rewards_date_TextView);
        name = itemView.findViewById(R.id.rewards_name_TextView);
        points = itemView.findViewById(R.id.rewards_points_TextView);
        comment = itemView.findViewById(R.id.rewards_story_TextView);
    }
}
