package com.iblog.root.socialapp.repositories;

import android.arch.lifecycle.MutableLiveData;

import com.iblog.root.socialapp.models.Notification_item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsRepo {

    private static NotificationsRepo instance;
    private ArrayList<Notification_item> dataSet = new ArrayList<>();
    private DatabaseReference reference;
    private FirebaseAuth auth;


    public static NotificationsRepo getInstance(){
        if (instance == null){
            instance = new NotificationsRepo();
        }
        return instance;
    }

    public MutableLiveData<List<Notification_item>> getNotifications(int num_items){
        getAllNotifications(num_items);
        MutableLiveData<List<Notification_item>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    private void getAllNotifications(int num_items) {
        auth =  FirebaseAuth.getInstance();
        String id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        final List<Notification_item> list = new ArrayList<>();
        Query query = reference.child(id).child("requests").child("coming").limitToLast(num_items);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    list.clear();
                    dataSet.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final Notification_item notification_item = new Notification_item();
                        final String id = snapshot.getKey();
                        reference.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                notification_item.setId(id);
                                notification_item.setName(dataSnapshot.child("name").getValue().toString());
                                notification_item.setImg(dataSnapshot.child("img").getValue().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        list.add(notification_item);
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

}
