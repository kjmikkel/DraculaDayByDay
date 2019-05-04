package com.jensen.draculadaybyday.sql_lite.DateConstraintArg;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jensen.draculadaybyday.sql_lite.DateConstructorUtility;

import java.time.LocalDateTime;

public class BetweenDateConstraintArg extends DateConstraintArg {

    private LocalDateTime endTime;

    public BetweenDateConstraintArg(LocalDateTime startTime, LocalDateTime endTime) {
        this.localDateTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getSecondDate() {
        return this.endTime;
    }

    public void setSecondDate(LocalDateTime endTime) {
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
            out.writeSerializable(this.localDateTime);
            out.writeSerializable(this.endTime);
        } catch (Exception e) {
            Log.d("DateConstraint", e.getMessage());
        }
    }

    private BetweenDateConstraintArg(Parcel in) {
        this.localDateTime = (LocalDateTime) in.readSerializable();
        this.endTime = (LocalDateTime) in.readSerializable();
    }
    //endregion
}
