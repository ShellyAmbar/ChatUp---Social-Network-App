package com.shelly.ambar.chatup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shelly.ambar.chatup.Models.StoryModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{

    StoriesProgressView storiesProgressView;
    int counter=0;
    long pressTime=0L;
    long limit= 500L;

    CircleImageView StoryPhoto;
    ImageView Image;
    TextView UserName;
    List<String> Images;
    List<String> StoryIds;
    String UserId;

    LinearLayout r_seen;
    TextView seen_number;
    ImageView story_delete;




    private View.OnTouchListener onTouchListener =new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:
                    pressTime=System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;

                case MotionEvent.ACTION_UP:
                    long now=System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit< now-pressTime;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        Image=(ImageView)findViewById(R.id.ImageStory);
        StoryPhoto=(CircleImageView)findViewById(R.id.storyPhoto);
        UserName=(TextView)findViewById(R.id.story_UserName);
        r_seen=findViewById(R.id.r_seen);
        seen_number=findViewById(R.id.seen_number);
        story_delete=findViewById(R.id.story_delete);

        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);

        UserId=getIntent().getStringExtra("userId");

        if(UserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            r_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);

        }
        getStories(UserId);
        userInfo(UserId);

        View reverse=(View) findViewById(R.id.Reverse);
        View skip=(View) findViewById(R.id.Skip);

        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

        r_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ViewsIntent=new Intent(StoryActivity.this, FollowersActivity.class);
                ViewsIntent.putExtra("id",UserId);
                ViewsIntent.putExtra("StoryId", StoryIds.get(counter));
                ViewsIntent.putExtra("title", "Views");
                startActivity(ViewsIntent);
            }
        });
        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story")
                        .child(UserId).child(StoryIds.get(counter));

                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(StoryActivity.this, "Deleted story! ", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onNext() {

        Glide.with(getApplicationContext()).load(Images.get(++counter)).into(Image);
        addView(StoryIds.get(counter));
        seenNumber(StoryIds.get(counter));

    }

    @Override
    public void onPrev() {
        if(counter-1 <0) return;

        Glide.with(getApplicationContext()).load(Images.get(--counter)).into(Image);
        seenNumber(StoryIds.get(counter));

    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userid){
        Images=new ArrayList<>();
        StoryIds=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Story").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Images.clear();
                StoryIds.clear();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    StoryModel story=dataSnapshot1.getValue(StoryModel.class);
                    long timeCurrent=System.currentTimeMillis();
                    if(timeCurrent> story.getTimeStart() && timeCurrent< story.getTimeEnd() ){
                        Images.add(story.getImageUrl());
                        StoryIds.add(story.getStoryId());

                    }
                }
                storiesProgressView.setStoriesCount(Images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);

                Glide.with(getApplicationContext()).load(Images.get(counter)).into(Image);

                addView(StoryIds.get(counter));
                seenNumber(StoryIds.get(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void userInfo(final String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersDataModel user=dataSnapshot.getValue(UsersDataModel.class);
                Glide.with(getApplicationContext()).load(user.getThumb_Image()).into(StoryPhoto);
                UserName.setText(user.getUserName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addView(String StoryId){
        FirebaseDatabase.getInstance()
                .getReference("Story")
                .child(UserId)
                .child(StoryId)
                .child("views")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(true);
    }
    private void seenNumber(String StoryId){
        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Story")
                .child(UserId)
                .child(StoryId)
                .child("views");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                seen_number.setText(""+ dataSnapshot.getChildrenCount());
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
