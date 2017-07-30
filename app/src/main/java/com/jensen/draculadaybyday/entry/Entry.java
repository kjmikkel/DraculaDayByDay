package com.jensen.draculadaybyday.entry;

import com.jensen.draculadaybyday.entries.EntryType;
import com.jensen.draculadaybyday.entries.Person;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Entry {
    // The number the mEntry is in the sequence
    private final short storyEntryNum;

    // The chapter
    private final short chapter;

    // The person who has written the mEntry
    private final Person person;

    // The diary mEntry itself
    private final String textEntry;

    // The date the fragment was written
    private final Calendar date;

    // The type of the mEntry
    private final EntryType type;

    // Whether or not the mEntry is unread
    private final boolean unread;

    public Entry(int storyEntryNum, int chapter, Person person, String diaryText, Calendar date, EntryType type, boolean unread) {
        this.storyEntryNum = (short) storyEntryNum;

        this.chapter = (short) chapter;

        this.person = person;

        this.textEntry = diaryText;

        this.date = date;

        this.type = type;

        this.unread = unread;
    }

    public Entry(int storyEntryNum, int chapter, String personName, String diaryText, Calendar date, String type, boolean unread) {
        this(storyEntryNum, chapter, getPerson(personName), diaryText, date, getEntryType(type), unread);
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

    private static Person getPerson(String person) {
        Person out = Person.JONATHAN_HARKER;
        for(Person p : Person.values()) {
            if (p.name().equals(person)) {
                out = p;
                break;
            }
        }
        return out;
    }

    public short getStoryEntryNum() {
        return storyEntryNum;
    }

    public short getChapter() {
        return chapter;
    }

    public String getPerson() {
        return person.toString();
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
