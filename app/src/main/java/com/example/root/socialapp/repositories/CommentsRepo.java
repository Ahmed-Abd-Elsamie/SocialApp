package com.example.root.socialapp.repositories;

import android.arch.lifecycle.MutableLiveData;

import com.example.root.socialapp.models.Comment;
import com.example.root.socialapp.models.Post;
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

public class CommentsRepo {

    private static CommentsRepo instance;
    private ArrayList<Comment> commentSet = new ArrayList<>();
    private DatabaseReference reference;
    private FirebaseAuth auth;


    public static CommentsRepo getInstance(){
        if (instance == null) {
            instance = new CommentsRepo();
        }
        return instance;
    }

    public MutableLiveData<List<Comment>> getComments(int num_items, String postId){
        getPostComment(num_items, postId);
        MutableLiveData<List<Comment>> data = new MutableLiveData<>();
        data.setValue(commentSet);
        return data;
    }

    private void getPostComment(int num_items, String postId){
        reference = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        final List<Comment> list = new ArrayList<>();
        Query query = reference.limitToLast(num_items);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    list.clear();
                    commentSet.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Comment comment = snapshot.getValue(Comment.class);
                        list.add(comment);
                    }
                    Collections.reverse(list);
                    commentSet.addAll(list);
                }else {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void postNewComment(String postId, Comment comment){
        reference = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        reference.push().setValue(comment);
    }

}