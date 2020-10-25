package com.example.hw1_insparationrewards;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;


public class AwardActivity extends AppCompatActivity {
    private static final String TAG = "AwardActivity";
    private Users user;
    private TextView name;
    private TextView department;
    private TextView position;
    private TextView story;
    private TextView pointsToAward;
    private ImageView profPic;
    private EditText comment;
    private EditText pointsGiven;
    private String username;
    private String password;
    private TextView commentLength;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award);
        name = findViewById(R.id.award_fullName_TextView);
        department = findViewById(R.id.award_department_TextView);
        position = findViewById(R.id.award_position_TextView);
        story = findViewById(R.id.award_story_TextView);
        pointsToAward = findViewById(R.id.award_pointsToAward_TextView);
        profPic = findViewById(R.id.award_userImage_ImageView);
        comment = findViewById(R.id.award_rewardComment_EditText);
        pointsGiven = findViewById(R.id.award_PointsToSend_EditText);
        commentLength = findViewById(R.id.award_Comment);
        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String currentText = s.toString();
                int currentLength = currentText.length();
                commentLength.setText("Your Story: ( "+String.valueOf(currentLength)+" of 80)");
            }
        });
        Intent intent = getIntent();
        if (intent.hasExtra("recipient")){
            user = (Users) intent.getSerializableExtra("recipient");
            name.setText(user.getLastName()+", "+user.getFirstName());
            department.setText(user.getDepartment());
            position.setText(user.getPosition());
            story.setText(user.getDescription());
            pointsToAward.setText(String.valueOf(user.getPointsToAward()));
            String imageString64 = user.getUserImage();
            if (imageString64 == null) return;
            byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profPic.setImageBitmap(bitmap);
            setTitle(user.getFirstName()+" " +user.getLastName());
        }
        if(intent.hasExtra("username")){
            username = intent.getStringExtra("username");
        }
        if(intent.hasExtra("password")){
            password = intent.getStringExtra("password");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_profile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String[] awardInfo = new String[8];
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String strDate= format.format(date);

        //TODO NEED TO FIX
        if (item.getItemId()==R.id.saveProflie){
            awardInfo[0] = user.getUsername();
            awardInfo[1] = user.getFirstName()+" "+user.getLastName();
            awardInfo[2] = strDate;
            awardInfo[3] = comment.getText().toString();
            awardInfo[4] = pointsGiven.getText().toString();
            awardInfo[5] = username;
            awardInfo[6] = password;
            saveDialog(awardInfo);

        }
        return true;
    }

    public void confirmConnection (String s){
        Log.d(TAG, "confirmConnection: "+s);
        String[] userdata = new String[3];
        Intent i = new Intent(this,LeaderboardActivity.class);
        userdata[0] = username;
        userdata[1] = password;
        i.putExtra("userData",userdata);
        startActivity(i);
    }

    ////////////////////////////   Dialog   //////////////////////////////////////////
    private void saveDialog(final String[] data){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Reward Points?");
        builder.setMessage("Add rewards for "+user.getFirstName()+" "+user.getLastName()+"?");
        builder.setIcon(R.drawable.icon);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: Rewards sent");
                showToast();
                new RewardsAPIAsyncTask(AwardActivity.this).execute(data);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: No Rewards for YOU");
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    ///////////////////////////////////   Custom Toast  //////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showToast() {
        String msg = "Add Reward Successful";
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }
}
