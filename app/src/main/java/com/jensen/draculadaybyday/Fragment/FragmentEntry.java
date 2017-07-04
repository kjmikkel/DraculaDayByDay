package com.jensen.draculadaybyday.Fragment;

import com.jensen.draculadaybyday.Entries.EntryType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

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
    private EntryType type;

    // Whether or not the entry is unlocked
    private boolean unlocked;

    // Whether or not the entry is unread
    private boolean unread;

    public FragmentEntry(int storyEntryNum, int dateEntryNum, int chapter, String personName, String diaryText, Calendar date, EntryType type, boolean unlocked, boolean unread) {

        this.storyEntryNum = (short) storyEntryNum;

        this.dateEntryNum = (short) dateEntryNum;

        this.chapter = (short) chapter;

        this.person = personName;

        this.diaryEntry = diaryText;

        this.date = date;

        this.type = type;

        this.unlocked = unlocked;

        this.unread = unread;
    }

    public FragmentEntry(int chapter, String personName, String diaryText, Calendar date, EntryType type, boolean unlocked, boolean unread) {
        this(-1, -1, chapter, personName, diaryText, date, type, unlocked, unread);
    }

    public FragmentEntry(int storyEntryNum, int dateEntryNum, int chapter, String personName, String diaryText, Calendar date, String type, boolean unlocked, boolean unread) {
        this(storyEntryNum, dateEntryNum, chapter, personName, diaryText, date, getEntryType(type), unlocked, unread);
    }

    private static EntryType getEntryType(String type) {
        EntryType out = EntryType.DIARY_ENTRY;
        for(EntryType et : EntryType.values()) {
            if (et.description.equals(type)) {
                out = et;
                break;
            }
        }
        return out;
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

    public EntryType getType() {
        return type;
    }

    public boolean getUnlocked() { return unlocked; }

    public boolean getUnread() { return unread; }

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

    public String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("MMMM yyyy");
        String noDateStr = format.format(date.getTime());

        int val = date.get(Calendar.DAY_OF_MONTH);
        return String.format("%2d", val) + getDayOfMonthSuffix(val) + "\n" + noDateStr;
    }

    @Override
    public String toString() {
        return getDateString() + "_" + person;
    }
}
