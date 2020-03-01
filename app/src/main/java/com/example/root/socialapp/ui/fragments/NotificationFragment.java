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
import com.example.root.socialapp.adapters.NotificationAdapter;
import com.example.root.socialapp.models.Notification_item;
import com.example.root.socialapp.viewmodels.NotificationsViewModel;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private int num_item = 8;
    private View view;
    private Activity context;
    private RecyclerView recyclerView;
    private ProgressBar pb;
    private NotificationsViewModel notificationsViewModel;
    private NotificationAdapter adapter;


    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        context = getActivity();
        // init views
        recyclerView = (RecyclerView) view.findViewById(R.id.notification_screen_recycler);
        pb = (ProgressBar) view.findViewById(R.id.pro);

        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        notificationsViewModel.init();
        notificationsViewModel.getNotification().observe(this, new Observer<List<Notification_item>>() {
            @Override
            public void onChanged(@Nullable List<Notification_item> notification_items) {
                adapter.notifyDataSetChanged();
            }
        });

        notificationsViewModel.getIsUpdating().observe(this, new Observer<Boolean>() {
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
                    notificationsViewModel.loadMoreNotifications(num_item);
                }

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
}