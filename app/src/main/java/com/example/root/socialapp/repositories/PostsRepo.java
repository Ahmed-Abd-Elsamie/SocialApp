package com.example.root.socialapp.repositories;

import android.arch.lifecycle.MutableLiveData;

import com.example.root.socialapp.models.Comment;
import com.example.root.socialapp.models.Post;
import com.example.root.socialapp.models.User;
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
                        if (snapshot.child("likes").hasChild("num")){
                            post.setLikes_num(snapshot.child("likes").child("num").getValue().toString());
                        }else {
                            post.setLikes_num("0");
                        }
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

    public void sendPostLike(final Post post){
        reference = FirebaseDatabase.getInstance().getReference().child("posts").child(post.getId());
        auth = FirebaseAuth.getInstance();
        final String id = auth.getCurrentUser().getUid();
        reference.child("likes").child("actors").child(id).setValue("like");
        final boolean[] inc = {true};
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (inc[0]){
                    String num = "0";
                    if (dataSnapshot.child("likes").hasChild("num")){
                        num = dataSnapshot.child("likes").child("num").getValue().toString();
                    }
                    incrementLikes(num, post.getId());
                    inc[0] = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendPostDisLike(final Post post){
        reference = FirebaseDatabase.getInstance().getReference().child("posts").child(post.getId());
        auth = FirebaseAuth.getInstance();
        final String id = auth.getCurrentUser().getUid();
        reference.child("likes").child("actors").child(id).removeValue();
        final boolean[] inc = {true};
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (inc[0]){
                    String num = dataSnapshot.child("likes").child("num").getValue().toString();
                    decrementLikes(num, post.getId());
                    inc[0] = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void incrementLikes(String likes, String postId) {
        int num = Integer.parseInt(likes) + 1;
        reference = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
        reference.child("num").setValue(num + "");
    }

    private void decrementLikes(String likes, String postId) {
        int num = Integer.parseInt(likes) - 1;
        reference = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
        reference.child("num").setValue(num + "");
    }

}