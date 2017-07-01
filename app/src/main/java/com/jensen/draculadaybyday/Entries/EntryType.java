package com.jensen.draculadaybyday.Entries;

public enum EntryType {
    DIARY_ENTRY("Diary Entry"),
    LETTER("Letter"),
    TELEGRAM("Telegram"),
    PHONOGRAPH("Phonograph"),
    NEWSPAPER("Newspaper"),
    NOTE("Note");

    public String description;

    EntryType(String description) {
        this.description = description;
    }
}
