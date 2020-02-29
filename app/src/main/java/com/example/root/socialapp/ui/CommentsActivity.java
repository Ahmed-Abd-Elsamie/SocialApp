package com.example.root.socialapp.ui;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.socialapp.MyData;
import com.example.root.socialapp.R;
import com.example.root.socialapp.adapters.CommentsAdapter;
import com.example.root.socialapp.adapters.PostAdapter;
import com.example.root.socialapp.models.Comment;
import com.example.root.socialapp.models.Post;
import com.example.root.socialapp.utils.comment_post_details;
import com.example.root.socialapp.viewmodels.CommentsViewModel;
import com.example.root.socialapp.viewmodels.HomeFragmentViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private ImageButton btnSendComment;
    private EditText txtComment;
    private CircleImageView Myimg;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ImageView postImg;
    private CircleImageView postUserImg;
    private TextView txtUserName;
    private TextView txtUserTitle;
    private TextView txtPostDate;
    private TextView txtPostDesc;
    private ImageButton btnShareMenu;
    private Button btnSharePost;
    private ImageButton btnBack;

    private RecyclerView recyclerView;
    private ProgressBar pb;
    private CommentsViewModel commentsViewModel;
    private CommentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // init views
        initViews();
        Intent intent = getIntent();
        final Post post = intent.getParcelableExtra("post");
        // Setting Post Data
        txtUserName.setText(post.getUser_name());
        txtUserTitle.setText("");
        txtPostDate.setText(post.getDate());
        txtPostDesc.setText(post.getPost_desc());
        // load Images
        Picasso.with(this).load(post.getUser_img()).into(Myimg);
        Picasso.with(this).load(post.getPost_img()).into(postImg);
        Picasso.with(this).load(post.getUser_img()).into(postUserImg);

        commentsViewModel = ViewModelProviders.of(this).get(CommentsViewModel.class);
        commentsViewModel.init(post.getId());

        commentsViewModel.getComments().observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(@Nullable List<Comment> list) {
                adapter.notifyDataSetChanged();
            }
        });

        commentsViewModel.getIsUpdating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean){
                    showProgressBar();
                }else {
                    hideProgressBar();
                    //recyclerView.smoothScrollToPosition(homeFragmentViewModel.getPosts().getValue().size() - 1);
                }
            }
        });

        initRecyclerView();

        btnShareMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(CommentsActivity.this , btnShareMenu);
                popupMenu.getMenuInflater().inflate(R.menu.share_menu , popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(CommentsActivity.this, "" + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popupMenu.show();

            }
        });

        btnSharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(CommentsActivity.this , btnShareMenu);
                popupMenu.getMenuInflater().inflate(R.menu.share_menu , popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(CommentsActivity.this, "" + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(txtComment.getText().toString())){
                    Comment comment = new Comment(post.getUser_name(),
                            post.getUser_img(),
                            getDate(),
                            txtComment.getText().toString());
                    commentsViewModel.NewComment(4, comment, post.getId());
                    txtComment.setText("");
                }


            }
        });
        // init firebase
        /*
        reference = FirebaseDatabase.getInstance().getReference().child("comments").child(post.getId());

        btnSendCommint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(txtComment.getText().toString())){

                    DatabaseReference r = reference.push();

                    r.child("name").setValue(MyData.name);
                    r.child("img").setValue(MyData.img);
                    r.child("date").setValue(GetDate());
                    r.child("comment").setValue(txtComment.getText().toString());

                    txtComment.setText("");



                }

            }
        });


        // Clicking on Post Image


        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(CommentsActivity.this);
                LayoutInflater inflater =  getLayoutInflater();
                View v = inflater.inflate(R.layout.dialog_img , null);
                ImageView img = (ImageView) v.findViewById(R.id.img_view);
                dialog.setContentView(v);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                Picasso.with(CommentsActivity.this).load(comment_post_details.postimg).into(img);
            }
        });


         */
/*
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(CommentsActivity.this);
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
        */

        // Getting Comments
        //GetAllComments();
    }

    private void initRecyclerView() {
        adapter = new CommentsAdapter(commentsViewModel.getComments().getValue(), this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initViews() {
        btnSendComment = (ImageButton) findViewById(R.id.btn_comment_send);
        txtComment = (EditText) findViewById(R.id.txt_comment_add);
        Myimg = (CircleImageView) findViewById(R.id.my_img_comment);
        recyclerView = (RecyclerView) findViewById(R.id.comments_recycler);
        postImg = (ImageView) findViewById(R.id.post_img_comment_post);
        postUserImg = (CircleImageView) findViewById(R.id.user_img_comment_post);
        txtUserName = (TextView) findViewById(R.id.user_name_comment_post);
        txtUserTitle = (TextView) findViewById(R.id.user_title_comment_post);
        txtPostDate = (TextView) findViewById(R.id.post_date_comment_post);
        txtPostDesc = (TextView) findViewById(R.id.post_desc_comment_post);
        btnShareMenu = (ImageButton) findViewById(R.id.btn_share_menu);
        btnBack = (ImageButton) findViewById(R.id.btn_back_menu);
        btnSharePost = (Button) findViewById(R.id.btn_share);
        pb = findViewById(R.id.pro);
    }


    private void GetAllComments(){
        /*final List<Comment> list = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Comment comment = snapshot.getValue(Comment.class);
                        list.add(comment);
                    }
                    Collections.reverse(list);
                    mAdapter = new CommentsAdapter(list , CommentsActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                }else {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

         */

    }

    private String getDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        String full_date = day + "/" + month + "/" + year + "   at " + hour + ":" + minutes;
        return full_date;
    }

    private void showProgressBar() {
        pb.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        pb.setVisibility(View.GONE);
    }
}
