package com.jensen.draculadaybyday.Entries;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import com.jensen.draculadaybyday.AboutPage;
import com.jensen.draculadaybyday.Fragment.FragmentEntry;
import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.SQLite.FragmentEntryDatabaseHandler;
import com.jensen.draculadaybyday.Preferences.DraculaPreferences;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * An activity representing a list of Entries. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EntryDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EntryListActivity extends AppCompatActivity {

    // Dramatis personæ
    private static final String JONATHAN_HARKER = "Jonathan Harker";
    private static final String MINA_MURRAY = "Mina Murray";
    private static final String MINA_HARKER = "Mina Harker";
    private static final String LUCY_WESTENRA = "Lucy Westenra";
    private static final String QUINCEY_MORRIS = "Quiencey Morris";
    private static final String ARTHUR_HOLMWOOD = "Arthur Holmwood";
    private static final String SEWARD = "Dr. Seward";
    private static final String SAMUEL_F_BILLINGTON = "Samuel F. Billington & Son";
    private static final String MESSRS = "Messrs. Carter, Paterson & Co.";
    private static final String SISTER_AGATHA = "Sister Agatha";
    private static final String ABRAHAM_VAN_HELSING = "Abraham Van Helsing";
    private static final String PALL_MALL_GAZETTE = "The Pall Mall Gazette";
    private static final String PATRICK_HENNESSEY = "Patrick Hennessey";
    private static final String WESTMINISTER_GAZETTE = "The Westminster Gazette";
    private static final String MITCHELL_AND_SONS = "Mitchell, Sons and Candy to Lord Godalming";

    // Types
    private static final String DIARY_ENTRY = "Diary Entry";
    private static final String LETTER = "Letter";
    private static final String TELEGRAM = "Telegram";
    private static final String PHONOGRAPH = "Phonograph";
    private static final String NEWSPAPER = "Newspaper";
    private static final String NOTE = "Note";

    private static FragmentEntryDatabaseHandler fragmentEntryHandler;
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
            case R.id.entry_list_general_preferences:
                Intent preferences = new Intent(EntryListActivity.this, DraculaPreferences.class);
                startActivity(preferences);
                return true;
            case R.id.entry_list_about_app:
                Intent about = new Intent(EntryListActivity.this, AboutPage.class);
                startActivity(about);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDefaultPreferences() {
        // Create the default preferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_user_interface, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Notifications
        /*
        //build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_fangs)
                .setContentTitle("Simple notification")
                .setContentText("This is test of simple notification.");

        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // To post your notification to the notification bar
        notificationManager.notify(0 , mBuilder.build());
        */

        setDefaultPreferences();

        setContentView(R.layout.activity_entry_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        fragmentEntryHandler = FragmentEntryDatabaseHandler.getInstance(this);

        // Chapter 1
        addEntryToDatabase(1, JONATHAN_HARKER, R.raw.may_03_harker, Calendar.MAY, 3, DIARY_ENTRY);
        addEntryToDatabase(1, JONATHAN_HARKER, R.raw.may_04_harker, Calendar.MAY, 4, DIARY_ENTRY);
        addEntryToDatabase(1, JONATHAN_HARKER, R.raw.may_05_harker_01, Calendar.MAY, 5, DIARY_ENTRY);

        // Chapter 2
        addEntryToDatabase(2, JONATHAN_HARKER, R.raw.may_05_harker_02, Calendar.MAY, 5, DIARY_ENTRY);
        addEntryToDatabase(2, JONATHAN_HARKER, R.raw.may_07_harker, Calendar.MAY, 7, DIARY_ENTRY);
        addEntryToDatabase(2, JONATHAN_HARKER, R.raw.may_08_harker_01, Calendar.MAY, 8, DIARY_ENTRY);

        // Chapter 3
        addEntryToDatabase(3, JONATHAN_HARKER, R.raw.may_08_harker_02, Calendar.MAY, 8, DIARY_ENTRY);
        addEntryToDatabase(3, JONATHAN_HARKER, R.raw.may_08_harker_03, Calendar.MAY, 8, DIARY_ENTRY);
        addEntryToDatabase(3, JONATHAN_HARKER, R.raw.may_12_harker_01, Calendar.MAY, 12, DIARY_ENTRY);
        addEntryToDatabase(3, JONATHAN_HARKER, R.raw.may_15_harker, Calendar.MAY, 15, DIARY_ENTRY);
        addEntryToDatabase(3, JONATHAN_HARKER, R.raw.may_16_harker_01, Calendar.MAY, 16, DIARY_ENTRY);

        // Chapter 4
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_16_harker_02, Calendar.MAY, 16, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_18_harker, Calendar.MAY, 18, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_19_harker, Calendar.MAY, 19, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_28_harker_01, Calendar.MAY, 28, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_28_harker_02, Calendar.MAY, 28, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_28_harker_03, Calendar.MAY, 28, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_28_harker_04, Calendar.MAY, 28, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.may_31_harker, Calendar.MAY, 31, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_17_harker, Calendar.JUNE, 17, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_24_harker, Calendar.JUNE, 24, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_25_harker_01, Calendar.JUNE, 25, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_25_harker_02, Calendar.JUNE, 25, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_29_harker, Calendar.JUNE, 29, DIARY_ENTRY);
        addEntryToDatabase(4, JONATHAN_HARKER, R.raw.june_30_harker, Calendar.JUNE, 30, DIARY_ENTRY);

        // Chapter 5
        addEntryToDatabase(5, MINA_MURRAY, R.raw.may_09_murray, Calendar.MAY, 9, LETTER);
        addEntryToDatabase(5, LUCY_WESTENRA, R.raw.may_10_westenra, Calendar.MAY, 10, LETTER);
        addEntryToDatabase(5, LUCY_WESTENRA, R.raw.may_24_westenra_01, Calendar.MAY, 24, LETTER);
        addEntryToDatabase(5, LUCY_WESTENRA, R.raw.may_24_westenra_02, Calendar.MAY, 24, LETTER);
        addEntryToDatabase(5, SEWARD, R.raw.may_25_seward, Calendar.MAY, 25, PHONOGRAPH);
        addEntryToDatabase(5, QUINCEY_MORRIS, R.raw.may_25_morris, Calendar.MAY, 25, LETTER);
        addEntryToDatabase(5, ARTHUR_HOLMWOOD, R.raw.may_26_holmwood, Calendar.MAY, 26, TELEGRAM);

        // Chapter 6
        addEntryToDatabase(6, MINA_MURRAY, R.raw.july_24_murray, Calendar.JULY, 24, DIARY_ENTRY);
        addEntryToDatabase(6, MINA_MURRAY, R.raw.august_01_murray_01, Calendar.AUGUST, 1, DIARY_ENTRY);
        addEntryToDatabase(6, MINA_MURRAY, R.raw.august_01_murray_02, Calendar.AUGUST, 1, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.june_05_seward, Calendar.JUNE, 5, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.june_18_seward, Calendar.JUNE, 18, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.july_01_seward, Calendar.JULY, 1, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.july_08_seward, Calendar.JULY, 8, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.july_19_seward_01, Calendar.JULY, 19, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.july_19_seward_02, Calendar.JULY, 19, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.july_20_seward_01, Calendar.JULY, 20, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.july_20_seward_02, Calendar.JULY, 20, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.july_20_seward_03, Calendar.JULY, 20, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.july_26_murray, Calendar.JULY, 26, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.july_27_murray, Calendar.JULY, 27, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.august_03_murray, Calendar.AUGUST, 3, DIARY_ENTRY);
        addEntryToDatabase(6, SEWARD, R.raw.august_06_murray, Calendar.AUGUST, 6, DIARY_ENTRY);

        // Chapter 7
        addEntryToDatabase(7, MINA_MURRAY, R.raw.august_08_murray_01, Calendar.AUGUST, 8, DIARY_ENTRY);
        addEntryToDatabase(7, MINA_MURRAY, R.raw.august_09_murray, Calendar.AUGUST, 9, DIARY_ENTRY);
        addEntryToDatabase(7, MINA_MURRAY, R.raw.august_08_murray_02, Calendar.AUGUST, 8, DIARY_ENTRY);
        addEntryToDatabase(7, MINA_MURRAY, R.raw.august_10_murray_01, Calendar.AUGUST, 10, DIARY_ENTRY);

        // Chapter 8
        addEntryToDatabase(8, MINA_MURRAY, R.raw.august_10_murray_02, Calendar.AUGUST, 10, DIARY_ENTRY);
        addEntryToDatabase(8, MINA_MURRAY, R.raw.august_11_murray, Calendar.AUGUST, 11, DIARY_ENTRY);
        addEntryToDatabase(8, MINA_MURRAY, R.raw.august_12_murray, Calendar.AUGUST, 12, DIARY_ENTRY);
        addEntryToDatabase(8, MINA_MURRAY, R.raw.august_13_murray, Calendar.AUGUST, 13, DIARY_ENTRY);
        addEntryToDatabase(8, MINA_MURRAY, R.raw.august_14_murray, Calendar.AUGUST, 14, DIARY_ENTRY);
        addEntryToDatabase(8, MINA_MURRAY, R.raw.august_15_murray, Calendar.AUGUST, 15, DIARY_ENTRY);
        addEntryToDatabase(8, MINA_MURRAY, R.raw.august_17_murray, Calendar.AUGUST, 17, DIARY_ENTRY);
        addEntryToDatabase(8, SAMUEL_F_BILLINGTON, R.raw.august_17_samuel, Calendar.AUGUST, 17, LETTER);
        addEntryToDatabase(8, MESSRS, R.raw.august_21_messrs, Calendar.AUGUST, 21, LETTER);
        addEntryToDatabase(8, MINA_MURRAY, R.raw.august_18_murray, Calendar.AUGUST, 18, DIARY_ENTRY);
        addEntryToDatabase(8, MINA_MURRAY, R.raw.august_19_murray, Calendar.AUGUST, 19, DIARY_ENTRY);
        addEntryToDatabase(8, SISTER_AGATHA, R.raw.august_12_harker, Calendar.AUGUST, 12, LETTER);
        addEntryToDatabase(8, SEWARD, R.raw.august_19_seward, Calendar.AUGUST, 19, DIARY_ENTRY);

        // Chapter 9
        addEntryToDatabase(9, MINA_MURRAY, R.raw.august_24_murray, Calendar.AUGUST, 24, LETTER);
        addEntryToDatabase(9, LUCY_WESTENRA, R.raw.august_30_murray, Calendar.AUGUST, 30, LETTER);
        addEntryToDatabase(9, SEWARD, R.raw.august_20_seward, Calendar.AUGUST, 20, DIARY_ENTRY);
        addEntryToDatabase(9, SEWARD, R.raw.august_23_seward, Calendar.AUGUST, 23, DIARY_ENTRY);
        addEntryToDatabase(9, LUCY_WESTENRA, R.raw.august_24_westenra, Calendar.AUGUST, 24, DIARY_ENTRY);
        addEntryToDatabase(9, LUCY_WESTENRA, R.raw.august_25_westenra, Calendar.AUGUST, 25, DIARY_ENTRY);
        addEntryToDatabase(9, ARTHUR_HOLMWOOD, R.raw.august_31_holmwood, Calendar.AUGUST, 31, LETTER);
        addEntryToDatabase(9, ARTHUR_HOLMWOOD, R.raw.september_01_holmwood, Calendar.SEPTEMBER, 1, TELEGRAM);
        addEntryToDatabase(9, SEWARD, R.raw.september_02_seward, Calendar.SEPTEMBER, 2, LETTER);
        addEntryToDatabase(9, ABRAHAM_VAN_HELSING, R.raw.september_02_helsing, Calendar.SEPTEMBER, 2, LETTER);
        addEntryToDatabase(9, SEWARD, R.raw.september_03_seward, Calendar.SEPTEMBER, 3, LETTER);
        addEntryToDatabase(9, SEWARD, R.raw.september_04_seward_01, Calendar.SEPTEMBER, 4, DIARY_ENTRY);
        addEntryToDatabase(9, SEWARD, R.raw.september_04_seward_02, Calendar.SEPTEMBER, 4, TELEGRAM);
        addEntryToDatabase(9, SEWARD, R.raw.september_05_seward, Calendar.SEPTEMBER, 5, TELEGRAM);
        addEntryToDatabase(9, SEWARD, R.raw.september_06_seward_01, Calendar.SEPTEMBER, 6, TELEGRAM);

        // Chapter 10
        addEntryToDatabase(10, SEWARD, R.raw.september_06_seward_02, Calendar.SEPTEMBER, 6, LETTER);
        addEntryToDatabase(10, SEWARD, R.raw.september_07_seward, Calendar.SEPTEMBER, 7, DIARY_ENTRY);
        addEntryToDatabase(10, SEWARD, R.raw.september_08_seward, Calendar.SEPTEMBER, 8, DIARY_ENTRY);
        addEntryToDatabase(10, SEWARD, R.raw.september_09_seward, Calendar.SEPTEMBER, 9, DIARY_ENTRY);
        addEntryToDatabase(10, LUCY_WESTENRA, R.raw.september_09_westenra, Calendar.SEPTEMBER, 9, DIARY_ENTRY);
        addEntryToDatabase(10, SEWARD, R.raw.september_10_seward, Calendar.SEPTEMBER, 10, DIARY_ENTRY);
        addEntryToDatabase(10, SEWARD, R.raw.september_11_seward, Calendar.SEPTEMBER, 11, DIARY_ENTRY);

        // Chapter 11
        addEntryToDatabase(11, LUCY_WESTENRA, R.raw.september_13_seward, Calendar.SEPTEMBER, 12, DIARY_ENTRY);
        addEntryToDatabase(11, SEWARD, R.raw.september_13_seward, Calendar.SEPTEMBER, 13, DIARY_ENTRY);
        addEntryToDatabase(11, LUCY_WESTENRA, R.raw.september_17_westenra_01, Calendar.SEPTEMBER, 17, DIARY_ENTRY);
        addEntryToDatabase(11, PALL_MALL_GAZETTE, R.raw.september_18_pallmall, Calendar.SEPTEMBER, 18, NEWSPAPER);
        addEntryToDatabase(11, SEWARD, R.raw.september_17_seward, Calendar.SEPTEMBER, 17, DIARY_ENTRY);
        addEntryToDatabase(11, ABRAHAM_VAN_HELSING, R.raw.september_17_helsing, Calendar.SEPTEMBER, 17, TELEGRAM);
        addEntryToDatabase(11, SEWARD, R.raw.september_18_seward_01, Calendar.SEPTEMBER, 18, DIARY_ENTRY);
        addEntryToDatabase(11, LUCY_WESTENRA, R.raw.september_17_westenra_02, Calendar.SEPTEMBER, 17, DIARY_ENTRY);

        // Chapter 12
        addEntryToDatabase(12, SEWARD, R.raw.september_18_seward_02, Calendar.SEPTEMBER, 18, DIARY_ENTRY);
        addEntryToDatabase(12, SEWARD, R.raw.september_19_seward, Calendar.SEPTEMBER, 19, DIARY_ENTRY);
        addEntryToDatabase(12, MINA_MURRAY, R.raw.september_17_murray, Calendar.SEPTEMBER, 17, LETTER );
        addEntryToDatabase(12, PATRICK_HENNESSEY, R.raw.september_20_hennessey, Calendar.SEPTEMBER, 20, LETTER);
        addEntryToDatabase(12, MINA_MURRAY, R.raw.september_18_murray, Calendar.SEPTEMBER, 18, LETTER);
        addEntryToDatabase(12, SEWARD, R.raw.september_20_seward_01, Calendar.SEPTEMBER, 20, DIARY_ENTRY);

        // Chapter 13
        addEntryToDatabase(13, SEWARD, R.raw.september_20_seward_02, Calendar.SEPTEMBER, 20, DIARY_ENTRY);
        addEntryToDatabase(13, MINA_MURRAY, R.raw.september_22_murray, Calendar.SEPTEMBER, 22, DIARY_ENTRY);
        addEntryToDatabase(13, SEWARD, R.raw.september_22_seward, Calendar.SEPTEMBER, 22, DIARY_ENTRY);
        addEntryToDatabase(13, WESTMINISTER_GAZETTE, R.raw.september_25_westminster_01, Calendar.SEPTEMBER, 25, NEWSPAPER);
        addEntryToDatabase(13, WESTMINISTER_GAZETTE, R.raw.september_25_westminster_02, Calendar.SEPTEMBER, 25, NEWSPAPER);

        // Chapter 14
        addEntryToDatabase(14, MINA_MURRAY, R.raw.september_23_murray, Calendar.SEPTEMBER, 23, DIARY_ENTRY);
        addEntryToDatabase(14, MINA_MURRAY, R.raw.september_24_murray, Calendar.SEPTEMBER, 24, DIARY_ENTRY);
        addEntryToDatabase(14, ABRAHAM_VAN_HELSING, R.raw.september_24_helsing, Calendar.SEPTEMBER, 24, LETTER);
        addEntryToDatabase(14, MINA_MURRAY, R.raw.september_25_murray_01, Calendar.SEPTEMBER, 25, TELEGRAM);
        addEntryToDatabase(14, MINA_MURRAY, R.raw.september_25_murray_02, Calendar.SEPTEMBER, 25, DIARY_ENTRY);
        addEntryToDatabase(14, ABRAHAM_VAN_HELSING, R.raw.september_25_helsing, Calendar.SEPTEMBER, 25, LETTER);
        addEntryToDatabase(14, MINA_MURRAY, R.raw.september_25_murray_03, Calendar.SEPTEMBER, 25, LETTER);
        addEntryToDatabase(14, JONATHAN_HARKER, R.raw.september_26_harker, Calendar.SEPTEMBER, 26, DIARY_ENTRY);
        addEntryToDatabase(14, SEWARD, R.raw.september_26_seward_01, Calendar.SEPTEMBER, 26, DIARY_ENTRY);

        // Chapter 15
        addEntryToDatabase(15, SEWARD, R.raw.september_26_seward_02, Calendar.SEPTEMBER, 26, DIARY_ENTRY);
        addEntryToDatabase(15, SEWARD, R.raw.september_27_seward, Calendar.SEPTEMBER, 27, DIARY_ENTRY);
        addEntryToDatabase(15, ABRAHAM_VAN_HELSING, R.raw.september_27_helsing, Calendar.SEPTEMBER, 27, NOTE);
        addEntryToDatabase(15, SEWARD, R.raw.september_28_seward, Calendar.SEPTEMBER, 28, DIARY_ENTRY);
        addEntryToDatabase(15, SEWARD, R.raw.september_29_seward_01, Calendar.SEPTEMBER, 29, DIARY_ENTRY);

        // Chapter 16
        addEntryToDatabase(16, SEWARD, R.raw.september_29_seward_02, Calendar.SEPTEMBER, 29, DIARY_ENTRY);
        addEntryToDatabase(16, SEWARD, R.raw.september_29_seward_03, Calendar.SEPTEMBER, 29, DIARY_ENTRY);

        // Chapter 17
        addEntryToDatabase(17, SEWARD, R.raw.september_29_seward_04, Calendar.SEPTEMBER, 29, DIARY_ENTRY);
        addEntryToDatabase(17, MINA_HARKER, R.raw.september_29_mina_harker_01, Calendar.SEPTEMBER, 29, DIARY_ENTRY);
        addEntryToDatabase(17, SEWARD, R.raw.september_29_seward_05, Calendar.SEPTEMBER, 29, DIARY_ENTRY);
        addEntryToDatabase(17, MINA_HARKER, R.raw.september_29_mina_harker_02, Calendar.SEPTEMBER, 29, DIARY_ENTRY);
        addEntryToDatabase(17, SEWARD, R.raw.september_30_seward_01, Calendar.SEPTEMBER, 30, DIARY_ENTRY);
        addEntryToDatabase(17, JONATHAN_HARKER, R.raw.september_29_harker, Calendar.SEPTEMBER, 29, DIARY_ENTRY);
        addEntryToDatabase(17, JONATHAN_HARKER, R.raw.september_30_harker, Calendar.SEPTEMBER, 30, DIARY_ENTRY);
        addEntryToDatabase(17, MINA_HARKER, R.raw.september_30_mina_harker_01, Calendar.SEPTEMBER, 30, DIARY_ENTRY);

        // Chapter 18
        addEntryToDatabase(18, SEWARD, R.raw.september_30_seward_02, Calendar.SEPTEMBER, 30, DIARY_ENTRY);
        addEntryToDatabase(18, MINA_HARKER, R.raw.september_30_mina_harker_02, Calendar.SEPTEMBER, 30, DIARY_ENTRY);
        addEntryToDatabase(18, SEWARD, R.raw.october_01_seward_01, Calendar.OCTOBER, 1, DIARY_ENTRY);

        // Chapter 19
        addEntryToDatabase(19, JONATHAN_HARKER, R.raw.october_01_harker_01, Calendar.OCTOBER, 1, DIARY_ENTRY);
        addEntryToDatabase(19, SEWARD, R.raw.october_01_seward_02, Calendar.OCTOBER, 1, DIARY_ENTRY);
        addEntryToDatabase(19, MINA_HARKER, R.raw.october_01_mina_harker, Calendar.OCTOBER, 1, DIARY_ENTRY);
        addEntryToDatabase(19, MINA_HARKER, R.raw.october_02_mina_harker, Calendar.OCTOBER, 2, DIARY_ENTRY);

        // Chapter 20
        addEntryToDatabase(20, JONATHAN_HARKER, R.raw.october_01_harker_02, Calendar.OCTOBER, 1, DIARY_ENTRY);
        addEntryToDatabase(20, JONATHAN_HARKER, R.raw.october_02_harker, Calendar.OCTOBER, 2, DIARY_ENTRY);
        addEntryToDatabase(20, SEWARD, R.raw.october_01_seward_03, Calendar.OCTOBER, 1, DIARY_ENTRY);
        addEntryToDatabase(20, MITCHELL_AND_SONS, R.raw.october_01_mitchell, Calendar.OCTOBER, 1, LETTER);
        addEntryToDatabase(20, SEWARD, R.raw.october_02_seward, Calendar.OCTOBER, 2, DIARY_ENTRY);

        // Chapter 21
        addEntryToDatabase(21, SEWARD, R.raw.october_03_seward_01, Calendar.OCTOBER, 3, DIARY_ENTRY);

        // Chapter 22
        addEntryToDatabase(22, JONATHAN_HARKER, R.raw.october_03_harker, Calendar.OCTOBER, 3, DIARY_ENTRY);

        // Chapter 23
        addEntryToDatabase(23, SEWARD, R.raw.october_03_seward_02, Calendar.OCTOBER, 3, DIARY_ENTRY);
        addEntryToDatabase(23, JONATHAN_HARKER, R.raw.october_03_harker, Calendar.OCTOBER, 3, DIARY_ENTRY);
        addEntryToDatabase(23, JONATHAN_HARKER, R.raw.october_04_harker_01, Calendar.OCTOBER, 4, DIARY_ENTRY);
        addEntryToDatabase(23, JONATHAN_HARKER, R.raw.october_04_harker_02, Calendar.OCTOBER, 4, DIARY_ENTRY);

        View recyclerView = findViewById(R.id.entry_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        Context context = getApplicationContext();
        //  JobScheduler jobScheduler =  context.getSystemService(context.JOB_SCHEDULER_SERVICE);

        if (findViewById(R.id.entry_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void addEntryToDatabase(int chapterNum, String personName, int diaryResource, int month, int date, String type) {
        if (fragmentEntryHandler != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(1893, month - 1, date);

            fragmentEntryHandler.addEntry(new FragmentEntry(chapterNum, personName, getStringFromId(diaryResource), calendar, type));
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Calendar currentDate = Calendar.getInstance();

        int month = currentDate.get(Calendar.MONTH) + 1;
        int dateOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(1897, month, dateOfMonth);
        Log.d("Database", "Test");
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(fragmentEntryHandler.getDiaryEntriesBeforeDate(calendar)));
    }

    private String getStringFromId(int id) {
        InputStream stream = getResources().openRawResource((id));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();

        try {
            String line;
            do {
                line = reader.readLine();
                sb.append(line + "\n");
            }
            while (line != null);
        } catch (Exception e) {

        }

        return sb.toString();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<FragmentEntry> mValues;

        public SimpleItemRecyclerViewAdapter(List<FragmentEntry> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.entry_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).toString());
            holder.mContentView.setText(mValues.get(position).getFragmentEntry());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putShort(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM, holder.mItem.getStoryEntryNum());
                        EntryDetailFragment fragment = new EntryDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.entry_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();

                        Intent intent = new Intent(context, EntryDetailActivity.class);

                        short seqEntry = holder.mItem.getStoryEntryNum();
                        Calendar date = holder.mItem.getDate();

                        FragmentEntry fragmentEntry = fragmentEntryHandler.getSpecificDiaryEntry(seqEntry, date);

                        if (fragmentEntry != null) {
                            // insert the values
                            intent.putExtra(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM, fragmentEntry.getStoryEntryNum());

                            context.startActivity(intent);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public FragmentEntry mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
