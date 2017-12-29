package com.jensen.draculadaybyday.presentation;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import com.jensen.draculadaybyday.entry.EntryText;

public class EntryView extends AppCompatTextView {

    private static final int SIZE_UNIT = TypedValue.COMPLEX_UNIT_DIP;

    public EntryView(Context context) {
        super(context);
    }

    public EntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EntryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setText(EntryText entry, InitialEnum initialEnum, FontEnum fontEnum, float fontSize) {
        setLocalText(entry, initialEnum, fontEnum, fontSize);
    }

    private void setLocalText(EntryText entry, InitialEnum initialFont, FontEnum basicFont, float fontSize) {
        try {
            setTextSize(SIZE_UNIT, fontSize);

            String finalText = "";

            String dateStr = null;
            if (entry.hasDate()) {
                dateStr = entry.getDate() + "\n";
                finalText += dateStr;
            }

            String locationStr = null;
            if (entry.hasLocation()) {
                locationStr = entry.getLocation() + "\n\n";
                finalText += locationStr;
            }

            String commentStr = null;
            if (entry.hasComment()) {
                commentStr = entry.getComment() + "\n\n";
                finalText += commentStr;
            }

            if (entry.hasEntry()) {
                finalText += entry.getMainEntry();
            }

            int currentIndex = 0;
            SpannableStringBuilder SS = new SpannableStringBuilder(finalText);
            if (dateStr != null) {
                SS.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), currentIndex, currentIndex + dateStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                SS.setSpan(new CustomTypefaceSpan("", getMainBodyType(basicFont)), currentIndex, currentIndex + dateStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                currentIndex += dateStr.length();
            }
            if (locationStr != null) {
                SS.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), currentIndex, currentIndex + locationStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                SS.setSpan(new CustomTypefaceSpan("", getMainBodyType(basicFont)), currentIndex, currentIndex + locationStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                currentIndex += locationStr.length();
            }
            if (commentStr != null) {
                SS.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), currentIndex, currentIndex + commentStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                SS.setSpan(new CustomTypefaceSpan("", getMainBodyType(basicFont)), currentIndex, currentIndex + commentStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                currentIndex += commentStr.length();
            }

            if (basicFont.hasInitial()) {
                SS.setSpan(new CustomTypefaceSpan("", getInitialType(initialFont)), currentIndex, currentIndex + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                SS.setSpan(new RelativeSizeSpan(2.5f), currentIndex, currentIndex + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                currentIndex++;
            }

            SS.setSpan(new CustomTypefaceSpan("", getMainBodyType(basicFont)), currentIndex, finalText.length() - 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            setText(SS);
        } catch (Exception e) {
            Log.d("setLocalText", e.getMessage());
        }
    }

    private Typeface makeFont(String fontName) {
        return Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
    }

    private Typeface getInitialType(InitialEnum font) {
        switch (font) {
            case PRECIOSA:
                return makeFont("Preciosa.ttf");
            case WIEYNK_FRAKTUR_ZIER:
                return makeFont("WieynkFrakturZier.ttf");
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
            case VICTORIAN_WITH_INITIAL:
                return makeFont("Victorian.ttf");
            case DROID_SANS:
            default:
                return Typeface.SANS_SERIF;
        }
    }
}
