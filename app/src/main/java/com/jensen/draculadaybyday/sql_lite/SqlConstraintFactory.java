package com.jensen.draculadaybyday.sql_lite;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class SqlConstraintFactory implements Parcelable {
    // The list that will be joined

    public static final int DATE = 0;
    public static final int PERSON = 1;
    public static final int TYPE = 2;
    public static final int CHAPTER = 3;
    public static final int READ = 4;
    public static final int UNLOCKED = 5;

    private final DateConstraint dateConstraint;
    private final Constraint personConstraint;
    private final Constraint typeConstraint;
    private final Constraint chapterConstraint;
    private final Constraint readConstraint;
    private final Constraint unlockedConstraint;

    // Time format
    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd").withLocale(Locale.getDefault());

    public SqlConstraintFactory() {
        dateConstraint = new DateConstraint();
        personConstraint = new Constraint();
        typeConstraint = new Constraint();
        chapterConstraint = new Constraint();
        readConstraint = new Constraint();
        unlockedConstraint = new Constraint();
    }

    private SqlConstraintFactory(Parcel in) {
        dateConstraint = in.readParcelable(DateConstraint.class.getClassLoader());
        personConstraint = in.readParcelable(Constraint.class.getClassLoader());
        typeConstraint = in.readParcelable(Constraint.class.getClassLoader());
        chapterConstraint = in.readParcelable(Constraint.class.getClassLoader());
        readConstraint = in.readParcelable(Constraint.class.getClassLoader());
        unlockedConstraint = in.readParcelable(Constraint.class.getClassLoader());
    }

    //region Date constraints
    public void exactDate(DateTime date) {
        String dateString = date.toString(fmt);
        dateConstraint.setDateConstraint(dateString);
    }

    public void beforeDate(DateTime date, boolean inclusive) {
        String beforeDate = date.toString(fmt);
        dateConstraint.setDateConstraint(DateConstraint.BEFORE, beforeDate, inclusive);
    }

    public void afterDate(DateTime date, boolean inclusive) {
        String afterDate = date.toString(fmt);
        dateConstraint.setDateConstraint(DateConstraint.AFTER, afterDate, inclusive);
    }

    public void betweenDates(DateTime beforeDateCalendar, DateTime afterDateCalendar) {
        String beforeDate = beforeDateCalendar.toString(fmt);
        String afterDate = afterDateCalendar.toString(fmt);

        dateConstraint.setDateConstraint(beforeDate, afterDate);
    }
    //endregion


    // Person constraints
    public void multiplePersons(List<String> personNames) {
        multipleNonExclusive(FragmentEntryDatabaseHandler.PERSON, personConstraint, personNames);
    }

    //Media constraint
    public void multipleTypes(List<String> typeNames) {
        multipleNonExclusive(FragmentEntryDatabaseHandler.TYPE, typeConstraint, typeNames);
    }

    //Chapter constraint
    public void multipleChapters(List<String> chaptersToAdd) {
        multipleNonExclusive(FragmentEntryDatabaseHandler.CHAPTER, chapterConstraint, chaptersToAdd);
    }

    public void readStatus(boolean unread) {
        int readInt = unread ? 1 : 0;
        specificConstraint(FragmentEntryDatabaseHandler.UNREAD, readConstraint, readInt);
    }

    public void unlocked(boolean unlockedBool) {
        int unlockedInt = unlockedBool ? 1 : 0;
        specificConstraint(FragmentEntryDatabaseHandler.UNLOCKED, unlockedConstraint, unlockedInt);
    }

    private void specificConstraint(String field, Constraint constraint, Object value) {
        constraint.setConstraintSqlText(field + " = ?");
        constraint.addConstraintSqlValue(String.valueOf(value));
    }

    private void multipleNonExclusive(String field, Constraint constraint, List<?> listValues) {
        List<String> tempConstraints = new LinkedList<>();

        for(Object val : listValues) {
            tempConstraints.add(field + " = ?");
            constraint.addConstraintSqlValue(String.valueOf(val));
        }

        constraint.setConstraintSqlText("(" + TextUtils.join(" OR ", tempConstraints) + ")");
    }

    public String getConstraint() {
        ArrayList<String> finalConstraints = new ArrayList<>();

        if (dateConstraint.hasConstraints()) {
            finalConstraints.add(dateConstraint.getConstraintSqlText());
        }
        if (personConstraint.hasConstraints()) {
            finalConstraints.add(personConstraint.getConstraintSqlText());
        }
        if (typeConstraint.hasConstraints()) {
            finalConstraints.add(typeConstraint.getConstraintSqlText());
        }
        if(chapterConstraint.hasConstraints()) {
            finalConstraints.add(chapterConstraint.getConstraintSqlText());
        }
        if(readConstraint.hasConstraints()) {
            finalConstraints.add(readConstraint.getConstraintSqlText());
        }
        if(unlockedConstraint.hasConstraints()) {
            finalConstraints.add(unlockedConstraint.getConstraintSqlText());
        }

        return TextUtils.join(" AND ", finalConstraints);
    }

    public String[] getValues() {
        String[] valArray = null;

        ArrayList<String> values = new ArrayList<>();
        values.addAll(dateConstraint.getConstraintSqlValues());
        values.addAll(personConstraint.getConstraintSqlValues());
        values.addAll(typeConstraint.getConstraintSqlValues());
        values.addAll(chapterConstraint.getConstraintSqlValues());
        values.addAll(readConstraint.getConstraintSqlValues());
        values.addAll(unlockedConstraint.getConstraintSqlValues());

        if (0 < values.size()) {
            valArray = new String[values.size()];
            values.toArray(valArray);
        }

        return valArray;
    }

    public Constraint getConstraint(int category) {
        Constraint constraint = null;
        switch (category) {
            case DATE:
                constraint = dateConstraint;
                break;
            case PERSON:
                constraint = personConstraint;
                break;
            case TYPE:
                constraint = typeConstraint;
                break;
            case CHAPTER:
                constraint = chapterConstraint;
                break;
            case READ:
                constraint = readConstraint;
                break;
            case UNLOCKED:
                constraint = unlockedConstraint;
                break;
        }

        return constraint;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(dateConstraint, flags);
        out.writeParcelable(personConstraint, flags);
        out.writeParcelable(typeConstraint, flags);
        out.writeParcelable(chapterConstraint, flags);
        out.writeParcelable(chapterConstraint, flags);
        out.writeParcelable(readConstraint, flags);
        out.writeParcelable(unlockedConstraint, flags);
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
