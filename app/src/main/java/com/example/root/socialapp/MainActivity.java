package com.example.root.socialapp;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.example.root.socialapp.models.User;
import com.example.root.socialapp.repositories.Repo;
import com.example.root.socialapp.ui.Login;
import com.example.root.socialapp.ui.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnHome;
    private ImageButton btnPeople;
    private ImageButton btnMessaging;
    private ImageButton btnNotifications;
    private Button btnJob;
    private ViewPager MainPager;
    private PagerAdapter mPagerViewAdapter;
    private CircleImageView btnProfile;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private String uid;
    private FirebaseUser user;
    private String MyIMG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OneSignal.startInit(this).init();


        // init Views

        btnHome = (ImageButton) findViewById(R.id.btn_home);
        btnPeople = (ImageButton) findViewById(R.id.btn_people);
        btnMessaging = (ImageButton) findViewById(R.id.btn_messaging);
        btnNotifications = (ImageButton) findViewById(R.id.btn_notifications);
        //btnJob = (Button) findViewById(R.id.btn_job);
        btnProfile = (CircleImageView) findViewById(R.id.btn_profile);
        MainPager = (ViewPager)findViewById(R.id.main_pager);


        // init firebase

        mAuth = FirebaseAuth.getInstance();
        mPagerViewAdapter = new PagerAdapter(getSupportFragmentManager());
        MainPager.setAdapter(mPagerViewAdapter);
        MainPager.setPageTransformer(true , new CubeOutTransformer());

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Repo repo = new Repo();
                User myData = repo.getMyData();
                Intent intent = new Intent(MainActivity.this, UserProfile.class);
                intent.putExtra("user", myData);
                startActivity(intent);
                MyData.userProfileIMG = MyData.img;
                MyData.UserProfileID = MyData.myid;
                //startActivity(new Intent(MainActivity.this , UserProfile.class));
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainPager.setCurrentItem(0);
            }
        });

        btnPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainPager.setCurrentItem(1);
            }
        });

        btnMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainPager.setCurrentItem(2);
            }
        });

        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainPager.setCurrentItem(3);
            }
        });

        MainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                changeTaps(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void changeTaps(int position) {

        if(position == 0){
            btnHome.setBackgroundResource(R.drawable.ic_home_click);
            btnPeople.setBackgroundResource(R.drawable.ic_people);
            btnMessaging.setBackgroundResource(R.drawable.ic_messaging);
            btnNotifications.setBackgroundResource(R.drawable.ic_notification);
        }

        if(position == 1){
            btnHome.setBackgroundResource(R.drawable.ic_home);
            btnPeople.setBackgroundResource(R.drawable.ic_people_click);
            btnMessaging.setBackgroundResource(R.drawable.ic_messaging);
            btnNotifications.setBackgroundResource(R.drawable.ic_notification);
        }

        if(position == 2){
            btnHome.setBackgroundResource(R.drawable.ic_home);
            btnPeople.setBackgroundResource(R.drawable.ic_people);
            btnMessaging.setBackgroundResource(R.drawable.ic_messaging_click);
            btnNotifications.setBackgroundResource(R.drawable.ic_notification);
        }
        if(position == 3){
            btnHome.setBackgroundResource(R.drawable.ic_home);
            btnPeople.setBackgroundResource(R.drawable.ic_people);
            btnMessaging.setBackgroundResource(R.drawable.ic_messaging);
            btnNotifications.setBackgroundResource(R.drawable.ic_notification_click);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        user = mAuth.getCurrentUser();

        if(user == null){

            startActivity(new Intent(MainActivity.this , Login.class));
            finish();

        }else {

            uid = mAuth.getCurrentUser().getUid().toString();
            reference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    MyData.img = dataSnapshot.child("img").getValue().toString();
                    MyData.name = dataSnapshot.child("name").getValue().toString();
                    MyData.myid = dataSnapshot.child("id").getValue().toString();
                    MyData.email = dataSnapshot.child("email").getValue().toString();

                    MyIMG = dataSnapshot.child("img").getValue().toString();
                    Picasso.with(MainActivity.this).load(MyIMG).into(btnProfile);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            OneSignal.sendTag("User_ID", uid);
        }

    }

}