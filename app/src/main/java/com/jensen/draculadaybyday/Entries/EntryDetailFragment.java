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

import com.jensen.draculadaybyday.Fragment.FragmentEntry;
import com.jensen.draculadaybyday.Presentation.EntryView;
import com.jensen.draculadaybyday.Presentation.FontEnum;
import com.jensen.draculadaybyday.Presentation.InitialEnum;
import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.SQLite.FragmentEntryDatabaseHandler;
import com.jensen.draculadaybyday.Settings.FontSizePickerPreference;

/**
 * A fragment representing a single Entry detail screen.
 * This fragment is either contained in a {@link EntryListActivity}
 * in two-pane mode (on tablets) or a {@link EntryDetailActivity}
 * on handsets.
 */
public class EntryDetailFragment extends Fragment {
    /**
     * The fragment argument representing the sequence number for entry represents.
     */
    private static final String DEFAULT_TITLE = "Default title";
    private static final String DEFAULT_BODY = "Default body";

    private EntryView entryView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EntryDetailFragment() {
        // Left empty on purpose
    }

    private FragmentEntry getEntryFromArgument() {
        FragmentEntry entry = null;
        FragmentEntryDatabaseHandler dbHandler = FragmentEntryDatabaseHandler.getInstance();

        if (dbHandler != null) {
            short entrySeqNum = getArguments().getShort(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM);
            entry = dbHandler.getSpecificDiaryEntry(entrySeqNum);
        }

        return entry;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments().containsKey(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                    activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                FragmentEntry entry = getEntryFromArgument();

                if (entry != null) {
                    // Set the values
                    appBarLayout.setTitle(entry.toString());
                } else {
                    // Could not get a value from the database
                    appBarLayout.setTitle(DEFAULT_TITLE);
                }
            } else {
                appBarLayout.setTitle(DEFAULT_TITLE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.entry_detail, null);

        entryView = (EntryView) rootView.findViewById(R.id.entry_view_detail);
        setText();

        return rootView;
    }

    private void setText() {
        if (entryView != null) {
            FragmentEntry entry = getEntryFromArgument();

            String text;
            if (entry != null) {
                text = entry.getFragmentEntry();
            } else {
                text = DEFAULT_BODY;
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            String strInitial = preferences.getString("display_pref_initial", getString(R.string.pref_default_value_initial_type));
            InitialEnum initialEnum = InitialEnum.valueOf(strInitial.toUpperCase().replace(" ", "_"));

            String strFont = preferences.getString("display_pref_font", getString(R.string.pref_default_value_font_type));
            FontEnum fontEnum = FontEnum.valueOf(strFont.toUpperCase().replace(" ", "_"));

            float textSize = preferences.getFloat(FontSizePickerPreference.PREFERENCE_NAME, 14.0f);

            entryView.setText(text, initialEnum, fontEnum, textSize);
        }
    }
}
