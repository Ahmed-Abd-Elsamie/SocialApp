package com.example.root.socialapp.ui.fragments;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.root.socialapp.R;
import com.example.root.socialapp.models.User;
import com.example.root.socialapp.adapters.UsersAdapter;
import com.example.root.socialapp.viewmodels.UsersViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {

    private int num_item = 8;
    private View view;
    private Activity context;
    private RecyclerView recyclerView;
    private ProgressBar pb;
    private UsersViewModel usersViewModel;
    private UsersAdapter adapter;

    public PeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people, container, false);
        context = getActivity();
        // init views
        recyclerView = (RecyclerView) view.findViewById(R.id.people_screen_recycler);
        pb = (ProgressBar) view.findViewById(R.id.pro);

        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        usersViewModel.init();
        usersViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> list) {
                adapter.notifyDataSetChanged();
            }
        });
        usersViewModel.getIsUpdating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean){
                    showProgressBar();
                }else {
                    hideProgressBar();
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
                    usersViewModel.loadMoreUsers(num_item);
                }

            }
        });


        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        adapter = new UsersAdapter(usersViewModel.getUsers().getValue(), context);
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

}