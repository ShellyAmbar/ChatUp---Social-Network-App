package com.shelly.ambar.chatup.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shelly.ambar.chatup.Models.ChatMessageModel;
import com.shelly.ambar.chatup.Models.ChatModel;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.shelly.ambar.chatup.R;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CardsAdapter extends ArrayAdapter<UsersDataModel> {

    private Context context;
    private FirebaseUser firebaseUser;
    private boolean isChatExist;
    private String lastMassage;
    private String currentUserName ="";
    private String currentUserPhoto="";


    public CardsAdapter(Context context, int resource, List<UsersDataModel> items) {
        super(context, resource, items);
        this.context=context;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        UsersDataModel cardItem=getItem(position);



        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView helloText=(TextView)convertView.findViewById(R.id.helloText);
        ImageView user_image=(ImageView)convertView.findViewById(R.id.user_image);
        TextView text_about=convertView.findViewById(R.id.text_about);
        TextView yesBTN=convertView.findViewById(R.id.yes);
        TextView notBTN=convertView.findViewById(R.id.not);


        if(cardItem!=null){
            if(!cardItem.getUserStatus().isEmpty()){
                text_about.setText(cardItem.getUserStatus());
            }else{

                text_about.setText("Hello everyone! ");
            }

            if(cardItem.getMyEducation().isEmpty()){
                helloText.setText(cardItem.getUserName() + " "+ cardItem.getUserAge()+" "
                        + cardItem.getUserCity()   );
            }else{
                helloText.setText(cardItem.getUserName() +" "+ cardItem.getUserAge()+" "
                        + cardItem.getUserCity() +", "+cardItem.getMyEducation()  );
            }

            if(cardItem.getThumb_Image().isEmpty()){
                Glide.with(context).load(R.drawable.com_facebook_profile_picture_blank_portrait).into(user_image);
            }else{
                Glide.with(context).load(cardItem.getThumb_Image())
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(user_image);
            }

            yesBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(context, "Yes? scroll In", Toast.LENGTH_SHORT).show();
                }
            });

            notBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(context, "No? scroll Out", Toast.LENGTH_SHORT).show();

                }
            });

        }



        return convertView;



    }


}
