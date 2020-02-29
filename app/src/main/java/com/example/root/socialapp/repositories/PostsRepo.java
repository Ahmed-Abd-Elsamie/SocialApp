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

public class PostsRepo {

    private static PostsRepo instance;
    private ArrayList<Post> dataSet = new ArrayList<>();
    private DatabaseReference reference;
    public static List<String> keys = new ArrayList<>();
    private FirebaseAuth auth;


    public static PostsRepo getInstance(){
        if (instance == null) {
            instance = new PostsRepo();
        }
        return instance;
    }

    public MutableLiveData<List<Post>> getPosts(int num_items){
        getMyPosts(num_items);
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    private void getMyPosts(int num_items) {
        auth = FirebaseAuth.getInstance();
        final String id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("posts");
        final List<Post> list = new ArrayList<>();
        Query query = reference.limitToLast(num_items);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    list.clear();
                    keys.clear();
                    dataSet.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Post post = new Post();
                        post.setUser_name(snapshot.child("name").getValue().toString());
                        post.setPost_img(snapshot.child("postImg").getValue().toString());
                        post.setPost_desc(snapshot.child("desc").getValue().toString());
                        post.setUser_img(snapshot.child("userImg").getValue().toString());
                        post.setDate(snapshot.child("date").getValue().toString());
                        post.setLikes_num(snapshot.child("likes").child("num").getValue().toString());
                        post.setId(snapshot.getKey());
                        post.setUid(snapshot.child("uid").getValue().toString());
                        if (snapshot.child("likes").child("actors").hasChild(id)){
                            post.setLiked(true);
                        }else {
                            post.setLiked(false);
                        }
                        keys.add(snapshot.getKey());
                        list.add(post);
                    }

                    Collections.reverse(list);
                    Collections.reverse(keys);
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