package com.shelly.ambar.chatup;

import android.app.Notification;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shelly.ambar.chatup.Models.UsersDataModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationActivity extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView textMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        imageView=findViewById(R.id.imagePerson);
        textMessage=findViewById(R.id.textMessage);

        String from=getIntent().getStringExtra("from_user_id");
        final String message=getIntent().getStringExtra("message");

        FirebaseDatabase.getInstance().getReference("Users").child(from).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersDataModel usersDataModel =dataSnapshot.getValue(UsersDataModel.class);
                Glide.with(getApplicationContext())
                        .load(usersDataModel.getThumb_Image())
                        .placeholder(R.drawable.blank_portrait).into(imageView);
                textMessage.setText("New message from "+ usersDataModel.getUserName()+ ": "+ message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
