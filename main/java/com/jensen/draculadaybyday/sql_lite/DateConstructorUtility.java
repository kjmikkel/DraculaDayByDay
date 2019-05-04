package com.jensen.draculadaybyday.sql_lite;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntRange;

import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.time.DateTimeConstants;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateConstructorUtility {

    public static final LocalDateTime initialDate = LocalDateTime.of(1893, DateTimeConstants.MAY, 3, 0, 0, 0, 0);

    private static final LocalDateTime firstDayOfJanuary = LocalDateTime.of(1893, DateTimeConstants.JANUARY, 1, 0, 0, 0, 0);

    public static LocalDateTime getUnlockDate(Context context) {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        String unlockMethod = prefManager.getString(context.getString(R.string.pref_key_how_to_experience), context.getString(R.string.pref_experience_default_value));

        LocalDateTime localDateTime;
        ExperienceMode mode = unlockMethod.equals(context.getString(R.string.pref_experience_default_value)) ? ExperienceMode.EXPERIENCE_ON_SAME_DAY : ExperienceMode.EXPERIENCE_IN_SAME_TEMPO;
        if (mode == ExperienceMode.EXPERIENCE_ON_SAME_DAY) {
            localDateTime = todayInThePast();
        } else {
            localDateTime = getInSameTempoDate(context);
        }

        return localDateTime;
    }

    public static LocalDateTime todayInThePast() {
        LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
        return getDateTime(today.getMonth().ordinal(), today.getDayOfMonth());
    }

    public static boolean inRightYear() {
        LocalDateTime today = LocalDateTime.now();
        boolean returnValue = true;
        if (firstDayOfJanuary.isBefore(today) && initialDate.isAfter(today)) {
            // if we are not in the interval first of January to the third of May, then we need to mark it
            returnValue = false;
        }
        return returnValue;
    }

    private static LocalDateTime getInSameTempoDate(Context context) {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        long timeInMilliseconds = prefManager.getLong(context.getString(R.string.pref_key_start_date_time), 0); //todayInThePast(ExperienceMode.EXPERIENCE_IN_SAME_TEMPO).getMillis());

        LocalDateTime startTime = getDateFromMilliseconds(timeInMilliseconds);
        LocalDateTime endTime = todayInThePast();

        LocalDateTime returnDateTime;
        if (startTime.isBefore(endTime) || startTime.isEqual(endTime)) {
            Duration diffPeriod = Duration.between(startTime, endTime);
            returnDateTime = initialDate.plus(diffPeriod);
        } else {
            // Due to the way the date are handled it may effectively wrap around, and in that case we just unlock everything
            returnDateTime = getDateTime(1894, DateTimeConstants.JANUARY, 1);
        }

        return returnDateTime;
    }

    public static long getMilliseconds(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime getDateFromMilliseconds(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime getDateTime(int year, int month, int day) {
        return getDateTime(year, month, day);
    }

    public static LocalDateTime getDateTime(@IntRange(from = 1, to = 12) int month, @IntRange(from = 1, to = 31) int day) {
        return LocalDateTime.of(1893, month, day, 0, 0);
    }
}
