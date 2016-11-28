package com.jensen.draculadaybyday.Settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.support.annotation.IntegerRes;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.jensen.draculadaybyday.Primitives.Tuple;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class FontSizePickerPreference extends DialogPreference {

    // allowed range

    // enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = false;

    private NumberPicker pickerInteger;
    private NumberPicker pickerFractional;

    private Tuple<Integer, Integer> value;

    public FontSizePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FontSizePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.gravity = Gravity.CENTER;
        layoutParams.weight = 1;

        pickerInteger = new NumberPicker(getContext());
        pickerInteger.setLayoutParams(layoutParams);

        TextView separator = new TextView(getContext());
        separator.setText(".");
        separator.setLayoutParams(layoutParams);

        pickerFractional = new NumberPicker(getContext());
        pickerFractional.setLayoutParams(layoutParams);

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

        setPicker(pickerInteger, 5, 100, 14);
        setPicker(pickerFractional, 0, 9, 0);
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
        return a.getInt(index, 14);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
     //   Tuple<>
        setValue(restorePersistedValue ? getPersistedInt(14) : (Integer) defaultValue);
    }

    public void setValue(float value) {
       // this.value = value;
       // PersistInt()
    //    persistFloat(this.value);
    }

    public int getValue() {
        return 0;
      //  return this.value;
    }
}
