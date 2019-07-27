package com.shelly.ambar.chatup.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.shelly.ambar.chatup.ChatActivity;
import com.shelly.ambar.chatup.Models.ChatMessageModel;
import com.shelly.ambar.chatup.Models.ChatModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.shelly.ambar.chatup.ProfileActivity;
import com.shelly.ambar.chatup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    @NonNull

    Context mContext;
    List<UsersDataModel> mUsers;
    private FirebaseUser firebaseUser;
    private boolean isChatExist;
    private String lastMassage;
    private String currentUserName ="";
    private String currentUserPhoto="";
    private String fromUserMessage;




    public UserAdapter(@NonNull Context mContext, List<UsersDataModel> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;



    }



    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,viewGroup,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        final UsersDataModel userModel=mUsers.get(i);


        getListenerForNewMassage(userModel);




        viewHolder.btn_follow.setVisibility(View.VISIBLE);
        viewHolder.UserName.setText(userModel.getUserName());
        // viewHolder.FullName.setText(userModel.getUserStatus());
        if(userModel.getThumb_Image().isEmpty()){

            Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_portrait).into(viewHolder.image_profile);
        }else{
            Glide.with(mContext).load(userModel.getThumb_Image())
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(viewHolder.image_profile);
        }

        isFollowing(userModel.getId(), viewHolder.btn_follow);

        if(userModel.getId().equals(firebaseUser.getUid())){
            viewHolder.btn_follow.setVisibility(View.GONE);
        }
        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                CharSequence[] options=new CharSequence[]{
                        userModel.getUserName()+"'s profile",
                        "Send Massage"
                };
                builder.setTitle("Select option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which==0){


                            Intent intent=new Intent(mContext, ProfileActivity.class);
                            intent.putExtra("publisherId",userModel.getId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            mContext.startActivity(intent);

                        }
                        if(which==1){
                           // addChatToAllChats(userModel.getId(), userModel.getUserName(),userModel.getThumb_Image());
                            Intent intent=new Intent(mContext, ChatActivity.class);
                            intent.putExtra("userId",userModel.getId());
                            intent.putExtra("userName",userModel.getUserName());
                            mContext.startActivity(intent);
                        }
                    }
                });
                builder.show();




            }
        });
        viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.btn_follow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following")
                            .child(userModel.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(userModel.getId()).child("followers")
                            .child(firebaseUser.getUid()).setValue(true);

                    addNotifications(userModel.getId());
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following")
                            .child(userModel.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(userModel.getId()).child("followers")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

    }

    public void GetOrderesSearchResult(List<UsersDataModel> newList){
        mUsers = newList;
        notifyDataSetChanged();
    }

    private void addNotifications(String userId){

        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Notifications").child(userId);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userId",firebaseUser.getUid());
        hashMap.put("postId","" );
        hashMap.put("text","Started to follow you!");
        hashMap.put("isPost","false");
        hashMap.put("isvideo", "false");

        reference.push().setValue(hashMap);


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView UserName;
        public TextView FullName;
        private CircleImageView image_profile;
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            UserName=itemView.findViewById(R.id.user_name);
            FullName=itemView.findViewById(R.id.full_name);
            image_profile=itemView.findViewById(R.id.image_profile);
            btn_follow=itemView.findViewById(R.id.btn_follow);




        }


    }

    private void isFollowing(final String userId, final Button friend_button){
        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userId).exists()){
                    friend_button.setText("Following");
                }else{
                    friend_button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getListenerForNewMassage(final UsersDataModel userModel){
        FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                currentUserName=usersDataModel.getUserName();
                currentUserPhoto=usersDataModel.getThumb_Image();

                getLastMessageOfChatAndSetChat( userModel.getId(),
                        firebaseUser.getUid(),userModel.getUserName(),userModel.getThumb_Image());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLastMessageOfChatAndSetChat(final String userId, final String currentUserId, final String userName, final String userPhoto){




        DatabaseReference referenceToMassages=FirebaseDatabase.getInstance().getReference("Massages")
                .child(currentUserId).child(userId);
        referenceToMassages.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {



                ChatMessageModel chatMessageModel=dataSnapshot.getValue(ChatMessageModel.class);
                lastMassage= chatMessageModel.getUser_Message();


                String time=chatMessageModel.getUserExactTime();
                fromUserMessage=chatMessageModel.getFrom();
                String type=chatMessageModel.getType();
                String toUser=chatMessageModel.getToUser();






                addChatToAllChatsIfPossible(userId,currentUserId,userName,userPhoto,time);
                addChatToAllChatsIfPossible(currentUserId,userId,currentUserName,currentUserPhoto,time);
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




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addChatToAllChatsIfPossible(final String userId,final String currentUserId, final String userName, final String userPhoto,final String time ){



        final DatabaseReference  referenceToAllChats=FirebaseDatabase.getInstance().getReference("Chats")
                .child(currentUserId);
        referenceToAllChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isChatExist=false;

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(Objects.requireNonNull(snapshot.getValue(ChatModel.class)).getUserId().equals(userId)) {
                        isChatExist = true;
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        if(isChatExist){

            DatabaseReference  reference=FirebaseDatabase.getInstance().getReference("Chats")
                    .child(currentUserId).child(userId);

            reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                    addChatToAllChats(userId,currentUserId,userName,userPhoto,time);

                }
            });

        }else{

            addChatToAllChats(userId,currentUserId,userName,userPhoto,time);
        }





    }

    private void addChatToAllChats(String userId, String currentUserId,String userName,String userPhoto,String time){

        DatabaseReference  reference=FirebaseDatabase.getInstance().getReference("Chats")
                .child(currentUserId).child(userId);




        HashMap<String,Object> hashMap=new HashMap<>();
        String chatId= reference.push().getKey();


        hashMap.put("userId",userId);
        hashMap.put("userName",userName);
        hashMap.put("userPhoto",userPhoto);
        hashMap.put("chatId",chatId);
        hashMap.put("lastMassage",lastMassage);
        hashMap.put("enterTime",time);

        reference.setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });



    }


}
