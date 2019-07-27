package com.shelly.ambar.chatup;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    private Uri imageUri;
    private Uri videoUri;
    private String myUrl="";
    private Bitmap finalBitMap;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private ImageView close,imageAdded;
    private TextView post;
    private EditText description;
    private TextView Crop;
    private String fileDirectory;
    private String imageDirectoryInFile;
    public final static String APP_PATH_SD_CARD = "/DesiredSubfolderName/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";
    private String ImageFileName;
    private DatabaseReference referenceToUsers;
    private UsersDataModel usersDataModel;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close=findViewById(R.id.close);
        imageAdded=findViewById(R.id.image_added);
        post=findViewById(R.id.post);
        description=findViewById(R.id.description);
        Crop=findViewById(R.id.Crop);




        ImageFileName=getIntent().getStringExtra("ImageFileName");

        finalBitMap= getThumbnail(ImageFileName);
        imageUri= getImageUri(this,finalBitMap);




        storageReference=FirebaseStorage.getInstance().getReference("Posts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        referenceToUsers=FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        referenceToUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        CropImage.activity(imageUri).setAspectRatio(4,3)
                .start(PostActivity.this);
        Crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(imageUri).setAspectRatio(3,3)
                        .start(PostActivity.this);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

            }
        });



        Glide.with(getApplicationContext()).load(imageUri).into(imageAdded);

    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));


    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void uploadImage(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Posting..");
        progressDialog.show();


        if(imageUri!=null){

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
            final String postId = reference.push().getKey();
            final StorageReference fileReference=storageReference
                    .child(postId);
            uploadTask=fileReference.putFile(imageUri);
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
                        myUrl = downloadUri.toString();


                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postId", postId);
                        hashMap.put("video", "");
                        hashMap.put("isvideo", "false");
                        hashMap.put("postImage", myUrl);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("publisherName", usersDataModel.getUserName());
                        hashMap.put("publisherImage", usersDataModel.getThumb_Image());

                        reference.child(Objects.requireNonNull(postId)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();

                                finish();
                            }
                        });
                    }else {
                        Toast.makeText(PostActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                        finish();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                    finish();
                }
            });

        }else{
            Toast.makeText(PostActivity.this, "Failed", Toast.LENGTH_SHORT).show();

            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            imageUri= result.getUri();

            imageAdded.setImageURI(imageUri);


        }else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

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
