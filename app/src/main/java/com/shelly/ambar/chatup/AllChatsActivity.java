package com.shelly.ambar.chatup;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shelly.ambar.chatup.Adapters.ChatsAdapter;
import com.shelly.ambar.chatup.Adapters.UserAdapter;
import com.shelly.ambar.chatup.Models.ChatModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class AllChatsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ChatsAdapter chatsAdapter;

    private ArrayList<ChatModel> chatModelList;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        //toolbar
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("All Chats ");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setNavigationIcon(ContextCompat.getDrawable(AllChatsActivity.this,R.drawable.ic_back_ground));

        //recycle

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        chatModelList=new ArrayList<>();
        chatsAdapter=new ChatsAdapter(this, chatModelList);

        recyclerView.setAdapter(chatsAdapter);



        putAllChatsInRecycler();



    }

    private void putAllChatsInRecycler() {
     DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

     reference.orderByChild("enterTime").addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             ChatModel chatModel = null;
             chatModelList.clear();
             for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                 chatModel=snapshot.getValue(ChatModel.class);

                 chatModelList.add(chatModel);

             }


             Collections.reverse(chatModelList);
             chatsAdapter.notifyDataSetChanged();




         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });



    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
