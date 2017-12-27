package com.jensen.draculadaybyday.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.sql_lite.DateConstructorUtility;
import com.jensen.draculadaybyday.sql_lite.ExperienceMode;

import org.joda.time.DateTime;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class DraculaPreferences extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        /**
         * Listener that runs when the  value of a preference is updated
         * @param preference the preference that is updated
         * @param value the value of the preference
         * @return whether or not the preference was successfully changed
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            boolean returnValue = false;

            try {
                String stringValue = value.toString();

                if (preference instanceof ListPreference) {
                    // For list preferences, look up the correct display value in
                    // the preference's 'entries' list.
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);

                    // Set the summary to reflect the new value.
                    preference.setSummary(
                            index >= 0
                                    ? listPreference.getEntries()[index]
                                    : null);

                } else if (preference instanceof RingtonePreference) {
                    // For ringtone preferences, look up the correct display value
                    // using RingtoneManager.
                    if (TextUtils.isEmpty(stringValue)) {
                        // Empty values correspond to 'silent' (no ringtone).
                        // FIXME: fix set the ringtone to silent
                        //    preference.setSummary(R.string.pref_ringtone_silent);

                    } else {
                        Ringtone ringtone = RingtoneManager.getRingtone(
                                preference.getContext(), Uri.parse(stringValue));

                        if (ringtone == null) {
                            // Clear the summary if there was a lookup error.
                            preference.setSummary(null);
                        } else {
                            // Set the summary to reflect the new ringtone display
                            // name.
                            String name = ringtone.getTitle(preference.getContext());
                            preference.setSummary(name);
                        }
                    }

                } else {
                    // For all other preferences, set the summary to the value's
                    // simple string representation.
                    preference.setSummary(stringValue);
                }

                returnValue = true;
            } catch (Exception e) {
                Log.e("Preference Exception", e.getMessage());
            }

            return returnValue;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     *
     * @param context the context of the application
     * @return whether or not we are dealing with an extra large tablet
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @param preference the preference that we want to bind to a value
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static SharedPreferences prefs;

    /**
     * Called when the preferences are created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //TODO: Make the code that monitors if the preferences change

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * When an item is selected in the options menu
     * @param item the item in the menu
     * @return if the selection was successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || UserInterfacePreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    //region General Preference
    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        // How to experience the book
        private static String howToExperienceKey = null;
        // Reset key
        private static String resetKey = null;

        private static final Preference.OnPreferenceChangeListener sExperiencePreferenceUpdate = new Preference.OnPreferenceChangeListener() {

            /**
             * What happens when you update the experience preference
             * @param preference the preference object to update
             * @param newValue the new value for the experience value
             * @return whether the preference was updated successfully
             */
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences.Editor prefEditor = prefs.edit();

                try {
                    if (preference.getKey().equals(howToExperienceKey)) {
                        ExperienceMode mode = newValue.toString().equals(preference.getContext().getString(R.string.pref_experience_default_value))
                                ? ExperienceMode.EXPERIENCE_ON_SAME_DAY : ExperienceMode.EXPERIENCE_IN_SAME_TEMPO;
                        if (mode == ExperienceMode.EXPERIENCE_IN_SAME_TEMPO) {
                            resetTime(prefEditor, preference.getContext());
                        }
                    } else if (preference.getKey().equals(resetKey)) {
                        Boolean reset = Boolean.valueOf(newValue.toString());
                        if (reset) {
                            String howToExperience = Resources.getSystem().getString(R.string.pref_key_how_to_experience);
                            String defaultValue = Resources.getSystem().getString(R.string.pref_experience_default_value);
                            ExperienceMode mode = prefs.getString(howToExperience, defaultValue).equals(defaultValue)
                                    ? ExperienceMode.EXPERIENCE_ON_SAME_DAY : ExperienceMode.EXPERIENCE_IN_SAME_TEMPO;

                            if (mode == ExperienceMode.EXPERIENCE_IN_SAME_TEMPO) {
                                resetTime(prefEditor, preference.getContext());
                            } else {
                            /*
                            if(mode == ExperienceMode.EXPERIENCE_ON_SAME_DAY && !(firstDayOfJanuary.isBefore(today) && initialDate.isAfter(today))) {
                                // if we are not in the interval first of January to the third of May, then the year is 1892
                                today.withYear(1892);
                            }
                            */
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("PreferenceChanged", e.getMessage());
                }
                return true;
            }
        };

        /**
         * Reset the time for the preferences
         * @param prefEditor the preference editor
         * @param context the context of the application
         */
        private static void resetTime(SharedPreferences.Editor prefEditor, Context context) {
            DateTime today = DateConstructorUtility.todayInThePast();
            prefEditor.putLong(context.getString(R.string.pref_key_start_date_time), today.getMillis());
            prefEditor.apply();
        }

        /**
         * When we create the experience preference
         * @param savedInstanceState
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_experience);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            howToExperienceKey = getString(R.string.pref_key_how_to_experience);
            resetKey = getString(R.string.pref_reset_book_key);

            findPreference(howToExperienceKey)
                    .setOnPreferenceChangeListener(sExperiencePreferenceUpdate);
            findPreference(resetKey)
                    .setOnPreferenceChangeListener(sExperiencePreferenceUpdate);
        }
    }
    //endregion

    //region User Interface Preference
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UserInterfacePreferenceFragment extends PreferenceFragment {

        // The Font example
        private static EntryViewPreference fontExample = null;

        // The List preferences
        private static ListPreference fontPreference = null;
        private static ListPreference initialPreference = null;

        // The name of the keys (which cannot be accessed during the preference update)
        private static String key_initial_type;

        private static final Preference.OnPreferenceChangeListener sFontPreferenceUpdate = new Preference.OnPreferenceChangeListener() {
            /**
             * When the preference is changed
             * @param preference the preference that is to be changed
             * @param value the new value that is to be changed
             * @return
             */
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                fontExample.updateToPreferences();

                if (preference.getKey().equals(key_initial_type)) {
                    setEnabledStateOfInitial(value.toString());
                }
                return true;
            }
        };

        /**
         * Set whether or not the intial is enabled or disabled
         * @param value the value of the type - the state is only set to true if the font contains "initial"
         */
        private static void setEnabledStateOfInitial(String value) {
            if (fontPreference != null && initialPreference != null) {
                String fontPreferenceValue = value.toLowerCase();
                initialPreference.setEnabled(fontPreferenceValue.contains("initial"));
            }
        }

        /**
         * Create the preference fragment
         * @param savedInstanceState the saved instance state
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_user_interface);
            setHasOptionsMenu(true);

            fontExample = (EntryViewPreference) findPreference(getString(R.string.pref_key_font_example));

            fontPreference = (ListPreference) findPreference((getString(R.string.pref_key_font_type)));
            initialPreference = (ListPreference) findPreference(getString(R.string.pref_key_initial_type));

            key_initial_type = getString(R.string.pref_key_initial_type);

            setUpdatePref(R.string.pref_key_font_type);
            setUpdatePref(R.string.pref_key_font_size);
            setUpdatePref(R.string.pref_key_initial_type);

            setEnabledStateOfInitial(fontPreference.getValue());
        }

        /**
         * Register the preference id with the change listener
         * @param prefId the id that will be registered with the listener
         */
        private void setUpdatePref(int prefId) {
            findPreference(getString(prefId)).setOnPreferenceChangeListener(sFontPreferenceUpdate);
        }

        /**
         * When an item is selected in the options menu
         * @param item the item in the menu
         * @return if the selection was successful
         */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), DraculaPreferences.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
    //endregion

    //region Notification Preference
    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            // bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        /**
         * When the option is selected
         * @param item the item that was selected
         * @return whether or not it was correctly executed
         */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            boolean returnValue;

            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), DraculaPreferences.class));
                returnValue = true;
            } else {
                returnValue = super.onOptionsItemSelected(item);
            }

            return returnValue;
        }
    }
    //endregion
}
