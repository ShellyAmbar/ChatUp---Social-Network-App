package com.shelly.ambar.chatup.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shelly.ambar.chatup.Adapters.NotificationAdapter;
import com.shelly.ambar.chatup.Models.NotificationModel;
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
import java.util.List;

public class NotificationFragment extends Fragment {
    private List<NotificationModel> notificationModelList;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationModelList=new ArrayList<>();
        notificationAdapter=new NotificationAdapter(getContext(),notificationModelList);
        recyclerView.setAdapter(notificationAdapter);

        if(FirebaseAuth.getInstance()==null){
            getFragmentManager().beginTransaction().remove(this).commit();
        }

        readNotifications();

        return view;

    }

    private void readNotifications() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Notifications").child(user.getUid());

        // DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationModelList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    NotificationModel notificationModel=snapshot.getValue(NotificationModel.class);

                    notificationModelList.add(notificationModel);

                }
                Collections.reverse(notificationModelList);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
