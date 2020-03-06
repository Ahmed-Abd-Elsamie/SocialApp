package com.example.root.socialapp.ui.fragments;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.root.socialapp.MainActivity;
import com.example.root.socialapp.R;
import com.example.root.socialapp.models.User;
import com.example.root.socialapp.ui.Login;
import com.example.root.socialapp.ui.NewPostActivity;
import com.example.root.socialapp.models.Post;
import com.example.root.socialapp.adapters.PostAdapter;
import com.example.root.socialapp.viewmodels.HomeFragmentViewModel;

import com.github.clans.fab.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private int num_item = 4;
    private View view;
    private Activity context;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private SwipyRefreshLayout swipyRefreshLayout;
    public static boolean fabState = true;

    private RecyclerView recyclerView;
    private ProgressBar pb;
    private HomeFragmentViewModel homeFragmentViewModel;
    private PostAdapter adapter;
    private User myData;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private DatabaseReference reference;


    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();

        // init views
        recyclerView = (RecyclerView) view.findViewById(R.id.main_screen_recycler);
        pb = (ProgressBar) view.findViewById(R.id.pro);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.refresh);
        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton)view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)view.findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(context, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(context,R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(context,R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(context,R.anim.rotate_backword);


        homeFragmentViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel.class);
        homeFragmentViewModel.init();

        homeFragmentViewModel.getPosts().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> list) {
                adapter.notifyDataSetChanged();
            }
        });

        homeFragmentViewModel.getIsUpdating().observe(this, new Observer<Boolean>() {
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
        ///////////////////
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (!recyclerView.canScrollVertically(1)){
                    num_item = num_item + 1;
                    recyclerView.refreshDrawableState();
                    homeFragmentViewModel.loadMorePosts(num_item);
                }

                if (dy < 0 && !fab.isShown()){ // scroll up
                    fab.show(true);
                    fabState = true;
                }

                else if(dy > 0 && fab.isShown()){ // scroll down
                    fab.hide(true);
                    fab1.hide(true);
                    fab2.hide(true);
                    fabState = false;
                }

            }
        });

        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                num_item = num_item + 1;
                recyclerView.refreshDrawableState();
                homeFragmentViewModel.loadMorePosts(num_item);
                //recyclerView.smoothScrollToPosition(num_item);
                swipyRefreshLayout.setRefreshing(false);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // new Post
                Intent intent = new Intent(context, NewPostActivity.class);
                intent.putExtra("user", myData);
                startActivity(intent);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "SOON !", Toast.LENGTH_SHORT).show();
            }
        });

        fab.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_anim));
        // Hide Keyboard
        hideKeyboard(context);
        return view;
    }

    public void animateFAB(){

        if(isFabOpen){
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void initRecyclerView() {
        adapter = new PostAdapter(homeFragmentViewModel.getPosts().getValue(), context);
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