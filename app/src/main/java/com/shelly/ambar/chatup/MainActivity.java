package com.shelly.ambar.chatup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.shelly.ambar.chatup.Adapters.UserAdapter;
import com.shelly.ambar.chatup.Fragments.HomeFragment;
import com.shelly.ambar.chatup.Fragments.NotificationFragment;
import com.shelly.ambar.chatup.Fragments.ProfileFragment;
import com.shelly.ambar.chatup.Fragments.SearchFragment;
import com.shelly.ambar.chatup.Models.ChatMessageModel;
import com.shelly.ambar.chatup.Models.ChatModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference dataBase;

    private CircleImageView person_image;
    private TextView welcome_text;
    private Fragment SelectedFragment=null;
    private BottomNavigationView bottomNavigationView;
    private ImageView chatPageBTN;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(" ");

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        chatPageBTN=findViewById(R.id.chatPageBTN);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        chatPageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AllChatsActivity.class));

            }
        });

        dataBase=FirebaseDatabase.getInstance().getReference();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        welcome_text=hView.findViewById(R.id.MY_welcome_text);
        person_image=hView.findViewById(R.id.MY_person_imageView);


        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this,EnterActivity.class));
            finish();
        }

        Bundle intent=getIntent().getExtras();

        getSupportFragmentManager().executePendingTransactions();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment()).commit();


        mAuth = FirebaseAuth.getInstance();

        if(mAuth!=null){
            String UserId=mAuth.getCurrentUser().getUid();

            DatabaseReference databaseReference=FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(UserId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UsersDataModel userModel=dataSnapshot.getValue(UsersDataModel.class);

                    if (userModel==null) {

                        Glide.with(getApplicationContext()).load(R.drawable.com_facebook_profile_picture_blank_portrait)
                                .into(person_image);

                        welcome_text.setText("Something went wrong.Try to enter again." );
                        Toast.makeText(MainActivity.this, "You need to SignUp first!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, EnterActivity.class));
                        finish();


                    }else{

                        Glide.with(getApplicationContext()).load(userModel.getThumb_Image()).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                                .into(person_image);
                        if (userModel.getUserName().equals("")) {
                            welcome_text.setText("Welcome " + userModel.getEmail());
                        }else{
                            welcome_text.setText("Welcome " + userModel.getUserName() );
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            getSupportFragmentManager().executePendingTransactions();
            finish();
        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            finish();
        } else {
           finish();
        }
    }







    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.main_SignOut) {

            mAuth.signOut();
            LoginManager.getInstance().logOut();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                getSupportFragmentManager().executePendingTransactions();
                finish();
            }else{
                getSupportFragmentManager().executePendingTransactions();
                finish();
            }


        } else if (id == R.id.main_AllUsers) {
            startActivity(new Intent(MainActivity.this, FindFriendsActivity.class));
            finish();

        } else if (id == R.id.main_contact) {

        } else if (id == R.id.main_change_pass) {
            startActivity(new Intent(MainActivity.this, ForgotPassActivity.class));


        } else if (id == R.id.main_Settings) {
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));


        } else if (id == R.id.main_share) {

            startActivity(new Intent(MainActivity.this,ShowPeopleOnMap.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //bottom navigation view
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    SelectedFragment=new HomeFragment();
                    break;
                case R.id.nav_add:
                    SelectedFragment=null;
                    Intent intent=new Intent(MainActivity.this,FilterActivity.class);
                    intent.putExtra("Activity","Post");
                    startActivity(intent);

                    break;
                case R.id.nav_like:
                    SelectedFragment=new NotificationFragment();
                    break;

                case R.id.nav_profile:
                    SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor.putString("profileId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    SelectedFragment=new ProfileFragment();
                    break;
                case R.id.nav_search:
                    SelectedFragment=new SearchFragment();
                    break;
            }
            if(SelectedFragment!=null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,SelectedFragment).commit();



            }
            return true;
        }


    };






}