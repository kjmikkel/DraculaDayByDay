package com.jensen.draculadaybyday.entries;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jensen.draculadaybyday.entry.Entry;
import com.jensen.draculadaybyday.presentation.EntryView;
import com.jensen.draculadaybyday.presentation.FontEnum;
import com.jensen.draculadaybyday.presentation.InitialEnum;
import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.sql_lite.FragmentEntryDatabaseHandler;
import com.jensen.draculadaybyday.preferences.FontSizePickerPreference;

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

    private static final FragmentEntryDatabaseHandler dbHandler = FragmentEntryDatabaseHandler.getInstance();
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
        @SuppressLint("InflateParams") final View rootView = inflater.inflate(R.layout.entry_detail, null);
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
            if (text != null && !DEFAULT_BODY.equals(text) && !text.isEmpty()) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                String fontType = preferences.getString(getString(R.string.pref_key_font_type), getString(R.string.pref_default_value_font_type));
                FontEnum fontEnum = FontEnum.valueOf(fontType.toUpperCase().replace(" ", "_"));

                String initialType = preferences.getString(getString(R.string.pref_key_initial_type), getString(R.string.pref_default_value_initial_type));
                InitialEnum initialEnum = InitialEnum.valueOf(initialType.toUpperCase().replace(" ", "_"));

                float textSize = preferences.getFloat(FontSizePickerPreference.PREFERENCE_NAME, 14.0f);

                mEntryView.setText(text, initialEnum, fontEnum, textSize);
            }
        }
    }
}



