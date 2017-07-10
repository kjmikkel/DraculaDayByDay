package com.jensen.draculadaybyday.entry;

import com.jensen.draculadaybyday.entries.EntryType;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Entry {
    // The number the mEntry is in the sequence
    private final short storyEntryNum;

    // The number the mEntry is in the sequence for that person
    private final short dateEntryNum;

    // The chapter
    private final short chapter;

    // The person who has written the mEntry
    private final String person;

    // The diary mEntry itself
    private final String textEntry;

    // The date the fragment was written
    private final Calendar date;

    // The type of the mEntry
    private final EntryType type;

    // Whether or not the mEntry is unlocked
    private final boolean unlocked;

    // Whether or not the mEntry is unread
    private final boolean unread;

    private Entry(int storyEntryNum, int dateEntryNum, int chapter, String personName, String diaryText, Calendar date, EntryType type, boolean unlocked, boolean unread) {

        this.storyEntryNum = (short) storyEntryNum;

        this.dateEntryNum = (short) dateEntryNum;

        this.chapter = (short) chapter;

        this.person = personName;

        this.textEntry = diaryText;

        this.date = date;

        this.type = type;

        this.unlocked = unlocked;

        this.unread = unread;
    }

    public Entry(int chapter, String personName, String diaryText, Calendar date, EntryType type, boolean unlocked, boolean unread) {
        this(-1, -1, chapter, personName, diaryText, date, type, unlocked, unread);
    }

    public Entry(int storyEntryNum, int dateEntryNum, int chapter, String personName, String diaryText, Calendar date, String type, boolean unlocked, boolean unread) {
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

    public String getTextEntry() {
        return textEntry;
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
        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String noDateStr = format.format(date.getTime());

        int val = date.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.getDefault(), "%2d", val) + getDayOfMonthSuffix(val) + "\n" + noDateStr;
    }

    @Override
    public String toString() {
        return getDateString() + "_" + person;
    }
}
