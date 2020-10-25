package com.example.hw1_insparationrewards;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

public class CreateProfileActivity extends AppCompatActivity {
    private static final String TAG = "CreateProfileActivity";
    private int REQUEST_IMAGE_GALLERY = 1;
    private int REQUEST_IMAGE_CAPTURE = 2;
    private File currentImageFile;
    private String location;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText department;
    private EditText position;
    private EditText description;
    private CheckBox admin;
    private ImageView profilePic;
    private String imageString64;
    private TextView story;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        setTitle("Create Profile");
        username =findViewById(R.id.selectUsName_TextView);
        password =findViewById(R.id.selectPass_EditText);
        firstName =findViewById(R.id.firstName_EditText);
        lastName =findViewById(R.id.lastName_EditText);
        department =findViewById(R.id.department_EditText);
        position =findViewById(R.id.position_EditText);
        description =findViewById(R.id.description_EditText);
        admin =findViewById(R.id.adminUser_CheckBox);
        profilePic =findViewById(R.id.profilePic_ImageView);
        story = findViewById(R.id.yourStory_TextView);
        description.addTextChangedListener(new TextWatcher() {
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

                story.setText("Your Story: ( "+String.valueOf(currentLength)+" of 360)");

            }
        });
        /////
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        /////
        Intent intent = getIntent();
        if (intent.hasExtra("loc")){
            location =intent.getStringExtra("loc");
            Log.d(TAG, "onCreate: Location is: "+location);
        }
    }


    public void loadProfile(String s) {
        Log.d(TAG, "loadProfile: Profile: "+s);
        Intent intent = new Intent(this,ProfileActivity.class);
        intent.putExtra("userData",s);
        startActivity(intent);
    }



    //This will an Onclick method for when the users is changing their profile pic
    public void setImage(final View view) {
        Log.d(TAG, "setImage: ");
        cameraORgallery(view);
    }


    /////////////////////////////////////////     CAMERA     /////////////////////////////
    public void doCamera(View v) {
        currentImageFile = new File(getExternalCacheDir(), "appimage_" + System.currentTimeMillis() + ".jpg");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
    private void processCamera() {
        Uri selectedImage = Uri.fromFile(currentImageFile);
        profilePic.setImageURI(selectedImage);
        Bitmap bm = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
    }


    //////////////////////////////////////////////////     GALLERY     ////////////////////////////////
    public void doGallery(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        profilePic.setImageBitmap(selectedImage);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                processCamera();
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    ///////////////////////////////////////////     Image to String ////////////////////////////////

    private void doConvert(int jpgQuality) {
        if (profilePic.getDrawable() == null)
            return;

        Bitmap origBitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();

        ByteArrayOutputStream bitmapAsByteArrayStream = new ByteArrayOutputStream();
        origBitmap.compress(Bitmap.CompressFormat.JPEG, jpgQuality, bitmapAsByteArrayStream);

        imageString64 = Base64.encodeToString(bitmapAsByteArrayStream.toByteArray(), Base64.DEFAULT);
        Log.d(TAG, "doConvert: Image in Base64 size: " + imageString64.length());



    }

    ///////////////////////////////////////////     Admin Check     ///////////////////////////////
    public String adminCheck() {
        if (admin.isChecked()){
            return "true";
        }
        else {
            return "false";
        }
    }

    ////////////////////////////////////////////    Options Menu    ///////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_profile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    ///This will be passing user Data to CreateProfileAPIASYNCTask
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.saveProflie){
            Log.d(TAG, "onOptionsItemSelected: save profile");
            String[] userData = new String[10];
            doConvert(50);

            userData[0]=(username.getText().toString());
            userData[1]=(password.getText().toString());
            userData[2]=(firstName.getText().toString());
            userData[3]=(lastName.getText().toString());
            userData[4]=(department.getText().toString());
            userData[5]=(description.getText().toString());
            userData[6]=(position.getText().toString());
            userData[7]=(adminCheck());
            userData[8]=(location);
            userData[9]=(imageString64);

            saveDialog(userData);

    }
        return true;
    }

    ////////////////////////    Dialogs     /////////////////////////////////


    private void cameraORgallery(final View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture");
        builder.setMessage("Take picture from: ");
        builder.setIcon(R.drawable.icon);
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: GO GO Gallery!");
                doGallery(view);    //will set profile to image from gallery
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: Cancel");
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: Camera");
                doCamera(view); //set image to camera pic

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveDialog(final String[] userData){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes?");
        builder.setIcon(R.drawable.icon);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: Save Profile");
                showToast();
                new CreateProfileAPIAsyncTask(CreateProfileActivity.this).execute(userData); //Async Task for API Info
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: Do not Save profile");
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    ///////////////////////////////////   Custom Toast  //////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showToast() {
        String msg = "User Create Successful";
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }
}
