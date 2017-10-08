package com.jensen.draculadaybyday.presentation;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.sql_lite.DateConstructorUtility;
import com.jensen.draculadaybyday.sql_lite.ExperienceMode;

import org.joda.time.DateTime;

public class HowToExperienceDialog extends DialogFragment {

    private DialogCloseListener closeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.custom_listview, container);
    }

    public void addDismissListener(DialogCloseListener closeListener) {
        this.closeListener = closeListener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private View setupView(View view) {
        // Set the text
        TextView textView = (TextView) view.findViewById(R.id.custom_list_view_description);
        textView.setText(getString(R.string.preference_how_to_experience));

        try {
            Dialog dlg = getDialog();
            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.custom_list_view_list_items);
            String[] experienceChoices = getResources().getStringArray(R.array.list_preference_how_to_experience);
            String[] choiceDescription = getResources().getStringArray(R.array.list_preference_how_to_experience_description);
            ExperienceViewAdapter experienceViewAdapter = new ExperienceViewAdapter(experienceChoices, choiceDescription, dlg);
            recyclerView.setAdapter(experienceViewAdapter);

            dlg.setTitle(getString(R.string.pref_how_to_experience_title));
            setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            if (dlg.getWindow() != null) {
                lp.copyFrom(dlg.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                dlg.getWindow().setAttributes(lp);
            }
        } catch (Exception e) {
            Log.e("HowToExperience", e.getMessage());
        }

        return view;
    }
    /*
    public View getView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.custom_listview, container);
        return setupView(view);
    }
    */

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (closeListener != null) {
            closeListener.handleDialogClose(null);
        }
    }

    public class ExperienceViewAdapter extends RecyclerView.Adapter<HowToExperienceHolder> {

        private final String[] headlines;
        private final String[] descriptions;
        private final Dialog dlg;

        public ExperienceViewAdapter(String[] headlines, String[] descriptions, Dialog dlg) throws Exception {
            this.headlines = headlines;
            this.descriptions = descriptions;
            this.dlg = dlg;

            if (headlines.length != descriptions.length) {
                throw new Exception(String.format("Array of different size - headlines: %s, description: %s",
                        headlines.length, descriptions.length));
            }
        }

        @Override
        public HowToExperienceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.how_to_experience_bar, parent, false);
            return new HowToExperienceHolder(view);
        }

        @Override
        public void onBindViewHolder(HowToExperienceHolder holder, final int position) {
            final Context context = getContext();
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            // Set the views
            holder.setText(headlines[position]);
            holder.setImageViewText(descriptions[position]);

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor prefEditor = prefs.edit();
                    String experienceModeString = headlines[position];
                    prefEditor.putString(context.getString(R.string.pref_key_how_to_experience), experienceModeString);
                    prefEditor.putBoolean(context.getString(R.string.pref_key_first_run), false);

                    ExperienceMode mode = context.getString(R.string.pref_experience_default_value).equals(experienceModeString) ? ExperienceMode.EXPERIENCE_ON_SAME_DAY : ExperienceMode.EXPERIENCE_IN_SAME_TEMPO;

                    // If it is the same tempo, then we save the current date
                    if (mode == ExperienceMode.EXPERIENCE_IN_SAME_TEMPO) {
                        DateTime today = DateConstructorUtility.todayInThePast();
                        prefEditor.putLong(context.getString(R.string.pref_key_start_date_time), today.getMillis());
                    } else {
                        prefEditor.putBoolean(context.getString(R.string.pref_in_the_right_year), DateConstructorUtility.inRightYear());
                    }

                    prefEditor.apply();
                    if (dlg != null) {
                        dlg.dismiss();
                    }
                }
            });

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle(headlines[position]);
                    alertDialog.setMessage(descriptions[position]);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return headlines.length;
        }
    }
}
