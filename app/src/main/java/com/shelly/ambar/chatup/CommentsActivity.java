package com.shelly.ambar.chatup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.shelly.ambar.chatup.Adapters.CommentAdapter;
import com.shelly.ambar.chatup.Models.CommentModel;
import com.shelly.ambar.chatup.Models.PostModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {


    private TextView postBTN;
    private ImageView imageView_profile;
    private EditText addComment;

    private String postId;
    private String publisherId;
    private  String isVideo;
    private String commentText;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<CommentModel> commentModelList;
    private Toolbar toolbar;

    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);



        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        publisherId=intent.getStringExtra("publisherId");

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentModelList=new ArrayList<>();
        commentAdapter=new CommentAdapter(this,commentModelList,postId);
        recyclerView.setAdapter(commentAdapter);

        addComment=findViewById(R.id.add_comment);
        postBTN=findViewById(R.id.btn_post);
        imageView_profile=findViewById(R.id.image_profile);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();


        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setNavigationIcon(ContextCompat.getDrawable(CommentsActivity.this,R.drawable.ic_goback));

        postBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentText=addComment.getText().toString();
                if(addComment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this, "Please enter your comment first", Toast.LENGTH_SHORT).show();
                }else{

                    addingComment();
                }
            }
        });

        getImage();
        readComments();

    }

    private void addNotifications(){

        final DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Notifications").child(publisherId);


        DatabaseReference reference2=FirebaseDatabase.getInstance()
                .getReference("Posts").child(postId);

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    isVideo=dataSnapshot.getValue(PostModel.class).getIsvideo();

                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("userId",firebaseUser.getUid());
                    hashMap.put("postId",postId );
                    hashMap.put("text","Commented: "+commentText);
                    hashMap.put("isPost","true");
                    hashMap.put("isvideo", isVideo);

                    reference.push().setValue(hashMap);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void addingComment() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        String commentId=reference.push().getKey();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("comment", addComment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentId", commentId);

        if (commentId != null) {
            reference.child(commentId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        addNotifications();
                        addComment.setText("");
                    }else{
                        Toast.makeText(CommentsActivity.this, "Failed to post your comment", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    private void getImage(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                if(!usersDataModel.getThumb_Image().equals("")) {
                    Glide.with(getApplicationContext()).load(usersDataModel.getThumb_Image()).into(imageView_profile);
                }else{
                    Glide.with(getApplicationContext()).load(R.drawable.com_facebook_profile_picture_blank_portrait).into(imageView_profile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readComments(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentModelList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    CommentModel commentModel=snapshot.getValue(CommentModel.class);
                    commentModelList.add(commentModel);
                }

                commentAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(commentModelList.size()-1);
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
