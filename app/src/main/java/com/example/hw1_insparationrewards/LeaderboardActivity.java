package com.example.hw1_insparationrewards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LeaderboardActivity";
    private RecyclerView recyclerView;
    private LeaderboardAdaptor adaptor;
    private List<Users> usersList = new ArrayList<>();
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setTitle("Inspiration Leaderboard");
        Log.d(TAG, "onCreate: You are in LeaderBoardActivity");
        recyclerView = findViewById(R.id.leaderboard_Recycler);
        adaptor = new LeaderboardAdaptor(usersList,this);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent i = getIntent();
        String[] userLogin = new String[4];
        if(i.hasExtra("userData")){
            String[] s =i.getStringArrayExtra("userData");
            username=s[0];
            password=s[1];
            new GetAllProfilesAPIAyncTask(this).execute(s);
            adaptor.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Users profile = usersList.get(pos);

        Intent i = new Intent(this,AwardActivity.class);
        i.putExtra("recipient",profile);
        i.putExtra("username",username);
        i.putExtra("password",password );
        startActivity(i);
    }

    public void listofUsers(String s) {
        Log.d(TAG, "listofUsers: "+s);
        try {
            JSONArray array = new JSONArray(s);
            JSONArray rewards = new JSONArray();
            Log.d(TAG, "listofUsers: array length: "+array.length());
            for (int i = 0; i<array.length();i++){
                Log.d(TAG, "listofUsers: count " +(i));
                JSONObject object = array.getJSONObject(i);
                Log.d(TAG, "listofUsers: User INFO: "+object.toString());
                String firstName = object.getString("firstName");
                String lastName = object.getString("lastName");
                String username = object.getString("username");
                String department = object.getString("department");
                String description = object.getString("story");
                String position = object.getString("position");
                int pointsToAward = object.getInt("pointsToAward");
                boolean admin = object.getBoolean("admin");
                String userImage = object.getString("imageBytes");
                if (!object.get("rewards").equals(null)){
                    Log.d(TAG, "listofUsers: Rewards for this user");
                    rewards = object.getJSONArray("rewards");
                    Log.d(TAG, "listofUsers: Rewards Array"+rewards.toString());
                }
                Users user = new Users(firstName,lastName,username,department,description,position,pointsToAward,admin,userImage,rewards);
                usersList.add(user);
            }
            adaptor.notifyDataSetChanged();
        }catch (Exception e){e.printStackTrace();}
    }
}
