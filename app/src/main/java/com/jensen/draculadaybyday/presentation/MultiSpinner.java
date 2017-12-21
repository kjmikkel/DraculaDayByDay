package com.jensen.draculadaybyday.presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jensen.draculadaybyday.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Inspired by: http://stackoverflow.com/a/6022474/1521064
 */
public class MultiSpinner extends android.support.v7.widget.AppCompatSpinner {

    private final CharSequence[] entries;
    private boolean[] selected;
    private MultiSpinnerListener listener;

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiSpinner);
        entries = a.getTextArray(R.styleable.MultiSpinner_android_entries);
        if (entries != null) {
            selected = new boolean[entries.length]; // false-filled by default
        }
        if (0 < selected.length) {
            selected[0] = true;
        }
        a.recycle();
    }

    public List<String> getSelected() {
        return getSelectedEntries();
    }

    private final OnMultiChoiceClickListener mOnMultiChoiceClickListener = new OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int position, boolean isChecked) {
            selected[position] = isChecked;
            AlertDialog alertDialog = (AlertDialog)dialog;

            ListView mListView = alertDialog.getListView();
            // Turn off the "All" item it is selected AND we are selecting something other item AND that item is being turend on
            if (mListView.isItemChecked(0) && 0 < position && isChecked) {
                selected[0] = false;
                mListView.performItemClick(mListView, 0, 0);
            } else if (mListView.isItemChecked(0) && position == 0) {
                for(int i = 1; i < mListView.getCount(); i++) {
                    if (mListView.isItemChecked(i)) {
                        mListView.performItemClick(mListView, i, 0);
                    }
                }
            }

            // If every item has been turned off, then we turn on the "All" item
            boolean anythingSelected = false;
            for(boolean items : selected) {
                anythingSelected |= items;
            }
            if(!anythingSelected) {
                mListView.performItemClick(mListView, 0, 0);
            }
        }
    };


    private List<String> getSelectedEntries() {
        LinkedList<String> selectedEntries = new LinkedList<>();

        //region If nothing is selected, then selected the first element
        boolean anythingSelected = false;
        for(boolean items : selected) {
            anythingSelected |= items;
        }

        if (!anythingSelected) {
            selected[0] = true;
        }
        //endregion

        if (!selected[0]) {
            for (int i = 1; i < entries.length; i++) {
                if (selected[i]) {
                    selectedEntries.add(String.valueOf(entries[i]));
                }
            }
        } else {
            selectedEntries.add(String.valueOf(entries[0]));
        }

        return selectedEntries;
    }

    public void setSelectedEntries(List<String> entriesToSelect) {
        // unset all values
        for(int i = 0; i < selected.length; i++) {
            selected[i] = false;
        }

        // Set the values
        int entriesToSelectIndex = 0;
        for(int i = 0; i < entries.length; i++) {
            if (entries[i].equals(entriesToSelect.get(entriesToSelectIndex))) {
                selected[i] = true;
                entriesToSelectIndex++;
                if (entriesToSelectIndex == entriesToSelect.size()) {
                    break;
                }
            }
        }

        // build new spinner text & delimiter management
        String finalSelectedString = TextUtils.join(", ", entriesToSelect);

        // display new text
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[] { finalSelectedString });
        setAdapter(adapter);

        if (listener != null) {
            listener.onItemsSelected(selected);
        }
    }

    private final DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // build new spinner text & delimiter management
            String finalSelectedString = TextUtils.join(", ", getSelectedEntries());

            // display new text
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item,
                    new String[] { finalSelectedString });
            setAdapter(adapter);

            if (listener != null) {
                listener.onItemsSelected(selected);
            }

            // hide dialog
            dialog.dismiss();
        }
    };

    @Override
    public boolean performClick() {
        new AlertDialog.Builder(getContext())
                .setMultiChoiceItems(entries, selected, mOnMultiChoiceClickListener)
                .setPositiveButton(android.R.string.ok, mOnClickListener)
                .show();

        return super.performClick();
    }

    @SuppressWarnings("unused")
    public void setMultiSpinnerListener(MultiSpinnerListener listener) {
        this.listener = listener;
    }

    @SuppressWarnings("unused")
    public interface MultiSpinnerListener {
        void onItemsSelected(boolean[] selected);
    }
}