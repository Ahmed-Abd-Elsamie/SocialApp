package com.iblog.root.socialapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iblog.root.socialapp.ui.fragments.HomeFragment;
import com.iblog.root.socialapp.ui.fragments.MessagingFragment;
import com.iblog.root.socialapp.ui.fragments.NotificationFragment;
import com.iblog.root.socialapp.ui.fragments.PeopleFragment;

/**
 * Created by root on 05/08/18.
 */

public class PagerAdapter extends FragmentPagerAdapter {




    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:

                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;

            case 1:

                PeopleFragment peopleFragment = new PeopleFragment();
                return peopleFragment;

            case 2:

                MessagingFragment messagingFragment = new MessagingFragment();
                return messagingFragment;

            case 3:

                NotificationFragment notificationFragment = new NotificationFragment();
                return notificationFragment;


                default:
                    return null;

        }



    }

    @Override
    public int getCount() {
        return 4;
    }
}