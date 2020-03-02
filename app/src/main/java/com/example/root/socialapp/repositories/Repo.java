package com.example.root.socialapp.repositories;

import com.example.root.socialapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Repo {

    private DatabaseReference reference;
    private FirebaseAuth auth;
    private String id;
    private User user;

    public Repo(){
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        auth = FirebaseAuth.getInstance();
        id = auth.getCurrentUser().getUid();
        fillMyData();
    }


    public void fillMyData(){
        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = new User(
                        id,
                        dataSnapshot.child("name").getValue().toString(),
                        dataSnapshot.child("email").getValue().toString(),
                        dataSnapshot.child("img").getValue().toString(),
                        dataSnapshot.child("token_id").getValue().toString()
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public User getMyData(){
        return user;
    }
}
