package com.jensen.draculadaybyday.sql_lite;

import android.text.TextUtils;

import com.jensen.draculadaybyday.entries.Person;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static android.text.TextUtils.join;

public class SqlConstraintFactory {
    // The list that will be joined
    private List<String> constraints;

    // Time
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public SqlConstraintFactory() {
        constraints = new LinkedList<>();
    }

    //region Date constraints
    public void exactDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        String dateString = dateFormat.format(date.getTime());
        constraints.add(String.format(Locale.getDefault(), "%s = Datetime('%s'))", FragmentEntryDatabaseHandler.DATE, dateString));
    }

    public void beforeDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        String beforeDate = dateFormat.format(date.getTime());
        constraints.add(String.format(Locale.getDefault(), "Datetime('%s') <= %s)", beforeDate, FragmentEntryDatabaseHandler.DATE));
    }

    public void afterDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        String afterDate = dateFormat.format(date.getTime());
        constraints.add(String.format(Locale.getDefault(), "%s <= Datetime('%s')", FragmentEntryDatabaseHandler.DATE, afterDate));
    }

    public void betweenDates(Calendar beforeDateCalendar, Calendar afterDateCalendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

        String beforeDate = dateFormat.format(beforeDateCalendar.getTime());
        String afterDate = dateFormat.format(afterDateCalendar.getTime());

        constraints.add(String.format(Locale.getDefault(), "%s BETWEEN Datetime('%s') AND Datetime('%s')", FragmentEntryDatabaseHandler.DATE, beforeDate, afterDate));
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

    public String getConstraint() {
        String finalConstraints = "1 = 1";
        if (0 < constraints.size()) {
            finalConstraints = TextUtils.join(" AND ", constraints);
        }
        return finalConstraints;
    }


    private void specificConstraint(String field, Object value) {
        constraints.add(String.format("%s = %s", field, value));
    }

    private void multipleNonExclusive(String field, List<? extends Object> listValues) {
        List<String> tempConstraints = new LinkedList<>();

        for(Object val : listValues) {
            tempConstraints.add(String.format("%s = %s", field, val));
        }

        constraints.add("(" + TextUtils.join(" OR ", tempConstraints) + ")");
    }
}
