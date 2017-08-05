package com.jensen.draculadaybyday.sql_lite.DateConstraintArg;

import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public abstract class DateConstraintArg implements Parcelable {

    DateTime dateTime;

    public DateTime getDate() {
        return dateTime;
    }

    public void setDate(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    final DateTime initialDateTime = new DateTime(1893, DateTimeConstants.MAY, 3, 0, 0, 0, 0);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValueDefault() {
        return initialDateTime.equals(dateTime);
    }

    public abstract String getSQLText();
}