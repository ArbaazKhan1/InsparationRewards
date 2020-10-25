package com.example.hw1_insparationrewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView name;
    private TextView username;
    private TextView location;
    private ImageView profilePic;
    private TextView totalPoints;
    private TextView department;
    private TextView position;
    private TextView pointsToGive;
    private TextView story;
    private RecyclerView recycler;
    private RewardsAdaptor adaptor;
    private List<Rewards> rewardsList = new ArrayList<>();
    private String firstName;
    private String lastName;
    private String password;
    private boolean admin;
    private String imageString64;
    private JSONArray rewards = new JSONArray();
    private int point = 0;
    private static final String TAG = "ProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Your Profile");
        name = findViewById(R.id.last_first_Name_TextView);
        username = findViewById(R.id.username_TextView);
        location = findViewById(R.id.location_TextView);
        profilePic = findViewById(R.id.picture_ImageView);
        totalPoints = findViewById(R.id.totalPoints_TextView);
        department = findViewById(R.id.Edit_userDepartment_TextView);
        position = findViewById(R.id.userPosition_TextView);
        pointsToGive = findViewById(R.id.points_to_Give_TextView);
        story = findViewById(R.id.story_TextView);
        recycler = findViewById(R.id.rewards_RecyclerView);
        adaptor = new RewardsAdaptor(rewardsList,this);
        recycler.setAdapter(adaptor);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        if (intent.hasExtra("userData")){
            String s =intent.getStringExtra("userData");

            try{
                JSONObject jsonObject = new JSONObject(s);
                Log.d(TAG, "onCreate: "+jsonObject.toString());
                firstName = jsonObject.getString("firstName");
                lastName = jsonObject.getString("lastName");
                String fullName =  lastName + ", "+ firstName;
                name.setText(fullName);
                username.setText("("+jsonObject.getString("username")+")");
                password = jsonObject.getString("password");
                admin = jsonObject.getBoolean("admin");
                location.setText(jsonObject.getString("location"));
                department.setText(jsonObject.getString("department"));
                position .setText(jsonObject.getString("position"));
                pointsToGive.setText(jsonObject.getString("pointsToAward"));
                story.setText(jsonObject.getString("story"));
                imageString64 = jsonObject.getString("imageBytes");
                textToImage(imageString64);
                if (!jsonObject.get("rewards").equals(null)){
                    Log.d(TAG, "onCreate: USER HAS REWARDS!");
                    rewards =jsonObject.getJSONArray("rewards");
                    parseRewardsArray(rewards);
                }
                adaptor.notifyDataSetChanged();
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /////////////////////////////////////   Parse Rewards Array ///////////////////////////////////
    private void parseRewardsArray(JSONArray rewardRecords){
        Log.d(TAG, "parseRewardsArray: Array Size: "+rewardRecords.length());
        try {
            for (int i =0;i<rewardRecords.length();i++){
                JSONObject rewardObject = rewardRecords.getJSONObject(i);
                Log.d(TAG, "parseRewardsArray: "+rewardObject.toString());
                String useName = rewardObject.getString("name");
                String date = rewardObject.getString("date");
                String comment = rewardObject.getString("notes");
                int points = rewardObject.getInt("value");
                point+=points;
                Rewards reward = new Rewards(date,useName,points,comment);
                rewardsList.add(reward);
                Log.d(TAG, "parseRewardsArray: Reward Source: "+useName);
            }
            totalPoints.setText(String.valueOf(point));
            adaptor.notifyDataSetChanged();
        }catch (Exception e){e.printStackTrace();}

    }

    /////////////////////////////////////     Text to Image   //////////////////////////////////////
    public void textToImage(String imageString64) {
        if (imageString64 == null) return;
        Log.d(TAG, "textToImage: "+imageString64);
        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        Log.d(TAG, "doConvert: Image byte array length: " + imageString64.length());

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        Log.d(TAG, "doConvert: Bitmap created from Base 64 text");
        profilePic.setImageBitmap(bitmap);
    }

    ///////////////////////////////////////////     Admin Check     ///////////////////////////////
    public String adminCheck() {
        if (admin){
            return "true";
        }
        else {
            return "false";
        }
    }


    ////////////////////////////////////////    options MENU STUFF ////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_options,menu);
        return super.onCreateOptionsMenu(menu);
    }


    //THis is where Leaderboard Activity and Edit Activity are called
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        String[] userData = new String[12];
        userData[0]=(username.getText().toString().replace("(", "").replace(")",""));
        Log.d(TAG, "onOptionsItemSelected: "+userData[0]);
        userData[1]=(password);
        userData[2]=(firstName);
        userData[3]=(lastName);
        userData[4]=(department.getText().toString());
        userData[5]=(story.getText().toString());
        userData[6]=(position.getText().toString());
        userData[7]=(adminCheck());
        userData[8]=(location.getText().toString());
        userData[9]=(imageString64);
        userData[10]=(pointsToGive.getText().toString());

        switch (item.getItemId()){
            case R.id.EditProfile_OptionItem:
                Log.d(TAG, "onOptionsItemSelected: Selected Edit Activity");
                Log.d(TAG, "onOptionsItemSelected: "+userData);
                intent = new Intent(this,EditProfileActivity.class);
                intent.putExtra("userData",userData);
                startActivity(intent);
                break;
            case R.id.Leaderboard_OptionItem:
                intent = new Intent(this,LeaderboardActivity.class);
                intent.putExtra("userData",userData);
                Log.d(TAG, "onOptionsItemSelected: Selected Leaderboard Activity"+userData);
                startActivity(intent);
                break;
             default:
                 Log.d(TAG, "onOptionsItemSelected: Invalid");
                break;       
        }
        return true;
    }
}
