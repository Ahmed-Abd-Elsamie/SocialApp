package com.iblog.root.socialapp.repositories;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.iblog.root.socialapp.adapters.FriendsAdapter;
import com.iblog.root.socialapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsersRepo {

    private static UsersRepo instance;
    private ArrayList<User> dataSet = new ArrayList<>();
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FriendsAdapter adapter;


    public static UsersRepo getInstance(){
        if (instance == null) {
            instance = new UsersRepo();
        }
        return instance;
    }

    public MutableLiveData<List<User>> getUsers(int num_items){
        getAllUsers(num_items);
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    private void getAllUsers(int num_items) {
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        auth = FirebaseAuth.getInstance();
        final String id = auth.getCurrentUser().getUid();
        final List<User> list = new ArrayList<>();
        Query query = reference.limitToLast(num_items);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    list.clear();
                    dataSet.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        if (user.getId().equals(id) || dataSnapshot.child(id).child("friends").hasChild(user.getId())){
                            continue;
                        }
                        list.add(user);
                    }
                    Collections.reverse(list);
                    dataSet.addAll(list);
                }else {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean isFriend(final User user) {
        auth = FirebaseAuth.getInstance();
        String id = auth.getCurrentUser().getUid();
        final boolean[] friend = {false};
        reference.child(id).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("id").getValue().equals(user.getId())){
                    friend[0] = true;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return friend[0];
    }

    public void sendConnectRequest(User user){
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        auth = FirebaseAuth.getInstance();
        final String id = auth.getCurrentUser().getUid();
        reference.child(id).child("requests").child("mine").child(user.getId()).setValue("sent");
        reference.child(user.getId()).child("requests").child("coming").child(id).setValue("received");
    }

    public void  checkRequestState(final User user, final Button btnConnect){
        auth = FirebaseAuth.getInstance();
        final String id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).child("requests").child("mine").hasChild(user.getId())){
                    btnConnect.setText("Request Pending...");
                    btnConnect.setEnabled(false);
                }else if(dataSnapshot.child(id).child("requests").child("coming").hasChild(user.getId())){
                    btnConnect.setText("Accept");
                    btnConnect.setEnabled(false);
                }else {
                    btnConnect.setText("Connect");
                    btnConnect.setEnabled(true);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getAllFriends(int num_items, final User user, final Activity context, final RecyclerView recyclerView){
        final List<User> listFriends = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.limitToFirst(num_items);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listFriends.clear();
                for (DataSnapshot snapshot : dataSnapshot.child(user.getId()).child("friends").getChildren()){
                    User friend = snapshot.getValue(User.class);
                    listFriends.add(friend);
                }
                adapter = new FriendsAdapter(listFriends , context);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}