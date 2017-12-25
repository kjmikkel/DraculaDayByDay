package com.jensen.draculadaybyday.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.jensen.draculadaybyday.presentation.EntryView;
import com.jensen.draculadaybyday.presentation.FontEnum;
import com.jensen.draculadaybyday.presentation.InitialEnum;
import com.jensen.draculadaybyday.R;

public class EntryViewPreference extends Preference {
    private EntryView entryView;

    private String text = null;
    private FontEnum fontEnum = FontEnum.NONE;
    private InitialEnum initialEnum = InitialEnum.NONE;
    private float fontSize = 14.0f;

    public EntryViewPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray array = context.obtainStyledAttributes(
                attrs,
                R.styleable.EntryViewPreference,
                defStyle,
                0);

        try {
            // Get the text
            text = array.getString(R.styleable.EntryViewPreference_text);

            // Get the font
            String font = array.getString(R.styleable.EntryViewPreference_font);
            makeFontFromString(font);

            String initialFont = array.getString(R.styleable.EntryViewPreference_initial);
            makeInitialFromString(initialFont);

            // Get the font size
            fontSize = array.getFloat(R.styleable.EntryViewPreference_fontSize, 14.0f);
        } finally {
            array.recycle();
        }
    }

    public EntryViewPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void makeFontFromString(String font) {
        font = fixEnumValue(font);
        if (font != null) {
            try {
                fontEnum = FontEnum.valueOf(font);
            } catch (Exception e) {
                Log.e("Preferences", e.getMessage());
            }
        }
    }

    private void makeInitialFromString(String initialFont) {
        initialFont = fixEnumValue(initialFont);
        if (initialFont != null) {
            try {
                initialEnum = InitialEnum.valueOf(initialFont);
            } catch (Exception e) {
                Log.e("Preferences", e.getMessage());
            }
        }
    }

    private String fixEnumValue(String enumName) {
        if (enumName != null) {
            enumName = enumName.toUpperCase().replace(" ", "_");
        }

        return enumName;
    }

    private void setValuesForEntryView() {
        if (entryView != null) {
            entryView.setText(text, initialEnum, fontEnum, fontSize);
        }
    }

    public void updateToPreferences() {
        // Initialization
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        Resources res = getContext().getResources();

        // Get the font
        String fontString = sp.getString(res.getString(R.string.pref_key_font_type), "-1");
        if (!"-1".equals(fontString)) {
            makeFontFromString(fontString);
        }

        // Get the initial
        String initialString = sp.getString(res.getString(R.string.pref_key_initial_type), "-1");
        if (!"-1".equals(initialString)) {
            makeInitialFromString(initialString);
        }

        fontSize = sp.getFloat(res.getString(R.string.pref_key_font_size), fontSize);

        entryView.setText(text, initialEnum, fontEnum, fontSize);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = null;
        try {
            view = super.onCreateView(parent);
            entryView = (EntryView) view.findViewById(R.id.entry_view_widget);
        } catch (Exception e) {
            Log.e("Preferences", e.getMessage());
        }
        setValuesForEntryView();

        return view;
    }

    @Override
    protected void onBindView(View view) {
        try {
            super.onBindView(view);
            entryView = (EntryView) view.findViewById(R.id.entry_view_widget);
            updateToPreferences();
        } catch (Exception e) {
            Log.e("Preferences", e.getMessage());
        }

        setValuesForEntryView();
    }
}
