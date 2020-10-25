package com.example.hw1_insparationrewards;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

public class Users implements Serializable{
    private String firstName;
    private String lastName;
    private String username;
    private String department;
    private String description;
    private String position;
    private int pointsToAward;
    private boolean admin;
    private String userImage;
    private transient JSONArray rewards;
    private int pointsAwarded=0;

    public Users(String firstName, String lastName, String username, String department, String description, String position, int pointsToAward, boolean admin, String userImage, JSONArray rewards) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.department = department;
        this.description = description;
        this.position = position;
        this.pointsToAward = pointsToAward;
        this.admin = admin;
        this.userImage = userImage;
        this.rewards = rewards;
        parseRewards(rewards);
    }

    private void parseRewards(JSONArray awards){
        int awardPoints = 0;
        try {
            for (int i =0;i<awards.length();i++){
                JSONObject rewardObject = awards.getJSONObject(i);
                String name = rewardObject.getString("name");
                String date = rewardObject.getString("date");
                String comment = rewardObject.getString("notes");
                awardPoints+= rewardObject.getInt("value");
            }
            setPointsAwarded(awardPoints);
        }catch (Exception e){e.printStackTrace();}
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getPointsToAward() {
        return pointsToAward;
    }

    public void setPointsToAward(int pointsToAward) {
        this.pointsToAward = pointsToAward;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public JSONArray getRewards() {
        return rewards;
    }

    public void setRewards(JSONArray rewards) {
        this.rewards = rewards;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }
}
