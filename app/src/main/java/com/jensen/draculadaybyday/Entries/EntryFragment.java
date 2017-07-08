package com.jensen.draculadaybyday.Entries;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jensen.draculadaybyday.Entry.Entry;
import com.jensen.draculadaybyday.Presentation.EntryView;
import com.jensen.draculadaybyday.Presentation.FontEnum;
import com.jensen.draculadaybyday.Presentation.InitialEnum;
import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.SQLite.FragmentEntryDatabaseHandler;
import com.jensen.draculadaybyday.Preferences.FontSizePickerPreference;

/**
 * A fragment representing a single Entry detail screen.
 * This fragment is either contained in a {@link EntryListActivity}
 * in two-pane mode (on tablets) or a {@link EntryActivity}
 * on handsets.
 */
public class EntryFragment extends Fragment {
    /**
     * The fragment argument representing the sequence number for entry represents.
     */
    private static final String DEFAULT_TITLE = "Default title";
    private static final String DEFAULT_BODY = "Default body";

    private Entry entry;
    private EntryView entryView;

    private static FragmentEntryDatabaseHandler dbHandler = FragmentEntryDatabaseHandler.getInstance();
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EntryFragment() {
        // Left empty on purpose
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (dbHandler != null) {
            short entrySeqNum = getArguments().getShort(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM);
            entry = dbHandler.getSpecificDiaryEntry(entrySeqNum);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.entry_detail, null);
        entryView = (EntryView) rootView.findViewById(R.id.entry_view_detail);

        setText();

        return rootView;
    }

    @Override
    public void onResume() {
        // Set the text again
        setText();
        setToolbar(0);
        super.onResume();
    }

    public void setToolbar(int position) {
        Activity activity = this.getActivity();
        if (activity != null) {
        CollapsingToolbarLayout appBarLayout =
                (CollapsingToolbarLayout)activity.findViewById(R.id.toolbar_layout);

        if (appBarLayout != null ) {
            if (entry != null) {
                // Set the values
                appBarLayout.setTitle(entry.toString());
            } else {
                // Could not get a value from the database
                appBarLayout.setTitle(DEFAULT_TITLE);
            }
        }
        }
    }

    private void setText() {
        if (entryView != null) {

            String text;
            if (entry != null) {
                text = entry.getTextEntry();
            } else {
                text = DEFAULT_BODY;
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            String strInitial = preferences.getString("user_interface_pref_initial", getString(R.string.pref_default_value_initial_type));
            InitialEnum initialEnum = InitialEnum.valueOf(strInitial.toUpperCase().replace(" ", "_"));

            String strFont = preferences.getString("user_interface_pref_font", getString(R.string.pref_default_value_font_type));
            FontEnum fontEnum = FontEnum.valueOf(strFont.toUpperCase().replace(" ", "_"));

            float textSize = preferences.getFloat(FontSizePickerPreference.PREFERENCE_NAME, 14.0f);

           entryView.setText(text, initialEnum, fontEnum, textSize);
        }
    }
}



