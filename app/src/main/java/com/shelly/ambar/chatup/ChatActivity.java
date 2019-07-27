package com.shelly.ambar.chatup;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shelly.ambar.chatup.Adapters.MassageAdapter;
import com.shelly.ambar.chatup.Models.ChatMessageModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private static final int REQEST_CODE_SPEECH_INPUT =1000 ;
    private RecyclerView recycler_view;
    private EditText text;
    private ImageButton send,camera,seconed_mic;
    private String userId;
    private String userName;
    private String CurrentUserId;
    private String userMessage;


    private CircleImageView photoUser;

    private TextView UserNameText;
    private MassageAdapter massageAdapter;
    private List<ChatMessageModel> chatMessageModelList;

    private LinearLayoutManager linearLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        recycler_view=findViewById(R.id.recycler_view);
        text=findViewById(R.id.text);
        send=findViewById(R.id.send);
        camera=findViewById(R.id.camera);
        seconed_mic=findViewById(R.id.seconed_mic);

        photoUser=findViewById(R.id.photoUser);

        UserNameText=findViewById(R.id.UserNameText);
        chatMessageModelList=new ArrayList<>();
        linearLayoutManager=new LinearLayoutManager(this);
        massageAdapter=new MassageAdapter(getApplicationContext(),chatMessageModelList);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setAdapter(massageAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.menu_chat);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(ChatActivity.this,R.drawable.ic_back_ground));

        Intent intent=getIntent();
        userId=intent.getStringExtra("userId");
        userName=intent.getStringExtra("userName");
        CurrentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(FirebaseAuth.getInstance()==null){
           finish();
        }



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMassege();

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ChatActivity.this,FilterActivity.class);
                intent.putExtra("Activity","ChatActivity");
                intent.putExtra("otherId",userId);
                intent.putExtra("User_Message",text.getText().toString());

                startActivity(intent);
            }
        });

        seconed_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speechToText();
            }
        });



        displayPhotoAndNameOfUser();


        displayMassages();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){


            case R.id.block_user:
                return true;

            case R.id.search_user:
                return true;

            case R.id.mute_user:
                return true;

            case R.id.media_user:
                return true;

            case R.id.report_user:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;

    }

    private void speechToText() {

        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS,30);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"say something");


        //start intent
        try{
            startActivityForResult(intent,REQEST_CODE_SPEECH_INPUT);

        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMassege() {
        if(TextUtils.isEmpty(text.getText().toString())){

            Toast.makeText(this, "Wo ho,This massage is Empty!", Toast.LENGTH_SHORT).show();
        }else {


            //set massage on current user
            DatabaseReference referenceSetMyMassege = FirebaseDatabase.getInstance()
                    .getReference("Massages")
                    .child(CurrentUserId).child(userId);
            String SenderKey=referenceSetMyMassege.push().getKey();

            //set massage on other user
            final DatabaseReference referenceSetHisMassege = FirebaseDatabase.getInstance()
                    .getReference("Massages")
                    .child(userId).child(CurrentUserId);
            final String reciverKey=referenceSetHisMassege.push().getKey();

            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
            String time=simpleDateFormat.format(calForDate.getTime());

            Calendar calForDate1=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            String timeExact=simpleDateFormat1.format(calForDate1.getTime());

            userMessage=text.getText().toString();
            final HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("User_Message",userMessage);
            hashMap.put("from",CurrentUserId);
            hashMap.put("toUser",userId);
            hashMap.put("User_Time",time);
            hashMap.put("type","text");
            hashMap.put("userPhoto","");
            hashMap.put("userExactTime" ,timeExact);



            referenceSetMyMassege.child(SenderKey).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Failure! Massage not sent..", Toast.LENGTH_SHORT).show();
                    }else{
                        referenceSetHisMassege.child(reciverKey).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    text.setText("");
                                    text.requestFocus();



                                }


                            }
                        });
                    }


                }
            });







        }

    }

    private void displayPhotoAndNameOfUser(){

        UserNameText.setText(userName);

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users")
                .child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                if(usersDataModel!=null){
                    Glide.with(getApplicationContext())
                            .load(usersDataModel.getThumb_Image())
                            .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                            .into(photoUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void displayMassages(){

        FirebaseDatabase.getInstance()
                .getReference().child("Massages").child(CurrentUserId).child(userId)
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.exists()){
                            ChatMessageModel chatMessageModel=dataSnapshot.getValue(ChatMessageModel.class);
                            chatMessageModelList.add(chatMessageModel);
                            massageAdapter.notifyDataSetChanged();
                            recycler_view.scrollToPosition(chatMessageModelList.size()-1);
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQEST_CODE_SPEECH_INPUT:
                if(resultCode==RESULT_OK && data!=null){
                    getResultSpeech(data);
                }
                break;
        }
    }

    private void getResultSpeech(Intent data) {

        //get string of text
        ArrayList<String> result= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        text.setText(result.get(0));

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
