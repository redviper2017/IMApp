package com.example.ath.imapp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {


    public HomeFragmentPagerAdapter(FragmentManager fm) {

        super(fm);

    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                CallsFragment callsFragment = new CallsFragment();
                return callsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {

        return 2;

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "CHATS";
            case 1:
                return "CALLS";
            default:
                return null;
        }

    }
}
