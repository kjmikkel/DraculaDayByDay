package com.jensen.draculadaybyday.Entries;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jensen.draculadaybyday.AboutPage;
import com.jensen.draculadaybyday.Entry.EntryCollectionPagerAdapter;
import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.SQLite.FragmentEntryDatabaseHandler;
import com.jensen.draculadaybyday.Preferences.DraculaPreferences;

/**
 * An activity representing a single Entry detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link EntryListActivity}.
 */
public class EntryActivity extends AppCompatActivity {

    private EntryCollectionPagerAdapter entryCollectionPagerAdapter;
    private ViewPager viewPager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.entry_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.entry_list_general_preferences:
                Intent preferences = new Intent(EntryActivity.this, DraculaPreferences.class);
                startActivity(preferences);
                return true;
            case R.id.entry_list_about_app:
                Intent about = new Intent(EntryActivity.this, AboutPage.class);
                startActivity(about);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_entry);
            Toolbar toolbar = (Toolbar) findViewById(R.id.entry_toolbar);
            setSupportActionBar(toolbar);

            // Show the Up button in the action bar.
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }

            // savedInstanceState is non-null when there is fragment state
            // saved from previous configurations of this activity
            // (e.g. when rotating the screen from portrait to landscape).
            // In this case, the fragment will automatically be re-added
            // to its container so we don't need to manually add it.
            // For more information, see the Fragments API guide at:
            //
            // http://developer.android.com/guide/components/fragments.html
            //
            if (savedInstanceState == null) {
                // Create the detail fragment and add it to the activity
                // using a fragment transaction.
                Bundle arguments = new Bundle();
                short startSeqNumber = getIntent().getShortExtra(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM, (short) 1);
                arguments.putShort(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM, startSeqNumber);

                entryCollectionPagerAdapter = new EntryCollectionPagerAdapter(getSupportFragmentManager());
                // NestedScrollView scrollView = (NestedScrollView)findViewById(R.id.entry_container);
                // scrollView.setFillViewport(true);
                viewPager = (ViewPager) findViewById(R.id.pager);
                viewPager.setAdapter(entryCollectionPagerAdapter);
                /*
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                        Fragment fragment = entryCollectionPagerAdapter.getItem(position);
                        if (fragment instanceof  EntryFragment) {
                            EntryFragment entryFragment = (EntryFragment)fragment;
                            entryFragment.setToolbar(position);
                        }

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                */
                viewPager.setCurrentItem(startSeqNumber - 1);
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }
}
