package com.example.hw1_insparationrewards;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RewardsAPIAsyncTask extends AsyncTask<String,Void,String> {
    private static final String TAG = "RewardsAPIAsyncTask";
    private AwardActivity awardActivity;
    public RewardsAPIAsyncTask(AwardActivity awardActivity) {
        this.awardActivity=awardActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        JSONObject object = new JSONObject();
        JSONObject target = new JSONObject();
        JSONObject source = new JSONObject();
        Log.d(TAG, "doInBackground: For everyone "+strings[0]);
        try {
            //Person who gets the Award
            target.put("studentId","1939680");
            target.put("username", strings[0]);
            target.put("name", strings[1]);
            target.put("date",strings[2]);
            target.put("notes",strings[3]);
            target.put("value",strings[4]);
            Log.d(TAG, "doInBackground: Target: "+target.toString());
            //Person who sent the Award
            source.put("studentId","1939680");
            source.put("username",strings[5]);
            source.put("password",strings[6]);
            Log.d(TAG, "doInBackground: Source: "+source.toString());
            //What is sent to the API
            object.put("target",target);
            object.put("source",source);
        }catch (Exception e) {e.printStackTrace();}

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com"+"/rewards";


            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
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
        awardActivity.confirmConnection(s);
    }
}
