package com.jensen.draculadaybyday.sql_lite;

import android.text.TextUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class SqlConstraintFactory {
    // The list that will be joined
    private List<String> constraints;
    private List<String> values;

    // Time format
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public SqlConstraintFactory() {
        constraints = new LinkedList<>();
        values = new LinkedList<>();
    }

    //region Date constraints
    public void exactDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        String dateString = dateFormat.format(date.getTime());
        constraints.add(FragmentEntryDatabaseHandler.DATE + " = Datetime(?))");
        values.add(dateString);
    }

    public void beforeDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        String beforeDate = dateFormat.format(date.getTime());
        constraints.add(FragmentEntryDatabaseHandler.DATE + " <= Datetime(?)");
        values.add(beforeDate);
    }

    public void afterDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        String afterDate = dateFormat.format(date.getTime());
        constraints.add("Datetime(?) <= " + FragmentEntryDatabaseHandler.DATE);
        values.add(afterDate);
    }

    public void betweenDates(Calendar beforeDateCalendar, Calendar afterDateCalendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

        String beforeDate = dateFormat.format(beforeDateCalendar.getTime());
        String afterDate = dateFormat.format(afterDateCalendar.getTime());

        constraints.add(FragmentEntryDatabaseHandler.DATE + " BETWEEN Datetime(?) AND Datetime(?)");
        values.add(beforeDate);
        values.add(afterDate);
    }
    //endregion


    //region Person constraints
    public void specificPerson(String personName) {
        specificConstraint(FragmentEntryDatabaseHandler.PERSON, personName);
    }

    public void multiplePersons(List<String> personNames) {
        multipleNonExclusive(FragmentEntryDatabaseHandler.PERSON, personNames);
    }
    //endregion

    //region Media constraint
    public void specificMedium(String typeName) {
        specificConstraint(FragmentEntryDatabaseHandler.TYPE, typeName);
    }

    public void multipleMediums(List<String> typeNames) {
        multipleNonExclusive(FragmentEntryDatabaseHandler.TYPE, typeNames);
    }
    //endregion

    //region Chapters
    public void specificChapter(int chapter) {
        specificConstraint(FragmentEntryDatabaseHandler.CHAPTER, chapter);
    }

    public void multipleChapters(List<Integer> chapters) {
        multipleNonExclusive(FragmentEntryDatabaseHandler.CHAPTER, chapters);
    }
    //endregion

    public void readStatus(boolean unread) {
        int readInt = unread ? 1 : 0;
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

    private void multipleNonExclusive(String field, List<? extends Object> listValues) {
        List<String> tempConstraints = new LinkedList<>();

        for(Object val : listValues) {
            tempConstraints.add(field + " = ?");
            values.add(String.valueOf(val));
        }

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
}
