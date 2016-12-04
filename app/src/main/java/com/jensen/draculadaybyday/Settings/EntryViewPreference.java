package com.jensen.draculadaybyday.Settings;

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

import com.jensen.draculadaybyday.Presentation.EntryView;
import com.jensen.draculadaybyday.Presentation.FontEnum;
import com.jensen.draculadaybyday.Presentation.InitialEnum;
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
            String font = fixEnumValue(array.getString(R.styleable.EntryViewPreference_font));
            makeFontFromString(font);

            String initialFont = fixEnumValue(array.getString(R.styleable.EntryViewPreference_initial));
            makeInitialFromString(initialFont);

           // Get the font size
           fontSize = array.getFloat(R.styleable.EntryViewPreference_fontSize, 14.0f);
        } finally {
            array.recycle();
        }
    }

    private void makeFontFromString(String font) {
        if (font != null) {
            try {
                fontEnum = FontEnum.valueOf(font);
            } catch (Exception e) {
                Log.e("Settings", e.getMessage());
            }
        }
    }

    private void makeInitialFromString(String initialFont) {
        if (initialFont != null) {
            try {
                initialEnum = InitialEnum.valueOf(initialFont);
            } catch (Exception e) {
                Log.e("Settings", e.getMessage());
            }
        }
    }

    public EntryViewPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
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

    /*
    @Override
    public boolean onPreferenceChange(Preference preference, Object value)
    {
        String textValue = value.toString();

        ListPreference listPreference = (ListPreference) preference;
        int index = listPreference.findIndexOfValue(textValue);

        CharSequence[] entries = listPreference.getEntries();

        if(index >= 0)
            Toast.makeText(preference.getContext(), entries[index], Toast.LENGTH_LONG);

        return true;
    }
    */

    private void updateToPreferences() {
        // Initialization
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        Resources res = getContext().getResources();

        // Get the font
        String fontString = sp.getString(res.getString(R.string.pref_key_font_type), "-1");
        if (fontString != "-1") {
            makeFontFromString(fontString);
        }

        // Get the initial
        String initialString = sp.getString(res.getString(R.string.pref_key_initial_type), "-1");
        if (initialString != "-1") {
            makeInitialFromString(initialString);
        }

        fontSize = sp.getFloat(res.getString(R.string.pref_key_fontsize), fontSize);

        entryView.setText(text, initialEnum, fontEnum, fontSize);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = null;
        try {
            view = super.onCreateView(parent);
            entryView = (EntryView) view.findViewById(R.id.entry_view_widget);
        } catch (Exception e) {
            Log.e("Settings", e.getMessage());
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
        } catch(Exception e) {
            Log.e("Settings", e.getMessage());
        }

        setValuesForEntryView();
    }
}
