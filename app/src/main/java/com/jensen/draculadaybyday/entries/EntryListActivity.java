package com.jensen.draculadaybyday.entries;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jensen.draculadaybyday.filter.FilterActivity;
import com.jensen.draculadaybyday.presentation.AboutPage;
import com.jensen.draculadaybyday.entry.Entry;
import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.sql_lite.DateConstructorUtility;
import com.jensen.draculadaybyday.sql_lite.ExperienceMode;
import com.jensen.draculadaybyday.sql_lite.FragmentEntryDatabaseHandler;
import com.jensen.draculadaybyday.preferences.DraculaPreferences;
import com.jensen.draculadaybyday.sql_lite.SqlConstraintFactory;
import com.jensen.draculadaybyday.sql_lite.SqlSortFactory;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

// Type
import static com.jensen.draculadaybyday.entries.EntryType.DIARY_ENTRY;
import static com.jensen.draculadaybyday.entries.EntryType.LETTER;
import static com.jensen.draculadaybyday.entries.EntryType.NEWSPAPER;
import static com.jensen.draculadaybyday.entries.EntryType.NOTE;
import static com.jensen.draculadaybyday.entries.EntryType.PHONOGRAPH;
import static com.jensen.draculadaybyday.entries.EntryType.TELEGRAM;

// Dramatis personæ
import static com.jensen.draculadaybyday.entries.Person.ABRAHAM_VAN_HELSING;
import static com.jensen.draculadaybyday.entries.Person.ARTHUR_HOLMWOOD;
import static com.jensen.draculadaybyday.entries.Person.DR_SEWARD;
import static com.jensen.draculadaybyday.entries.Person.JONATHAN_HARKER;
import static com.jensen.draculadaybyday.entries.Person.LUCY_WESTENRA;
import static com.jensen.draculadaybyday.entries.Person.MESSRS;
import static com.jensen.draculadaybyday.entries.Person.MINA_HARKER;
import static com.jensen.draculadaybyday.entries.Person.MINA_MURRAY;
import static com.jensen.draculadaybyday.entries.Person.MITCHELL_AND_SONS;
import static com.jensen.draculadaybyday.entries.Person.PALL_MALL_GAZETTE;
import static com.jensen.draculadaybyday.entries.Person.PATRICK_HENNESSEY;
import static com.jensen.draculadaybyday.entries.Person.QUINCEY_MORRIS;
import static com.jensen.draculadaybyday.entries.Person.SAMUEL_F_BILLINGTON;
import static com.jensen.draculadaybyday.entries.Person.SISTER_AGATHA;
import static com.jensen.draculadaybyday.entries.Person.WESTMINISTER_GAZETTE;

