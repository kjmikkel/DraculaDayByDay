package com.jensen.draculadaybyday.Presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.jensen.draculadaybyday.Primitives.Tuple;

import java.util.regex.Pattern;

public class EntryView extends TextView {

    private static final int SIZE_UNIT = TypedValue.COMPLEX_UNIT_DIP;

    private static final Pattern date = Pattern.compile("[0-9]+");

    public EntryView(Context context) {
        super(context);
    }

    public EntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EntryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setText(String text, InitialEnum initialEnum, FontEnum fontEnum, float fontSize) {
        if (!fontEnum.withInitial()) {
            setTextWithoutInitial(text, fontEnum, fontSize);
        } else {
            setTextWithInitial(text, initialEnum, fontEnum, fontSize);
        }
    }

    private void setTextWithoutInitial(String text, FontEnum fontEnum, float fontSize) {
        setTextSize(SIZE_UNIT,fontSize);

        String finalText = "";

        String dateStr = null;
        Tuple<Integer, Integer> dateInterval = findDatePart(text);
        if (dateInterval.fst != -1 && dateInterval.snd != -1) {
            dateStr = text.substring(dateInterval.fst, dateInterval.snd) + "\n";
            finalText += dateStr;
        }

        String locationStr = null;
        Tuple<Integer, Integer> locationInterval = findLocation(text);
        if (locationInterval.fst != -1 && locationInterval.snd != -1) {
            locationStr = text.substring(locationInterval.fst, locationInterval.snd) + "\n\n";
            finalText += locationStr;
        }

        Tuple<Integer, Integer> entryInterval = findEntry(text);
        if (entryInterval.fst != -1 && entryInterval.snd != -1) {
            finalText  += text.substring(entryInterval.fst, entryInterval.snd);
        }

        int currentIndex = 0;
        SpannableStringBuilder SS = new SpannableStringBuilder(finalText);
        if (dateInterval != null) {
            SS.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), currentIndex, currentIndex + dateStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            currentIndex += dateStr.length();
        }
        if (locationInterval != null) {
            SS.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), currentIndex, currentIndex + locationStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        SS.setSpan(new CustomTypefaceSpan("", getMainBodyType(fontEnum)), 0, finalText.length() - 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        setText(SS);
    }

    private void setTextWithInitial(String text, InitialEnum initialFont, FontEnum basicFont, float fontSize) {
        setTextSize(SIZE_UNIT, fontSize);

        String finalText = "";

        String dateStr = null;
        Tuple<Integer, Integer> dateInterval = findDatePart(text);
        if (dateInterval.fst != -1 && dateInterval.snd != -1) {
            dateStr = text.substring(dateInterval.fst, dateInterval.snd) + "\n";
            finalText += dateStr;
        }

        String locationStr = null;
        Tuple<Integer, Integer> locationInterval = findLocation(text);
        if (locationInterval.fst != -1 && locationInterval.snd != -1) {
            locationStr = text.substring(locationInterval.fst, locationInterval.snd) + "\n\n";
            finalText += locationStr;
        }

        Tuple<Integer, Integer> entryInterval = findEntry(text);
        if (entryInterval.fst != -1 && entryInterval.snd != -1) {
            finalText  += text.substring(entryInterval.fst, entryInterval.snd);
        }

        int currentIndex = 0;
        SpannableStringBuilder SS = new SpannableStringBuilder(finalText);
        if (dateInterval != null) {
            SS.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), currentIndex, currentIndex + dateStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            SS.setSpan(new CustomTypefaceSpan("", getMainBodyType(basicFont)),    currentIndex, currentIndex + dateStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            currentIndex += dateStr.length();
        }
        if (locationInterval != null) {
            SS.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), currentIndex, currentIndex + locationStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            SS.setSpan(new CustomTypefaceSpan("", getMainBodyType(basicFont)),    currentIndex, currentIndex + locationStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            currentIndex += locationStr.length();
        }

        SS.setSpan(new CustomTypefaceSpan("", getInitialType(initialFont)), currentIndex, currentIndex + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan(new CustomTypefaceSpan("", getMainBodyType(basicFont)), currentIndex + 1, finalText.length() - 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        setText(SS);
    }

    private Tuple<Integer, Integer> findDatePart(String text) {
        return parseText("d", text);
    }

    private Tuple<Integer, Integer> findLocation(String text) {
        return parseText("l", text);
    }

    private Tuple<Integer, Integer> findEntry(String text) {
        return parseText("e", text);
    }

    private Tuple<Integer, Integer> parseText(String indicator, String text) {
        String start = String.format("[%s]", indicator);
        String end = String.format("[/%s]", indicator);

        int startIndex = text.indexOf(start) + start.length();
        int endIndex = text.indexOf(end);

        return new Tuple<>(startIndex, endIndex);
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

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
