package com.shelly.ambar.chatup.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shelly.ambar.chatup.FilterActivity;
import com.shelly.ambar.chatup.Models.StoryModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.shelly.ambar.chatup.R;
import com.shelly.ambar.chatup.StoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>
{
    private Context mContext;
    private List<StoryModel> mStory;

    public StoryAdapter(Context mContext, List<StoryModel> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==0){
            View view=LayoutInflater.from(mContext).inflate(R.layout.add_story_item,viewGroup,false);
            return new StoryAdapter.ViewHolder(view);
        }else{
            View view=LayoutInflater.from(mContext).inflate(R.layout.story_item,viewGroup,false);
            return new StoryAdapter.ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final StoryModel storyModel=mStory.get(i);
        UserInfo(viewHolder,storyModel.getUserId(),i);

        if(viewHolder.getAdapterPosition()!=0){
            SeenStory(viewHolder,storyModel.getUserId());
        }

        if(viewHolder.getAdapterPosition()==0){
            MyStory(viewHolder.Story_Plus,viewHolder.Add_StoryText,false);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.getAdapterPosition()==0){
                    MyStory(viewHolder.Story_Plus,viewHolder.Add_StoryText,true);
                }else{
                    Intent storyIntent=new Intent(mContext,StoryActivity.class);
                    storyIntent.putExtra("userId",storyModel.getUserId() );
                    mContext.startActivity(storyIntent);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView Story_Seen,Story_Photo, Story_Plus;
        public TextView Story_UserName, Add_StoryText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Story_Seen=itemView.findViewById(R.id.story_photo_seen);
            Story_Photo=itemView.findViewById(R.id.story_photo);
            Story_Plus=itemView.findViewById(R.id.story_plus);
            Story_UserName=itemView.findViewById(R.id.story_UserName);
            Add_StoryText=itemView.findViewById(R.id.story_text);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0;
        }
        return 1;
    }
    private void UserInfo(final ViewHolder viewHolder, String UserId,final int Pos){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance()
                .getReference().child("Users").child(UserId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersDataModel userModel=dataSnapshot.getValue(UsersDataModel.class);

               if(userModel!=null){
                   if (!userModel.getThumb_Image().isEmpty()) {
                       Glide.with(mContext).load(userModel.getThumb_Image()).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                               .into(viewHolder.Story_Photo);
                   }else{
                       Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_portrait)
                               .into(viewHolder.Story_Photo);
                   }
                   if(Pos !=0){
                       if (!userModel.getThumb_Image().isEmpty()) {
                           Glide.with(mContext).load(userModel.getThumb_Image()).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                                   .into(viewHolder.Story_Seen);
                           viewHolder.Story_UserName.setText(userModel.getUserName());
                       }else{
                           Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_portrait)
                                   .into(viewHolder.Story_Seen);
                       }
                   }
               }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void MyStory(final ImageView imageView, final TextView textView, final boolean Click){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                long CurrentTime=System.currentTimeMillis();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    StoryModel storyModel=dataSnapshot1.getValue(StoryModel.class);
                    if(CurrentTime > storyModel.getTimeStart() && CurrentTime < storyModel.getTimeEnd()){
                        count++;

                    }
                }
                if(Click){
                    if(count>0){
                        final AlertDialog dialog=new AlertDialog.Builder(mContext).create();
                        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "View story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent storyIntent=new Intent(mContext,StoryActivity.class);
                                storyIntent.putExtra("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                mContext.startActivity(storyIntent);
                                dialog.dismiss();

                            }
                        });

                        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent AddStoryIntent=new Intent(mContext, FilterActivity.class);
                                AddStoryIntent.putExtra("Activity", "Story");
                                mContext.startActivity(AddStoryIntent);
                                dialog.dismiss();

                            }
                        });

                        dialog.show();

                    }else{
                        Intent AddStoryIntent=new Intent(mContext, FilterActivity.class);
                        AddStoryIntent.putExtra("Activity", "Story");
                        mContext.startActivity(AddStoryIntent);
                    }

                }else{
                    if(count>0){
                        textView.setText("WATCH-ME!");
                        textView.setTextColor(Color.GREEN);
                        textView.setTextSize(12);
                        imageView.setVisibility(View.GONE);

                    }else{
                        textView.setText("ADD-STORY");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void SeenStory(final ViewHolder viewHolder , String UserId){
        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Story").child(UserId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if(!dataSnapshot1.child("views").child(FirebaseAuth.getInstance()
                            .getCurrentUser().getUid()).exists()
                            && System.currentTimeMillis() < dataSnapshot1.getValue(StoryModel.class).getTimeEnd() ){
                        i++;
                    }
                }
                if(i>0){
                    viewHolder.Story_Photo.setVisibility(View.VISIBLE);
                    viewHolder.Story_Seen.setVisibility(View.GONE);
                }else{
                    viewHolder.Story_Photo.setVisibility(View.GONE);
                    viewHolder.Story_Seen.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
