package com.jensen.draculadaybyday.Entries;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.SQLite.FragmentEntryDatabaseHandler;
import com.jensen.draculadaybyday.Settings.DraculaSettings;

/**
 * An activity representing a single Entry detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link EntryListActivity}.
 */
public class EntryDetailActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.entry_detail_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          //      NavUtils.
//                NavUtils.navigateUpTo(this, new Intent(this, DraculaSettings.class));
                ///Snackbar.make(view, "REPLACE!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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
            arguments.putShort(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM,  getIntent().getShortExtra(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM, (short)1));
            EntryDetailFragment fragment = new EntryDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.entry_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.entry_detail_general_settings:
                Intent settings = new Intent(EntryDetailActivity.this, DraculaSettings.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
