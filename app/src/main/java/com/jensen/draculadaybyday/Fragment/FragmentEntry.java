package com.jensen.draculadaybyday.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FragmentEntry {
    // The number the entry is in the sequence
    private short storyEntryNum;

    // The number the entry is in the sequence for that person
    private short dateEntryNum;

    // The chapter
    private short chapter;

    // The person who has written the entry
    private String person;

    // The diary entry itself
    private String diaryEntry;

    // The date the fragment was written
    private Calendar date;

    // The type of the entry
    private String type;

    public FragmentEntry(int storyEntryNum, int dateEntryNum, int chapter, String personName, String diaryText, Calendar date, String type) {

        this.storyEntryNum = (short) storyEntryNum;

        this.dateEntryNum = (short) dateEntryNum;

        this.chapter = (short) chapter;

        this.person = personName;

        this.diaryEntry = diaryText;

        this.date = date;

        this.type = type;
    }

    public FragmentEntry(int chapter, String personName, String diaryText, Calendar date, String type) {
        this(-1, -1, chapter, personName, diaryText, date, type);
    }

    public short getStoryEntryNum() {
        return storyEntryNum;
    }

    public short getDateEntryNum() {
        return dateEntryNum;
    }

    public short getChapter() {
        return chapter;
    }

    public String getPerson() {
        return person;
    }

    public String getFragmentEntry() {
        return diaryEntry;
    }

    public Calendar getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    private String getDayOfMonthSuffix(final int n) {
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("MMM yyyy");
        String noDateStr = format.format(date.getTime());

        int val = date.get(Calendar.DAY_OF_MONTH);
        String date = String.format("%2d", val) + getDayOfMonthSuffix(val) + " ";

        return date + noDateStr
                + "_" +
                person;
    }
}
