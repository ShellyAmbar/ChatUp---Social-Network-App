package com.shelly.ambar.chatup.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shelly.ambar.chatup.MainActivity;
import com.shelly.ambar.chatup.Models.CommentModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
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

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder>{

    private Context mContext;
    private List<CommentModel> mComments;
    private FirebaseUser firebaseUser;
    private String postId;

    public CommentAdapter(Context mContext, List<CommentModel> mComments,String postId) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.postId=postId;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =LayoutInflater.from(mContext).inflate(R.layout.comment_item,viewGroup,false);
        return new CommentAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, final int i) {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        final CommentModel commentModel=mComments.get(i);
        viewHolder.Comment.setText(commentModel.getComment());
        getUserInfo(viewHolder.Image_Profile,viewHolder.userName,commentModel.getPublisher() );



        viewHolder.Image_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ProfileActivity.class);
                intent.putExtra("publisherId",commentModel.getPublisher());
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(commentModel.getPublisher().equals(firebaseUser.getUid())){
                    AlertDialog alertDialog=new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete your comment?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference("Comments")
                                    .child(postId).child(commentModel.getCommentId())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(mContext, "Your comment was deleted successfully!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(mContext, "Error occurred, try to delete again please.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            dialog.dismiss();
                        }
                    });



                    alertDialog.show();
                }
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public ImageView Image_Profile;
        public TextView userName, Comment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            Image_Profile=itemView.findViewById(R.id.image_profile_comment);
            userName=itemView.findViewById(R.id.username);
            Comment=itemView.findViewById(R.id.commentText);
        }
    }
    private void getUserInfo(final ImageView imageView, final TextView username, String publisherId){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersDataModel usersDataModel=dataSnapshot.getValue(UsersDataModel.class);
                if(usersDataModel!=null){
                    if(usersDataModel.getThumb_Image().isEmpty()){
                        Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_portrait).into(imageView);
                    }else{
                        Glide.with(mContext).load(usersDataModel.getThumb_Image()).into(imageView);

                    }

                    username.setText(usersDataModel.getUserName());

                }else{

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
