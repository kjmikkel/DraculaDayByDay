package com.jensen.draculadaybyday.Entries;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jensen.draculadaybyday.Fragment.FragmentEntry;
import com.jensen.draculadaybyday.Presentation.EntryView;
import com.jensen.draculadaybyday.Presentation.FontEnum;
import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.SQLite.FragmentEntryDatabaseHandler;

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
    /*
    public static final String STORY_ENTRY_NUM = "sequenceNumber";
    public static final String ENTRY_PERSON_NUM = "entryPersonNumber";
    public static final String CHAPTER = "chapter";
    public static final String PERSON = "person";
    public static final String TEXT = "text";
    public static final String DATE = "date";
    public static final String TYPE = "type";
    */

    private static final String DEFUALT_TITLE = "Default title";
    private static final String DEFAULT_BODY = "Default body";

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
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                FragmentEntry entry = getEntryFromArgument();

                if (entry != null) {
                    // Set the values
                    appBarLayout.setTitle(entry.toString());
                } else {
                    // Could not get a value from the database
                    appBarLayout.setTitle(DEFUALT_TITLE);
                }
            } else {
                appBarLayout.setTitle(DEFUALT_TITLE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.entry_detail, container, false);
     //   EntryView view = (EntryView) rootView.findViewById(R.id.entry_detail_container);
        EntryView view = (EntryView) rootView;

        FragmentEntry entry = getEntryFromArgument();

        String text;
        if (entry != null) {
            text =  entry.getFragmentEntry();
        } else {
            text = DEFAULT_BODY;
        }

        view.SetText(text, FontEnum.GOTHIC_FONT, 20);

        return rootView;
    }
}
