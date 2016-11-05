package com.jensen.draculadaybyday.Presentation;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class CustomTypefaceSpan extends TypefaceSpan {
    private final Typeface newType;

    public CustomTypefaceSpan(String family, Typeface type) {
        super(family);
        newType = type;
    }

    public CustomTypefaceSpan(Parcel in) {
        super(in.readString());
        newType = Typeface.createFromFile(in.readString());
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, newType);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }
    /*
    public void writeToParcel(Parcel out) {
        out.writeString(getFamily());
        out.writeString(newType.);
    }
    */

    public static final Parcelable.Creator<CustomTypefaceSpan> CREATOR = new Parcelable.Creator<CustomTypefaceSpan>() {

        public CustomTypefaceSpan createFromParcel(Parcel in) {
            return new CustomTypefaceSpan(in);
        }

        public CustomTypefaceSpan[] newArray(int size) {
            return new CustomTypefaceSpan[size];
        }
    };
}
