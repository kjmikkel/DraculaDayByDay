package com.jensen.draculadaybyday.sql_lite;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jensen.draculadaybyday.sql_lite.DateConstraintArg.AfterDateConstraintArg;
import com.jensen.draculadaybyday.sql_lite.DateConstraintArg.BeforeDateConstraintArg;
import com.jensen.draculadaybyday.sql_lite.DateConstraintArg.BetweenDateConstraintArg;
import com.jensen.draculadaybyday.sql_lite.DateConstraintArg.DateConstraintArg;
import com.jensen.draculadaybyday.sql_lite.DateConstraintArg.ExactDateConstraintArg;
import com.jensen.draculadaybyday.sql_lite.DateConstraintArg.NoSpecificDateConstraintArg;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DateConstraint extends Constraint {

    private DateConstraintArg constraintArg;

    // Time format
    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd").withLocale(Locale.getDefault());

    public DateConstraint() {
        super();
    }

    public void setDateConstraint(DateConstraintArg constraint) {
        this.constraintArg = constraint;

        if (!(this.constraintArg instanceof NoSpecificDateConstraintArg)) {
            setConstraintSqlText(String.format(Locale.getDefault(), this.constraintArg.getSQLText(), FragmentEntryDatabaseHandler.DATE));
            addConstraintSqlValue(this.constraintArg.getDate().toString(fmt));

            if (this.constraintArg instanceof BetweenDateConstraintArg) {
                BetweenDateConstraintArg betweenDateConstraintArg = (BetweenDateConstraintArg)this.constraintArg;
                addConstraintSqlValue(betweenDateConstraintArg.getSecondDate().toString(fmt));
            }
        }
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
            out.writeParcelable(constraintArg, flags);
        } catch (Exception e) {
            Log.d("DateConstraint", e.getMessage());
        }
    }

    public List<DateConstraintArg> getDateConstraintArgs() {
        List<DateConstraintArg> dateConstraintArgs = new ArrayList<>(5);
        dateConstraintArgs.add(new NoSpecificDateConstraintArg());

        if (constraintArg instanceof ExactDateConstraintArg) {
            dateConstraintArgs.add(constraintArg);
        } else {
            dateConstraintArgs.add(new ExactDateConstraintArg(DateConstructorUtility.initialDate));
        }

        if (constraintArg instanceof BeforeDateConstraintArg) {
            dateConstraintArgs.add(constraintArg);
        } else {
            dateConstraintArgs.add(new BeforeDateConstraintArg(DateConstructorUtility.initialDate, false));
        }

        if (constraintArg instanceof AfterDateConstraintArg) {
            dateConstraintArgs.add(constraintArg);
        } else {
            dateConstraintArgs.add(new AfterDateConstraintArg(DateConstructorUtility.initialDate, false));
        }

        if (constraintArg instanceof BetweenDateConstraintArg) {
            dateConstraintArgs.add(constraintArg);
        } else {
            dateConstraintArgs.add(new BetweenDateConstraintArg(DateConstructorUtility.initialDate, DateConstructorUtility.initialDate));
        }

        return dateConstraintArgs;
    }

    private DateConstraint(Parcel in) {
        super(in);
        this.constraintArg = in.readParcelable(DateConstraintArg.class.getClassLoader());
    }
    //endregion
}
