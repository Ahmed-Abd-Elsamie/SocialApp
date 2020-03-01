package com.example.root.socialapp.ui.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.root.socialapp.R;
import com.example.root.socialapp.adapters.NotificationAdapter;
import com.example.root.socialapp.models.Notification_item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


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





    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notification, container, false);
        context = getActivity();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();
        referenceUsers = FirebaseDatabase.getInstance().getReference().child("users");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.notification_screen_recycler);


        keys = new ArrayList<>();


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
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






        GetAllNotifications();




        return view;
    }




    private void GetAllNotifications(){



        final List<Notification_item> list = new ArrayList<>();

        Query query = referenceUsers.limitToFirst(num_item);


        referenceUsers.child(uid).child("myrequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Notification_item not = new Notification_item();

                        /*not.setName(snapshot.child("name").getValue().toString());
                        not.setImg(snapshot.child("img").getValue().toString());
                        not.setId(snapshot.child("id").getValue().toString());
                        */
                        not.setId(snapshot.child("id").getValue().toString());

                        //Toast.makeText(context , snapshot.getValue().toString() , Toast.LENGTH_SHORT).show();

                        keys.add(snapshot.getKey().toString());
                        list.add(not);

                    }


                    mAdapter = new NotificationAdapter(list , context);

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
