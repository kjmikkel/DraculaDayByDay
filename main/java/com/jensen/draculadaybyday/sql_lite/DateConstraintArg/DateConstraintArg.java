package com.jensen.draculadaybyday.sql_lite.DateConstraintArg;

import android.os.Parcelable;

import com.jensen.draculadaybyday.sql_lite.DateConstructorUtility;

import java.time.LocalDateTime;

public abstract class DateConstraintArg implements Parcelable {

    LocalDateTime localDateTime;

    public LocalDateTime getDate() {
        return localDateTime;
    }

    public void setDate(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValueDefault() {
        return DateConstructorUtility.initialDate.equals(localDateTime);
    }

    public abstract String getSQLText();
}
