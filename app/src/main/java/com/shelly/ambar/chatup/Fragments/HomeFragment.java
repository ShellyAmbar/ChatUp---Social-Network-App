package com.shelly.ambar.chatup.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shelly.ambar.chatup.Adapters.PostAdapter;
import com.shelly.ambar.chatup.Adapters.StoryAdapter;
import com.shelly.ambar.chatup.LoginActivity;
import com.shelly.ambar.chatup.Models.PostModel;
import com.shelly.ambar.chatup.Models.StoryModel;
import com.shelly.ambar.chatup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<PostModel> postList;


    private RecyclerView recyclerView_Story;
    private StoryAdapter storyAdapter;
    private List<StoryModel> List_Story;
    private DatabaseReference databaseReference;
    private FirebaseUser CurrentUser;

    private List<String>followingList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postList);
        recyclerView.setAdapter(postAdapter);



        if(FirebaseAuth.getInstance()==null){
            getFragmentManager().beginTransaction().remove(this).commit();
        }


        //recycle story
        recyclerView_Story=view.findViewById(R.id.Users_List_Story);
        recyclerView_Story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView_Story.setLayoutManager(linearLayoutManager1);
        List_Story= new ArrayList<>();
        storyAdapter= new StoryAdapter(getContext(),List_Story);
        recyclerView_Story.setAdapter(storyAdapter);

        followingList=new ArrayList<String>();


        CurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        // String CurrentUserID=CurrentUser.getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");


        readPosts();
        ReadStory();


        onStart();



        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance()==null){
            startActivity(new Intent(getContext(),LoginActivity.class));

        }
        // String UserId=Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // readPosts();
        // ReadStory();





    }

    private void ReadStory(){


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    followingList.add(snapshot.getKey());


                }

                DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("Story");
                reference1.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long TimeCurrent=System.currentTimeMillis();
                        List_Story.clear();
                        List_Story.add(new StoryModel("",0,0,"",FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()));

                        for(String id : followingList){
                            int CountStory=0;
                            StoryModel storyModel=null;
                            for(DataSnapshot snapshot : dataSnapshot.child(id).getChildren()){
                                storyModel=snapshot.getValue(StoryModel.class);
                                if(TimeCurrent> storyModel.getTimeStart() && TimeCurrent< storyModel.getTimeEnd()){
                                    CountStory++;
                                }
                            }
                            if(CountStory>0){
                                List_Story.add(storyModel);
                            }
                        }

                        storyAdapter.notifyDataSetChanged();
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



    }


    private void  checkFollowing(){

        final List<String>followingList=new ArrayList<>();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    followingList.add(snapshot.getKey());
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readPosts(){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    followingList.add(snapshot.getKey());
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Posts");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();

                String MyId=FirebaseAuth.getInstance().getCurrentUser().getUid();

                followingList.add(MyId);

                for(DataSnapshot snapshot: dataSnapshot.getChildren() ){
                    PostModel post=snapshot.getValue(PostModel.class);
                    for(String id : followingList){
                        if(post.getPublisher().equals(id)  ){
                            postList.add(post);
                        }
                    }

                }


                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
