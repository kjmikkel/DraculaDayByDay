package com.jensen.draculadaybyday.Settings;

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

import com.jensen.draculadaybyday.Primitives.Tuple;
import com.jensen.draculadaybyday.R;

public class NotificationTime extends DialogPreference {

    public static final String PREFERENCE_TYPE_NAME = "notification_type";
    public static final String PREFERENCE_FROM_NAME = "notification_from";
    public static final String PREFERENCE_TO_NAME = "notification_to";
    public static final String PREFERENCE_SET_TIME = "notification_set_time";

    // Time
    private Tuple<Integer, Integer> setTime;
    private Tuple<Integer, Integer> fromTime;
    private Tuple<Integer, Integer> toTime;

    // Radio buttons
    private RadioButton setTimeRadioButton;
    private RadioButton intervalRadioButton;

    // Buttons
    private Button setButton;
    private Button fromButton;
    private Button toButton;

    // Text view
    private TextView setTextView;
    private TextView toTextView;
    private TextView fromTextView;

    private boolean setAt;

    private static final String TIME_FORMAT = "%02d:%02d";

    public static Tuple<Integer, Integer> getTimeTuple(CharSequence time) {
        String[] pieces = ((String)time).split(":");

        return new Tuple<>(Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1]));
    }

    private static String getTimeStringRep(Tuple<Integer, Integer> time) {
        return String.format(TIME_FORMAT, time.fst, time.snd);
    }

    private void getAttributesArguments(Context context, AttributeSet attrs, int defStyle) {
        TypedArray array = context.obtainStyledAttributes(
                attrs,
                R.styleable.NotificationTime,
                defStyle,
                0);

        try {
            fromTime = getTime(R.styleable.NotificationTime_fromHour, R.styleable.NotificationTime_fromMinute, array, defStyle);
            toTime = getTime(R.styleable.NotificationTime_toHour, R.styleable.NotificationTime_toMinute, array, defStyle);
            setTime = getTime(R.styleable.NotificationTime_setHour, R.styleable.NotificationTime_setMinute, array, defStyle);

            setAt = array.getBoolean(R.styleable.NotificationTime_setTime, true);
        } finally {
            array.recycle();
        }
    }

    private static Tuple<Integer, Integer> getTime(int hour, int minute, TypedArray array, int defStyle) {
        int fromHour =  array.getInt(hour, defStyle);
        int fromMinute = array.getInt(minute, defStyle);

        return new Tuple<>(fromHour, fromMinute);
    }

    public NotificationTime(Context context, AttributeSet attrs) {
        super(context, attrs);

        getAttributesArguments(context, attrs, 0);

        setPositiveButtonText(getContext().getString(R.string.pref_notification_set));
        setNegativeButtonText(getContext().getString(R.string.pref_notification_cancel));
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

        setTimeRadioButton = makeRadioButton(context, R.string.pref_notification_set_time_notification);
        intervalRadioButton = makeRadioButton(context, R.string.pref_notification_interval_notification);

        notificationView.setOrientation(LinearLayout.HORIZONTAL);
        notificationView.addView(setTimeRadioButton);
        notificationView.addView(intervalRadioButton);

        // notificationViewParams.addRule(RelativeLayout.TOP, notificationView.getId());
        notificationViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        //endregion

        //region Make the set at group
        final LinearLayout setAtView = new LinearLayout(context);
        setAtView.setId(View.generateViewId());

        setTextView = new TextView(context);
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
        fromTextView = new TextView(context);
        fromTextView.setText(context.getString(R.string.pref_notification_interval_from));

        fromButton = new Button(context);
        setupButton(fromButton, fromTime.fst, fromTime.snd);

        // To
        toTextView = new TextView(context);
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

        //region Begin setup
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
                layout.removeView(setAtView);
                layout.addView(intervalView, intervalViewParams);
            }
        });

        setTimeRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(intervalView);
                layout.addView(setAtView, setAtViewParams);
            }
        });
        //endregion

        return layout;
    }

    // Setup the buttons so that they can be use for setting the time
    private void setupButton(final Button button, int hour, int minute) {
        button.setText(String.format(TIME_FORMAT, hour, minute));

        final boolean isToButton = button == toButton;

        final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                button.setText(String.format(TIME_FORMAT, hourOfDay, minute));

                // We have to store the values in the correct data structure
                if (isToButton) {
                    if (setTimeRadioButton.isChecked()) {
                        setTime = new Tuple<>(hourOfDay, minute);
                    } else {
                        toTime = new Tuple<>(hourOfDay, minute);
                    }
                } else {
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
    protected void onBindDialogView(View v) { super.onBindDialogView(v); }

    @Override
    protected  void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // Save the preferences for later
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString(PREFERENCE_FROM_NAME, fromTime.fst + "," + fromTime.snd);
            editor.putString(PREFERENCE_TO_NAME, toTime.fst + "," + toTime.snd);
            editor.putString(PREFERENCE_SET_TIME, setTime.fst + "," + setTime.snd);
            editor.putBoolean(PREFERENCE_TYPE_NAME, setTimeRadioButton.isChecked());
            editor.apply();

            // Set the summary
            String newSummary;
            if (setTimeRadioButton.isChecked()) {
                newSummary = getContext().getString(R.string.pref_notification_summary_setat).replace("%s", getTimeStringRep(setTime));
            } else {
                newSummary = getContext().getString(R.string.pref_notification_summary_interval).replaceFirst("%%", getTimeStringRep(fromTime));
                newSummary = newSummary.replaceFirst("%%", getTimeStringRep(toTime));
            }

            setSummary(newSummary);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {

            /*
            SharedPreferences preference = getSharedPreferences();
            if (preference != null) {

            }
            */

        }

        /*
        Float fDefaultValue = (Float) defaultValue;

        if (fDefaultValue == null) {
            TypedValue outValue = new TypedValue();
            getContext().getResources().getValue(R.dimen.pref_default_fontsize, outValue, true);
            fDefaultValue = outValue.getFloat();
        }

        SharedPreferences preference = getSharedPreferences();
        if (preference != null) {
            setValue(preference.getFloat(PREFERENCE_NAME, fDefaultValue));
        } else {
            setValue(fDefaultValue);
        }
        */
    }
}