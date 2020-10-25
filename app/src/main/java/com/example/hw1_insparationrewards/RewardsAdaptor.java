package com.example.hw1_insparationrewards;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RewardsAdaptor extends RecyclerView.Adapter<RewardsViewHolder> {
    private static final String TAG = "RewardsAdaptor";
    private List<Rewards> rewardsList;
    private ProfileActivity profileActivity;

    public RewardsAdaptor(List<Rewards> rewardsList, ProfileActivity profileActivity) {
        this.rewardsList = rewardsList;
        this.profileActivity = profileActivity;
    }

    @NonNull
    @Override
    public RewardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rewards_list, parent, false);

        return new RewardsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardsViewHolder holder, int position) {
        Rewards reward= rewardsList.get(position);
        Log.d(TAG, "onBindViewHolder: Rewards Pos: "+rewardsList.toString());

        holder.date.setText(reward.getDate());
        holder.name.setText(reward.getName());
        holder.comment.setText(reward.getComment());
        holder.points.setText(String.valueOf(reward.getPoints()));

    }

    @Override
    public int getItemCount() {
        return rewardsList.size();

    }
}
