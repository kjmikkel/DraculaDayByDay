package com.jensen.draculadaybyday.Settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
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
            if (font != null) {
                try {
                    fontEnum = FontEnum.valueOf(font);
                } catch (Exception e) {
                    Log.e("Settings", e.getMessage());
                }
            }

            String initialFont = fixEnumValue(array.getString(R.styleable.EntryViewPreference_initial));
            if (initialFont != null) {
                try {
                    initialEnum = InitialEnum.valueOf(initialFont);
                } catch (Exception e) {
                    Log.e("Settings", e.getMessage());
                }
           }

           // Get the font size
           fontSize = array.getFloat(R.styleable.EntryViewPreference_fontSize, 14.0f);

            // Try to execute
            setValuesForEntryView();

            //          entryView.setText(text);

//        String text = attrs.getAttributeValue(textId);
            //      entryView.setText(text);

            //getResources().getResourceEntryName(R.attr.text);

            //attrs.

            //  this.entryView.setText(attrs.getAttributeValue);
            //   this.setWidgetLayoutResource(R.layout.entry_detail);
        /*
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EntryViewPreference, 0, 0);
        try {
            String text = typedArray.getString(R.styleable.EntryViewPreference_text);
            String initial = typedArray.getString(R.styleable.EntryViewPreference_initial);
            String font = typedArray.getString(R.styleable.EntryViewPreference_font);

            float fontSize = typedArray.getFloat(R.styleable.EntryViewPreference_fontSize, 14.0f);

            FontEnum fontEnum = FontEnum.valueOf(font);

            if (initial == null || initial.isEmpty()) {
                setText(text, fontEnum, fontSize);
            } else {
                InitialEnum initialEnum = InitialEnum.valueOf(initial);
                setText(text, initialEnum, fontEnum, fontSize);
            }
        */
        } finally {
            array.recycle();
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
        } catch(Exception e) {
            Log.e("Settings", e.getMessage());
        }

        setValuesForEntryView();
    }
}
