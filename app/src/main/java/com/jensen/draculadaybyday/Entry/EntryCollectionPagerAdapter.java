package com.jensen.draculadaybyday.Entry;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.jensen.draculadaybyday.Entries.EntryFragment;
import com.jensen.draculadaybyday.SQLite.FragmentEntryDatabaseHandler;

import java.util.Calendar;

public class EntryCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private FragmentEntryDatabaseHandler db;

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
        Calendar date =  Calendar.getInstance();
        return db.getNumUnlcokedDiaries(date);
    }
}
