package com.shelly.ambar.chatup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class ShowPeopleOnMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private LatLng currentLocation;
    private static final int Request_User_Location_Code=99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_people_on_map);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserPermissions();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);







    }


    public boolean checkUserPermissions(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED ){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }else{
            return true;
        }

    }



    public void addMarkerLocation(){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    UsersDataModel usersDataModel=snapshot.getValue(UsersDataModel.class);

                    String userCity=usersDataModel.getUserCity();
                    String userName=usersDataModel.getUserName();
                    if(userCity.isEmpty()&& usersDataModel.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                        Toast.makeText(ShowPeopleOnMap.this, userName+" you need to set your location first..", Toast.LENGTH_SHORT).show();
                        finish();
                    }else if (userCity.isEmpty()&& !(usersDataModel.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){

                    }else{
                        getLongLatFromCityName(userCity, userName );
                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getLongLatFromCityName(String userCity,String userName) {

        Geocoder geocoder=new Geocoder(getApplicationContext());
        try {
            List<Address> adressList=geocoder.getFromLocationName(userCity,10);
            longitude=adressList.get(0).getLongitude();
            latitude=adressList.get(0).getLatitude();
            currentLocation=new LatLng(latitude,longitude);

            mMap.addMarker(new MarkerOptions()
                    .position(currentLocation).title(userName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,10f));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_User_Location_Code:
                //if permissions is granted
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                        addMarkerLocation();

                        mMap.setMyLocationEnabled(true);

                    }
                }else{
                    Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkUserPermissions();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);


            LoadPeopleOnMap loadPeopleOnMap = new LoadPeopleOnMap();
            loadPeopleOnMap.execute();

        }



    }

    @Override
    public void onBackPressed() {
        finish();
    }



    private class LoadPeopleOnMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            addMarkerLocation();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}


