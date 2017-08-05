package com.jensen.draculadaybyday.sql_lite.DateConstraintArg;

import android.os.Parcel;
import android.os.Parcelable;

public class NoSpecificDateConstraintArg extends DateConstraintArg {

    public NoSpecificDateConstraintArg() {
        // Do nothing
    }

    public String getSQLText() {
        return "";
    }

    @Override
    public String toString() {
        return "No specific date";
    }

    //region Parcelable
    public static final Parcelable.Creator<NoSpecificDateConstraintArg> CREATOR = new Parcelable.Creator<NoSpecificDateConstraintArg>() {
        public NoSpecificDateConstraintArg createFromParcel(Parcel in) {
            return new NoSpecificDateConstraintArg(in);
        }

        public NoSpecificDateConstraintArg[] newArray(int size) {
            return new NoSpecificDateConstraintArg[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
            // Do nothing
    }

    private NoSpecificDateConstraintArg(Parcel in) {
        // Do nothing
    }
    //endregion
}
