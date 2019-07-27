package com.shelly.ambar.chatup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shelly.ambar.chatup.Models.ChatMessageModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.shelly.ambar.chatup.PitchSpeachActivity;
import com.shelly.ambar.chatup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MassageAdapter extends RecyclerView.Adapter<MassageAdapter.massageViewHolder> {

    private List<ChatMessageModel> chatMessageModelList;
    private Context mContext;

    public MassageAdapter(Context context, List<ChatMessageModel> chatMessageModelList) {
        this.chatMessageModelList = chatMessageModelList;
        mContext=context;
    }

    @NonNull
    @Override
    public massageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lv_item,viewGroup,false);

        return new MassageAdapter.massageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final massageViewHolder massageViewHolder, int i) {
        String currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        final ChatMessageModel chatMessageModel=chatMessageModelList.get(i);
        final String FromId=chatMessageModel.getFrom();
        String massageType=chatMessageModel.getType();
        String time=chatMessageModel.getUser_Time();
        String massageBody=chatMessageModel.getUser_Message();
        String imageView=chatMessageModel.getUserPhoto();

        massageViewHolder.Time.setText(time);
        massageViewHolder.Time2.setText(time);

        if(chatMessageModel.getType().equals("text")) {
            massageViewHolder.recieverTextView.setVisibility(View.GONE);
            massageViewHolder.recieverPhoto.setVisibility(View.GONE);
            massageViewHolder.Time.setVisibility(View.GONE);
            massageViewHolder.Time2.setVisibility(View.GONE);



            if (FromId.equals(currentUserId)) {

                massageViewHolder.Time.setVisibility(View.GONE);
                massageViewHolder.Time2.setVisibility(View.VISIBLE);
                massageViewHolder.senderPhoto.setVisibility(View.VISIBLE);
                massageViewHolder.senderTextView.setVisibility(View.VISIBLE);
                massageViewHolder.senderTextView.setText(massageBody);
                massageViewHolder.recieverPhoto.setVisibility(View.GONE);
                massageViewHolder.recieverTextView.setVisibility(View.GONE);
                massageViewHolder.imageViewPerson2.setVisibility(View.GONE);
                massageViewHolder.imageViewPerson1.setVisibility(View.GONE);

                DatabaseReference reference=FirebaseDatabase.getInstance()
                        .getReference("Users").child(currentUserId);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot!=null){
                            UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);

                            if(usersDataModel.getThumb_Image().isEmpty()){
                                Glide.with(mContext)
                                        .load(R.drawable.com_facebook_profile_picture_blank_portrait)
                                        .into(massageViewHolder.senderPhoto);

                            }else{
                                Glide.with(mContext)
                                        .load(usersDataModel.getThumb_Image())
                                        .into(massageViewHolder.senderPhoto);
                            }



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            } else {

                massageViewHolder.Time.setVisibility(View.VISIBLE);
                massageViewHolder.Time2.setVisibility(View.GONE);
                massageViewHolder.senderPhoto.setVisibility(View.GONE);
                massageViewHolder.recieverPhoto.setVisibility(View.VISIBLE);

                massageViewHolder.recieverTextView.setText(massageBody);
                massageViewHolder.recieverTextView.setVisibility(View.VISIBLE);
                massageViewHolder.senderTextView.setVisibility(View.GONE);
                massageViewHolder.imageViewPerson2.setVisibility(View.GONE);
                massageViewHolder.imageViewPerson1.setVisibility(View.GONE);

                DatabaseReference reference=FirebaseDatabase.getInstance()
                        .getReference("Users").child(FromId);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){

                            UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                            if(usersDataModel.getThumb_Image().isEmpty()){

                                Glide.with(massageViewHolder.recieverPhoto.getContext())
                                        .load(R.drawable.com_facebook_profile_picture_blank_portrait)
                                        .into(massageViewHolder.recieverPhoto);
                            }else{
                                Glide.with(massageViewHolder.recieverPhoto.getContext())
                                        .load(usersDataModel.getThumb_Image())
                                        .into(massageViewHolder.recieverPhoto);
                            }






                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        }else if(chatMessageModel.getType().equals("photo")){

            massageViewHolder.recieverTextView.setVisibility(View.GONE);
            massageViewHolder.recieverPhoto.setVisibility(View.GONE);
            massageViewHolder.Time.setVisibility(View.GONE);
            massageViewHolder.Time2.setVisibility(View.GONE);



            if (FromId.equals(currentUserId)) {

                massageViewHolder.Time.setVisibility(View.GONE);
                massageViewHolder.Time2.setVisibility(View.VISIBLE);
                massageViewHolder.senderPhoto.setVisibility(View.VISIBLE);
                massageViewHolder.senderTextView.setVisibility(View.GONE);
                massageViewHolder.imageViewPerson1.setVisibility(View.VISIBLE);
                massageViewHolder.imageViewPerson2.setVisibility(View.GONE);
                massageViewHolder.senderTextView.setText(massageBody);

                massageViewHolder.recieverPhoto.setVisibility(View.GONE);
                massageViewHolder.recieverTextView.setVisibility(View.GONE);
                massageViewHolder.imageViewPerson2.setVisibility(View.GONE);

                Glide.with(mContext).load(imageView).placeholder(R.drawable.blank_portrait)
                        .into(massageViewHolder.imageViewPerson1);

                imageView=null;

                DatabaseReference reference=FirebaseDatabase.getInstance()
                        .getReference("Users").child(currentUserId);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
                            UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);

                            if(usersDataModel.getThumb_Image().isEmpty()){
                                Glide.with(mContext)
                                        .load(R.drawable.com_facebook_profile_picture_blank_portrait)
                                        .into(massageViewHolder.senderPhoto);

                            }else{
                                Glide.with(mContext)
                                        .load(usersDataModel.getThumb_Image())
                                        .into(massageViewHolder.senderPhoto);
                            }



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            } else {

                massageViewHolder.Time.setVisibility(View.VISIBLE);
                massageViewHolder.Time2.setVisibility(View.GONE);
                massageViewHolder.senderPhoto.setVisibility(View.GONE);
                massageViewHolder.recieverPhoto.setVisibility(View.VISIBLE);
                massageViewHolder.imageViewPerson1.setVisibility(View.GONE);
                massageViewHolder.imageViewPerson2.setVisibility(View.VISIBLE);
                massageViewHolder.recieverTextView.setText(massageBody);
                massageViewHolder.recieverTextView.setVisibility(View.GONE);
                massageViewHolder.senderTextView.setVisibility(View.GONE);


                Glide.with(mContext).load(imageView).placeholder(R.drawable.blank_portrait)
                        .into(massageViewHolder.imageViewPerson2);

                imageView=null;

                DatabaseReference reference=FirebaseDatabase.getInstance()
                        .getReference("Users").child(FromId);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){

                            UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                            if(usersDataModel.getThumb_Image().isEmpty()){

                                Glide.with(massageViewHolder.recieverPhoto.getContext())
                                        .load(R.drawable.com_facebook_profile_picture_blank_portrait)
                                        .into(massageViewHolder.recieverPhoto);
                            }else{
                                Glide.with(massageViewHolder.recieverPhoto.getContext())
                                        .load(usersDataModel.getThumb_Image())
                                        .into(massageViewHolder.recieverPhoto);
                            }



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


        }


        massageViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(chatMessageModel.getType().equals("text")){
                    Intent intent=new Intent(massageViewHolder.itemView.getContext(),PitchSpeachActivity.class);
                    intent.putExtra("text",chatMessageModel.getUser_Message());
                    massageViewHolder.itemView.getContext().startActivity(intent);
                }

                return true;
            }
        });







    }

    @Override
    public int getItemCount() {
        return chatMessageModelList.size();
    }

    public class massageViewHolder extends RecyclerView.ViewHolder {

        private TextView senderTextView,recieverTextView;
        private TextView Time,Time2;
        private CircleImageView senderPhoto,recieverPhoto;
        private ImageView imageViewPerson1,imageViewPerson2;




        public massageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView=itemView.findViewById(R.id.MessageTextSender);
            recieverTextView=itemView.findViewById(R.id.MessageTextReciever);
            Time=itemView.findViewById(R.id.MessageTime);
            Time2=itemView.findViewById(R.id.MessageTime2);
            senderPhoto=itemView.findViewById(R.id.user_photo);
            recieverPhoto=itemView.findViewById(R.id.user_photo2);
            imageViewPerson1=itemView.findViewById(R.id.imageViewPerson1);
            imageViewPerson2=itemView.findViewById(R.id.imageViewPerson2);
        }
    }
}
