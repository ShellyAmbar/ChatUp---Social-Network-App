package com.shelly.ambar.chatup;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView image_profile;
    private Button btn_change_img;
    private Button btn_my_location;

    private AutoCompleteTextView new_location_provided;
    private AutoCompleteTextView education_choice;
    private AutoCompleteTextView my_education;
    private AutoCompleteTextView new_match_location_provided;
    private ImageView Arrow_down_education;
    private ImageView Arrow_down_city;
    private ImageView Arrow_down_my_education;
    private ImageView Arrow_down_match_city;
    private RadioGroup radioGroupUserGender;
    private RadioGroup radioGroupMatchGender;

    private RadioButton radioButton;
    private SeekBar seekBar_Age;
    private TextView progressAge;
    private SeekBar seekBar_Start_Age;
    private TextView progressStartAge;
    private SeekBar seekBar_End_Age;
    private TextView progressEndAge;
    private int progressStartAgeValue;
    private int progressEndAgeValue;
    private int progressAgeValue;
    private EditText Chage_About;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    private String UserId;
    private DatabaseReference UserDataBaseReference;
    private StorageReference storageReference;
    private  String aboutMySelf;
    private Button submit_btn;
    private String UserGender;
    private String InterestedIn;

    private String UserEducation;
    private String MatchEducation;
    private String MatchCity;
    private URL ImageURL;
    LocationManager locationManager;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String MyCity;
    private Uri ImageUri;
    private Toolbar toolbar;
    private static final String[] city={"All","Tel Aviv","Jerusalem","Haifa","Beer Sheva","Bat yam","Ramat Gan","eilat"};
    private static final String[] myCity={"Tel Aviv","Jerusalem","Haifa","Beer Sheva","Bat yam","Ramat Gan","eilat"};
    private static final String[] education={"All","Engineering",
            "Computer Science","Biology","Med","physics","Mathematics","Electronics"};
    private static final String[] myEducation={"none education","Engineering",
            "Computer Science","Biology","Med","physics","Mathematics","Electronics"};


    public static final int PERMISSION_PIC_IMAGE =1000;
    public static final int SELECT_GALLERY_IMAGE = 101;
    private static Bitmap bitmap = null;

    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        image_profile=findViewById(R.id.image_profile);
        submit_btn=findViewById(R.id.submit_btn);
        Chage_About=findViewById(R.id.Chage_About);
        btn_change_img=findViewById(R.id.btn_change_img);
        btn_my_location=findViewById(R.id.btn_my_location);
        new_location_provided=findViewById(R.id.new_location_provided);
        education_choice=findViewById(R.id.education_choice);
        my_education=findViewById(R.id.my_education);
        Arrow_down_education=findViewById(R.id.Arrow_down_education);
        Arrow_down_city=findViewById(R.id.Arrow_down_city);
        Arrow_down_my_education=findViewById(R.id.Arrow_down_my_education);
        new_match_location_provided=findViewById(R.id.new_match_location_provided);
        Arrow_down_match_city=findViewById(R.id.Arrow_down_match_city);
        radioGroupUserGender=findViewById(R.id.radio_group);
        radioGroupMatchGender=findViewById(R.id.radio_group2);






        ArrayAdapter<String> adapterCity=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,myCity);
        new_location_provided.setAdapter(adapterCity);

        ArrayAdapter<String> adapterEducation=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,education);
        education_choice.setAdapter(adapterEducation);

        ArrayAdapter<String> adapterMyEducation=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,myEducation);
        my_education.setAdapter(adapterMyEducation);

        ArrayAdapter<String> adapterMatchCity=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,city);
        new_match_location_provided.setAdapter(adapterMatchCity);




        //my age
        seekBar_Age=findViewById(R.id.seekBar_Age);
        progressAge=findViewById(R.id.progressAge);
        //start age
        seekBar_Start_Age=findViewById(R.id.seekBar_Start_Age);
        progressStartAge=findViewById(R.id.progressStartAge);
        //end age
        seekBar_End_Age=findViewById(R.id.seekBar_End_Age);
        progressEndAge=findViewById(R.id.progressEndAge);
        ImageUri=null;
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        Arrow_down_education.setOnClickListener(this);
        Arrow_down_city.setOnClickListener(this);
        Arrow_down_my_education.setOnClickListener(this);
        Arrow_down_match_city.setOnClickListener(this);

        seekBar_Age.setMax(100);
        seekBar_Start_Age.setMax(100);
        seekBar_Start_Age.setMax(100);

        //auth

        mAuth=FirebaseAuth.getInstance();
        User=mAuth.getCurrentUser();
        UserId=User.getUid();
        MyCity="";
        //get reference to current user data
        UserDataBaseReference=FirebaseDatabase.getInstance()
                .getReference("Users").child(UserId);

        UserDataBaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);



                if(usersDataModel!=null) {
                    //set default ages on seekbar
                    if (!usersDataModel.getUserAge().isEmpty()) {
                        String Age = usersDataModel.getUserAge();

                        progressAgeValue = Integer.parseInt(Age);


                    } else {
                        progressAgeValue = 25;
                    }
                    if (!usersDataModel.getFromAge().isEmpty()) {

                        String Age = usersDataModel.getFromAge();

                        progressStartAgeValue = Integer.parseInt(Age);

                    } else {
                        progressStartAgeValue = 18;
                    }
                    if (!usersDataModel.getToAge().isEmpty()) {

                        String Age = usersDataModel.getToAge();

                        progressEndAgeValue = Integer.parseInt(Age);

                    } else {
                        progressEndAgeValue = 38;
                    }


                    seekBar_Age.setProgress( progressAgeValue);
                    seekBar_Start_Age.setProgress(progressStartAgeValue);
                    seekBar_End_Age.setProgress(progressEndAgeValue);

                    progressAge.setText(""+progressAgeValue);
                    progressStartAge.setText(""+progressStartAgeValue );
                    progressEndAge.setText(""+progressEndAgeValue);




                    InterestedIn=usersDataModel.getInterestedIn();
                    UserGender=usersDataModel.getUserGender();
                    UserEducation=usersDataModel.getMyEducation();
                    MatchEducation=usersDataModel.getMatchEducation();
                    aboutMySelf=usersDataModel.getUserStatus();
                    MatchCity=usersDataModel.getMatchCity();
                    Chage_About.setText(aboutMySelf);


                    new_location_provided.setHint(usersDataModel.getUserCity());
                    new_match_location_provided.setHint(usersDataModel.getMatchCity());
                    my_education.setHint(usersDataModel.getMyEducation());
                    education_choice.setHint(usersDataModel.getMatchEducation());


                    if(UserGender.equals("Male")){
                        radioButton=findViewById(R.id.Male);
                       radioGroupUserGender.check(radioButton.getId());
                    }else{
                        radioButton=findViewById(R.id.Female);
                        radioGroupUserGender.check(radioButton.getId());
                    }
                    if(InterestedIn.equals("Male")){
                        radioButton=findViewById(R.id.Male2);
                        radioGroupMatchGender.check(radioButton.getId());
                    }else{
                        radioButton=findViewById(R.id.Female2);
                        radioGroupMatchGender.check(radioButton.getId());
                    }




                    if(usersDataModel.getThumb_Image().isEmpty()){
                        Glide.with(getApplicationContext())
                                .load(R.drawable.com_facebook_profile_picture_blank_portrait).into(image_profile);
                    }else{
                        Glide.with(getApplicationContext()).load(usersDataModel.getThumb_Image())
                                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(image_profile);
                    }


                }else{
                    startActivity(new Intent(SettingsActivity.this,EnterActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });













        seekBar_Age.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                progressAgeValue=progress;
                seekBar_Age.setProgress(progressAgeValue);
                progressAge.setText(""+progressAgeValue);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_Start_Age.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressStartAgeValue= progress;
                seekBar_Start_Age.setProgress(progressStartAgeValue);
                progressStartAge.setText(""+progressStartAgeValue );


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_End_Age.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressEndAgeValue=progress;
                seekBar_End_Age.setProgress(progressEndAgeValue);
                progressEndAge.setText(""+progressEndAgeValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        submit_btn.setOnClickListener(this);

        //change picture
        btn_change_img.setOnClickListener(this);

        btn_my_location.setOnClickListener(this);



    }



    public void CheckClick(View view) {


        int radioId = radioGroupUserGender.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        UserGender=radioButton.getText().toString();



    }

    public void CheckClickInterested(View view) {

        int radioId = radioGroupMatchGender.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);


        InterestedIn=radioButton.getText().toString();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.submit_btn:
                setDataInDatabaseSubmit();
                break;

            case R.id.btn_change_img:
                openImageFromGallery();
                break;

            case R.id.btn_my_location:
                new_location_provided.setText("");
                MyCity="";

                startActivity(new Intent(this,MapsActivity2.class));
                break;
            case R.id.Arrow_down_education:
                arrow_down_education();
                break;

            case R.id.Arrow_down_city:
                arrow_down_city();
                break;

            case R.id.Arrow_down_my_education:
                arrow_my_education();
                break;

            case R.id.Arrow_down_match_city:
                arrow_down_match_city();
                break;
        }
    }

    private void arrow_down_match_city() {
        new_match_location_provided.showDropDown();
        new_match_location_provided.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MatchCity=new_match_location_provided.getText().toString();
            }
        });
    }

    private void arrow_down_education() {
        education_choice.showDropDown();

        education_choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MatchEducation=education_choice.getText().toString();
            }
        });

    }
    private void arrow_down_city(){
        new_location_provided.showDropDown();

        new_location_provided.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCity =new_location_provided.getText().toString();
            }
        });

    }
    private void arrow_my_education(){
        my_education.showDropDown();

        my_education.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserEducation=my_education.getText().toString();
            }
        });

    }

    private void getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude= location.getLatitude();
                    longitude=location.getLongitude();
                    LatLng latlng = new LatLng(latitude, longitude);
                    Geocoder geocoder=new Geocoder(getApplicationContext());
                    try {
                        List<Address> adressList=geocoder.getFromLocation(latitude,longitude,1);
                        MyCity =adressList.get(0).getAddressLine(0).toString();
                        String myLocation=adressList.get(0).getLocality()+",";
                        myLocation+=adressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latlng).title(myLocation));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10.2f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }




                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude= location.getLatitude();
                    longitude=location.getLongitude();
                    LatLng latlng = new LatLng(latitude, longitude);
                    Geocoder geocoder=new Geocoder(getApplicationContext());
                    try {
                        List<Address> adressList=geocoder.getFromLocation(latitude,longitude,1);
                        MyCity =adressList.get(0).getAddressLine(0).toString();
                        String myLocation=adressList.get(0).getLocality()+",";
                        myLocation+=adressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latlng).title(myLocation));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10.2f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

        Map<String,Object> hashMapUsr=new HashMap<>();
        hashMapUsr.put("UserCity",MyCity);

        mAuth=FirebaseAuth.getInstance();
        User=mAuth.getCurrentUser();
        if (User != null) {
            UserId=User.getUid();
        }
        //get reference to current user data
        UserDataBaseReference=FirebaseDatabase.getInstance()
                .getReference("Users").child(UserId);

        UserDataBaseReference.updateChildren(hashMapUsr).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SettingsActivity.this, "Your location has been Changed Successfully. ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void setDataInDatabaseSubmit() {

        Map<String,Object> hashMapUsr=new HashMap<>();

        aboutMySelf = Chage_About.getText().toString();

        hashMapUsr.put("UserStatus" ,aboutMySelf );
        hashMapUsr.put("UserAge",String.valueOf(progressAgeValue));
        hashMapUsr.put("UserGender",UserGender);
        hashMapUsr.put("InterestedIn",InterestedIn);
        hashMapUsr.put("FromAge",String.valueOf(progressStartAgeValue));
        hashMapUsr.put("ToAge",String.valueOf(progressEndAgeValue));
        hashMapUsr.put("MyEducation",UserEducation);
        hashMapUsr.put("MatchEducation", MatchEducation);
        if(!MyCity.isEmpty()){
            hashMapUsr.put("UserCity",MyCity);
        }

        hashMapUsr.put("MatchCity",MatchCity);


        mAuth=FirebaseAuth.getInstance();
        User=mAuth.getCurrentUser();
        if (User != null) {
            UserId=User.getUid();
        }
        //get reference to current user data
        UserDataBaseReference=FirebaseDatabase.getInstance()
                .getReference("Users").child(UserId);

        UserDataBaseReference.updateChildren(hashMapUsr).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SettingsActivity.this, "Data has been Changed Successfully. ", Toast.LENGTH_SHORT).show();

                    finish();
                }
                else{
                    Toast.makeText(SettingsActivity.this, "Data has'nt been Changed, Try again please. ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void getLongLatFromCityName(String userCity) {

        Geocoder geocoder=new Geocoder(getApplicationContext());
        try {
            List<Address> adressList=geocoder.getFromLocationName(userCity,0);
            longitude=adressList.get(0).getLongitude();
            latitude=adressList.get(0).getLatitude();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void openImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted()){
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(SettingsActivity.this);
                        }else{
                            Toast.makeText(SettingsActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).check();
    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if (result != null) {
                ImageUri= result.getUri();

                storageReference=FirebaseStorage.getInstance().getReference("Users")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                final StorageReference fileReference=storageReference
                        .child(System.currentTimeMillis()+"."+getFileExtention(ImageUri));
                uploadTask=fileReference.putFile(ImageUri);
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
                        if (task.isSuccessful()) {
                          final   Uri resultUri = task.getResult();

                            //upload image

                            Map<String,Object> hashMapUsr=new HashMap<>();
                            hashMapUsr.put("Thumb_Image",resultUri.toString());

                            mAuth=FirebaseAuth.getInstance();
                            User=mAuth.getCurrentUser();

                            if (User != null) {
                                UserId = User.getUid();


                                //get reference to current user data
                                UserDataBaseReference = FirebaseDatabase.getInstance()
                                        .getReference("Users").child(UserId);

                                UserDataBaseReference.updateChildren(hashMapUsr).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingsActivity.this, "Picture has been Changed Successfully. ", Toast.LENGTH_SHORT).show();

                                            Glide.with(SettingsActivity.this).load(resultUri.toString())
                                                    .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(image_profile);

                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(SettingsActivity.this, "Problem occurred, try to enter again. ", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(SettingsActivity.this, "Problem occurred, try to enter again. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });





            }else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

                //startActivity(new Intent(this,SettingsActivity.class));
                // finish();
            }

        }else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(SettingsActivity.this,MainActivity.class));
            // finish();
        }



    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
