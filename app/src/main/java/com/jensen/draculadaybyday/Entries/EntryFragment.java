package com.jensen.draculadaybyday.Entries;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
     * The fragment argument representing the sequence number for mEntry represents.
     */
    private static final String DEFAULT_TITLE = "Default title";
    private static final String DEFAULT_BODY = "Default body";

    private Entry mEntry;
    private EntryView mEntryView;

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
        if (mEntry == null) {
            short entrySeqNum = getArguments().getShort(FragmentEntryDatabaseHandler.ENTRY_SEQ_NUM);
            mEntry = dbHandler.getSpecificDiaryEntry(entrySeqNum);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.entry_detail, null);
        mEntryView = (EntryView) rootView.findViewById(R.id.entry_view_detail);

        setText();

        return rootView;
    }

    @Override
    public void onResume() {
        // Set the text again
        setText();

        super.onResume();
    }

    public String getTitle() {
        String title = DEFAULT_TITLE;

        if (mEntry != null) {
            title = mEntry.toString();
        }

        return title;
    }

    private void setText() {
        if (mEntryView != null) {

            String text = DEFAULT_BODY;
            if (mEntry != null) {
                text = mEntry.getTextEntry();
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            String strInitial = preferences.getString("user_interface_pref_initial", getString(R.string.pref_default_value_initial_type));
            InitialEnum initialEnum = InitialEnum.valueOf(strInitial.toUpperCase().replace(" ", "_"));

            String strFont = preferences.getString("user_interface_pref_font", getString(R.string.pref_default_value_font_type));
            FontEnum fontEnum = FontEnum.valueOf(strFont.toUpperCase().replace(" ", "_"));

            float textSize = preferences.getFloat(FontSizePickerPreference.PREFERENCE_NAME, 14.0f);

           mEntryView.setText(text, initialEnum, fontEnum, textSize);
        }
    }
}



