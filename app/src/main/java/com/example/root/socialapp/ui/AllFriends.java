package com.example.root.socialapp.ui;

import android.app.Activity;
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
    private int num_item = 2;
    public static List<String> keys;
    private RecyclerView.Adapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friends);


        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();
        referenceUsers = FirebaseDatabase.getInstance().getReference().child("users");

        mRecyclerView = (RecyclerView)findViewById(R.id.friends_screen_recycler);


        keys = new ArrayList<>();


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(AllFriends.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {



            }
        });


        GetAllUsers();


    }








    private void GetAllUsers(){


        final List<User> list = new ArrayList<>();

        Query query = referenceUsers.limitToFirst(num_item);


        referenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChildren()){

                    String listfriendsid;
                    list.clear();

                    if (MyData.UserProfileID.equals(uid)){
                        listfriendsid = uid;
                    }else {
                        listfriendsid = MyData.UserProfileID;
                    }

                    for (DataSnapshot snapshot : dataSnapshot.child(listfriendsid).child("friends").getChildren()){

                        User user = new User();


                        // Getting user data

                        String id = snapshot.child("id").getValue().toString();

                        DataSnapshot s = dataSnapshot.child(id);

                        user.setName(s.child("name").getValue().toString());
                        user.setImg(s.child("img").getValue().toString());
                        user.setId(s.child("id").getValue().toString());

                        keys.add(snapshot.getKey().toString());
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
