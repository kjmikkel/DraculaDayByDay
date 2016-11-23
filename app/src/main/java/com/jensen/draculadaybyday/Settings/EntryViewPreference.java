package com.jensen.draculadaybyday.Settings;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.jensen.draculadaybyday.Presentation.EntryView;
import com.jensen.draculadaybyday.Presentation.FontEnum;
import com.jensen.draculadaybyday.Presentation.InitialEnum;
import com.jensen.draculadaybyday.R;

public class EntryViewPreference extends Preference {
    private EntryView entryView;

    public EntryViewPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

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

        } finally {
            typedArray.recycle();
        }
        */
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        entryView = (EntryView) view.findViewById(R.id.entry_view);

        int i = 0;
        i++;
    }

    public void setText(String text, FontEnum fontEnum, float fontSize) {
        entryView.setText(text, fontEnum, fontSize);
    }

    public void setText(String text, InitialEnum initialEnum, FontEnum fontEnum, float fontSize) {
        entryView.setText(text, initialEnum, fontEnum, fontSize);
    }


}
