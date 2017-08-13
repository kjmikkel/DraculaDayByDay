package com.jensen.draculadaybyday.sql_lite;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntRange;

import com.jensen.draculadaybyday.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Period;

public class DateConstructorUtility {

    public static final DateTime initialDate = new DateTime(1893, DateTimeConstants.MAY, 3, 0, 0, 0, 0);

    private static final DateTime firstDayOfJanuary = new DateTime(1893, DateTimeConstants.JANUARY, 1, 0, 0, 0, 0);

    public static DateTime getUnlockDate(Context context) {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        String unlockMethod = prefManager.getString(context.getString(R.string.pref_key_how_to_experience), context.getString(R.string.pref_experience_default_value));

        DateTime dateTime;
        ExperienceMode mode = unlockMethod.equals(context.getString(R.string.pref_experience_default_value)) ? ExperienceMode.EXPERIENCE_ON_SAME_DAY : ExperienceMode.EXPERIENCE_IN_SAME_TEMPO;
        if (mode == ExperienceMode.EXPERIENCE_ON_SAME_DAY) {
            dateTime = todayInThePast(ExperienceMode.EXPERIENCE_ON_SAME_DAY);
        } else {
            dateTime = getInSameTempoDate(context);
        }

        return dateTime;
    }

    public static DateTime todayInThePast(ExperienceMode mode) {
        DateTime today = new DateTime();
        today = getDateTime(today.monthOfYear().get(), today.dayOfMonth().get());

        if(mode == ExperienceMode.EXPERIENCE_ON_SAME_DAY && !(firstDayOfJanuary.isBefore(today) && initialDate.isAfter(today))) {
            // if we are not in the interval first of January to the third of May, then the year is 1892
            today.withYear(1892);
        }

        return today;
    }

    private static DateTime getInSameTempoDate(Context context) {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        long timeInMilliseconds = prefManager.getLong(context.getString(R.string.pref_key_start_date_time), 0); //todayInThePast(ExperienceMode.EXPERIENCE_IN_SAME_TEMPO).getMillis());

        DateTime startTime = new DateTime(timeInMilliseconds);
        DateTime endTime = todayInThePast(ExperienceMode.EXPERIENCE_IN_SAME_TEMPO);

        DateTime returnDateTime;
        if (startTime.isBefore(endTime) || startTime.isEqual(endTime)) {
            Period diffPeriod = new Period(startTime, endTime);
            returnDateTime = initialDate.plus(diffPeriod);
        } else {
            // Due to the way the date are handled it may effectively wrap around, and in that case we just unlock everything
            returnDateTime = new DateTime(1894, DateTimeConstants.JANUARY, 1, 0, 0, 0, 0);
        }

        return returnDateTime;
    }

    public static DateTime getDateTime(@IntRange(from = 1, to = 12) int month, @IntRange(from = 1, to = 31) int day) {
        return getDateTime(1893, month, day);
    }

    public static DateTime getDateTime(int year, @IntRange(from = 1, to = 12) int month, @IntRange(from = 1, to = 31) int day) {
        return new DateTime(year, month, day, 0, 0, 0, 0);
    }
}
