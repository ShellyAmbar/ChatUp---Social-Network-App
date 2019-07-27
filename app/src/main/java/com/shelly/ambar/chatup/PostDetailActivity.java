package com.shelly.ambar.chatup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.shelly.ambar.chatup.Adapters.PostAdapter;
import com.shelly.ambar.chatup.Models.PostModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private String postId;
    private RecyclerView recycler_view;
    private PostAdapter postAdapter;
    private List<PostModel> postModelList;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        SharedPreferences preferences=getApplicationContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE);
        postId=preferences.getString("postId","none");

        recycler_view=findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(linearLayoutManager);

        postModelList=new ArrayList<PostModel>();
        postAdapter=new PostAdapter(this,postModelList);


        recycler_view.setAdapter(postAdapter);




        readPost();





    }
    private void readPost() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    postModelList.clear();

                    PostModel postModel=dataSnapshot.getValue(PostModel.class);
                    postModelList.add(postModel);
                    postAdapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(PostDetailActivity.this, "Post not exist anymore..", Toast.LENGTH_SHORT).show();
                }

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
