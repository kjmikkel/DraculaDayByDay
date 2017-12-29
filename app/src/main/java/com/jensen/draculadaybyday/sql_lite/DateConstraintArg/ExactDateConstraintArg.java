package com.jensen.draculadaybyday.sql_lite.DateConstraintArg;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jensen.draculadaybyday.sql_lite.DateConstraint;
import com.jensen.draculadaybyday.sql_lite.DateConstructorUtility;

import org.joda.time.DateTime;

public class ExactDateConstraintArg extends DateConstraintArg {

    public ExactDateConstraintArg(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getSQLText() {
        return "%s = ?";
    }

    @Override
    public String toString() {
        return "Exact date";
    }

    //region Parcelable
    public static final Parcelable.Creator<ExactDateConstraintArg> CREATOR = new Parcelable.Creator<ExactDateConstraintArg>() {
        public ExactDateConstraintArg createFromParcel(Parcel in) {
            return new ExactDateConstraintArg(in);
        }

        public ExactDateConstraintArg[] newArray(int size) {
            return new ExactDateConstraintArg[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        try {
            out.writeSerializable(this.dateTime);
        } catch (Exception e) {
            Log.d("DateConstraint", e.getMessage());
        }
    }

    private ExactDateConstraintArg(Parcel in) {
        this.dateTime = (DateTime) in.readSerializable();
    }
    //endregion
}
