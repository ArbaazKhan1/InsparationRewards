package com.example.hw1_insparationrewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdaptor extends RecyclerView.Adapter<LeaderboardViewHolder>{
    private static final String TAG = "LeaderboardAdaptor";
    private List<Users> userList;
    private LeaderboardActivity leaderboardActivity;

    public LeaderboardAdaptor(List<Users> userList, LeaderboardActivity leaderboardActivity) {
        this.userList = userList;
        this.leaderboardActivity = leaderboardActivity;
    }


    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_list, parent, false);

        itemView.setOnClickListener(leaderboardActivity);
        //itemView.setOnLongClickListener(mainActivity);

        return new LeaderboardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        Users user = userList.get(position);
        Log.d(TAG, "onBindViewHolder: "+user.getUsername());
        String imageString64 = user.getUserImage();
        if (imageString64 == null) return;
        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        holder.userImage.setImageBitmap(bitmap);
        Log.d(TAG, "onBindViewHolder: "+user.getFirstName());
        holder.fullName.setText(user.getLastName()+", "+user.getFirstName());
        holder.posDep.setText(user.getPosition()+", "+user.getDepartment());
        holder.points.setText(String.valueOf(user.getPointsAwarded()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
