package com.shelly.ambar.chatup.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shelly.ambar.chatup.ChatActivity;
import com.shelly.ambar.chatup.MainActivity;
import com.shelly.ambar.chatup.Models.NotificationModel;
import com.shelly.ambar.chatup.Models.PostModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.shelly.ambar.chatup.PostDetailActivity;
import com.shelly.ambar.chatup.ProfileActivity;
import com.shelly.ambar.chatup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context context;
    private List<NotificationModel> notificationModelList;
    private String otherUserName="";

    public NotificationAdapter(Context context, List<NotificationModel> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.notification_item,viewGroup,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final NotificationModel notificationModel=notificationModelList.get(i);
        viewHolder.commentText.setText(notificationModel.getText());



        getUserInfo(viewHolder.image_profile, viewHolder.username,notificationModel.getUserId());
        if(notificationModel.getIsPost().equals("true")){
            viewHolder.post_image.setVisibility(View.VISIBLE);
            getPostImage(viewHolder.post_image,notificationModel.getPostId());
        }else{
            viewHolder.post_image.setVisibility(View.GONE);
        }

        viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(notificationModel.getIsPost().equals("true")) {



                    DatabaseReference reference=FirebaseDatabase.getInstance()
                            .getReference("Posts").child(notificationModel.getPostId());

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if(dataSnapshot.exists()){
                                PostModel postModel=dataSnapshot.getValue(PostModel.class);
                                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                                editor.putString("postId", notificationModel.getPostId());
                                editor.apply();
                                Intent intent=new Intent(context, PostDetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                context.startActivity(intent);
                            }else{
                                Toast.makeText(context, "Sorry,the post is no longer available.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }else{



                }

            }
        });
        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(context, ProfileActivity.class);
                intent.putExtra("publisherId",notificationModel.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);



            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView image_profile;
        private TextView username,commentText;
        private ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);
            commentText=itemView.findViewById(R.id.commentText);
            post_image=itemView.findViewById(R.id.post_image);

        }
    }
    public void getUserInfo(final CircleImageView imageView, final TextView username, String publisherId){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(publisherId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                    otherUserName=usersDataModel.getUserName();
                    if(usersDataModel.getThumb_Image().isEmpty()){
                        Glide.with(context).load(R.drawable.com_facebook_profile_picture_blank_portrait).into(imageView);
                    }else{
                        Glide.with(context).load(usersDataModel.getThumb_Image())
                                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(imageView);
                    }

                    username.setText(usersDataModel.getUserName());
                }
                else{
                    UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                    Glide.with(context).load(R.drawable.com_facebook_profile_picture_blank_portrait)
                            .into(imageView);
                    username.setText("Not Exist");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getPostImage(final ImageView postImageView, String postId){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    PostModel postModel=dataSnapshot.getValue(PostModel.class);
                    Glide.with(context).load(postModel.getPostImage())
                            .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(postImageView);
                }else{
                    Glide.with(context).load(R.drawable.com_facebook_profile_picture_blank_portrait).into(postImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
