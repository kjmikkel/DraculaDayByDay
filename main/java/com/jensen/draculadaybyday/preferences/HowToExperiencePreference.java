package com.jensen.draculadaybyday.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.presentation.HowToExperienceHolder;
import com.jensen.draculadaybyday.sql_lite.DateConstructorUtility;
import com.jensen.draculadaybyday.sql_lite.ExperienceMode;

import java.time.LocalDateTime;

public class HowToExperiencePreference extends DialogPreference {

    private final Context mContext;

    public HowToExperiencePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setPersistent(true);
    }

    public HowToExperiencePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        setPersistent(true);
    }

    @Override
    protected View onCreateDialogView() {
        View view = View.inflate(mContext, R.layout.custom_listview, null);
        return setupView(view);
    }

    private View setupView(View view) {
        // Set the text
        TextView textView = view.findViewById(R.id.custom_list_view_description);
        textView.setText(mContext.getString(R.string.preference_how_to_experience));

        try {
            final RecyclerView recyclerView = view.findViewById(R.id.custom_list_view_list_items);
            String[] experienceChoices = mContext.getResources().getStringArray(R.array.list_preference_how_to_experience);
            String[] choiceDescription = mContext.getResources().getStringArray(R.array.list_preference_how_to_experience_description);
            HowToExperiencePreference.ExperienceViewAdapter experienceViewAdapter = new HowToExperiencePreference.ExperienceViewAdapter(experienceChoices, choiceDescription, mContext);
            recyclerView.setAdapter(experienceViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        } catch (Exception e) {
            Log.e("HowToExperience", e.getMessage());
        }

        return view;
    }

    public class ExperienceViewAdapter extends RecyclerView.Adapter<HowToExperienceHolder> {

        private final String[] headlines;
        private final String[] descriptions;
        private final Context mContext;

        private ExperienceViewAdapter(@NonNull String[] headlines, @NonNull String[] descriptions, Context context) throws Exception {
            this.headlines = headlines;
            this.descriptions = descriptions;
            this.mContext = context;

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
        public void onBindViewHolder(final HowToExperienceHolder holder, int position) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            // Set the views
            holder.setText(headlines[position]);
            holder.setImageViewText(descriptions[position]);

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor prefEditor = prefs.edit();
                    String experienceModeString = headlines[holder.getAdapterPosition()];

                    if (callChangeListener(experienceModeString)) {
                        prefEditor.putString(mContext.getString(R.string.pref_key_how_to_experience), experienceModeString);
                        prefEditor.putBoolean(mContext.getString(R.string.pref_key_first_run), false);

                        ExperienceMode mode = mContext.getString(R.string.pref_experience_default_value).equals(experienceModeString) ? ExperienceMode.EXPERIENCE_ON_SAME_DAY : ExperienceMode.EXPERIENCE_IN_SAME_TEMPO;

                        // If it is the same tempo, then we save the current date
                        if (mode == ExperienceMode.EXPERIENCE_IN_SAME_TEMPO) {
                            LocalDateTime today = DateConstructorUtility.todayInThePast();
                            prefEditor.putLong(mContext.getString(R.string.pref_key_start_date_time), DateConstructorUtility.getMilliseconds(today));
                            // We apply the reset
                            prefEditor.putBoolean(mContext.getString(R.string.pref_reset_book_key), true);
                        } else {
                            prefEditor.putBoolean(mContext.getString(R.string.pref_in_the_right_year), DateConstructorUtility.inRightYear());
                        }

                        prefEditor.apply();
                    }

                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                }
            });

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle(headlines[holder.getAdapterPosition()]);
                    alertDialog.setMessage(descriptions[holder.getAdapterPosition()]);
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
