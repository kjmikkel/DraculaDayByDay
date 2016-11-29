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
import com.jensen.draculadaybyday.R;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class FontSizePickerPreference extends DialogPreference {

    // allowed range for the pickerFractional
    private static final int MINIMAL_FRACTIONAL = 0;
    private static final int MAXIMUM_FRACTIONAL = 9;

    // enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = false;

    private NumberPicker pickerInteger;
    private NumberPicker pickerFractional;

    private Tuple<Integer, Integer> value;

    // Minimum and maximum of the pickerInteger
    private int minimumInteger;
    private int maximumInteger;

    // The start value of the pickerInteger and pickerFractional
    private int integerComponent;
    private int fractionalComponent;

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

        minimumInteger = array.getInt(R.styleable.FontSizePickerPreference_min, defStyle);
        maximumInteger = array.getInt(R.styleable.FontSizePickerPreference_max, defStyle);

        integerComponent = array.getInt(R.styleable.FontSizePickerPreference_integer, defStyle);
        fractionalComponent = array.getInt(R.styleable.FontSizePickerPreference_fractional, defStyle);

        integerComponent = minimumInteger <= integerComponent ? integerComponent : minimumInteger;
        integerComponent = integerComponent <= maximumInteger ? integerComponent : maximumInteger;

        fractionalComponent = MINIMAL_FRACTIONAL <= fractionalComponent ? fractionalComponent : MINIMAL_FRACTIONAL;
        fractionalComponent = fractionalComponent <= MAXIMUM_FRACTIONAL ? fractionalComponent : MAXIMUM_FRACTIONAL;
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

        setPicker(pickerInteger, minimumInteger, maximumInteger, integerComponent);
        setPicker(pickerFractional, MINIMAL_FRACTIONAL, MAXIMUM_FRACTIONAL, fractionalComponent);
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
