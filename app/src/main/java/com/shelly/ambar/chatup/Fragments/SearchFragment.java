package com.shelly.ambar.chatup.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.shelly.ambar.chatup.Adapters.UserAdapter;
import com.shelly.ambar.chatup.Models.UsersDataModel;
import com.shelly.ambar.chatup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    List<UsersDataModel> mUsers;
    EditText search_bar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView=view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_bar=view.findViewById(R.id.search_bar);
        mUsers=new ArrayList<>();
        userAdapter=new UserAdapter(getContext(),mUsers);
        recyclerView.setAdapter(userAdapter);

        if(FirebaseAuth.getInstance()==null){
            getFragmentManager().beginTransaction().remove(this).commit();
        }

        readUsers();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private void searchUsers(String s){

        List<UsersDataModel> usersDataModels = new ArrayList<>();
        for(UsersDataModel usersDataModel : mUsers){
            if(usersDataModel.getUserName().toLowerCase().contains(s)){
                usersDataModels.add(usersDataModel);
            }
        }

        userAdapter.GetOrderesSearchResult(usersDataModels);


    }

    private void readUsers(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(search_bar.getText().toString().equals("")){
                    mUsers.clear();
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        UsersDataModel user=dataSnapshot1.getValue(UsersDataModel.class);
                        mUsers.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
