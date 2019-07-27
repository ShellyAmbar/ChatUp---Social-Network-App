package com.shelly.ambar.chatup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.shelly.ambar.chatup.Adapters.ViewPagerAdapter;
import com.shelly.ambar.chatup.Fragments.EditImageFragment;
import com.shelly.ambar.chatup.Fragments.FilterlistFragment;
import com.shelly.ambar.chatup.Interfaces.EditImageFragmentListener;
import com.shelly.ambar.chatup.Interfaces.FiltersListFragmentListener;
import com.shelly.ambar.chatup.Utils.BitmapUtils;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class FilterActivity extends AppCompatActivity implements FiltersListFragmentListener,EditImageFragmentListener {
    public static final String pictureName="flash.png";
    public static final int PERMISSION_PIC_IMAGE =1000;
    public static final int SELECT_GALLERY_IMAGE = 101;
    public static final int SELECT_GALLERY_VIDEO = 102;
    public static final int OPEN_CAMERA = 103;
    public static final int RECORD_VIDEO = 104;
    public final static String APP_PATH_SD_CARD = "/DesiredSubfolderName/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";

    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private String [] permissions = {Manifest.permission.CAPTURE_VIDEO_OUTPUT,
            Manifest.permission.CAMERA
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int REQUEST_PERMISSION = 200;



    private StorageReference storageReference;
    private ImageView image_preview;
    private CoordinatorLayout coordinatorLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Uri imageUri;
    private Uri videoUri;
    private Uri cameraImageUri;
    private String myUrl="";
    private ImageView close;
    private TextView Next;

    private StorageTask uploadTask;

    private String fileName;
    private String ImageFileName;
    private String VideoFileName;
    private boolean permissionToRecordAccepted = false;


    private Bitmap originalBitmap, filteredBitmap, finalbitmap,bitmap;
    private FilterlistFragment filterlistFragment;
    private EditImageFragment editImageFragment;
    int brightnessFinal=0;
    float saturationFinal= 1.0f;
    float contrastFinal=1.0f;
    static {System.loadLibrary("NativeImageProcessor");}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_filters);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //getSupportActionBar().setTitle("ChatUp Filters");
        image_preview=findViewById(R.id.image_preview);
        tabLayout=findViewById(R.id.tabs);
        viewPager=findViewById(R.id.viewPager);
        coordinatorLayout=findViewById(R.id.coordinator);
        close=findViewById(R.id.close);
        Next=findViewById(R.id.Next);
        cameraImageUri=null;

        finalbitmap=null;

        ImageFileName="newImagePost";
        VideoFileName="VideoFileName";




        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if(finalbitmap==null){
                    finish();
                }





                Intent intent1= getIntent();

                if (intent1.getStringExtra("Activity").equals("Post") ){

                    if(saveImageToExternalStorage(finalbitmap)){

                        Toast.makeText(FilterActivity.this, "External storage", Toast.LENGTH_SHORT).show();

                    }else{
                        saveImageToInternalStorage(finalbitmap);
                        Toast.makeText(FilterActivity.this, "Internal storage", Toast.LENGTH_SHORT).show();
                    }


                    Intent intent =new Intent(FilterActivity.this,PostActivity.class);
                    intent.putExtra("ImageFileName", ImageFileName);


                    startActivity(intent);
                    finish();

                } else if (intent1.getStringExtra("Activity").equals("Story") ){

                    if(saveImageToExternalStorage(finalbitmap)){

                        Toast.makeText(FilterActivity.this, "External storage", Toast.LENGTH_SHORT).show();

                    }else{
                        saveImageToInternalStorage(finalbitmap);
                        Toast.makeText(FilterActivity.this, "Internal storage", Toast.LENGTH_SHORT).show();
                    }



                    Intent intent =new Intent(FilterActivity.this,AddStoryActivity.class);
                    intent.putExtra("ImageFileName", ImageFileName);
                    startActivity(intent);
                    finish();

                }else if(intent1.getStringExtra("Activity").equals("ChatActivity")){
                 final String otherId=intent1.getStringExtra("otherId");
                 final String User_Message=intent1.getStringExtra("User_Message");

                    Calendar calForDate=Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
                    final String time=simpleDateFormat.format(calForDate.getTime());

                    final DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Massages")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(otherId);

                    final DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("Massages")
                            .child(otherId)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Uri imageUri=getImageUri(FilterActivity.this,finalbitmap);
                    final StorageReference fileReference=FirebaseStorage.getInstance().getReference("MassagesPhotos")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(time + "photo");
                    uploadTask= fileReference.putFile(imageUri);
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task!=null) {
                                Uri downloadUri = task.getResult();
                                myUrl = downloadUri.toString();


                                final HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("User_Message",User_Message);
                                hashMap.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("User_Time",time);
                                hashMap.put("type","photo");
                                hashMap.put("userPhoto",myUrl );

                                reference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        reference2.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                Toast.makeText(FilterActivity.this, "Image sent", Toast.LENGTH_SHORT).show();
                                                finish();

                                            }
                                        });

                                    }
                                });
                            }
                        }
                    });






                }





            }
        });



        // loadImage();
        openImageFromGallery();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void loadImage() {
        originalBitmap=BitmapUtils.getBitmapFromAssets(this,pictureName,300,300);
        filteredBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        finalbitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        image_preview.setImageBitmap(originalBitmap);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter Adapter= new ViewPagerAdapter(getSupportFragmentManager());
        filterlistFragment  =new FilterlistFragment();
        filterlistFragment.setListener(this);
        editImageFragment=new EditImageFragment();
        editImageFragment.setListener(this);


        Adapter.AddFragment(filterlistFragment, "FILTERS");
        Adapter.AddFragment(editImageFragment, "EDIT");

        viewPager.setAdapter(Adapter);

    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal=brightness;
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        image_preview.setImageBitmap(myFilter.processFilter(finalbitmap
                .copy(Bitmap.Config.ARGB_8888,true)));


    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal=saturation;
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        image_preview.setImageBitmap(myFilter.processFilter(finalbitmap
                .copy(Bitmap.Config.ARGB_8888,true)));


    }

    @Override
    public void onConstrantChanged(float constrant) {
        contrastFinal=constrant;
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(constrant));
        image_preview.setImageBitmap(myFilter.processFilter(finalbitmap
                .copy(Bitmap.Config.ARGB_8888,true)));


    }

    @Override
    public void onEditStart() {



    }

    @Override
    public void onEditComplete() {

        Bitmap bitmap= filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        myFilter.addSubFilter(new ContrastSubFilter( contrastFinal));

        finalbitmap=myFilter.processFilter(bitmap);

    }


    @Override
    public void onFilterSelected(Filter filter) {

        resetControl();
        filteredBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        image_preview.setImageBitmap(filter.processFilter(filteredBitmap));
        finalbitmap=filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);


    }

    private void resetControl() {
        if (editImageFragment!=null){
            editImageFragment.resetControls();
            brightnessFinal=0;
            saturationFinal=1.0f;
            contrastFinal=1.0f;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filters, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int Id= item.getItemId();
        if (Id== R.id.Action_open){
            openImageFromGallery();
        }else if (Id == R.id.Action_save){
            saveImageToGallery();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImageToGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){

                            try {
                                final String path= BitmapUtils.InsertImage(getContentResolver()
                                        ,finalbitmap,
                                        System.currentTimeMillis()+"_profile.jpg"
                                        ,null);
                                if(!TextUtils.isEmpty(path)){
                                    Snackbar snackbar= Snackbar.make(coordinatorLayout,"Image saved into gallery",
                                            Snackbar.LENGTH_SHORT).setAction("OPEN", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            openImage(path);
                                        }
                                    });
                                    snackbar.show();
                                }else{
                                    Snackbar snackbar= Snackbar.make(coordinatorLayout,"Unable to save image into gallery",
                                            Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else{
                            Toast.makeText(FilterActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).check();


    }

    private void openImage(String path) {
        Intent intent =new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path),"image/*");
        startActivity(intent);
    }

    private void openImageFromGallery() {

        ChooseImageFromGalerry();


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode== SELECT_GALLERY_IMAGE && resultCode==RESULT_OK){
            //select image

            bitmap=null;
            if (data != null) {


                bitmap = BitmapUtils.getBitmapFromGallery(this,data.getData(), 400, 400);

            }else{

                finish();
            }

            //clearing bitmap memory
            if (originalBitmap!=null ) {originalBitmap.recycle();}
            if (finalbitmap!= null){ finalbitmap.recycle();}
            if (filteredBitmap!=null){ filteredBitmap.recycle();}

            originalBitmap=bitmap.copy(Bitmap.Config.ARGB_8888,true);
            filteredBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
            finalbitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);


            image_preview.setImageBitmap(originalBitmap);
            bitmap.recycle();

            //render selected img tumbnail

            filterlistFragment.DisplayThumbNail(originalBitmap);

        }else if(requestCode== SELECT_GALLERY_VIDEO  && resultCode==RESULT_OK){

            // select video


            if(data.getData()!=null){


                Intent intent=new Intent(FilterActivity.this, VideoActivity.class);

                Intent intentFilter= getIntent();
                if (intentFilter.getStringExtra("Activity").equals("Post") ){

                    intent.putExtra("Activity","Post");

                }else if (intentFilter.getStringExtra("Activity").equals("Story") ){
                    intent.putExtra("Activity","Story");
                }

                intent.putExtra("VideoFileName",VideoFileName);
                //intent.putExtra("From_Activity","Post");
                startActivity(intent);
                finish();

            }



        }else if(requestCode== OPEN_CAMERA && resultCode==RESULT_OK){

            //open camera for pic


            if (data!=null) {

                bitmap=getThumbnailFromStorage(VideoFileName);

                //  Bundle extras = data.getExtras();
                //  bitmap = (Bitmap) extras.get("data");

                // if ( data.getExtras() != null) {
                //      bitmap = (Bitmap) data.getExtras().get("data");
                //  }




                //clearing bitmap memory

                if (originalBitmap!=null ) {originalBitmap.recycle();}
                if (finalbitmap!= null){ finalbitmap.recycle();}
                if (filteredBitmap!=null){ filteredBitmap.recycle();}

                originalBitmap=bitmap.copy(Bitmap.Config.ARGB_8888,true);
                filteredBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
                finalbitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);


                image_preview.setImageBitmap(originalBitmap);
                bitmap.recycle();




            }else{

                finish();
            }



        }else if(requestCode==RECORD_VIDEO && resultCode==RESULT_OK){

            //recored video

            if(videoUri!=null){





                Intent intent=new Intent(FilterActivity.this, VideoActivity.class);

                Intent intentFilter= getIntent();
                if (intentFilter.getStringExtra("Activity").equals("Post") ){

                    intent.putExtra("Activity","Post");

                }else if (intentFilter.getStringExtra("Activity").equals("Story") ){
                    intent.putExtra("Activity","Story");
                }
                intent.putExtra("VideoFileName",VideoFileName);
                //intent.putExtra("From_Activity","Post");
                startActivity(intent);
                finish();

            }


        }else {

            finish();
        }



    }





    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input =getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        Bitmap bitmap=BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        return bitmap;



    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public boolean saveImageToExternalStorage(Bitmap image) {
        String fullPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, ImageFileName);
            file.createNewFile();
            fOut = new FileOutputStream(file);

