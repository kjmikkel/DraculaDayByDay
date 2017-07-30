package com.jensen.draculadaybyday.sql_lite;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class DateConstraint extends Constraint {

    public static final int EXACT = 0;
    public static final int BEFORE = 1;
    public static final int AFTER = 2;
    public static final int BETWEEN = 3;


    private static final String BEFORE_DATE = FragmentEntryDatabaseHandler.DATE + " <= Datetime(?)";
    private static final String AFTER_DATE = "Datetime(?) <= " + FragmentEntryDatabaseHandler.DATE;
    private static final String EXACT_DATE = FragmentEntryDatabaseHandler.DATE + " = Datetime(?)";
    private static final String BETWEEN_DATES = FragmentEntryDatabaseHandler.DATE + " BETWEEN Datetime(?) AND Datetime(?)";

    private int dateType;

    public DateConstraint() {
        super();
        dateType = -1;
    }

    public int getDateType() {
        return dateType;
    }

    public void setDateConstraint(int dateType, String date) {
        if (EXACT <= dateType && dateType <= AFTER) {
            this.dateType = dateType;

            // Set the SQL
            switch (dateType) {
                case EXACT:
                    setConstraintSqlText(EXACT_DATE);
                    break;
                case BEFORE:
                    setConstraintSqlText(BEFORE_DATE);
                    break;
                case AFTER:
                    setConstraintSqlText(AFTER_DATE);
                    break;
            }
        }

        // Add the date to the values
        addConstraintSqlValue(date);
    }

    public void setDateConstraint(String beforeDate, String afterDate) {
        this.dateType = BETWEEN;
        setConstraintSqlText(BETWEEN_DATES);
        addConstraintSqlValue(beforeDate);
        addConstraintSqlValue(afterDate);
    }

    public static final Parcelable.Creator<DateConstraint> CREATOR = new Parcelable.Creator<DateConstraint>() {
        public DateConstraint createFromParcel(Parcel in) {
            return new DateConstraint(in);
        }

        public DateConstraint[] newArray(int size) {
            return new DateConstraint[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        try {
            super.writeToParcel(out, flags);
            out.writeInt(dateType);
        } catch (Exception e) {
            Log.d("DateConstraint", e.getMessage());
        }
    }

    private DateConstraint(Parcel in) {
        super(in);
        dateType = in.readInt();
    }
    //endregion
}