/**
 * An activity representing a list of Entries. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EntryActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EntryListActivity extends AppCompatActivity {

    private static FragmentEntryDatabaseHandler mFragmentEntryHandler;

    private SwipeRefreshLayout mSwipeContainer;
    private SimpleItemRecyclerViewAdapter mSimpleItemAdapter;

    private static final int FILTER_REQUEST = 1; // The filter code

    private static SqlConstraintFactory constraintFactory;
    private static SqlSortFactory sortFactory;

    private int entrySequenceNum;

    private SharedPreferences prefs = null;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.entry_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.entry_list_filter:
                Intent filter = new Intent(EntryListActivity.this, FilterActivity.class);
                filter.putExtra(FilterActivity.CONSTRAINTS_INTENT_KEY, constraintFactory);
                filter.putExtra(FilterActivity.SORTING_INTENT_KEY, sortFactory);
                startActivityForResult(filter, FILTER_REQUEST);
                return true;
            case R.id.entry_list_general_preferences:
                Intent preferences = new Intent(EntryListActivity.this, DraculaPreferences.class);
                startActivity(preferences);
                return true;
            case R.id.entry_list_about_app:
                Intent about = new Intent(EntryListActivity.this, AboutPage.class);
                startActivity(about);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Check which request we're responding to
                if (requestCode == FILTER_REQUEST) {
                    // The user picked a new filter.
                    // The Intent's data Uri identifies which contact was selected.
                    constraintFactory = data.getParcelableExtra(FilterActivity.CONSTRAINTS_INTENT_KEY);
                    constraintFactory.unlocked(true);
                    sortFactory = data.getParcelableExtra(FilterActivity.SORTING_INTENT_KEY);

                    //region Set entries
                    // Get the entries
                    List<Entry> entries = mFragmentEntryHandler.getEntries(constraintFactory, sortFactory);
                    mSimpleItemAdapter.clear();
                    mSimpleItemAdapter.addAll(entries);
                  //endregion
                }
            }
        } catch (Exception e) {
            Log.d("onActivityResult", e.getMessage());
        }
    }

    private void setDefaultPreferences() {
        // Create the default preferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_user_interface, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_experience, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);

        //region Preferences
        setDefaultPreferences();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //endregion

        setContentView(R.layout.activity_entry_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (prefs.getBoolean(getString(R.string.pref_key_first_run), true)) {

                final Dialog dlg = new Dialog(this, R.style.CustomDialog);
                View description = View.inflate(this, R.layout.custom_listview, null);

                // Set the text
                TextView textView = (TextView) description.findViewById(R.id.custom_list_view_description);
                textView.setText(getString(R.string.preference_how_to_experience));

                // Set the list items
                ListView listView = (ListView) description.findViewById(R.id.custom_list_view_list_items);

                String[] experienceChoices = getResources().getStringArray(R.array.list_preference_how_to_experience);
                final ArrayAdapter<String> arrayAdapterItems = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, experienceChoices);

                listView.setAdapter(arrayAdapterItems);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SharedPreferences.Editor prefEditor = prefs.edit();
                        prefEditor.putString(getString(R.string.pref_key_how_to_experience), parent.getItemAtPosition(position).toString());
                        prefEditor.putBoolean(getString(R.string.pref_key_first_run), false);

                        // If it is the same tempo, then we save the current date
                        if (parent.getItemAtPosition(position).toString().equals(getString(R.string.pref_experience_tempo))) {
                            DateTime today = DateConstructorUtility.todayInThePast(ExperienceMode.EXPERIENCE_IN_SAME_TEMPO);
                            prefEditor.putLong(getString(R.string.pref_key_start_date_time), today.getMillis());
                        }

                        prefEditor.apply();
                        dlg.dismiss();
                        setUpEntries();
                    }
                });

                dlg.setContentView(description);
                dlg.setTitle(getString(R.string.pref_how_to_experience_title));

                dlg.setCancelable(false);
                dlg.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                if (dlg.getWindow() != null) {
                    lp.copyFrom(dlg.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    dlg.getWindow().setAttributes(lp);
                }
            } else {
               setUpEntries();
            }
        } catch (Exception e) {
            Log.d("", e.getMessage());
        }
    }

    private void setUpEntries() {
        // Set the sort and constraint factories (only once)
        if (constraintFactory == null) {
            constraintFactory = new SqlConstraintFactory();
            constraintFactory.unlocked(true);

            sortFactory = new SqlSortFactory();
            sortFactory.dateOrder(true);
            sortFactory.bookOrder(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        mFragmentEntryHandler = FragmentEntryDatabaseHandler.getInstance(this);

        try {
            mFragmentEntryHandler.open();

            entrySequenceNum = 0;

            //region Add entries to the database
            // Chapter 1
            addEntryToDatabase(1, JONATHAN_HARKER, R.raw.may_03_jonathan_harker, DateTimeConstants.MAY, 3, DIARY_ENTRY);
            addEntryToDatabase(1, JONATHAN_HARKER, R.raw.may_04_jonathan_harker, DateTimeConstants.MAY, 4, DIARY_ENTRY);
            addEntryToDatabase(1, JONATHAN_HARKER, R.raw.may_05_jonathan_harker_01, DateTimeConstants.MAY, 5, DIARY_ENTRY);

            // Chapter 2
            addEntryToDatabase(2, JONATHAN_HARKER, R.raw.may_05_jonathan_harker_02, DateTimeConstants.MAY, 5, DIARY_ENTRY);
            addEntryToDatabase(2, JONATHAN_HARKER, R.raw.may_07_jonathan_harker, DateTimeConstants.MAY, 7, DIARY_ENTRY);
            addEntryToDatabase(2, JONATHAN_HARKER, R.raw.may_08_jonathan_harker_01, DateTimeConstants.MAY, 8, DIARY_ENTRY);

            // Chapter 3
            addEntryToDatabase(3, JONATHAN_HARKER, R.raw.may_08_jonathan_harker_02, DateTimeConstants.MAY, 8, DIARY_ENTRY);
            addEntryToDatabase(3, JONATHAN_HARKER, R.raw.may_12_jonathan_harker, DateTimeConstants.MAY, 12, DIARY_ENTRY);
            addEntryToDatabase(3, JONATHAN_HARKER, R.raw.may_15_jonathan_harker, DateTimeConstants.MAY, 15, DIARY_ENTRY);
            addEntryToDatabase(3, JONATHAN_HARKER, R.raw.may_16_jonathan_harker_01, DateTimeConstants.MAY, 16, DIARY_ENTRY);

            // Chapter 4
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_16_jonathan_harker_02, DateTimeConstants.MAY, 16, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_18_jonathan_harker, DateTimeConstants.MAY, 18, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_19_jonathan_harker, DateTimeConstants.MAY, 19, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_28_jonathan_harker_01, DateTimeConstants.MAY, 28, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_28_jonathan_harker_02, DateTimeConstants.MAY, 28, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_28_jonathan_harker_03, DateTimeConstants.MAY, 28, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_28_jonathan_harker_04, DateTimeConstants.MAY, 28, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_31_jonathan_harker, DateTimeConstants.MAY, 31, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_17_jonathan_harker, DateTimeConstants.JUNE, 17, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_24_jonathan_harker, DateTimeConstants.JUNE, 24, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_25_jonathan_harker_01, DateTimeConstants.JUNE, 25, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_25_jonathan_harker_02, DateTimeConstants.JUNE, 25, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_29_jonathan_harker, DateTimeConstants.JUNE, 29, DIARY_ENTRY);
            addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_30_jonathan_harker, DateTimeConstants.JUNE, 30, DIARY_ENTRY);

            // Chapter 5
            addEntryToDatabase(5, MINA_MURRAY, R.raw.may_09_mina_murray, DateTimeConstants.MAY, 9, LETTER);
            addEntryToDatabase(5, LUCY_WESTENRA, R.raw.may_10_lucy_westenra, DateTimeConstants.MAY, 10, LETTER);
            addEntryToDatabase(5, LUCY_WESTENRA, R.raw.may_24_lucy_westenra_01, DateTimeConstants.MAY, 24, LETTER);
            addEntryToDatabase(5, LUCY_WESTENRA, R.raw.may_24_lucy_westenra_02, DateTimeConstants.MAY, 24, LETTER);
            addEntryToDatabase(5, DR_SEWARD, R.raw.may_25_dr_seward, DateTimeConstants.MAY, 25, PHONOGRAPH);
            addEntryToDatabase(5, QUINCEY_MORRIS, R.raw.may_25_quincey_morris, DateTimeConstants.MAY, 25, LETTER);
            addEntryToDatabase(5, ARTHUR_HOLMWOOD, R.raw.may_26_arthur_holmwood, DateTimeConstants.MAY, 26, TELEGRAM);

            // Chapter 6
            addEntryToDatabase(6, MINA_MURRAY, R.raw.july_24_mina_murray, DateTimeConstants.JULY, 24, DIARY_ENTRY);
            addEntryToDatabase(6, MINA_MURRAY, R.raw.august_01_mina_murray_01, DateTimeConstants.AUGUST, 1, DIARY_ENTRY);
            addEntryToDatabase(6, MINA_MURRAY, R.raw.august_01_mina_murray_02, DateTimeConstants.AUGUST, 1, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.june_05_dr_seward, DateTimeConstants.JUNE, 5, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.june_18_dr_seward, DateTimeConstants.JUNE, 18, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.july_01_dr_seward, DateTimeConstants.JULY, 1, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.july_08_dr_seward, DateTimeConstants.JULY, 8, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.july_19_dr_seward_01, DateTimeConstants.JULY, 19, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.july_19_dr_seward_02, DateTimeConstants.JULY, 19, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.july_20_dr_seward_01, DateTimeConstants.JULY, 20, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.july_20_dr_seward_02, DateTimeConstants.JULY, 20, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.july_20_dr_seward_03, DateTimeConstants.JULY, 20, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.july_26_mina_murray, DateTimeConstants.JULY, 26, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.july_27_mina_murray, DateTimeConstants.JULY, 27, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.august_03_mina_murray, DateTimeConstants.AUGUST, 3, DIARY_ENTRY);
            addEntryToDatabase(6, DR_SEWARD, R.raw.august_06_mina_murray, DateTimeConstants.AUGUST, 6, DIARY_ENTRY);

            // Chapter 7
            addEntryToDatabase(7, MINA_MURRAY, R.raw.august_08_mina_murray_01, DateTimeConstants.AUGUST, 8, DIARY_ENTRY);
            addEntryToDatabase(7, MINA_MURRAY, R.raw.august_09_mina_murray, DateTimeConstants.AUGUST, 9, DIARY_ENTRY);
            addEntryToDatabase(7, MINA_MURRAY, R.raw.august_08_mina_murray_02, DateTimeConstants.AUGUST, 8, DIARY_ENTRY);
            addEntryToDatabase(7, MINA_MURRAY, R.raw.august_10_mina_murray_01, DateTimeConstants.AUGUST, 10, DIARY_ENTRY);

            // Chapter 8
            addEntryToDatabase(8, MINA_MURRAY, R.raw.august_10_mina_murray_02, DateTimeConstants.AUGUST, 10, DIARY_ENTRY);
            addEntryToDatabase(8, MINA_MURRAY, R.raw.august_11_mina_murray, DateTimeConstants.AUGUST, 11, DIARY_ENTRY);
            addEntryToDatabase(8, MINA_MURRAY, R.raw.august_12_mina_murray, DateTimeConstants.AUGUST, 12, DIARY_ENTRY);
            addEntryToDatabase(8, MINA_MURRAY, R.raw.august_13_mina_murray, DateTimeConstants.AUGUST, 13, DIARY_ENTRY);
            addEntryToDatabase(8, MINA_MURRAY, R.raw.august_14_mina_murray, DateTimeConstants.AUGUST, 14, DIARY_ENTRY);
            addEntryToDatabase(8, MINA_MURRAY, R.raw.august_15_mina_murray, DateTimeConstants.AUGUST, 15, DIARY_ENTRY);
            addEntryToDatabase(8, MINA_MURRAY, R.raw.august_17_mina_murray, DateTimeConstants.AUGUST, 17, DIARY_ENTRY);
            addEntryToDatabase(8, SAMUEL_F_BILLINGTON, R.raw.august_17_samuel, DateTimeConstants.AUGUST, 17, LETTER);
            addEntryToDatabase(8, MESSRS, R.raw.august_21_messrs, DateTimeConstants.AUGUST, 21, LETTER);
            addEntryToDatabase(8, MINA_MURRAY, R.raw.august_18_mina_murray, DateTimeConstants.AUGUST, 18, DIARY_ENTRY);
            addEntryToDatabase(8, MINA_MURRAY, R.raw.august_19_mina_murray, DateTimeConstants.AUGUST, 19, DIARY_ENTRY);
            addEntryToDatabase(8, SISTER_AGATHA, R.raw.august_12_jonathan_harker, DateTimeConstants.AUGUST, 12, LETTER);
            addEntryToDatabase(8, DR_SEWARD, R.raw.august_19_dr_seward, DateTimeConstants.AUGUST, 19, DIARY_ENTRY);

            // Chapter 9
            addEntryToDatabase(9, MINA_HARKER, R.raw.august_24_mina_murray, DateTimeConstants.AUGUST, 24, LETTER);
            addEntryToDatabase(9, LUCY_WESTENRA, R.raw.august_30_mina_murray, DateTimeConstants.AUGUST, 30, LETTER);
            addEntryToDatabase(9, DR_SEWARD, R.raw.august_20_dr_seward, DateTimeConstants.AUGUST, 20, DIARY_ENTRY);
            addEntryToDatabase(9, DR_SEWARD, R.raw.august_23_dr_seward, DateTimeConstants.AUGUST, 23, DIARY_ENTRY);
            addEntryToDatabase(9, LUCY_WESTENRA, R.raw.august_24_lucy_westenra, DateTimeConstants.AUGUST, 24, DIARY_ENTRY);
            addEntryToDatabase(9, LUCY_WESTENRA, R.raw.august_25_lucy_westenra, DateTimeConstants.AUGUST, 25, DIARY_ENTRY);
            addEntryToDatabase(9, ARTHUR_HOLMWOOD, R.raw.august_31_arthur_holmwood, DateTimeConstants.AUGUST, 31, LETTER);
            addEntryToDatabase(9, ARTHUR_HOLMWOOD, R.raw.september_01_arthur_holmwood, DateTimeConstants.SEPTEMBER, 1, TELEGRAM);
            addEntryToDatabase(9, DR_SEWARD, R.raw.september_02_dr_seward, DateTimeConstants.SEPTEMBER, 2, LETTER);
            addEntryToDatabase(9, ABRAHAM_VAN_HELSING, R.raw.september_02_van_helsing, DateTimeConstants.SEPTEMBER, 2, LETTER);
            addEntryToDatabase(9, DR_SEWARD, R.raw.september_03_dr_seward, DateTimeConstants.SEPTEMBER, 3, LETTER);
            addEntryToDatabase(9, DR_SEWARD, R.raw.september_04_dr_seward_01, DateTimeConstants.SEPTEMBER, 4, DIARY_ENTRY);
            addEntryToDatabase(9, DR_SEWARD, R.raw.september_04_dr_seward_02, DateTimeConstants.SEPTEMBER, 4, TELEGRAM);
            addEntryToDatabase(9, DR_SEWARD, R.raw.september_05_dr_seward, DateTimeConstants.SEPTEMBER, 5, TELEGRAM);
            addEntryToDatabase(9, DR_SEWARD, R.raw.september_06_dr_seward_01, DateTimeConstants.SEPTEMBER, 6, TELEGRAM);

            // Chapter 10
            addEntryToDatabase(10, DR_SEWARD, R.raw.september_06_dr_seward_02, DateTimeConstants.SEPTEMBER, 6, LETTER);
            addEntryToDatabase(10, DR_SEWARD, R.raw.september_07_dr_seward, DateTimeConstants.SEPTEMBER, 7, DIARY_ENTRY);
            addEntryToDatabase(10, DR_SEWARD, R.raw.september_08_dr_seward, DateTimeConstants.SEPTEMBER, 8, DIARY_ENTRY);
            addEntryToDatabase(10, DR_SEWARD, R.raw.september_09_dr_seward, DateTimeConstants.SEPTEMBER, 9, DIARY_ENTRY);
            addEntryToDatabase(10, LUCY_WESTENRA, R.raw.september_09_lucy_westenra, DateTimeConstants.SEPTEMBER, 9, DIARY_ENTRY);
            addEntryToDatabase(10, DR_SEWARD, R.raw.september_10_dr_seward, DateTimeConstants.SEPTEMBER, 10, DIARY_ENTRY);
            addEntryToDatabase(10, DR_SEWARD, R.raw.september_11_dr_seward, DateTimeConstants.SEPTEMBER, 11, DIARY_ENTRY);

            // Chapter 11
            addEntryToDatabase(11, LUCY_WESTENRA, R.raw.september_12_lucy_westenra, DateTimeConstants.SEPTEMBER, 12, DIARY_ENTRY);
            addEntryToDatabase(11, DR_SEWARD, R.raw.september_13_dr_seward, DateTimeConstants.SEPTEMBER, 13, DIARY_ENTRY);
            addEntryToDatabase(11, LUCY_WESTENRA, R.raw.september_17_lucy_westenra_01, DateTimeConstants.SEPTEMBER, 17, DIARY_ENTRY);
            addEntryToDatabase(11, PALL_MALL_GAZETTE, R.raw.september_18_pallmall, DateTimeConstants.SEPTEMBER, 18, NEWSPAPER);
            addEntryToDatabase(11, DR_SEWARD, R.raw.september_17_dr_seward, DateTimeConstants.SEPTEMBER, 17, DIARY_ENTRY);
            addEntryToDatabase(11, ABRAHAM_VAN_HELSING, R.raw.september_17_van_helsing, DateTimeConstants.SEPTEMBER, 17, TELEGRAM);
            addEntryToDatabase(11, DR_SEWARD, R.raw.september_18_dr_seward_01, DateTimeConstants.SEPTEMBER, 18, DIARY_ENTRY);
            addEntryToDatabase(11, LUCY_WESTENRA, R.raw.september_17_lucy_westenra_02, DateTimeConstants.SEPTEMBER, 17, DIARY_ENTRY);

            // Chapter 12
            addEntryToDatabase(12, DR_SEWARD, R.raw.september_18_dr_seward_02, DateTimeConstants.SEPTEMBER, 18, DIARY_ENTRY);
            addEntryToDatabase(12, DR_SEWARD, R.raw.september_19_dr_seward, DateTimeConstants.SEPTEMBER, 19, DIARY_ENTRY);
            addEntryToDatabase(12, MINA_HARKER, R.raw.september_17_mina_harker, DateTimeConstants.SEPTEMBER, 17, LETTER);
            addEntryToDatabase(12, PATRICK_HENNESSEY, R.raw.september_20_hennessey, DateTimeConstants.SEPTEMBER, 20, LETTER);
            addEntryToDatabase(12, MINA_HARKER, R.raw.september_18_mina_harker, DateTimeConstants.SEPTEMBER, 18, LETTER);
            addEntryToDatabase(12, DR_SEWARD, R.raw.september_20_dr_seward_01, DateTimeConstants.SEPTEMBER, 20, DIARY_ENTRY);

            // Chapter 13
            addEntryToDatabase(13, DR_SEWARD, R.raw.september_20_dr_seward_02, DateTimeConstants.SEPTEMBER, 20, DIARY_ENTRY);
            addEntryToDatabase(13, MINA_HARKER, R.raw.september_22_mina_harker, DateTimeConstants.SEPTEMBER, 22, DIARY_ENTRY);
            addEntryToDatabase(13, DR_SEWARD, R.raw.september_22_dr_seward, DateTimeConstants.SEPTEMBER, 22, DIARY_ENTRY);
            addEntryToDatabase(13, WESTMINISTER_GAZETTE, R.raw.september_25_westminster_01, DateTimeConstants.SEPTEMBER, 25, NEWSPAPER);
            addEntryToDatabase(13, WESTMINISTER_GAZETTE, R.raw.september_25_westminster_02, DateTimeConstants.SEPTEMBER, 25, NEWSPAPER);

            // Chapter 14
            addEntryToDatabase(14, MINA_HARKER, R.raw.september_23_mina_harker, DateTimeConstants.SEPTEMBER, 23, DIARY_ENTRY);
            addEntryToDatabase(14, MINA_HARKER, R.raw.september_24_mina_harker, DateTimeConstants.SEPTEMBER, 24, DIARY_ENTRY);
            addEntryToDatabase(14, ABRAHAM_VAN_HELSING, R.raw.september_24_van_helsing, DateTimeConstants.SEPTEMBER, 24, LETTER);
            addEntryToDatabase(14, MINA_HARKER, R.raw.september_25_mina_harker_01, DateTimeConstants.SEPTEMBER, 25, TELEGRAM);
            addEntryToDatabase(14, MINA_HARKER, R.raw.september_25_mina_harker_02, DateTimeConstants.SEPTEMBER, 25, DIARY_ENTRY);
            addEntryToDatabase(14, ABRAHAM_VAN_HELSING, R.raw.september_25_van_helsing, DateTimeConstants.SEPTEMBER, 25, LETTER);
            addEntryToDatabase(14, MINA_HARKER, R.raw.september_25_mina_harker_03, DateTimeConstants.SEPTEMBER, 25, LETTER);
            addEntryToDatabase(14, JONATHAN_HARKER, R.raw.september_26_jonathan_harker, DateTimeConstants.SEPTEMBER, 26, DIARY_ENTRY);
            addEntryToDatabase(14, DR_SEWARD, R.raw.september_26_dr_seward_01, DateTimeConstants.SEPTEMBER, 26, DIARY_ENTRY);

            // Chapter 15
            addEntryToDatabase(15, DR_SEWARD, R.raw.september_26_dr_seward_02, DateTimeConstants.SEPTEMBER, 26, DIARY_ENTRY);
            addEntryToDatabase(15, DR_SEWARD, R.raw.september_27_dr_seward, DateTimeConstants.SEPTEMBER, 27, DIARY_ENTRY);
            addEntryToDatabase(15, ABRAHAM_VAN_HELSING, R.raw.september_27_van_helsing, DateTimeConstants.SEPTEMBER, 27, NOTE);
            addEntryToDatabase(15, DR_SEWARD, R.raw.september_28_dr_seward, DateTimeConstants.SEPTEMBER, 28, DIARY_ENTRY);
            addEntryToDatabase(15, DR_SEWARD, R.raw.september_29_dr_seward_01, DateTimeConstants.SEPTEMBER, 29, DIARY_ENTRY);

            // Chapter 16
            addEntryToDatabase(16, DR_SEWARD, R.raw.september_29_dr_seward_02, DateTimeConstants.SEPTEMBER, 29, DIARY_ENTRY);
            addEntryToDatabase(16, DR_SEWARD, R.raw.september_29_dr_seward_03, DateTimeConstants.SEPTEMBER, 29, DIARY_ENTRY);

            // Chapter 17
            addEntryToDatabase(17, DR_SEWARD, R.raw.september_29_dr_seward_04, DateTimeConstants.SEPTEMBER, 29, DIARY_ENTRY);
            addEntryToDatabase(17, MINA_HARKER, R.raw.september_29_mina_harker_01, DateTimeConstants.SEPTEMBER, 29, DIARY_ENTRY);
            addEntryToDatabase(17, DR_SEWARD, R.raw.september_29_dr_seward_05, DateTimeConstants.SEPTEMBER, 29, DIARY_ENTRY);
            addEntryToDatabase(17, MINA_HARKER, R.raw.september_29_mina_harker_02, DateTimeConstants.SEPTEMBER, 29, DIARY_ENTRY);
            addEntryToDatabase(17, DR_SEWARD, R.raw.september_30_dr_seward_01, DateTimeConstants.SEPTEMBER, 30, DIARY_ENTRY);
            addEntryToDatabase(17, JONATHAN_HARKER, R.raw.september_29_jonathan_harker, DateTimeConstants.SEPTEMBER, 29, DIARY_ENTRY);
            addEntryToDatabase(17, JONATHAN_HARKER, R.raw.september_30_jonathan_harker, DateTimeConstants.SEPTEMBER, 30, DIARY_ENTRY);
            addEntryToDatabase(17, MINA_HARKER, R.raw.september_30_mina_harker_01, DateTimeConstants.SEPTEMBER, 30, DIARY_ENTRY);

            // Chapter 18
            addEntryToDatabase(18, DR_SEWARD, R.raw.september_30_dr_seward_02, DateTimeConstants.SEPTEMBER, 30, DIARY_ENTRY);
            addEntryToDatabase(18, MINA_HARKER, R.raw.september_30_mina_harker_02, DateTimeConstants.SEPTEMBER, 30, DIARY_ENTRY);
            addEntryToDatabase(18, DR_SEWARD, R.raw.october_01_dr_seward_01, DateTimeConstants.OCTOBER, 1, DIARY_ENTRY);

            // Chapter 19
            addEntryToDatabase(19, JONATHAN_HARKER, R.raw.october_01_jonathan_harker_01, DateTimeConstants.OCTOBER, 1, DIARY_ENTRY);
            addEntryToDatabase(19, DR_SEWARD, R.raw.october_01_dr_seward_02, DateTimeConstants.OCTOBER, 1, DIARY_ENTRY);
            addEntryToDatabase(19, MINA_HARKER, R.raw.october_01_mina_harker, DateTimeConstants.OCTOBER, 1, DIARY_ENTRY);
            addEntryToDatabase(19, MINA_HARKER, R.raw.october_02_mina_harker, DateTimeConstants.OCTOBER, 2, DIARY_ENTRY);

            // Chapter 20
            addEntryToDatabase(20, JONATHAN_HARKER, R.raw.october_01_jonathan_harker_02, DateTimeConstants.OCTOBER, 1, DIARY_ENTRY);
            addEntryToDatabase(20, JONATHAN_HARKER, R.raw.october_02_jonathan_harker, DateTimeConstants.OCTOBER, 2, DIARY_ENTRY);
            addEntryToDatabase(20, DR_SEWARD, R.raw.october_01_dr_seward_03, DateTimeConstants.OCTOBER, 1, DIARY_ENTRY);
            addEntryToDatabase(20, MITCHELL_AND_SONS, R.raw.october_01_mitchell, DateTimeConstants.OCTOBER, 1, LETTER);
            addEntryToDatabase(20, DR_SEWARD, R.raw.october_02_dr_seward, DateTimeConstants.OCTOBER, 2, DIARY_ENTRY);

            // Chapter 21
            addEntryToDatabase(21, DR_SEWARD, R.raw.october_03_dr_seward_01, DateTimeConstants.OCTOBER, 3, DIARY_ENTRY);

            // Chapter 22
            addEntryToDatabase(22, JONATHAN_HARKER, R.raw.october_03_jonathan_harker, DateTimeConstants.OCTOBER, 3, DIARY_ENTRY);

            // Chapter 23
            addEntryToDatabase(23, DR_SEWARD, R.raw.october_03_dr_seward_02, DateTimeConstants.OCTOBER, 3, DIARY_ENTRY);
            addEntryToDatabase(23, JONATHAN_HARKER, R.raw.october_03_jonathan_harker, DateTimeConstants.OCTOBER, 3, DIARY_ENTRY);
            addEntryToDatabase(23, JONATHAN_HARKER, R.raw.october_04_jonathan_harker_01, DateTimeConstants.OCTOBER, 4, DIARY_ENTRY);
            addEntryToDatabase(23, JONATHAN_HARKER, R.raw.october_04_jonathan_harker_02, DateTimeConstants.OCTOBER, 4, DIARY_ENTRY);

            // Chapter 24
            addEntryToDatabase(24, ABRAHAM_VAN_HELSING, R.raw.october_04_van_helsing, DateTimeConstants.OCTOBER, 4, PHONOGRAPH);
            addEntryToDatabase(24, JONATHAN_HARKER, R.raw.october_04_jonathan_harker_03, DateTimeConstants.OCTOBER, 4, DIARY_ENTRY);
            addEntryToDatabase(24, MINA_HARKER, R.raw.october_05_mina_harker, DateTimeConstants.OCTOBER, 5, DIARY_ENTRY);
            addEntryToDatabase(24, DR_SEWARD, R.raw.october_05_dr_seward, DateTimeConstants.OCTOBER, 5, DIARY_ENTRY);
            addEntryToDatabase(24, JONATHAN_HARKER, R.raw.october_05_jonathan_harker, DateTimeConstants.OCTOBER, 5, DIARY_ENTRY);
            addEntryToDatabase(24, JONATHAN_HARKER, R.raw.october_06_jonathan_harker, DateTimeConstants.OCTOBER, 6, DIARY_ENTRY);

            // Chapter 25
            addEntryToDatabase(25, DR_SEWARD, R.raw.october_11_dr_seward, DateTimeConstants.OCTOBER, 11, DIARY_ENTRY);
            addEntryToDatabase(25, JONATHAN_HARKER, R.raw.october_15_jonathan_harker, DateTimeConstants.OCTOBER, 15, DIARY_ENTRY);
            addEntryToDatabase(25, JONATHAN_HARKER, R.raw.october_16_jonathan_harker, DateTimeConstants.OCTOBER, 16, DIARY_ENTRY);
            addEntryToDatabase(25, JONATHAN_HARKER, R.raw.october_17_jonathan_harker, DateTimeConstants.OCTOBER, 17, DIARY_ENTRY);
            addEntryToDatabase(25, JONATHAN_HARKER, R.raw.october_24_jonathan_harker, DateTimeConstants.OCTOBER, 24, DIARY_ENTRY);
            addEntryToDatabase(25, DR_SEWARD, R.raw.october_25_dr_seward_01, DateTimeConstants.OCTOBER, 25, DIARY_ENTRY);
            addEntryToDatabase(25, DR_SEWARD, R.raw.october_25_dr_seward_02, DateTimeConstants.OCTOBER, 25, DIARY_ENTRY);
            addEntryToDatabase(25, DR_SEWARD, R.raw.october_26_dr_seward, DateTimeConstants.OCTOBER, 26, DIARY_ENTRY);
            addEntryToDatabase(25, DR_SEWARD, R.raw.october_27_dr_seward, DateTimeConstants.OCTOBER, 27, DIARY_ENTRY);
            addEntryToDatabase(25, DR_SEWARD, R.raw.october_28_dr_seward, DateTimeConstants.OCTOBER, 28, DIARY_ENTRY);

            // Chapter 26
            addEntryToDatabase(26, DR_SEWARD, R.raw.october_29_dr_seward, DateTimeConstants.OCTOBER, 29, DIARY_ENTRY);
            addEntryToDatabase(26, DR_SEWARD, R.raw.october_30_dr_seward, DateTimeConstants.OCTOBER, 30, DIARY_ENTRY);
            addEntryToDatabase(26, MINA_HARKER, R.raw.october_30_mina_harker_01, DateTimeConstants.OCTOBER, 30, DIARY_ENTRY);
            addEntryToDatabase(26, JONATHAN_HARKER, R.raw.october_30_jonathan_harker_01, DateTimeConstants.OCTOBER, 30, DIARY_ENTRY);
            addEntryToDatabase(26, MINA_HARKER, R.raw.october_30_mina_harker_02, DateTimeConstants.OCTOBER, 30, DIARY_ENTRY);
            addEntryToDatabase(26, MINA_HARKER, R.raw.october_30_mina_harker_03, DateTimeConstants.OCTOBER, 30, DIARY_ENTRY);
            addEntryToDatabase(26, MINA_HARKER, R.raw.october_30_mina_harker_04, DateTimeConstants.OCTOBER, 30, DIARY_ENTRY);
            addEntryToDatabase(26, JONATHAN_HARKER, R.raw.october_30_jonathan_harker_02, DateTimeConstants.OCTOBER, 30, DIARY_ENTRY);
            addEntryToDatabase(26, JONATHAN_HARKER, R.raw.october_31_jonathan_harker, DateTimeConstants.OCTOBER, 31, DIARY_ENTRY);
            addEntryToDatabase(26, JONATHAN_HARKER, R.raw.november_01_jonathan_harker, DateTimeConstants.NOVEMBER, 1, DIARY_ENTRY);
            addEntryToDatabase(26, JONATHAN_HARKER, R.raw.november_02_jonathan_harker, DateTimeConstants.NOVEMBER, 2, DIARY_ENTRY);
            addEntryToDatabase(26, DR_SEWARD, R.raw.november_02_dr_seward, DateTimeConstants.NOVEMBER, 2, DIARY_ENTRY);
            addEntryToDatabase(26, DR_SEWARD, R.raw.november_03_dr_seward, DateTimeConstants.NOVEMBER, 3, DIARY_ENTRY);
            addEntryToDatabase(26, DR_SEWARD, R.raw.november_04_dr_seward, DateTimeConstants.NOVEMBER, 4, DIARY_ENTRY);
            addEntryToDatabase(26, MINA_HARKER, R.raw.october_31_mina_harker, DateTimeConstants.OCTOBER, 31, DIARY_ENTRY);

            // Chapter 27
            addEntryToDatabase(27, MINA_HARKER, R.raw.november_01_mina_harker, DateTimeConstants.NOVEMBER, 1, DIARY_ENTRY);
            addEntryToDatabase(27, MINA_HARKER, R.raw.november_02_mina_harker_01, DateTimeConstants.NOVEMBER, 2, DIARY_ENTRY);
            addEntryToDatabase(27, MINA_HARKER, R.raw.november_02_mina_harker_02, DateTimeConstants.NOVEMBER, 2, DIARY_ENTRY);
            addEntryToDatabase(27, ABRAHAM_VAN_HELSING, R.raw.november_04_van_helsing, DateTimeConstants.NOVEMBER, 4, DIARY_ENTRY);
            addEntryToDatabase(27, ABRAHAM_VAN_HELSING, R.raw.november_05_van_helsing_01, DateTimeConstants.NOVEMBER, 5, DIARY_ENTRY);
            addEntryToDatabase(27, JONATHAN_HARKER, R.raw.november_04_jonathan_harker, DateTimeConstants.NOVEMBER, 4, DIARY_ENTRY);
            addEntryToDatabase(27, DR_SEWARD, R.raw.november_05_dr_seward, DateTimeConstants.NOVEMBER, 5, DIARY_ENTRY);
            addEntryToDatabase(27, ABRAHAM_VAN_HELSING, R.raw.november_05_van_helsing_02, DateTimeConstants.NOVEMBER, 5, DIARY_ENTRY);
            addEntryToDatabase(27, MINA_HARKER, R.raw.november_06_mina_harker, DateTimeConstants.NOVEMBER, 6, DIARY_ENTRY);

            // Epilogue
            addEntryToDatabase(27, JONATHAN_HARKER, R.raw.final_note_jonathan_harker, DateTimeConstants.NOVEMBER, 6, DIARY_ENTRY);
            //endregion

        } catch (Exception e) {
            Log.d("Database", e.getMessage());
        } finally {
            mFragmentEntryHandler.close();
        }

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.entry_recycle_view);
        assert recyclerView != null;
        updateRecyclerView(recyclerView, false);

        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.entry_list_swipe);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecyclerView(recyclerView, true);
            }
        });

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_red_dark, android.R.color.black);

        if (findViewById(R.id.entry_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void addEntryToDatabase(int chapterNum, Person person, int diaryResource, @IntRange(from=1, to=12) int month, @IntRange(from=1, to=31) int date, EntryType type) {
        if (mFragmentEntryHandler != null) {
            DateTime dateTime = DateConstructorUtility.getDateTime(month, date);

            mFragmentEntryHandler.addEntry(new Entry(entrySequenceNum, chapterNum, person, getStringFromId(diaryResource), dateTime, type, true));
            entrySequenceNum++;
        }
    }

    private void updateRecyclerView(@NonNull RecyclerView recyclerView, boolean update) {
        // We need today
        DateTime dateTime = DateConstructorUtility.getUnlockDate(this);

        // Check if there are new entries available
        mFragmentEntryHandler.unlockEntriesBeforeDate(dateTime);

        // Get the entries
        List<Entry> entries = mFragmentEntryHandler.getEntries(constraintFactory, sortFactory);

        if (update) {
            mSimpleItemAdapter.clear();
            mSimpleItemAdapter.addAll(entries);
            mSwipeContainer.setRefreshing(false);
        } else {
            mSimpleItemAdapter = new SimpleItemRecyclerViewAdapter(entries);
            recyclerView.setAdapter(mSimpleItemAdapter);
        }
    }

    private String getStringFromId(int id) {
        InputStream stream = getResources().openRawResource((id));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();

        try {
            String line;
            do {
                line = reader.readLine();
                sb.append(line).append("\n");
            }
            while (line != null);
        } catch (Exception e) {
            Log.d("Reading problems", e.getMessage());
        }

        return sb.toString();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<EntryViewHolder> {

        private final List<Entry> mValues;

        public SimpleItemRecyclerViewAdapter(List<Entry> items) {
            mValues = items;
        }

        @Override
        public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.entry_list_content, parent, false);
            return new EntryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final EntryViewHolder holder, int position) {
            // Set the views
            holder.entry = mValues.get(position);
            holder.setViews();

            //region View on click
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putShort(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM, holder.entry.getStoryEntryNum());
                        EntryFragment fragment = new EntryFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.entry_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();

                        Intent intent = new Intent(context, EntryActivity.class);

                        // insert the values
                        intent.putExtra(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM, holder.entry.getStoryEntryNum());
                        startActivity(intent);
                    }
                }
            });
            //endregion
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        // Clean all elements of the recycler
        public void clear() {
            mValues.clear();
            notifyDataSetChanged();
        }

        // Add a list of items
        public void addAll(List<Entry> list) {
            mValues.addAll(list);
            notifyDataSetChanged();
        }
    }
}
