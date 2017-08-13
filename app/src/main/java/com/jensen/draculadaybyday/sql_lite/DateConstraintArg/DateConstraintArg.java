package com.jensen.draculadaybyday.sql_lite.DateConstraintArg;

import android.os.Parcelable;

import com.jensen.draculadaybyday.sql_lite.DateConstructorUtility;

import org.joda.time.DateTime;

public abstract class DateConstraintArg implements Parcelable {

    DateTime dateTime;

    public DateTime getDate() {
        return dateTime;
    }

    public void setDate(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValueDefault() {
        return DateConstructorUtility.initialDate.equals(dateTime);
    }

    public abstract String getSQLText();
}
