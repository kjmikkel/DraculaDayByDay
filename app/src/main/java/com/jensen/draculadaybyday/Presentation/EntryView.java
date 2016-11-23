package com.jensen.draculadaybyday.Presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;
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

    public void setText(String text, FontEnum fontEnum, float fontSize) {
        setTextSize(TypedValue.COMPLEX_UNIT_DIP ,fontSize);
        setTypeface(getMainBodyType(fontEnum));
        setText(text);
    }

    public void setText(String text, InitialEnum initialFont, FontEnum basicFont, float fontSize) {
        setTextSize(fontSize);

        SpannableStringBuilder SS = new SpannableStringBuilder(text);
        SS.setSpan(new CustomTypefaceSpan("", getInitialType(initialFont)), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan(new CustomTypefaceSpan("", getMainBodyType(basicFont)), 1, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        setText(SS);
    }

    private Typeface makeFont(String fontName) {
        return Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
    }

    private Typeface getInitialType(InitialEnum font) {
        switch (font) {
            case PRECIOSA:
               return makeFont("Preciosa.ttf");
            case REDIVIVA_ZIERBUCHSTABEN:
            default:
                return makeFont("RedivivaZierbuchstaben.ttf");
        }
    }

    private Typeface getMainBodyType(FontEnum font) {
        switch (font) {
            case DROID_SANS_MONO:
                return Typeface.MONOSPACE;
            case DROID_SERIF:
                return Typeface.SERIF;
            case RAVALI:
                return makeFont("Ravali.ttf");
            case HARRINGTON:
                return makeFont("Harrington.ttf");
            case VICTORIAN:
                return makeFont("Victorian.ttf");
            case DROID_SANS:
            default:
                return Typeface.SANS_SERIF;
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
