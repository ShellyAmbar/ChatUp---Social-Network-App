package com.shelly.ambar.chatup;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shelly.ambar.chatup.Models.UsersDataModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchActivity extends AppCompatActivity {

    private EditText massageToMatch;
    private Button  button_send;
    private String matchName;
    private String matchPhoto;
    private String matchId;
    private String myPhoto;
    private String myName;
    private CircleImageView photo1;
    private CircleImageView photo2;
    private TextView matchNames;


    private TextView match_wohoo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        massageToMatch=findViewById(R.id.massageToMatch);
        button_send=findViewById(R.id.button_send);
        matchNames=findViewById(R.id.matchNames);
        matchName=getIntent().getStringExtra("matchName");
        matchPhoto=getIntent().getStringExtra("matchPhoto");
        matchId=getIntent().getStringExtra("matchId");
        photo1=findViewById(R.id.photo1);
        photo2=findViewById(R.id.photo2);
        match_wohoo=findViewById(R.id.match_wohoo);

        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(4)
                .playOn(match_wohoo);

        YoYo.with(Techniques.BounceInLeft)
                .duration(700)
                .repeat(2)
                .playOn(photo1);
        YoYo.with(Techniques.BounceInRight)
                .duration(700)
                .repeat(2)
                .playOn(photo2);
        YoYo.with(Techniques.Flash)
                .duration(700)
                .repeat(3)
                .playOn(matchNames);


        YoYo.with(Techniques.Wave)
                .repeat(2)
                .duration(700)
                .playOn(massageToMatch);







        setPhotosAndNames();

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMassageToMatch();



            }
        });

    }

    private void setPhotosAndNames() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                        myName=usersDataModel.getUserName();
                        myPhoto=usersDataModel.getThumb_Image();

                        Glide.with(MatchActivity.this).load(myPhoto)
                                .placeholder(R.drawable.blank_portrait).into(photo1);
                        Glide.with(MatchActivity.this).load(matchPhoto)
                                .placeholder(R.drawable.blank_portrait)
                                .into(photo2);

                        matchNames.setText(myName + " & " + matchName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void sendMassageToMatch() {

        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
        String time=simpleDateFormat.format(calForDate.getTime());

       final String textMassageToMatch=massageToMatch.getText().toString();

        final DatabaseReference massagesReferenceMine=FirebaseDatabase
                .getInstance().getReference("Massages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(matchId);

        final DatabaseReference massagesReferenceOther=FirebaseDatabase
                .getInstance().getReference("Massages")
                .child(matchId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Calendar calForDate1=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String timeExact=simpleDateFormat1.format(calForDate1.getTime());

        final HashMap<String, Object> hashMapMine=new HashMap<>();
        hashMapMine.put("User_Message",textMassageToMatch);
        hashMapMine.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMapMine.put("toUser",matchId);
        hashMapMine.put("userPhoto","");
        hashMapMine.put("User_Time",time);
        hashMapMine.put("type","text");
        hashMapMine.put("userExactTime" ,timeExact);


        massagesReferenceMine.push().setValue(hashMapMine).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                massagesReferenceOther.push().setValue(hashMapMine).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        massageToMatch.setText(" ");
                        massageToMatch.requestFocus();

                        finish();
                    }
                });

            }
        });








    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