// 100 means no compression, the lower you go, the stronger the compression
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    public boolean saveImageToInternalStorage(Bitmap image) {

        try {
// Use the compress method on the Bitmap object to write image to
// the OutputStream
            FileOutputStream fos = getApplicationContext()
                    .openFileOutput(ImageFileName, Context.MODE_PRIVATE);

// Writing the bitmap to the output stream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return true;
        } catch (Exception e) {

            return false;
        }
    }



    public File getFile() {
        String fullPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;


        File dir = new File(fullPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(fullPath, VideoFileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;

    }


    public void recordVideo(){
        Intent recordIntent= new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File videoFile=getFile();
        //videoUri=Uri.fromFile(videoFile);

        recordIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoFile);
        recordIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(recordIntent, RECORD_VIDEO);
    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FilterActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void uploadVideoFromGallery() {
        Intent recordIntent= new Intent(Intent.ACTION_PICK);
        recordIntent.setType("video/*");
        File videoFile=getFile();
        videoUri=Uri.fromFile(videoFile);

        recordIntent.putExtra(MediaStore.EXTRA_OUTPUT,videoUri);
        recordIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(recordIntent, RECORD_VIDEO);
    }

    private void captureImage() {
        Intent captureImageIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile=getFile();
        //imageUri=Uri.fromFile(imageFile);

        captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageFile);
        captureImageIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(captureImageIntent, OPEN_CAMERA);
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

    public Bitmap getThumbnailFromStorage(String filename) {

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


    private void ChooseImageFromGalerry(){
        Dexter.withActivity(FilterActivity.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted()){

                            //open image from gallery
                            Intent intent=new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");

                            // fileName=Environment.getExternalStorageDirectory().getAbsolutePath();
                            //  fileName+="/image.jpg";

                            // intent.putExtra(MediaStore.EXTRA_OUTPUT,fileName);


                            // intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent,SELECT_GALLERY_IMAGE);

                        }else{
                            Toast.makeText(FilterActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                            if (report.isAnyPermissionPermanentlyDenied()) {

                                showSettingsDialog();
                                // navigate user to app settings
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).check();
    }

    private void ChooseOptionOfUploading(){
        AlertDialog.Builder builder=new AlertDialog.Builder(FilterActivity.this);
        CharSequence[] options=new CharSequence[]{
                "Select Image ",
                "Select Video",
                "Record LIVE video ",
                "Open your camera"
        };
        builder.setTitle("Select option");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==0){

                    ChooseImageFromGalerry();
                }
                if(which==1){

                    Dexter.withActivity(FilterActivity.this)
                            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
                                    ,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if(report.areAllPermissionsGranted()){

                                        uploadVideoFromGallery();

                                    }else{
                                        Toast.makeText(FilterActivity.this, report.getDeniedPermissionResponses()+"", Toast.LENGTH_LONG).show();
                                        if (report.isAnyPermissionPermanentlyDenied()) {

                                            showSettingsDialog();
                                            // navigate user to app settings
                                        }
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }


                            }).check();


                }
                if(which==2){

                    Dexter.withActivity(FilterActivity.this)
                            .withPermissions(Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if(report.areAllPermissionsGranted()){

                                        //recored live video
                                        recordVideo();



                                    }else{

                                        Toast.makeText(FilterActivity.this, report.getDeniedPermissionResponses()+"", Toast.LENGTH_LONG).show();
                                        if (report.isAnyPermissionPermanentlyDenied()) {

                                            showSettingsDialog();
                                            // navigate user to app settings
                                        }
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }


                            }).check();

                }
                if(which==3){

                    Dexter.withActivity(FilterActivity.this)
                            .withPermissions(Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)

                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if(report.areAllPermissionsGranted()){


                                        //capture new image

                                        captureImage();


                                    }else{
                                        Toast.makeText(FilterActivity.this, report.getDeniedPermissionResponses()+"", Toast.LENGTH_LONG).show();
                                        if (report.isAnyPermissionPermanentlyDenied()) {

                                            showSettingsDialog();
                                            // navigate user to app settings
                                        }
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }


                            }).check();


                }
            }
        });
        builder.show();

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
