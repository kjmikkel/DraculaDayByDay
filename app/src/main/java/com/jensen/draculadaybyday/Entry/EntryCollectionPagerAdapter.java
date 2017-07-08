package com.jensen.draculadaybyday.Entry;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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
        Fragment fragment =  new EntryFragment();

        // Put the value of the next sequence number
        Bundle arguments = new Bundle();
        arguments.putShort(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM, (short) (i+1));
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public int getCount() {
        Calendar date =  Calendar.getInstance();
        return db.getNumUnlcokedDiaries(date);
    }
}
