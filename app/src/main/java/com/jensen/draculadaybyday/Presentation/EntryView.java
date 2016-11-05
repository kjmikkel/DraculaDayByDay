package com.jensen.draculadaybyday.Presentation;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

public class EntryView extends TextView {

    public EntryView(Context context) {
        super(context);
    }

    public EntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EntryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void SetText(String text, FontEnum fontEnum, float fontSize) {
        if (fontEnum == FontEnum.DEFAULT_FONT) {
            setTextSize(fontSize);
            setText(text);
        } else {
            AssetManager assetManager = getContext().getAssets();

            Typeface font1 = Typeface.createFromAsset(assetManager, "fonts/WieynkFrakturZier.ttf");
            Typeface font2 = Typeface.createFromAsset(assetManager, "fonts/RedivivaZierbuchstaben.ttf");

            SpannableStringBuilder SS = new SpannableStringBuilder(text);
            SS.setSpan (new CustomTypefaceSpan("", font2), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            SS.setSpan (new CustomTypefaceSpan("", font1), 1, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            setText(SS);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
