package com.jensen.draculadaybyday.sql_lite;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class SqlConstraintFactory implements Parcelable {
    // The list that will be joined
    private final List<String> constraints;
    private final List<String> values;
    private final List<Constraint> constraintEnum;

    // Time format
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public SqlConstraintFactory() {
        constraints = new LinkedList<>();
        values = new LinkedList<>();
        constraintEnum = new LinkedList<>();
    }

    private SqlConstraintFactory(Parcel in) {
        constraints = new LinkedList<>();
        in.readStringList(constraints);

        constraintEnum = new LinkedList<>();

        List<Integer> constraintOrdinals = new LinkedList<>();
        in.readList(constraintOrdinals, null);
        Constraint[] constraintValues = Constraint.values();
        for(Integer constraintOrdinal : constraintOrdinals) {
            constraintEnum.add(constraintValues[constraintOrdinal]);
        }

        values = new LinkedList<>();
        in.readStringList(values);
    }

    //region Date constraints
    public void exactDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        String dateString = dateFormat.format(date.getTime());
        constraints.add(FragmentEntryDatabaseHandler.DATE + " = Datetime(?)");
        constraintEnum.add(Constraint.EXACT_DATE);
        values.add(dateString);
    }

    public void beforeDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        String beforeDate = dateFormat.format(date.getTime());
        constraints.add(FragmentEntryDatabaseHandler.DATE + " <= Datetime(?)");
        constraintEnum.add(Constraint.BEFORE_DATE);
        values.add(beforeDate);
    }

    public void afterDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        String afterDate = dateFormat.format(date.getTime());
        constraints.add("Datetime(?) <= " + FragmentEntryDatabaseHandler.DATE);
        constraintEnum.add(Constraint.AFTER_DATE);
        values.add(afterDate);
    }

    public void betweenDates(Calendar beforeDateCalendar, Calendar afterDateCalendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

        String beforeDate = dateFormat.format(beforeDateCalendar.getTime());
        String afterDate = dateFormat.format(afterDateCalendar.getTime());

        constraints.add(FragmentEntryDatabaseHandler.DATE + " BETWEEN Datetime(?) AND Datetime(?)");
        constraintEnum.add(Constraint.BETWEEN_DATE);
        values.add(beforeDate);
        values.add(afterDate);
    }
    //endregion


    // Person constraints
    public void multiplePersons(List<String> personNames) {
        multipleNonExclusive(FragmentEntryDatabaseHandler.PERSON, personNames, Constraint.PERSON);
    }

    //Media constraint
    public void multipleTypes(List<String> typeNames) {
        multipleNonExclusive(FragmentEntryDatabaseHandler.TYPE, typeNames, Constraint.TYPE);
    }

    //Chapter constraint
    public void multipleChapters(List<String> chapters) {
        multipleNonExclusive(FragmentEntryDatabaseHandler.CHAPTER, chapters, Constraint.CHAPTER);
    }

    public void readStatus(boolean unread) {
        int readInt = unread ? 1 : 0;
        constraintEnum.add(Constraint.READ_OR_UNREAD);
        specificConstraint(FragmentEntryDatabaseHandler.UNREAD, readInt);
    }

    public void unlocked(boolean unlocked) {
        int unlockedInt = unlocked ? 1 : 0;
        specificConstraint(FragmentEntryDatabaseHandler.UNLOCKED, unlockedInt);
    }

    private void specificConstraint(String field, Object value) {
        constraints.add(field + " = ?");
        values.add(String.valueOf(value));
    }

    private void multipleNonExclusive(String field, List<?> listValues, Constraint cEnum) {
        List<String> tempConstraints = new LinkedList<>();

        for(Object val : listValues) {
            tempConstraints.add(field + " = ?");
            values.add(String.valueOf(val));
        }

        // Add the constraint enum
        constraintEnum.add(cEnum);

        constraints.add("(" + TextUtils.join(" OR ", tempConstraints) + ")");
    }

    public String getConstraint() {
        String finalConstraints = "";

        if (0 < constraints.size()) {
            finalConstraints = TextUtils.join(" AND ", constraints);
        }

        return finalConstraints;
    }

    public String[] getValues() {
        String[] valArray = null;

        if (0 < values.size()) {
            valArray = new String[values.size()];
            values.toArray(valArray);
        }

        return valArray;
    }

    public List<Constraint> getConstraintEnums() {
        return constraintEnum;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeStringList(constraints);

        LinkedList<Integer> intList = new LinkedList<>();
        for(Constraint constraint : constraintEnum) {
            intList.add(constraint.ordinal());
        }
        out.writeList(intList);

        out.writeStringList(values);
    }

    public int describeContents () {
        return 0;
    }

    public static final Parcelable.Creator<SqlConstraintFactory> CREATOR
            = new Parcelable.Creator<SqlConstraintFactory>() {

        public SqlConstraintFactory createFromParcel(Parcel in) {
            return new SqlConstraintFactory(in);
        }

        public SqlConstraintFactory[] newArray(int size) {
            return new SqlConstraintFactory[size];
        }
    };
}
