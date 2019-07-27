package com.shelly.ambar.chatup.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shelly.ambar.chatup.ChatActivity;
import com.shelly.ambar.chatup.MainActivity;
import com.shelly.ambar.chatup.Models.ChatMessageModel;
import com.shelly.ambar.chatup.Models.ChatModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.shelly.ambar.chatup.ProfileActivity;
import com.shelly.ambar.chatup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.viewHolder> {

    private Context mContext;
    private List<ChatModel> chatModelList;
    private FirebaseUser firebaseUser;
    private String lastMassage;
    private String currentUserName ="";
    private String currentUserPhoto="";

    public ChatsAdapter(Context mContext, ArrayList<ChatModel> chatModelList) {
        this.mContext = mContext;
        this.chatModelList = chatModelList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =LayoutInflater.from(mContext).inflate(R.layout.item_chat,viewGroup,false);
        return new ChatsAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, int i) {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        final ChatModel chatModel=chatModelList.get(i);

        //listener to a new message


       // addMassagesListener(chatModel);


        if(chatModel.getUserPhoto().isEmpty()){
            Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_portrait)
                    .into(viewHolder.image_profile);
        }else{
            Glide.with(mContext).load(chatModel.getUserPhoto())
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                    .into(viewHolder.image_profile);
        }


        viewHolder.lastTime.setText(chatModel.getEnterTime());
        viewHolder.last_massage.setText(chatModel.getLastMassage());
        viewHolder.user_name.setText(chatModel.getUserName());
        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(mContext, ProfileActivity.class);
               intent.putExtra("publisherId",chatModel.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
               mContext.startActivity(intent);
            }
        });
        viewHolder.last_massage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ChatActivity.class);
                intent.putExtra("userId",chatModel.getUserId());
                intent.putExtra("userName",chatModel.getUserName());
                mContext.startActivity(intent);
            }
        });


        viewHolder.last_massage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                    AlertDialog alertDialog=new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete this chat?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(chatModel.getUserId())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        FirebaseDatabase.getInstance().getReference("Massages")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chatModel.getUserId())
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(mContext, "This chat has been deleted successfully from your chats!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }else{
                                        Toast.makeText(mContext, "Error occurred, try to delete again please.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            dialog.dismiss();
                        }
                    });


                    alertDialog.show();

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        private CircleImageView image_profile;
        private TextView user_name;
        private TextView last_massage;
        private TextView lastTime;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile=itemView.findViewById(R.id.image_profile);
            user_name=itemView.findViewById(R.id.user_name);
            last_massage=itemView.findViewById(R.id.last_massage);
            lastTime=itemView.findViewById(R.id.lastTime);
        }
    }

    private void addMassagesListener(final ChatModel chatModel){
        FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                currentUserName=usersDataModel.getUserName();
                currentUserPhoto=usersDataModel.getThumb_Image();

                addNewChat(firebaseUser.getUid(),chatModel.getUserId(),currentUserName,currentUserPhoto);

                addNewChat(chatModel.getUserId(),firebaseUser.getUid(),chatModel.getUserName(),chatModel.getUserPhoto());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addNewChat(final String userId, final String currentUserId, final String userName, final String userPhoto){

        DatabaseReference referenceToMassages=FirebaseDatabase.getInstance().getReference("Massages")
                .child(currentUserId).child(userId);
        referenceToMassages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Calendar calForDate=Calendar.getInstance();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String time=simpleDateFormat.format(calForDate.getTime());
                ChatMessageModel chatMessageModel=dataSnapshot.getValue(ChatMessageModel.class);
               final String lastMassage= chatMessageModel.getUser_Message();
               final String lastTime=time;


                DatabaseReference  reference=FirebaseDatabase.getInstance().getReference("Chats")
                        .child(currentUserId).child(userId);

                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        addChatToAllChats(userId,currentUserId,userName,userPhoto,lastMassage,lastTime);

                    }
                });


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


    private void addChatToAllChats(String userId, String currentUserId,String userName,String userPhoto,String lastMassage,String lastTime){

        DatabaseReference  reference=FirebaseDatabase.getInstance().getReference("Chats")
                .child(currentUserId).child(userId);


        HashMap<String,Object> hashMap=new HashMap<>();
        String chatId= reference.push().getKey();


        hashMap.put("userId",userId);
        hashMap.put("userName",userName);
        hashMap.put("userPhoto",userPhoto);
        hashMap.put("chatId",chatId);
        hashMap.put("lastMassage",lastMassage);
        hashMap.put("enterTime",lastTime);



        reference.setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }
}
