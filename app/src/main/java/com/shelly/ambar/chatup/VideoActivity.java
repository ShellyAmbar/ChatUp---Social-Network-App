package com.shelly.ambar.chatup;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Objects;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button post_video;
    private Button start_again_btn;
    private Button stop_btn;
    private Uri contentURI;
    private MediaPlayer mediaPlayer;
    private MediaController.MediaPlayerControl mediaPlayerControl;
    MediaController mediaController ;
    private String VideoFileName;
    private final static String APP_PATH_SD_CARD = "/DesiredSubfolderName/";
    private final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";

    StorageTask uploadTask;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView=findViewById(R.id.video_preview);




        post_video=findViewById(R.id.post_video);
        start_again_btn=findViewById(R.id.start_again_btn);
        stop_btn=findViewById(R.id.stop_btn);
        storageReference=FirebaseStorage.getInstance().getReference("Posts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        VideoFileName=getIntent().getStringExtra("VideoFileName");
        contentURI=getContentVideoFromStorageFile(VideoFileName);

        videoView.setVideoURI(contentURI);


        videoView.requestFocus();
        videoView.start();




        post_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFilter= getIntent();
                if (intentFilter.getStringExtra("Activity").equals("Post") ){

                    uploadVideo();



                }else if (intentFilter.getStringExtra("Activity").equals("Story") ){

                    uploadVideo();

                    Intent intent =new Intent(VideoActivity.this,StoryActivity.class);
                    intent.putExtra("VideoFileName", VideoFileName);
                    intent.putExtra("Activity","Video");
                    startActivity(intent);
                    finish();

                }
            }
        });

        start_again_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
            }
        });


        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
            }
        });


    }

    private Uri getContentVideoFromStorageFile(String uriFileName) {

        Bitmap videoBitmap=getThumbnail(uriFileName);

        Uri videoFileUri=getImageUri(getApplicationContext(),videoBitmap);
        return  videoFileUri;
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void saveVideoToInternalStorage(String selectedVideoPath) {



    }



    private void uploadVideo(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Posting..");
        progressDialog.show();

        // if(imageUri==null) {
        //   imageUri = getImageUri(this, finalBitMap);
        // }


        isStoragePermissionGranted();
        if(contentURI!=null ){
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
            final String postId = reference.push().getKey();
            final StorageReference fileReference=storageReference.child("video")
                    .child(postId);




            uploadTask=fileReference.putFile(contentURI);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();


                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postId", postId);
                        hashMap.put("postImage", " ");
                        hashMap.put("video", myUrl);
                        hashMap.put("isvideo", "true");
                        hashMap.put("description", " ");
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(Objects.requireNonNull(postId)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                startActivity(new Intent(VideoActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    }else {
                        Toast.makeText(VideoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VideoActivity.this, MainActivity.class));
                        progressDialog.dismiss();
                        finish();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(VideoActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(VideoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    public boolean isSdReadable() {

        boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
// We can read and write the media
            mExternalStorageAvailable = true;
            Log.i("isSdReadable", "External storage card is readable.");
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
// We can only read the media
            Log.i("isSdReadable", "External storage card is readable.");
            mExternalStorageAvailable = true;
        } else {
// Something else is wrong. It may be one of many other
// states, but all we need to know is we can neither read nor write
            mExternalStorageAvailable = false;
        }

        return mExternalStorageAvailable;
    }

    public Bitmap getThumbnail(String filename) {

        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
        Bitmap thumbnail = null;

// Look for the file on the external storage
        try {
            if (isSdReadable() == true) {
                thumbnail = BitmapFactory.decodeFile(fullPath + "/" + filename);
            }
        } catch (Exception e) {

            e.getMessage();
        }

// If no file on external storage, look in internal storage
        if (thumbnail == null) {
            try {
                File filePath = getApplicationContext().getFileStreamPath(filename);
                FileInputStream fileInputStream = new FileInputStream(filePath);
                thumbnail = BitmapFactory.decodeStream(fileInputStream);
            } catch (Exception ex) {
                ex.getMessage();
            }
        }
        return thumbnail;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
