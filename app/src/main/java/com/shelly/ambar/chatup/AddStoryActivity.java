package com.shelly.ambar.chatup;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

public class AddStoryActivity extends AppCompatActivity {
    private Uri Imageuri;
    private String MyImageUrl="";
    private StorageTask storegeTask;
    private StorageReference storageReference;
    public final static String APP_PATH_SD_CARD = "/DesiredSubfolderName/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";
    private String ImageFileName;

    private Bitmap finalBitMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        storageReference=FirebaseStorage.getInstance().getReference("Story");

        ImageFileName=getIntent().getStringExtra("ImageFileName");

        finalBitMap= getThumbnail(ImageFileName);
        Imageuri= getImageUri(this,finalBitMap);




        PublishStory();

    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private String getFileExtention(Uri uri){
        ContentResolver contentResolver= getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void PublishStory(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Posting story..");
        progressDialog.show();



        if(Imageuri!=null){

            final StorageReference ImageReference=storageReference.child(System.currentTimeMillis()+ "." + getFileExtention(Imageuri));

            storegeTask=ImageReference.putFile(Imageuri);
            storegeTask.continueWithTask(new Continuation() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return ImageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        MyImageUrl=downloadUri.toString();

                        String MyId= FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story").child(MyId);

                        String StoryId=reference.push().getKey();
                        long timeEnd=System.currentTimeMillis()+86400000;

                        HashMap<String,Object> hashMap= new HashMap<>();
                        hashMap.put("imageUrl",MyImageUrl);
                        hashMap.put("timeStart",ServerValue.TIMESTAMP);
                        hashMap.put("timeEnd",timeEnd);
                        hashMap.put("storyId",StoryId);
                        hashMap.put("userId",MyId);
                        reference.child(StoryId).setValue(hashMap);

                        progressDialog.dismiss();

                        finish();
                    }else{
                        Toast.makeText(AddStoryActivity.this, "Failed to save the story", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddStoryActivity.this, "Try again.", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(this, "you must select image..!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){

            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            Imageuri=result.getUri();


            PublishStory();

        }else{
            Toast.makeText(this, "Something went WRONG..", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddStoryActivity.this,MainActivity.class));
            finish();
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

