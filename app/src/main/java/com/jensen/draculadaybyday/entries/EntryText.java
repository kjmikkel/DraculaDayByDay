package com.jensen.draculadaybyday.entries;

public class EntryText {

    private String rawText;

    private String date;

    private String location;

    private String comment;

    private String mainEntry;

    public EntryText(String text) {
        this.rawText = text;

        date = parseText("d", text);
        location = parseText("l", text);
        comment = parseText("c", text);
        mainEntry = parseText("e", text);
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
}
