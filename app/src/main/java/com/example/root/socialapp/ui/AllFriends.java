package com.example.root.socialapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.root.socialapp.MyData;
import com.example.root.socialapp.R;
import com.example.root.socialapp.adapters.FriendsAdapter;
import com.example.root.socialapp.models.User;
import com.example.root.socialapp.repositories.UsersRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllFriends extends AppCompatActivity {


    private View view;
    private DatabaseReference referenceUsers;
    private String uid;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private RecyclerView.LayoutManager mLayoutManager;
    private Activity context;
    private int num_items = 8;
    public static List<String> keys;
    private RecyclerView.Adapter mAdapter;
    private UsersRepo usersRepo;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friends);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        usersRepo = UsersRepo.getInstance();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();
        referenceUsers = FirebaseDatabase.getInstance().getReference().child("users");

        mRecyclerView = (RecyclerView)findViewById(R.id.friends_screen_recycler);

        keys = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(AllFriends.this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!mRecyclerView.canScrollVertically(1)){
                    num_items = num_items + 1;
                    mRecyclerView.refreshDrawableState();
                    usersRepo.getAllFriends(num_items, user,AllFriends.this, mRecyclerView);
                }
            }
        });

         */

        usersRepo.getAllFriends(num_items, user,AllFriends.this, mRecyclerView);

        //GetAllFriends(num_items);

    }

    private void GetAllFriends(int num_items){
        final List<User> list = new ArrayList<>();
        Query query = referenceUsers.limitToFirst(num_items);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.child(user.getId()).child("friends").getChildren()){
                        User user = snapshot.getValue(User.class);
                        list.add(user);
                    }
                    mAdapter = new FriendsAdapter(list , AllFriends.this);
                    mRecyclerView.setAdapter(mAdapter);

                }else {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}