package com.iblog.root.socialapp.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.iblog.root.socialapp.R;
import com.iblog.root.socialapp.adapters.NotificationAdapter;
import com.iblog.root.socialapp.models.User;
import com.iblog.root.socialapp.ui.AllFriends;
import com.iblog.root.socialapp.viewmodels.NotificationsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagingFragment extends Fragment {

    private int num_item = 8;
    private View view;
    private Activity context;
    private RecyclerView recyclerView;
    private ProgressBar pb;
    private NotificationsViewModel notificationsViewModel;
    private NotificationAdapter adapter;
    private Button btnAllFriends;
    private User myData;
    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    public MessagingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messaging, container, false);
        context = getActivity();
        // init views
        recyclerView = (RecyclerView) view.findViewById(R.id.messaging_screen_recycler);
        pb = (ProgressBar) view.findViewById(R.id.pro);
        btnAllFriends = view.findViewById(R.id.btn_all_friends);

        btnAllFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendsIntent = new Intent(context , AllFriends.class);
                friendsIntent.putExtra("user", myData);
                startActivity(friendsIntent);
            }
        });


        return view;
    }

    private void initRecyclerView() {
        adapter = new NotificationAdapter(notificationsViewModel.getNotification().getValue(), context);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void showProgressBar() {
        pb.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        pb.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null){

        }else {
            myData = new User();
            String uid = auth.getCurrentUser().getUid();
            reference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myData = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
}
