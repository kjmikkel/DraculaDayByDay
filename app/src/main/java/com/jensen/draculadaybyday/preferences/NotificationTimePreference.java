package com.jensen.draculadaybyday.preferences;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jensen.draculadaybyday.primitives.Tuple;
import com.jensen.draculadaybyday.R;

import java.util.Locale;

public class NotificationTimePreference extends DialogPreference {

    // Preference IDs
    private static final String PREFERENCE_TYPE_NAME = "notification_type";
    private static final String PREFERENCE_FROM_NAME = "notification_from";
    private static final String PREFERENCE_TO_NAME = "notification_to";
    private static final String PREFERENCE_SET_TIME = "notification_set_time";
    private static final String TIME_FORMAT = "%02d:%02d";
    // Time
    private Tuple<Integer, Integer> setTime;
    private Tuple<Integer, Integer> fromTime;
    private Tuple<Integer, Integer> toTime;
    // Buttons
    private Button setButton;
    private Button fromButton;
    private Button toButton;
    private boolean setAt;

    public NotificationTimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get the attributes
        getAttributesArguments(context, attrs, 0);

        // Set the "Cancel" and "Set" buttons
        setNegativeButtonText(getContext().getString(R.string.pref_notification_cancel));
        setPositiveButtonText(getContext().getString(R.string.pref_notification_set));
    }

    public NotificationTimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Get the attributes
        getAttributesArguments(context, attrs, defStyleAttr);

        // Set the "Cancel" and "Set" buttons
        setNegativeButtonText(getContext().getString(R.string.pref_notification_cancel));
        setPositiveButtonText(getContext().getString(R.string.pref_notification_set));
    }

    private static Tuple<Integer, Integer> getTimeTuple(CharSequence time) {
        String[] pieces = ((String) time).split(":");
        return new Tuple<>(Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1]));
    }

    private static String getTimeStringRep(Tuple<Integer, Integer> time) {
        return String.format(Locale.getDefault(), TIME_FORMAT, time.fst, time.snd);
    }

    private void getAttributesArguments(Context context, AttributeSet attrs, int defStyle) {
        TypedArray array = context.obtainStyledAttributes(
                attrs,
                R.styleable.NotificationTimePreference,
                defStyle,
                0);

        try {
            setAt = array.getBoolean(R.styleable.NotificationTimePreference_setTime, true);
        } finally {
            array.recycle();
        }
    }

    @Override
    protected View onCreateDialogView() {
        final Context context = getContext();

        //region Setup the layouts
        final RelativeLayout layout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams notificationViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams intervalViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams setAtViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //endregion

        //region Make the radio group
        final RadioGroup notificationView = new RadioGroup(context);
        notificationView.setId(View.generateViewId());
        notificationView.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton setTimeRadioButton = makeRadioButton(context, R.string.pref_notification_set_time_notification);
        RadioButton intervalRadioButton = makeRadioButton(context, R.string.pref_notification_interval_notification);

        notificationView.setOrientation(LinearLayout.HORIZONTAL);
        notificationView.addView(setTimeRadioButton);
        notificationView.addView(intervalRadioButton);

        // notificationViewParams.addRule(RelativeLayout.TOP, notificationView.getId());
        notificationViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        //endregion

        //region Make the set at group
        final LinearLayout setAtView = new LinearLayout(context);
        setAtView.setId(View.generateViewId());

        TextView setTextView = new TextView(context);
        setTextView.setText(context.getString(R.string.pref_notification_interval_at_time));

        setButton = new Button(context);
        setupButton(setButton, setTime.fst, setTime.snd);

        setAtView.addView(setTextView);
        setAtView.addView(setButton);
        setAtView.setOrientation(LinearLayout.VERTICAL);

        setAtViewParams.addRule(RelativeLayout.BELOW, notificationView.getId());
        setAtViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        //endregion

        //region Make the interval group
        final LinearLayout intervalView = new LinearLayout(context);
        intervalView.setId(View.generateViewId());

        // From
        TextView fromTextView = new TextView(context);
        fromTextView.setText(context.getString(R.string.pref_notification_interval_from));

        fromButton = new Button(context);
        setupButton(fromButton, fromTime.fst, fromTime.snd);

        // To
        TextView toTextView = new TextView(context);
        toTextView.setText(context.getString(R.string.pref_notification_interval_to));

        toButton = new Button(context);
        setupButton(toButton, toTime.fst, toTime.snd);

        intervalView.addView(fromTextView);
        intervalView.addView(fromButton);
        intervalView.addView(toTextView);
        intervalView.addView(toButton);
        intervalView.setOrientation(LinearLayout.VERTICAL);

        intervalViewParams.addRule(RelativeLayout.BELOW, notificationView.getId());
        intervalViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        //endregion

        //region Layout setup
        layout.addView(notificationView, notificationViewParams);
        if (setAt) {
            setTimeRadioButton.setChecked(true);
            layout.addView(setAtView, setAtViewParams);
        } else {
            intervalRadioButton.setChecked(true);
            layout.addView(intervalView, intervalViewParams);
        }
        //endregion

        //region On button click
        intervalRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAt = false;
                layout.removeView(setAtView);
                layout.addView(intervalView, intervalViewParams);
            }
        });

        setTimeRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAt = true;
                layout.removeView(intervalView);
                layout.addView(setAtView, setAtViewParams);
            }
        });
        //endregion

        return layout;
    }

    // Setup the buttons so that they can be use for setting the time
    private void setupButton(final Button button, int hour, int minute) {
        button.setText(String.format(Locale.getDefault(), TIME_FORMAT, hour, minute));

        final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                button.setText(String.format(Locale.getDefault(), TIME_FORMAT, hourOfDay, minute));

                // We have to store the values in the correct data structure
                if (button == setButton) {
                    setTime = new Tuple<>(hourOfDay, minute);
                } else if (button == toButton) {
                    toTime = new Tuple<>(hourOfDay, minute);
                } else if (button == fromButton) {
                    fromTime = new Tuple<>(hourOfDay, minute);
                }
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Tuple<Integer, Integer> timeTuple = getTimeTuple(button.getText());

                TimePickerDialog timePick
                        = new TimePickerDialog(getContext(), listener, timeTuple.fst, timeTuple.snd, true);
                timePick.show();
            }
        });
    }

    private RadioButton makeRadioButton(Context context, int stringId) {
        RadioButton radioButton = new RadioButton(context);
        radioButton.setText(context.getResources().getString(stringId));
        return radioButton;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // Save the preferences for later
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString(PREFERENCE_FROM_NAME, fromTime.fst + ":" + fromTime.snd);
            editor.putString(PREFERENCE_TO_NAME, toTime.fst + ":" + toTime.snd);
            editor.putString(PREFERENCE_SET_TIME, setTime.fst + ":" + setTime.snd);
            editor.putBoolean(PREFERENCE_TYPE_NAME, setAt);
            editor.apply();

            persistBoolean(setAt);

            // Set the summary
            String newSummary;
            if (setAt) {
                newSummary = getContext().getString(R.string.pref_notification_summary_setat).replace("%s", getTimeStringRep(setTime));
            } else {
                newSummary = getContext().getString(R.string.pref_notification_summary_interval).replaceFirst("%%", getTimeStringRep(fromTime));
                newSummary = newSummary.replaceFirst("%%", getTimeStringRep(toTime));
            }

            setSummary(newSummary);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getTextArray(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);

        CharSequence[] defaultValueArray = (CharSequence[]) defaultValue;

        if (restorePersistedValue) {
            SharedPreferences preference = getSharedPreferences();
            if (preference != null) {
                setAt = preference.getBoolean(PREFERENCE_TYPE_NAME, true);

                String setTimeStr = preference.getString(PREFERENCE_SET_TIME, "");
                if (!setTimeStr.isEmpty()) {
                    setTime = getTimeTuple(setTimeStr);
                }

                String fromTimeStr = preference.getString(PREFERENCE_FROM_NAME, "");
                if (!fromTimeStr.isEmpty()) {
                    fromTime = getTimeTuple(fromTimeStr);
                }

                String toTimeStr = preference.getString(PREFERENCE_TO_NAME, "");
                if (!toTimeStr.isEmpty()) {
                    toTime = getTimeTuple(toTimeStr);
                }
            }
        } else {
            setAt = true;
            setTime = getTimeTuple(defaultValueArray[0]);
            fromTime = getTimeTuple(defaultValueArray[1]);
            toTime = getTimeTuple(defaultValueArray[2]);
        }
    }
}
