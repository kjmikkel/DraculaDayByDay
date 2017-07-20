package com.jensen.draculadaybyday.presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import com.jensen.draculadaybyday.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Inspired by: http://stackoverflow.com/a/6022474/1521064
 */
public class MultiSpinner extends android.support.v7.widget.AppCompatSpinner {

    private CharSequence[] entries;
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

    private OnMultiChoiceClickListener mOnMultiChoiceClickListener = new OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            selected[which] = isChecked;
        }
    };

    private List<String> getSelectedEntries() {
        LinkedList<String> selectedEntries = new LinkedList<>();

        //region If nothing is selected, then selected the frist element
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

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
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
        return true;
    }

    public void setMultiSpinnerListener(MultiSpinnerListener listener) {
        this.listener = listener;
    }

    public interface MultiSpinnerListener {
        public void onItemsSelected(boolean[] selected);
    }
}