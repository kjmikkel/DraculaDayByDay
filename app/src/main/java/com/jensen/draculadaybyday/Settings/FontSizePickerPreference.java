package com.jensen.draculadaybyday.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.jensen.draculadaybyday.R;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class FontSizePickerPreference extends DialogPreference {

    // allowed range for the pickerFractional
    private static final int MINIMAL_FRACTIONAL = 0;
    private static final int MAXIMUM_FRACTIONAL = 9;

    // enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker pickerInteger;
    private NumberPicker pickerFractional;

    // Minimum and maximum of the pickerInteger
    private int minimumInteger;
    private int maximumInteger;

    private int integerFontSize = -1;
    private int fractionalFontSize = -1;

    private float value;

    public FontSizePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get the attributes
        getAttributesArguments(context, attrs, 0);
    }

    public FontSizePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        // Get the attributes
        getAttributesArguments(context, attrs, defStyleAttr);
    }

    private void getAttributesArguments(Context context, AttributeSet attrs, int defStyle) {
        TypedArray array = context.obtainStyledAttributes(
                attrs,
                R.styleable.FontSizePickerPreference,
                defStyle,
                0);
        try {
            minimumInteger = array.getInt(R.styleable.FontSizePickerPreference_min, defStyle);
            maximumInteger = array.getInt(R.styleable.FontSizePickerPreference_max, defStyle);
        } finally {
            array.recycle();
        }
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.gravity = Gravity.CENTER;
        layoutParams.weight = 1;

        pickerInteger = new NumberPicker(getContext());
        pickerInteger.setLayoutParams(layoutParams);
        setPicker(pickerInteger, minimumInteger, maximumInteger, integerFontSize);

        TextView separator = new TextView(getContext());
        separator.setText(".");
        separator.setLayoutParams(layoutParams);

        pickerFractional = new NumberPicker(getContext());
        pickerFractional.setLayoutParams(layoutParams);
        setPicker(pickerFractional, MINIMAL_FRACTIONAL, MAXIMUM_FRACTIONAL, fractionalFontSize);

        LinearLayout dialogView = new LinearLayout(getContext());

        dialogView.addView(pickerInteger);
        dialogView.addView(separator);
        dialogView.addView(pickerFractional);

        FrameLayout frameLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;

        frameLayout.addView(dialogView, frameLayoutParams);

        return frameLayout;
    }

    private void setPicker(NumberPicker picker, int min, int max, int startValue) {
        picker.setMinValue(min);
        picker.setMaxValue(max);
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue(startValue);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            pickerInteger.clearFocus();
            pickerFractional.clearFocus();

            float valueInt = pickerInteger.getValue();
            float valueFrac = pickerFractional.getValue();

            float newValue = valueInt + (valueFrac / 10.0F);

            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getFloat(index, 14.0f);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        Float fDefaultValue = (Float) defaultValue;
        setValue(restorePersistedValue ? getPersistedFloat(fDefaultValue) : fDefaultValue);
    }

    public void setValue(float value) {
        // Set and save the value
        this.value = value;
        persistFloat(this.value);

        // Get the values for the number pickers
        String[] strValue = Float.toString(value).split("\\.");

        integerFontSize = Integer.valueOf(strValue[0]);
        fractionalFontSize = Integer.valueOf(strValue[1].substring(0, 1));

        integerFontSize = getWithinLimits(integerFontSize, minimumInteger, maximumInteger);
        fractionalFontSize = getWithinLimits(fractionalFontSize, MINIMAL_FRACTIONAL, MAXIMUM_FRACTIONAL);

        // Save the preference for later
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putFloat("font_size", value);
        editor.apply();
    }

    private int getWithinLimits(int value, int min, int max) {
        value = min <= value ? value : min;
        return value <= max ? value : max;
    }

    public float getValue() {
        return this.value;
    }
}
