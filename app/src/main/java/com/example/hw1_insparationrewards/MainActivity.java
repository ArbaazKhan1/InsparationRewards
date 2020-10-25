package com.example.hw1_insparationrewards;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private LocationManager locationManager;
    private Criteria criteria;
    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private ConnectivityManager connectivityManager;
    private String location;
    private EditText username;
    private EditText password;
    private CheckBox memery;
    private SharedPreferences rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Rewards");
        username = findViewById(R.id.username_EditText);
        password = findViewById(R.id.password_EditText);
        memery = findViewById(R.id.remember_CheckBox);
        rememberMe = getSharedPreferences("remember_users",Context.MODE_PRIVATE);
        boolean checked = rememberMe.getBoolean("checked",false);
        if (checked){
            Log.d(TAG, "onCreate: Loading preferences");
            username.setText(rememberMe.getString("username",null));
            password.setText(rememberMe.getString("password",null));
            memery.setChecked(true);
        }
        if(!netCheck()){    //Checks for Network Connectivity
            //noConnectionAlert();
            Log.d(TAG, "onCreate: Cannot Connect to Network");
        }
        else {
            Log.d(TAG, "onCreate: Network Connected");
            //The following is all work setup for location Permissions
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_LOCATION_REQUEST_CODE_ID);
            } else {

                setLocation();
            }

        }
    }

    /////////////////////////////////////////   Location Services (ask permision and set location)   ///////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
            }
        }
    }


    private void setLocation() {
        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
            return;
        }
        Location currentLocation = locationManager.getLastKnownLocation(bestProvider);
        if (currentLocation != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses;
                addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(),1);
                displayAddresses(addresses);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "setLocation: "+location);
        } else {

            Log.d(TAG, "setLocation: Location Unavailable");
        }
    }

    private void displayAddresses(List<Address> addresses) {
        if (addresses.size() == 0) {
            return;
        }
        for (Address ad : addresses) {

            location = String.format("%s, %s",

                    (ad.getLocality() == null ? "" : ad.getLocality()),
                    (ad.getAdminArea() == null ? "" : ad.getAdminArea()));
        }
        Log.d(TAG, "displayAddresses: "+location);
    }
    ////////////////////////////   NetWork Connections   ///////////////////////////////////////

    private boolean netCheck(){
        if (connectivityManager==null){
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager==null){
                Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            Toast.makeText(this, "Connected to Network", Toast.LENGTH_SHORT).show();
            return true;

        } else {
            Toast.makeText(this, "Cannot Connect to Network", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /////////////////////////////////         CREATE PROFILE   ////////////////////////////////

    public void launchCreateActivity(View view) {
        Log.d(TAG, "launchCreateActivity: Started Create Profile Activity");
        Intent i = new Intent(this,CreateProfileActivity.class);
        i.putExtra("loc",location);
        startActivity(i);
    }

    /////////////////////////////////          DELETE ALL USERS    ////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void deleteUsers(View view) {
        Log.d(TAG, "deleteUsers: ");

        new DeleteAllUsers(this).execute();
        showToast();
    }

    public void deleteConfirm(String s) {
        Log.d(TAG, "deleteConfirm: "+s);
    }


    //////////////////////////////////////      LOGIN STUFF    ////////////////////////////////////
    public void login(View view) {
        SharedPreferences.Editor editor = rememberMe.edit();
        String[] s = {username.getText().toString(),password.getText().toString()};
        Log.d(TAG, "login: "+s);
        if (memery.isChecked()){
            editor.putBoolean("checked",true);
            editor.putString("username",username.getText().toString());
            editor.putString("password",password.getText().toString());
            Log.d(TAG, "login: Preferences Saved");
        }
        editor.apply();
        new LoginAPIAyncTask(this).execute(s);
    }

    public void loadProfile(String s) {
        Log.d(TAG, "loadProfile: Profile: "+s);
        try {
            JSONObject object = new JSONObject(s);

            if (object.has("errordetails")){
                Log.d(TAG, "loadProfile: No Profile: ");
                nonUserAlert();
                return;
            }
            Log.d(TAG, "loadProfile: User Logging In");
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("userData",s);
            startActivity(intent);


            
        }catch (Exception e){e.printStackTrace();}

    }

    ////////////////////////////////////////  Dialog        ////////////////////////////////////
    private void nonUserAlert() {
        Log.d(TAG, "nonUserAlert: Not a user!");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Invalid Username or Password");
        builder.setIcon(R.drawable.icon);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    ///////////////////////////////////   Custom Toast  //////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showToast() {
        String msg = "All users Deleted";
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }
}
