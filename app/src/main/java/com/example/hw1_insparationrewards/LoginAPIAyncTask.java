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

public class LoginAPIAyncTask extends AsyncTask<String,Void, String> {
    private static final String TAG = "LoginAPIAyncTask";
    private MainActivity mainActivity;
    public LoginAPIAyncTask (MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }
    @Override
    protected String doInBackground(String... strings) {
        JSONObject object = new JSONObject();
        try {
            object.put("studentId","1939680");
            object.put("username", strings[0]);
            object.put("password", strings[1]);
        }catch (Exception e) {e.printStackTrace();}

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com"+"/login";


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
        mainActivity.loadProfile(s);
    }
}
