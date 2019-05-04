package com.jensen.draculadaybyday.entry;

import android.util.Log;

import java.util.ArrayList;

public class EntryText {

    private final String seperator = "-sep-";

    private final String rawText;

    private final String date;

    private final String location;

    private final String comment;

    private String mainEntry;

    private final ArrayList<Integer> dividers = new ArrayList<>();

    public EntryText(String text) {
        this.rawText = text;

        date = parseText("d", text);
        location = parseText("l", text);
        comment = parseText("c", text);
        mainEntry = parseText("e", text);

        findDividers();
    }

    private void findDividers() {
        int startIndex = mainEntry.indexOf(seperator, 0);
        try {

            if (startIndex > -1) {
                do {
                    dividers.add(startIndex);

                    // Replace the separation indicator with a space
                    mainEntry = mainEntry.replaceFirst(seperator, " ");
                    startIndex = mainEntry.indexOf(seperator, startIndex);
                } while (startIndex > -1);
            }
        } catch(Exception e) {
            Log.e("EntryText", e.getMessage());
        }
    }

    private String parseText(String indicator, String text) {
        String returnValue = null;

        String start = String.format("[%s]", indicator);
        String end = String.format("[/%s]", indicator);

        int startIndex = text.indexOf(start) + start.length();
        int endIndex = text.indexOf(end);

        if (startIndex != -1 && endIndex != -1) {
            returnValue  = text.substring(startIndex, endIndex);
        }

        return returnValue;
    }

    public String getRawText() {
        return rawText;
    }

    //region Date
    public boolean hasDate() {
        return date != null;
    }

    public String getDate()  {
        return date;
    }
    //endregion

    //region Location
    public boolean hasLocation() {
        return location != null;
    }

    public String getLocation() {
        return location;
    }
    //endregion

    //region Comment
    public boolean hasComment() {
        return comment != null;
    }

    public String getComment() {
        return comment;
    }
    //endregion

    //region Main Entry
    public boolean hasEntry() {
        return mainEntry != null;
    }

    public String getMainEntry() {
        return mainEntry;
    }
    //endregion

    //region Dividers
    public boolean hasDividers() { return !dividers.isEmpty(); }

    public ArrayList<Integer> getDividers() { return dividers; }
    //endregion
}
