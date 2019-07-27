package com.shelly.ambar.chatup;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shelly.ambar.chatup.Adapters.CardsAdapter;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FindFriendsActivity extends AppCompatActivity {



    private int i;
    private CardsAdapter cardsAdapter;

    private Toolbar toolbar;
    private UsersDataModel myUsersDataModel;
    private String matchGender;
    private String matchEducation;
    private String matchCity;
    private int matchFromAge;
    private int matchToAge;
    private TextView settings_icon;
    private UsersDataModel lastPersonBeforeRemove;
    private FirebaseAuth mAuth;



    ListView listView;
    private List<UsersDataModel> rowItems;



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        rowItems= new ArrayList<UsersDataModel>();
        cardsAdapter = new CardsAdapter(this, R.layout.item, rowItems );
        toolbar=findViewById(R.id.toolbar);
        settings_icon=findViewById(R.id.settings_icon);


       // allMassagesIcon=findViewById(R.id.allMassagesIcon);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FindFriendsActivity.this, MainActivity.class);

                startActivity(intent);

                finish();
            }
        });


        settings_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(FindFriendsActivity.this,SettingsActivity.class));

            }
        });

        mAuth = FirebaseAuth.getInstance();

        if(mAuth!=null){
            String UserId=mAuth.getCurrentUser().getUid();

            DatabaseReference databaseReference=FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(UserId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (!dataSnapshot.exists()) {


                        Toast.makeText(FindFriendsActivity.this, "You need to SignUp first at the SIGN-UP page!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(FindFriendsActivity.this, EnterActivity.class));
                        FirebaseAuth.getInstance().signOut();
                        finish();


                    }else{

                        final DatabaseReference databaseReferenceToAllUsers=FirebaseDatabase.getInstance()
                                .getReference().child("Users");

                        final DatabaseReference databaseReference1ToCurrentUser=FirebaseDatabase
                                .getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth
                                        .getInstance().getCurrentUser()).getUid());

                        databaseReference1ToCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                myUsersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                                matchEducation=Objects.requireNonNull(myUsersDataModel).getMatchEducation();
                                matchGender=myUsersDataModel.getInterestedIn();
                                matchFromAge=Integer.parseInt(myUsersDataModel.getFromAge());
                                matchToAge=Integer.parseInt(myUsersDataModel.getToAge());
                                matchCity=myUsersDataModel.getMatchCity();


                                databaseReferenceToAllUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        for( DataSnapshot snapshot: dataSnapshot.getChildren()){
                                            UsersDataModel usersDataModel=snapshot.getValue(UsersDataModel.class);

                                            if((usersDataModel.getMyEducation().equals(matchEducation) ||
                                                    matchEducation.equals("All"))
                                                    && (usersDataModel.getUserCity().equals(matchCity)||
                                                    matchCity.equals("All"))
                                                    && (usersDataModel.getUserGender().equals(matchGender)||
                                                    matchGender.equals("All") )
                                                    && (((Integer.parseInt(usersDataModel.getUserAge())<matchToAge)
                                                    && (Integer.parseInt(usersDataModel.getUserAge())> matchFromAge )))
                                                    && (!usersDataModel.getId().equals(userId))){

                                                rowItems.add(usersDataModel);
                                            }else {
                                                //meanwhile we don't have enough users
                                                // rowItems.add(usersDataModel);
                                            }

                                        }
                                        cardsAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        SwipeFlingAdapterView flingContainer=findViewById(R.id.frame);

                        flingContainer.setAdapter(cardsAdapter);
                        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                            @Override
                            public void removeFirstObjectInAdapter() {
                                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                                Log.d("LIST", "removed object!");
                                lastPersonBeforeRemove=rowItems.get(0);
                                rowItems.remove(0);
                                cardsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onLeftCardExit(Object dataObject) {
                                //Do something on the left!
                                //You also have access to the original object.
                                //If you want to use it just cast it (String) dataObject
                                Toast.makeText(FindFriendsActivity.this, "Naa..!",Toast.LENGTH_LONG).show();
                                if(rowItems.size()>0){
                                    DatabaseReference databaseReferenceOfMatces=FirebaseDatabase.getInstance()
                                            .getReference("Match")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(lastPersonBeforeRemove.getId());

                                    databaseReferenceOfMatces.setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                        }
                                    });
                                }else{
                                    //TODO
                                }


                            }

                            @Override
                            public void onRightCardExit(Object dataObject) {
                                Toast.makeText(FindFriendsActivity.this, "yes!",Toast.LENGTH_SHORT).show();
                                if(rowItems.size()>0){
                                    DatabaseReference databaseReferenceOfMatces=FirebaseDatabase.getInstance()
                                            .getReference("Match")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(lastPersonBeforeRemove.getId());

                                    final DatabaseReference databaseReferenceOfOtherMatces=FirebaseDatabase.getInstance()
                                            .getReference("Match")
                                            .child(lastPersonBeforeRemove.getId())
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                                    databaseReferenceOfMatces.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            databaseReferenceOfOtherMatces.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if(dataSnapshot.exists()){
                                                        if(dataSnapshot.getValue().equals(true)){
                                                            Intent matchIntent=new Intent(FindFriendsActivity.this,MatchActivity.class);
                                                            matchIntent.putExtra("matchName",lastPersonBeforeRemove.getUserName());
                                                            matchIntent.putExtra("matchPhoto",lastPersonBeforeRemove.getThumb_Image() );
                                                            matchIntent.putExtra("matchId",lastPersonBeforeRemove.getId());

                                                            startActivity(matchIntent);


                                                        }else{
                                                            Toast.makeText(FindFriendsActivity.this, "Will it be a Match?!..we will see..", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }
                                    });



                                }else{
                                    //TODO
                                }

                            }

                            @Override
                            public void onAdapterAboutToEmpty(int itemsInAdapter) {

                                //rowItems.add());
                                cardsAdapter.notifyDataSetChanged();
                                // Log.d("LIST", "notified");
                                //i++;
                            }



                            @Override
                            public void onScroll(float scrollProgressPercent) {

                            }
                        });



                        // Optionally add an OnItemClickListener
                        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClicked(int itemPosition, Object dataObject) {





                                Toast.makeText(FindFriendsActivity.this, "Clicked!",Toast.LENGTH_SHORT);
                            }


                        });


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            startActivity(new Intent(FindFriendsActivity.this, EnterActivity.class));
            finish();
        }



    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
