package com.jensen.draculadaybyday.sql_lite;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class DateConstraint extends Constraint {

    public static final int EXACT = 0;
    public static final int BEFORE = 1;
    public static final int AFTER = 2;
    public static final int BETWEEN = 3;

    private static final String BEFORE_DATE_INCLUSIVE = String.format("%s <= ?", FragmentEntryDatabaseHandler.DATE );
    private static final String BEFORE_DATE_EXCLUSIVE = String.format("%s < ?", FragmentEntryDatabaseHandler.DATE );
    private static final String AFTER_DATE_INCLUSIVE = String.format("? <= %s", FragmentEntryDatabaseHandler.DATE);
    private static final String AFTER_DATE_EXCLUSIVE = String.format("? < %s", FragmentEntryDatabaseHandler.DATE);
    private static final String EXACT_DATE = String.format("%s = ?", FragmentEntryDatabaseHandler.DATE);
    private static final String BETWEEN_DATES = String.format("%s BETWEEN ? AND ?", FragmentEntryDatabaseHandler.DATE);

    // The type of DateConstraint
    private int dateType;

    // Whether it the test is inclusive
    private boolean inclusive;

    public DateConstraint() {
        super();
        dateType = -1;
        inclusive = false;
    }

    public boolean getInclusive() { return inclusive; }

    public int getDateType() {
        return dateType;
    }

    public void setDateConstraint(String date) {
        this.dateType = EXACT;
        setConstraintSqlText(EXACT_DATE);
        // Add the date to the values
        addConstraintSqlValue(date);
    }

    public void setDateConstraint(int dateType, String date, boolean inclusive) {
        if (BEFORE <= dateType && dateType <= AFTER) {
            this.dateType = dateType;
            this.inclusive = inclusive;

            // Set the SQL
            switch (dateType) {
                case BEFORE:
                    if (inclusive) {
                        setConstraintSqlText(BEFORE_DATE_INCLUSIVE);
                    } else {
                        setConstraintSqlText(BEFORE_DATE_EXCLUSIVE);
                    }
                    break;
                case AFTER:
                    if (inclusive) {
                        setConstraintSqlText(AFTER_DATE_INCLUSIVE);
                    } else {
                        setConstraintSqlText(AFTER_DATE_EXCLUSIVE);
                    }
                    break;
            }
            addConstraintSqlValue(date);
        }
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

            boolean[] inclusiveArray = { inclusive };
            out.writeBooleanArray(inclusiveArray);
        } catch (Exception e) {
            Log.d("DateConstraint", e.getMessage());
        }
    }

    private DateConstraint(Parcel in) {
        super(in);
        dateType = in.readInt();

        boolean[] inclusiveBool = new boolean[1];
        in.readBooleanArray(inclusiveBool);
        inclusive = inclusiveBool[0];
    }
    //endregion
}
