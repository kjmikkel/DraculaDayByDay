package com.jensen.draculadaybyday.Entries;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jensen.draculadaybyday.Fragment.FragmentEntry;
import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.SQLite.FragmentEntryDatabaseHandler;
import com.jensen.draculadaybyday.Settings.DraculaSettings;

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

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static FragmentEntryDatabaseHandler fragmentEntryHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                context.startActivity(new Intent(context, DraculaSettings.class));
            }
        });

        FragmentEntry entry = new FragmentEntry((short)1, (short)1, (short)1, "Mikkel", "Today I coded a bit - Yeah!", Calendar.getInstance(), "Hard coded entry");

        fragmentEntryHandler = FragmentEntryDatabaseHandler.getInstance(this);
        fragmentEntryHandler.addEntry(entry);

        View recyclerView = findViewById(R.id.entry_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.entry_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
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
