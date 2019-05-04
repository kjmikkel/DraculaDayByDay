package com.jensen.draculadaybyday.sql_lite.DateConstraintArg;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.time.LocalDateTime;

public class BeforeDateConstraintArg extends BeforeAfterDateConstraintArg {

    public BeforeDateConstraintArg(LocalDateTime localDateTime, boolean inclusive) {
        super(inclusive);
        this.localDateTime = localDateTime;
    }

    public String getSQLText() {
        String returnValue;

        if (inclusive) {
            returnValue = "%s <= ?";
        } else {
            returnValue = "%s < ?";
        }

        return returnValue;
    }

    @Override
    public String toString() {
        return "Before date";
    }

    //region Parcelable
    public static final Parcelable.Creator<BeforeDateConstraintArg> CREATOR = new Parcelable.Creator<BeforeDateConstraintArg>() {
        public BeforeDateConstraintArg createFromParcel(Parcel in) {
            return new BeforeDateConstraintArg(in);
        }

        public BeforeDateConstraintArg[] newArray(int size) {
            return new BeforeDateConstraintArg[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        try {
            out.writeSerializable(this.localDateTime);
            out.writeInt(this.inclusive ? 1 : 0);
        } catch (Exception e) {
            Log.d("DateConstraint", e.getMessage());
        }
    }

    private BeforeDateConstraintArg(Parcel in) {
        this.localDateTime = (LocalDateTime) in.readSerializable();
        this.inclusive = in.readInt() == 1;
    }
    //endregion
}
