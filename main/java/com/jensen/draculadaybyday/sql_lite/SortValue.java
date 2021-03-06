package com.jensen.draculadaybyday.sql_lite;

import android.os.Parcel;
import android.os.Parcelable;

public class SortValue implements Parcelable {
    private final String column;
    private final boolean asc;
    private final int spinnerIndex;

    public SortValue(String column, boolean asc, int spinnerIndex) {
        this.column = column;
        this.asc = asc;
        this.spinnerIndex = spinnerIndex;
    }

    public String getColumn() {
        return column;
    }

    public boolean getAscending() {
        return asc;
    }

    public int getSpinnerIndex() {
        return spinnerIndex;
    }

    public SortValue(Parcel in) {
        this.column = in.readString();
        boolean[] singleBool = new boolean[1];
        in.readBooleanArray(singleBool);
        this.asc = singleBool[0];
        this.spinnerIndex = in.readInt();
    }

    public int describeContents () {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(column);
        out.writeBooleanArray(new boolean[] { asc });
        out.writeInt(this.spinnerIndex);
    }

    public static final Parcelable.Creator<SortValue> CREATOR = new Parcelable.Creator<SortValue>() {
        public SortValue createFromParcel(Parcel in) {
            return new SortValue(in);
        }

        public SortValue[] newArray(int size) {
            return new SortValue[size];
        }
    };

    @Override
    public String toString() {
        return String.format("%s %s", column, asc ? "ASC" : "DESC");
    }

    @Override
    public boolean equals(Object o) {
        boolean isEquals;
        if (!(o instanceof  SortValue)) {
            isEquals = false;
        } else {
            SortValue sortValue = (SortValue)o;
            isEquals = column.equals(sortValue.column);
        }

        return isEquals;
    }

    @Override
    public int hashCode() {
        return column.hashCode();
    }
}
