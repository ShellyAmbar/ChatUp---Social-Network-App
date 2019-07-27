package com.shelly.ambar.chatup.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.shelly.ambar.chatup.ChatActivity;
import com.shelly.ambar.chatup.CommentsActivity;
import com.shelly.ambar.chatup.FollowersActivity;
import com.shelly.ambar.chatup.MainActivity;
import com.shelly.ambar.chatup.Models.ChatMessageModel;
import com.shelly.ambar.chatup.Models.ChatModel;
import com.shelly.ambar.chatup.Models.PostModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.shelly.ambar.chatup.PostDetailActivity;
import com.shelly.ambar.chatup.ProfileActivity;
import com.shelly.ambar.chatup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<PostModel> mPosts;
    private FirebaseUser firebaseUser;
    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    private boolean isChatExist;
    private String lastMassage;


    public PostAdapter(Context mContext, List<PostModel> posts) {
        this.mContext = mContext;
        mPosts = posts;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.post_item,viewGroup,false);


        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        final PostModel postModel=mPosts.get(i);



       // getLastMessageOfChatAndSetChat(postModel.getPublisher(),firebaseUser.getUid(),postModel.getPublisherName(),postModel.getPublisherImage());



        if(postModel==null){
            viewHolder.Post_Image.setVisibility(View.VISIBLE);
            viewHolder.videoView.setVisibility(View.GONE);
            Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_portrait).into(viewHolder.Post_Image);
        }else if(postModel.getIsvideo().equals("true")){


            mediaController=new MediaController(getApplicationContext());
            //mediaPlayer=new MediaPlayer();

            viewHolder.Post_Image.setVisibility(View.GONE);
            viewHolder.videoView.setVisibility(View.VISIBLE);
            viewHolder.videoView.setVideoURI(Uri.parse(postModel.getVideo()));

            viewHolder.videoView.setMediaController(mediaController);
            mediaController.setAnchorView(viewHolder.videoView);


            viewHolder.videoView.requestFocus();
            viewHolder.videoView.start();


            if(!viewHolder.videoView.isPlaying()){
                viewHolder.videoView.resume();
            }


            if(postModel.getDescription().equals("")){
                viewHolder.Description.setVisibility(View.GONE);

            }else{
                viewHolder.Description.setVisibility(View.VISIBLE);
                viewHolder.Description.setText(postModel.getDescription());
            }

            if(postModel.getPublisherImage().isEmpty()){
                Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .into(viewHolder.Profile_Image);
            }else{
                Glide.with(mContext).load(postModel.getPublisherImage())
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .into(viewHolder.Profile_Image);
            }


        }else{

            viewHolder.Post_Image.setVisibility(View.VISIBLE);
            viewHolder.videoView.setVisibility(View.GONE);
            Glide.with(mContext).load(postModel.getPostImage())
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(viewHolder.Post_Image);

            if(postModel.getDescription().isEmpty()){
                viewHolder.Description.setVisibility(View.GONE);

            }else{
                viewHolder.Description.setVisibility(View.VISIBLE);
                viewHolder.Description.setText(postModel.getDescription());
            }
            viewHolder.Publisher.setText(postModel.getPublisherName());
            viewHolder.UserName.setText(postModel.getPublisherName());

           if(postModel.getPublisherImage().isEmpty()){
               Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_portrait)
                       .into(viewHolder.Profile_Image);
           }else{
               Glide.with(mContext).load(postModel.getPublisherImage())
                       .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                       .into(viewHolder.Profile_Image);
           }
        }





        isLiked(postModel.getPostId(),viewHolder.LikeIMG);
        numberOfLikes(viewHolder.Likes,postModel.getPostId());
        getComments(postModel.getPostId(),viewHolder.Comments);

        viewHolder.LikeIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.LikeIMG.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(postModel.getPostId()).child(firebaseUser.getUid()).setValue(true);

                    addNotifications(postModel.getPublisher(),postModel.getPostId(), postModel.getIsvideo());
                }
                else if(viewHolder.LikeIMG.getTag().equals("liked")){
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(postModel.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        viewHolder.CommentIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postId",postModel.getPostId());
                intent.putExtra("publisherId",postModel.getPublisher());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            }
        });

        viewHolder.Comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postId",postModel.getPostId());
                intent.putExtra("publisherId",postModel.getPublisher());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            }
        });

        viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu popupMenu=new PopupMenu(mContext,v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                editPost(postModel.getPostId());
                                return true;

                            case R.id.delete:

                                FirebaseDatabase.getInstance().getReference("Posts")
                                        .child(postModel.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            StorageReference storageReference=FirebaseStorage.getInstance()
                                                    .getReference("Posts").child(postModel.getPublisher()).child(postModel.getPostId());

                                            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isComplete()){
                                                        Toast.makeText(mContext, "The post was deleted successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }else{
                                            Toast.makeText(mContext, "A problem occurred,try to delete again later", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                return true;
                            case R.id.report:
                                Toast.makeText(mContext, "A report has been sent", Toast.LENGTH_SHORT).show();
                                return  true;
                            default: return false;
                        }

                    }
                });

                popupMenu.inflate(R.menu.post_menu);
                if(!postModel.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });

        viewHolder.Profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(mContext, ProfileActivity.class);
                intent.putExtra("publisherId",postModel.getPublisher());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);

            }




        });

        viewHolder.UserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(mContext, ProfileActivity.class);
                intent.putExtra("publisherId",postModel.getPublisher());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            }
        });
        viewHolder.Publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent=new Intent(mContext, ProfileActivity.class);
                intent.putExtra("publisherId",postModel.getPublisher());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);


            }
        });

        viewHolder.Post_Image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postId",postModel.getPostId());
                editor.apply();

                if(mContext.getClass().equals(PostDetailActivity.class)){
                    Toast.makeText(mContext, "You are already in the post..", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent=new Intent(getApplicationContext(),PostDetailActivity.class);
                    intent.putExtra("postId",postModel.getPostId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    mContext.startActivity(intent);
                }

                return true;
            }
        });
        viewHolder.Likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,FollowersActivity.class);
                intent.putExtra("id",postModel.getPostId());
                intent.putExtra("title","likes");
                mContext.startActivity(intent);
            }
        });




    }



    private void getComments(String postId, final TextView comments){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View all "+ dataSnapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView Profile_Image, Post_Image,LikeIMG,CommentIMG,SaveIMG,more;
        public TextView UserName, Likes,Comments,Publisher,Description;
        public VideoView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Profile_Image=itemView.findViewById(R.id.image_profile);
            Post_Image=itemView.findViewById(R.id.post_image);
            LikeIMG=itemView.findViewById(R.id.like);
            CommentIMG=itemView.findViewById(R.id.comment);
            SaveIMG=itemView.findViewById(R.id.save_book);
            UserName=itemView.findViewById(R.id.user_name);
            Likes=itemView.findViewById(R.id.likes);
            Comments=itemView.findViewById(R.id.comments);
            Publisher=itemView.findViewById(R.id.publisher);
            Description=itemView.findViewById(R.id.description);
            more=itemView.findViewById(R.id.more);
            videoView=itemView.findViewById(R.id.post_video);





        }

    }



    private void isLiked(String postId, final ImageView imageView){

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final String userId=firebaseUser.getUid();

        final DatabaseReference databaseReference=FirebaseDatabase.getInstance()
                .getReference().child("Likes").child(postId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");

                }else{
                    imageView.setImageResource(R.drawable.ic_emptylike);
                    imageView.setTag("like");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void numberOfLikes(final TextView likes, String postId){
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance()
                .getReference().child("Likes").child(postId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+"likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editPost(final String postId){

        AlertDialog.Builder alertDialog= new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit post");
        final EditText editText=new EditText(mContext);
        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT

        );
        editText.setLayoutParams(layoutParams);
        alertDialog.setView(editText);

        getText(postId,editText);
        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("description", editText.getText().toString());
                FirebaseDatabase.getInstance().getReference("Posts")
                        .child(postId).updateChildren(hashMap);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void getText(String postId, final EditText editText){

        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Posts").child(postId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(PostModel.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotifications(String userId,String postId,String isvideo){

        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Notifications").child(userId);

        HashMap<String,Object> hashMap=new HashMap<>();

        if(isvideo.equals("true")){


            hashMap.put("userId",firebaseUser.getUid());
            hashMap.put("postId",postId );
            hashMap.put("text","Liked your post");
            hashMap.put("isPost","true");
            hashMap.put("isvideo", "true");

        }else{

            hashMap.put("userId",firebaseUser.getUid());
            hashMap.put("postId",postId );
            hashMap.put("text","Liked your post");
            hashMap.put("isPost","true");
            hashMap.put("isvideo", "false");

        }

        reference.push().setValue(hashMap);


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


                addChatToAllChatsIfPossible(userId,currentUserId,userName,userPhoto,chatMessageModel.getUser_Time());
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
