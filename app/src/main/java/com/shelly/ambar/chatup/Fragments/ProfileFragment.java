package com.shelly.ambar.chatup.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shelly.ambar.chatup.Adapters.MyPhotoAdapter;
import com.shelly.ambar.chatup.FollowersActivity;
import com.shelly.ambar.chatup.Models.PostModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.shelly.ambar.chatup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment  {
    private CircleImageView image_profile;
    private TextView posts;
    private TextView followersName;
    private TextView followingName;
    private TextView followers;
    private TextView following;
    private Button btn_follow_me;
    private FirebaseUser firebaseUser;
    private TextView text_about;
    private RecyclerView recycler_view_photos;
    private ScrollView about_person_scrollView;
    private ImageView photosLibrary_Icon;
    private ImageView about_Icon;
    private TextView UserNameText;
    private MyPhotoAdapter myPhotoAdapter;
    private List<PostModel> postModelList;


    private String profileId;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);


        SharedPreferences preferences=Objects.requireNonNull(getContext()).getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId=preferences.getString("profileId","none");



        image_profile=view.findViewById(R.id.image_profile);
        posts=view.findViewById(R.id.posts);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        btn_follow_me=view.findViewById(R.id.btn_follow_me);
        text_about=view.findViewById(R.id.text_about);
        about_person_scrollView=view.findViewById(R.id.about_person_scrollView);
        photosLibrary_Icon=view.findViewById(R.id.photosLibrary_Icon);
        about_Icon=view.findViewById(R.id.about_Icon);
        UserNameText=view.findViewById(R.id.UserNameText);
        followersName=view.findViewById(R.id.followersName);
        followingName=view.findViewById(R.id.followingName);

        recycler_view_photos=view.findViewById(R.id.recycler_view_photos);
        recycler_view_photos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3);
        recycler_view_photos.setLayoutManager(linearLayoutManager);

        postModelList=new ArrayList<PostModel>();
        myPhotoAdapter=new MyPhotoAdapter(getContext(),postModelList);

        recycler_view_photos.setAdapter(myPhotoAdapter);

        if(FirebaseAuth.getInstance()==null){
            getFragmentManager().beginTransaction().remove(this).commit();
        }

        showUserBioAndImage();
        showNumberOfPostsFollowersFollowing();
        myPhotos();


        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(profileId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            btn_follow_me.setVisibility(View.GONE);
        }else{
            btn_follow_me.setVisibility(View.VISIBLE);
            checkFollowStatus();
        }

        btn_follow_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_follow_me.getText().toString().equals("Un Follow Me")){

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following")
                            .child(profileId).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(profileId).child("followers")
                            .child(firebaseUser.getUid()).removeValue();

                    btn_follow_me.setText("Follow Me");

                    showNumberOfPostsFollowersFollowing();

                }else if(btn_follow_me.getText().toString().equals("Follow Me")){

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following")
                            .child(profileId).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(profileId).child("followers")
                            .child(firebaseUser.getUid()).setValue(true);

                    btn_follow_me.setText("Un Follow Me");

                    addNotifications();
                    showNumberOfPostsFollowersFollowing();

                }
            }
        });

        photosLibrary_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler_view_photos.setVisibility(View.VISIBLE);
                about_person_scrollView.setVisibility(View.GONE);
            }
        });

        about_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler_view_photos.setVisibility(View.GONE);
                about_person_scrollView.setVisibility(View.VISIBLE);
            }
        });


        followersName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1=new Intent(getContext(),FollowersActivity.class);
                intent1.putExtra("id",profileId);
                intent1.putExtra("title","followers");
                startActivity(intent1);

            }
        });
        followingName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(getContext(),FollowersActivity.class);
                intent2.putExtra("id",profileId);
                intent2.putExtra("title","following");
                startActivity(intent2);

            }
        });





        return view;
    }

    private void addNotifications(){

        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Notifications").child(profileId);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userId",firebaseUser.getUid());
        hashMap.put("postId","" );
        hashMap.put("text","Started following you!");
        hashMap.put("isPost","false");

        reference.push().setValue(hashMap);


    }


    public void showUserBioAndImage(){
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        //String UserId=user.getUid();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance()
                .getReference().child("Users").child(profileId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                if(usersDataModel!=null) {
                    //set bio

                    text_about.setText(usersDataModel.getUserStatus());
                    UserNameText.setText(usersDataModel.getUserName());
                    //set image
                    String uri=usersDataModel.getThumb_Image();
                    if(getContext()!=null)
                        Glide.with(getContext()).load(uri).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(image_profile);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public  void showNumberOfPostsFollowersFollowing(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        //final String UserId=user.getUid();
        DatabaseReference databaseReferencePosts=FirebaseDatabase.getInstance()
                .getReference().child("Posts");

        databaseReferencePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;
                PostModel postModel;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    postModel=snapshot.getValue(PostModel.class);
                    if(postModel.getPublisher().equals(profileId) ){
                        i++;
                    }
                }
                posts.setText(""+i);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseReferenceFollowers=FirebaseDatabase.getInstance()
                .getReference().child("Follow").child(profileId).child("followers");

        databaseReferenceFollowers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference databaseReferenceFollowing=FirebaseDatabase.getInstance()
                .getReference().child("Follow").child(profileId).child("following");

        databaseReferenceFollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                following.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void checkFollowStatus(){

        DatabaseReference databaseReferenceFollowing=FirebaseDatabase.getInstance()
                .getReference().child("Follow").child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).child("following");

        databaseReferenceFollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(profileId).exists()){
                    btn_follow_me.setText("Un Follow Me");
                }else{
                    btn_follow_me.setText("Follow Me");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void myPhotos(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postModelList.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    PostModel postModel=snapshot.getValue(PostModel.class);
                    if (postModel.getPublisher().equals(profileId)){
                        postModelList.add(postModel);
                    }

                }
                Collections.reverse(postModelList);
                myPhotoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
