package com.example.hw1_insparationrewards;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateProfileAPIAsyncTask extends AsyncTask<String,Void,String> {
    private static final String TAG = "UpdateProfileAPIAsyncTa";
    private EditProfileActivity editProfileActivity;
    private String userData;
    public  UpdateProfileAPIAsyncTask (EditProfileActivity editProfileActivity){
        this.editProfileActivity=editProfileActivity;
    }
    @Override
    protected String doInBackground(String... strings) {
        JSONObject object = new JSONObject();
        boolean admin;
        JSONArray jsonArray = new JSONArray();
        if (strings[8].equals("true")){
            admin = true;
        }
        else {
            admin = false;
        }
        Log.d(TAG, "doInBackground: "+admin);
        try {
            object.put("studentId","1939680");
            object.put("username", strings[0]);
            object.put("password", strings[1]);
            object.put("firstName",strings[2]);
            object.put("lastName",strings[3]);
            object.put("pointsToAward",Integer.parseInt(strings[4]));
            object.put("department",strings[5]);
            object.put("story",strings[6]);
            object.put("position",strings[7]);
            object.put("admin",admin);
            object.put("location",strings[9]);
            object.put("imageBytes",strings[10]);
            object.put("rewardsRecords",jsonArray);
            userData = object.toString();
        }catch (Exception e) {e.printStackTrace();}

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com"+"/profiles";


            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(object.toString());
            out.close();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();

            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                return result.toString();

            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                Log.d(TAG, "doInBackground: Result: "+ result.toString());
                return result.toString();

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        return "Error performing POST request";
    }

    @Override
    protected void onPostExecute(String s) {
        editProfileActivity.confirmUpdate(s,userData);
    }
}
