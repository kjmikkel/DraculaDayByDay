package com.jensen.draculadaybyday.entry;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.jensen.draculadaybyday.entries.EntryFragment;
import com.jensen.draculadaybyday.sql_lite.FragmentEntryDatabaseHandler;

import java.util.Calendar;

public class EntryCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private final FragmentEntryDatabaseHandler db;

    public EntryCollectionPagerAdapter(FragmentManager fm) {

        super(fm);
        this.db = FragmentEntryDatabaseHandler.getInstance();
    }

    @Override
    public Fragment getItem(int i) {
        Log.d("Item", "The index is " + i);
        EntryFragment fragment =  new EntryFragment();

        // Put the value of the next sequence number
        Bundle argBundle = new Bundle();
        argBundle.putShort(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM, (short) (i+1));
        fragment.setArguments(argBundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return db.getNumUnlockedDiaries();
    }
}
