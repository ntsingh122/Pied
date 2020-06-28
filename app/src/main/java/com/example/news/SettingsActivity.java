package com.example.news;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private TextView displayName,displayStatus;
    private Button changeImageButton,changeStatusButton;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;
    private ProgressBar progressBar;
    View parentLayout;
    private Uri mCropImageUri;
    private StorageReference mStorageRef;
    private String cloudImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        circleImageView = findViewById(R.id.profile_image);
        displayName = findViewById(R.id.display_name);
        displayStatus = findViewById(R.id.status_name);
        changeImageButton = findViewById(R.id.change_image_btn);
        changeStatusButton = findViewById(R.id.status_change_btn);
        progressBar = findViewById(R.id.settings_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        parentLayout = findViewById(android.R.id.content);


        if(displayName.getText().toString().equalsIgnoreCase("Display Name"))
            progressBar.setVisibility(View.VISIBLE);



        mStorageRef = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                displayName.setText(users.name);
                displayStatus.setText(users.status);
                cloudImagePath = users.image;
                Picasso.get().load(cloudImagePath).into(circleImageView);

                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusChange();
            }
        });


        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);

            }
        });
//    String imageName = mCurrentUser.getUid()+"_dp";
//        try {
//            String mImageUri = preferences.getString(imageName, null);
//            if (mImageUri != null)
//                circleImageView.setImageURI(Uri.parse(mImageUri));
//            else {
//               String url = downloadImage(SettingsActivity.this, Uri.parse(cloudImagePath),imageName);
//               saveInSharedPreference(imageName,url);
//               circleImageView.setImageURI(Uri.parse(url));
//            }
//        }
//        catch (NullPointerException e)
//        {
//
//        }
    }

    private void onSelectImageClick(View v) {
        CropImage.startPickImageActivity(this);
    }


    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            String message="";
            if (resultCode == RESULT_OK) {
                message = "Cropping successful, Sample: " + result.getSampleSize();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                 message = "Cropping failed: " + result.getError();
            }
            setProfilePicture(result.getUri(),message);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setMultiTouchEnabled(true)
                .start(this);
    }



    private void setProfilePicture(Uri uri, String message) {
        circleImageView.setImageURI(uri);
        final String imageName = mCurrentUser.getUid()+"_dp";


        Log.i("path : ",uri.toString());


        UploadTask uploadTask = mStorageRef.child("profile_images").child(imageName).putFile(uri);



        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SettingsActivity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
                {
                             public void onComplete(@NonNull Task<Uri> task) {
                                String fileLink = task.getResult().toString();
                                //next work with URL
//                                 String url = downloadImage(SettingsActivity.this,Uri.parse(fileLink),imageName);
//                                 saveInSharedPreference(imageName, url);
                                Toast.makeText(SettingsActivity.this, fileLink, Toast.LENGTH_SHORT).show();
                                mUserDatabase.child("image").setValue(fileLink);

                            }
                });
            }
        });

        Snackbar.make(parentLayout,message,Snackbar.LENGTH_SHORT).show();

    }

//file:///data/user/0/com.example.news/cache/cropped537920682911385029.jpg
    private void statusChange(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Update Status");
        final  View customLayout = getLayoutInflater().inflate(R.layout.alertdialog_layout,null,true);
        builder.setView(customLayout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = customLayout.findViewById(R.id.editText);
                String newStatus = editText.getText().toString();
               sendStatus(newStatus);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendStatus(String newStatus) {
        displayStatus.setText(newStatus);
        mUserDatabase.child("status").setValue(newStatus);
        Snackbar.make(parentLayout,"Status Updated",Snackbar.LENGTH_LONG).show();
    }

    private void saveInSharedPreference(String key,String value)
    {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    private String downloadImage(Context context,Uri uri,String fileName){
        DownloadManager downloadManager  = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalFilesDir(context,DIRECTORY_DCIM,fileName+".jpg");
        downloadManager.enqueue(request);
        return DIRECTORY_DCIM+fileName+".jpg";
    }
}