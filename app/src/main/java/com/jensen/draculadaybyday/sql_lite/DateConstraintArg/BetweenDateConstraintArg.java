package com.jensen.draculadaybyday.sql_lite.DateConstraintArg;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jensen.draculadaybyday.sql_lite.DateConstructorUtility;

import org.joda.time.DateTime;

public class BetweenDateConstraintArg extends DateConstraintArg {

    private DateTime endTime;

    public BetweenDateConstraintArg(DateTime startTime, DateTime endTime) {
        this.dateTime = startTime;
        this.endTime = endTime;
    }

    public DateTime getSecondDate() {
        return this.endTime;
    }

    public void setSecondDate(DateTime endTime) {
        this.endTime = endTime;
    }

    public String getSQLText() {
        return "(%s BETWEEN ? AND ?)";
    }

    @Override
    public String toString() {
        return "Between dates";
    }

    @Override
    public boolean isValueDefault() {
        return super.isValueDefault() && DateConstructorUtility.initialDate.equals(endTime);
    }

    //region Parcelable
    public static final Parcelable.Creator<BetweenDateConstraintArg> CREATOR = new Parcelable.Creator<BetweenDateConstraintArg>() {
        public BetweenDateConstraintArg createFromParcel(Parcel in) {
            return new BetweenDateConstraintArg(in);
        }

        public BetweenDateConstraintArg[] newArray(int size) {
            return new BetweenDateConstraintArg[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        try {
            out.writeSerializable(this.dateTime);
            out.writeSerializable(this.endTime);
        } catch (Exception e) {
            Log.d("DateConstraint", e.getMessage());
        }
    }

    private BetweenDateConstraintArg(Parcel in) {
        this.dateTime = (DateTime) in.readSerializable();
        this.endTime = (DateTime) in.readSerializable();
    }
    //endregion
}
